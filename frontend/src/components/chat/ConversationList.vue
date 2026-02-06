<template>
  <div class="conversation-list">
    <el-input
      v-model="keyword"
      placeholder="查找用户名/聊天内容"
      size="small"
      clearable
      class="search-input"
    />
    <div class="list">
      <div
        v-for="item in sessions"
        :key="item.sessionId"
        class="item"
        :class="{ active: item.sessionId === activeSessionId }"
        @click="emitSelect(item)"
      >
        <el-avatar :size="40" :src="item.otherAvatar || placeholder" />
        <div class="content">
          <div class="top">
            <span class="name" v-html="highlightText(item.otherName || '未知用户')" />
            <span class="time">{{ formatTimeLabel(item.lastTime) }}</span>
          </div>
          <div class="bottom">
            <span class="last" v-html="highlightText(item.lastMessage || '')" />
            <el-badge v-if="item.unreadCount > 0" :value="item.unreadCount" class="badge" />
          </div>
        </div>
      </div>
      <el-empty v-if="!sessions.length" description="暂无会话" />
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { formatTimeLabel } from '../../utils/time'

const props = defineProps({
  sessions: {
    type: Array,
    default: () => [],
  },
  activeSessionId: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['select', 'search'])

const keyword = ref('')
const placeholder = 'https://via.placeholder.com/40x40?text=U'

let debounceTimer = null
watch(
  () => keyword.value,
  (val) => {
    if (debounceTimer) {
      clearTimeout(debounceTimer)
    }
    debounceTimer = setTimeout(() => {
      emit('search', val.trim())
    }, 300)
  },
)

const escapeHtml = (value) =>
  String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

const highlightText = (value) => {
  const text = escapeHtml(value || '')
  const key = keyword.value.trim()
  if (!key) {
    return text
  }
  const escaped = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const reg = new RegExp(escaped, 'gi')
  return text.replace(reg, (match) => `<span class="highlight">${match}</span>`)
}

const emitSelect = (item) => {
  emit('select', item)
}
</script>

<style scoped>
.conversation-list {
  display: grid;
  gap: 8px;
  padding: 10px;
  height: 100%;
  box-sizing: border-box;
}
.search-input {
  max-width: 180px;
}
.search-input :deep(.el-input__wrapper) {
  min-height: 22px;
  height: 22px;
  padding: 0 6px;
}
.search-input :deep(.el-input__inner) {
  height: 20px;
  line-height: 20px;
  font-size: 12px;
}
.list {
  display: grid;
  gap: 6px;
  overflow-y: auto;
}
.item {
  display: grid;
  grid-template-columns: 40px 1fr;
  gap: 8px;
  padding: 8px;
  border-radius: 6px;
  cursor: pointer;
}
.item.active {
  background: #f0f2f5;
}
.content {
  display: grid;
  gap: 4px;
}
.top {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.name {
  font-weight: 600;
}
.time {
  color: #909399;
  font-size: 12px;
}
.bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 6px;
}
.last {
  color: #606266;
  font-size: 12px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 180px;
}
.highlight {
  background: #fff2a8;
  padding: 0 2px;
  border-radius: 2px;
}
:deep(.highlight) {
  background: #fff2a8;
  padding: 0 2px;
  border-radius: 2px;
}
.badge {
  margin-left: auto;
}
</style>
