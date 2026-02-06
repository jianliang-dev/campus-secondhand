<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">留言反馈</div>
      </template>
      <el-select v-model="type" placeholder="请选择反馈类型" style="margin-bottom: 10px; width: 200px">
        <el-option label="功能建议" value="功能建议" />
        <el-option label="问题反馈" value="问题反馈" />
        <el-option label="投诉举报" value="投诉举报" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-input v-model="content" type="textarea" rows="4" placeholder="请输入反馈内容" />
      <el-button type="primary" style="margin-top: 10px" @click="handleSubmit">提交</el-button>
    </el-card>

    <el-card class="card">
      <template #header>
        <div class="title">我的反馈</div>
      </template>
      <el-empty v-if="!feedbacks.length" description="暂无反馈" />
      <el-timeline v-else>
        <el-timeline-item v-for="item in feedbacks" :key="item.id">
          <div class="feedback-item">
            <div class="feedback-content">{{ item.content }}</div>
            <div class="feedback-meta">
              <el-tag size="small" type="info">{{ item.type || '其他' }}</el-tag>
              <el-tag size="small" :type="statusTagType(item.status)">
                {{ statusLabel(item.status) }}
              </el-tag>
            </div>
            <div v-if="item.adminReply" class="feedback-reply">
              管理员回复：{{ item.adminReply }}
            </div>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { addFeedback, getMyFeedback } from '../api/feedback'

const content = ref('')
const feedbacks = ref([])
const type = ref('功能建议')

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  const res = await getMyFeedback()
  feedbacks.value = unwrapList(res)
}

const handleSubmit = async () => {
  if (!content.value.trim()) {
    ElMessage.warning('请输入内容')
    return
  }
  const res = await addFeedback(content.value.trim(), type.value)
  if (res.code !== 200) {
    ElMessage.error(res.message || '提交失败')
    return
  }
  ElMessage.success('提交成功')
  content.value = ''
  await load()
}

const statusLabel = (status) => {
  if (status === 1) return '处理中'
  if (status === 2) return '已处理'
  return '未处理'
}

const statusTagType = (status) => {
  if (status === 1) return 'warning'
  if (status === 2) return 'success'
  return 'info'
}

onMounted(load)
</script>

<style scoped>
.page {
  display: grid;
  gap: 12px;
}
.card {
  border-radius: 8px;
}
.title {
  font-weight: 600;
}
.feedback-item {
  display: grid;
  gap: 6px;
}
.feedback-content {
  color: #303133;
}
.feedback-meta {
  display: flex;
  gap: 6px;
}
.feedback-reply {
  color: #409eff;
  font-size: 12px;
}
</style>
