<template>
  <div class="input-area">
    <el-input
      v-model="localValue"
      type="textarea"
      :rows="3"
      placeholder="输入消息，Enter发送，Shift+Enter换行"
      @keydown="handleKeydown"
    />
    <el-button type="primary" @click="emitSend">发送</el-button>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: '',
  },
})

const emit = defineEmits(['update:modelValue', 'send'])

const localValue = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const emitSend = () => {
  emit('send')
}

const handleKeydown = (event) => {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    emit('send')
  }
}
</script>

<style scoped>
.input-area {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  align-items: end;
}
</style>
