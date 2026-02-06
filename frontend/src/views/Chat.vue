<template>
  <div class="page">
    <el-card class="chat-card">
      <div class="chat-layout">
        <ConversationList
          :sessions="sessions"
          :active-session-id="activeSessionId"
          @select="handleSelect"
          @search="handleSearch"
        />
        <ChatWindow
          v-model="draft"
          :session="activeSession"
          :messages="messages"
          :current-user-id="currentUserId"
          :search-keyword="searchKeyword"
          @send="handleSend"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import ConversationList from '../components/chat/ConversationList.vue'
import ChatWindow from '../components/chat/ChatWindow.vue'
import { useAuthStore } from '../store/auth'
import {
  getChatHistory,
  getChatSessions,
  getOrCreateSession,
  readSession,
} from '../api/chat'

const authStore = useAuthStore()
const route = useRoute()
const sessions = ref([])
const messages = ref([])
const activeSessionId = ref('')
const draft = ref('')
const searchKeyword = ref('')
const ws = ref(null)
const pendingClientIds = ref(new Set())

const currentUserId = computed(() => authStore.user?.id || '')

const activeSession = computed(
  () => sessions.value.find((item) => item.sessionId === activeSessionId.value) || null,
)

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const loadSessions = async (keyword = '') => {
  const res = await getChatSessions(keyword || undefined)
  sessions.value = unwrapList(res)
}

const loadHistory = async (sessionId) => {
  const res = await getChatHistory(sessionId)
  messages.value = unwrapList(res)
}

const handleSelect = async (session) => {
  activeSessionId.value = session.sessionId
  await loadHistory(session.sessionId)
  await readSession(session.sessionId)
  session.unreadCount = 0
}

const handleSearch = async (keyword) => {
  searchKeyword.value = keyword
  await loadSessions(keyword)
  if (keyword && sessions.value.length) {
    const exists = sessions.value.some((item) => item.sessionId === activeSessionId.value)
    if (!exists) {
      await handleSelect(sessions.value[0])
    }
  }
}

const handleSend = () => {
  if (!activeSession.value) {
    ElMessage.warning('请选择会话')
    return
  }
  const text = draft.value.trim()
  if (!text) {
    ElMessage.warning('请输入消息')
    return
  }
  const clientId = `${Date.now()}_${Math.random().toString(16).slice(2)}`
  pendingClientIds.value.add(clientId)
  const tempMessage = {
    id: clientId,
    sessionId: activeSession.value.sessionId,
    senderId: currentUserId.value,
    receiverId: activeSession.value.otherUserId,
    content: text,
    type: 'text',
    status: 1,
    sendTime: new Date().toISOString(),
  }
  messages.value.push(tempMessage)
  updateSessionAfterMessage(activeSession.value.sessionId, text, tempMessage.sendTime, false)
  draft.value = ''
  if (ws.value && ws.value.readyState === WebSocket.OPEN) {
    ws.value.send(
      JSON.stringify({
        type: 'send',
        toUserId: activeSession.value.otherUserId,
        content: text,
        clientId,
      }),
    )
  } else {
    ElMessage.error('连接未建立，发送失败')
  }
}

const updateSessionAfterMessage = (sessionId, content, time, incoming) => {
  const session = sessions.value.find((item) => item.sessionId === sessionId)
  if (session) {
    session.lastMessage = content
    session.lastTime = time
    if (incoming && sessionId !== activeSessionId.value) {
      session.unreadCount = (session.unreadCount || 0) + 1
    }
    sessions.value = [...sessions.value].sort((a, b) => {
      const aTime = a.lastTime ? new Date(a.lastTime).getTime() : 0
      const bTime = b.lastTime ? new Date(b.lastTime).getTime() : 0
      return bTime - aTime
    })
  }
}

const handleWsMessage = async (event) => {
  try {
    const payload = JSON.parse(event.data)
    if (payload.type !== 'message') {
      return
    }
    const msg = payload.data
    const clientId = payload.clientId
    if (clientId && pendingClientIds.value.has(clientId)) {
      pendingClientIds.value.delete(clientId)
    } else {
      if (msg.sessionId === activeSessionId.value) {
        messages.value.push(msg)
        await readSession(msg.sessionId)
        updateSessionAfterMessage(msg.sessionId, msg.content, msg.sendTime, false)
      } else {
        updateSessionAfterMessage(msg.sessionId, msg.content, msg.sendTime, true)
      }
    }
    if (!sessions.value.find((item) => item.sessionId === msg.sessionId)) {
      await loadSessions()
    }
  } catch (err) {
    ElMessage.error('消息解析失败')
  }
}

const connectWs = () => {
  if (!authStore.token) {
    return
  }
  const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
  const wsUrl = `${protocol}://${window.location.hostname}:8080/ws/chat?token=${authStore.token}`
  ws.value = new WebSocket(wsUrl)
  ws.value.onmessage = handleWsMessage
}

const initWithUserId = async () => {
  const targetId = route.query.userId
  if (!targetId) {
    return
  }
  const res = await getOrCreateSession(Number(targetId))
  const session = res.data || res
  if (session && session.sessionId) {
    if (!sessions.value.find((item) => item.sessionId === session.sessionId)) {
      sessions.value.unshift(session)
    }
    await handleSelect(session)
  }
}

onMounted(async () => {
  await loadSessions()
  connectWs()
  await initWithUserId()
})
</script>

<style scoped>
.page {
  padding: 12px;
}
.chat-card {
  border-radius: 8px;
}
.chat-layout {
  display: grid;
  grid-template-columns: 280px 1fr;
  height: 680px;
}
</style>
