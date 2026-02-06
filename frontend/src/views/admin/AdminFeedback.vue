<template>
  <el-card class="card">
    <template #header>
      <div class="title">反馈处理</div>
    </template>
    <div class="toolbar">
      <el-select v-model="filters.status" placeholder="状态" class="input" clearable>
        <el-option label="未处理" :value="0" />
        <el-option label="处理中" :value="1" />
        <el-option label="已处理" :value="2" />
      </el-select>
      <el-select v-model="filters.type" placeholder="类型" class="input" clearable>
        <el-option label="功能建议" value="功能建议" />
        <el-option label="问题反馈" value="问题反馈" />
        <el-option label="投诉举报" value="投诉举报" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-input v-model="filters.keyword" placeholder="关键词搜索" class="input" clearable />
      <el-button type="primary" @click="load">筛选</el-button>
      <el-button @click="resetFilter">重置</el-button>
    </div>
    <el-table :data="feedbacks">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="类型" width="120">
        <template #default="{ row }">
          <el-tag size="small" type="info">{{ row.type || '其他' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="用户" width="160">
        <template #default="{ row }">
          {{ row.username || '匿名' }} / {{ row.userId }}
        </template>
      </el-table-column>
      <el-table-column label="内容摘要">
        <template #default="{ row }">
          {{ summary(row.content) }}
        </template>
      </el-table-column>
      <el-table-column label="提交时间" width="180">
        <template #default="{ row }">
          {{ formatDateTime(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag size="small" :type="statusTagType(row.status)">
            {{ statusLabel(row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260">
        <template #default="{ row }">
          <el-button size="small" @click="openDetail(row)">查看详情</el-button>
          <el-button size="small" type="warning" @click="quickUpdate(row, 1)">
            标记处理中
          </el-button>
          <el-button size="small" type="success" @click="quickUpdate(row, 2)">
            标记已处理
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!feedbacks.length" description="暂无反馈" />
  </el-card>

  <el-dialog v-model="dialogVisible" title="反馈详情" width="560px">
    <div class="detail">
      <div>用户：{{ detail.username || '匿名' }} / {{ detail.userId }}</div>
      <div>类型：{{ detail.type || '其他' }}</div>
      <div>提交时间：{{ formatDateTime(detail.createTime) }}</div>
      <div class="detail-content">内容：{{ detail.content }}</div>
    </div>
    <el-form :model="detailForm" label-width="90px" style="margin-top: 12px">
      <el-form-item label="状态">
        <el-select v-model="detailForm.status" placeholder="请选择状态">
          <el-option label="未处理" :value="0" />
          <el-option label="处理中" :value="1" />
          <el-option label="已处理" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="管理员回复">
        <el-input v-model="detailForm.adminReply" type="textarea" rows="3" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleUpdate">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminFeedback, updateAdminFeedback, updateAdminFeedbackStatus } from '../../api/admin'
import { formatDateTime } from '../../utils/time'

const feedbacks = ref([])
const dialogVisible = ref(false)
const detail = reactive({})
const detailForm = reactive({
  id: null,
  status: 0,
  adminReply: '',
})
const filters = reactive({
  status: '',
  type: '',
  keyword: '',
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  try {
    const res = await getAdminFeedback({
      status: filters.status === '' ? undefined : filters.status,
      type: filters.type || undefined,
      keyword: filters.keyword || undefined,
    })
    feedbacks.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const resetFilter = async () => {
  filters.status = ''
  filters.type = ''
  filters.keyword = ''
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

const summary = (content) => {
  if (!content) return '-'
  return content.length > 20 ? `${content.slice(0, 20)}...` : content
}

const openDetail = (row) => {
  Object.assign(detail, row)
  Object.assign(detailForm, {
    id: row.id,
    status: row.status ?? 0,
    adminReply: row.adminReply || '',
  })
  dialogVisible.value = true
}

const handleUpdate = async () => {
  const res = await updateAdminFeedback(detailForm)
  if (res.code !== 200) {
    ElMessage.error(res.message || '更新失败')
    return
  }
  ElMessage.success('更新成功')
  dialogVisible.value = false
  await load()
}

const quickUpdate = async (row, status) => {
  const res = await updateAdminFeedbackStatus(row.id, status)
  if (res.code !== 200) {
    ElMessage.error(res.message || '更新失败')
    return
  }
  ElMessage.success('状态已更新')
  await load()
}

onMounted(load)
</script>

<style scoped>
.card {
  border-radius: 8px;
}
.title {
  font-weight: 600;
}
.toolbar {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 12px;
}
.input {
  max-width: 220px;
}
.detail {
  display: grid;
  gap: 6px;
  color: #606266;
}
.detail-content {
  color: #303133;
}
</style>
