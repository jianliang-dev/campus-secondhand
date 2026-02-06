<template>
  <div class="chat-window">
    <div class="header" v-if="session">
      <el-avatar :size="40" :src="session.otherAvatar || placeholder" />
      <div class="info">
        <div class="name">{{ session.otherName || '未知用户' }}</div>
        <div class="status" v-if="session.lastTime">最近消息：{{ formatTimeLabel(session.lastTime) }}</div>
      </div>
    </div>
    <div v-else class="header placeholder">请选择会话</div>

    <div ref="listRef" class="message-list">
      <el-empty v-if="!displayItems.length" description="暂无消息" />
      <template v-else>
        <div
          v-for="(item, index) in displayItems"
          :key="item.key || index"
          class="row"
        >
          <div v-if="item.type === 'time'" class="time-separator">
            {{ item.label }}
          </div>
          <MessageItem
            v-else
            :message="item.message"
            :is-mine="String(item.message.senderId) === String(currentUserId)"
            :highlight-keyword="searchKeyword"
          />
        </div>
      </template>
    </div>

    <div class="footer">
      <ChatInput v-model="draft" @send="emitSend" />
    </div>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import ChatInput from './ChatInput.vue'
import MessageItem from './MessageItem.vue'
import { formatTimeLabel } from '../../utils/time'

const props = defineProps({
  session: {
    type: Object,
    default: null,
  },
  messages: {
    type: Array,
    default: () => [],
  },
  currentUserId: {
    type: [String, Number],
    default: '',
  },
  searchKeyword: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['send'])

const draft = defineModel({ type: String, default: '' })

const listRef = ref(null)
const placeholder = 'https://via.placeholder.com/40x40?text=U'

const displayItems = computed(() => {
  const result = []
  let lastTime = null
  props.messages.forEach((msg, index) => {
    const time = msg.sendTime || msg.createTime
    if (!lastTime || Math.abs(new Date(time) - new Date(lastTime)) > 5 * 60 * 1000) {
      result.push({
        type: 'time',
        label: formatTimeLabel(time),
        key: `time-${index}`,
      })
      lastTime = time
    }
    result.push({ type: 'msg', message: msg, key: `msg-${msg.id || index}` })
  })
  return result
})

const emitSend = () => {
  emit('send')
}

const scrollToBottom = async () => {
  await nextTick()
  const el = listRef.value
  if (el) {
    el.scrollTop = el.scrollHeight
  }
}

watch(
  () => props.messages.length,
  () => {
    scrollToBottom()
  },
)

const scrollToMatch = async () => {
  if (!props.searchKeyword) {
    return
  }
  await nextTick()
  const el = listRef.value?.querySelector('.message-item.match')
  if (el && el.scrollIntoView) {
    el.scrollIntoView({ block: 'center' })
  }
}

watch(
  () => [props.searchKeyword, props.messages.length],
  () => {
    scrollToMatch()
  },
)
</script>

<style scoped>
.chat-window {
  display: grid;
  grid-template-rows: auto 1fr auto;
  height: 100%;
  background: #f5f7fa;
}
.header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.header.placeholder {
  justify-content: center;
  color: #909399;
}
.info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.name {
  font-weight: 600;
}
.status {
  font-size: 12px;
  color: #909399;
}
.message-list {
  padding: 12px;
  overflow-y: auto;
}
.row {
  margin-bottom: 6px;
}
.time-separator {
  text-align: center;
  color: #909399;
  font-size: 12px;
  margin: 8px 0;
}
.footer {
  padding: 10px;
  border-top: 1px solid #eee;
  background: #fff;
}
</style>
