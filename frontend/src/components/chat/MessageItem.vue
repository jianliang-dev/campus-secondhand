<template>
  <div class="message-item" :class="{ mine: isMine, match: isMatch }">
    <div class="bubble" :class="{ mine: isMine }" v-html="displayContent" />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  message: {
    type: Object,
    required: true,
  },
  isMine: {
    type: Boolean,
    default: false,
  },
  highlightKeyword: {
    type: String,
    default: '',
  },
})

const escapeHtml = (value) =>
  String(value)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')

const displayContent = computed(() => {
  const text = escapeHtml(props.message?.content || '')
  const key = props.highlightKeyword?.trim()
  if (!key) {
    return text
  }
  const escaped = key.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  const reg = new RegExp(escaped, 'gi')
  return text.replace(reg, (match) => `<span class="highlight">${match}</span>`)
})

const isMatch = computed(() => {
  const key = props.highlightKeyword?.trim()
  if (!key) {
    return false
  }
  return String(props.message?.content || '').toLowerCase().includes(key.toLowerCase())
})
</script>

<style scoped>
.message-item {
  display: flex;
  justify-content: flex-start;
}
.message-item.mine {
  justify-content: flex-end;
}
.bubble {
  max-width: 70%;
  padding: 8px 10px;
  border-radius: 8px;
  background: #f2f3f5;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}
.bubble.mine {
  background: #95ec69;
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
</style>
