<template>
  <div class="page">
    <el-row :gutter="12" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="8" :lg="4" v-for="item in kpis" :key="item.label">
        <el-card class="kpi-card">
          <div class="kpi-label">{{ item.label }}</div>
          <div class="kpi-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="section-row">
      <el-col :xs="24" :md="14">
        <el-card class="card">
          <template #header>
            <div class="title">待处理提醒</div>
          </template>
          <div class="list">
            <div v-for="item in recentFeedbacks" :key="item.id" class="list-item">
              <div class="list-title">用户ID：{{ item.userId }}</div>
              <div class="list-content">{{ item.content }}</div>
              <div class="list-time">{{ formatDateTime(item.createTime) }}</div>
            </div>
            <el-empty v-if="!recentFeedbacks.length" description="暂无提醒" />
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="10">
        <el-card class="card">
          <template #header>
            <div class="title">快捷入口</div>
          </template>
          <div class="quick-actions">
            <el-button type="primary" @click="go('/admin/products')">去商品管理</el-button>
            <el-button type="primary" @click="go('/admin/orders')">去订单管理</el-button>
            <el-button type="primary" @click="go('/admin/feedback')">去反馈处理</el-button>
            <el-button type="primary" @click="go('/admin/statistics')">去数据统计</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAdminDashboard } from '../../api/admin'
import { formatDateTime } from '../../utils/time'

const router = useRouter()
const kpis = ref([])
const recentFeedbacks = ref([])

const formatMoney = (value) => {
  if (value == null) return '0'
  return Number(value).toFixed(2)
}

const load = async () => {
  try {
    const res = await getAdminDashboard()
    if (res.code !== 200) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    const data = res.data || {}
    kpis.value = [
      { label: '用户总数', value: data.totalUsers ?? 0 },
      {
        label: '商品总数/在售',
        value: `${data.totalProducts ?? 0} / ${data.activeProducts ?? 0}`,
      },
      { label: '订单总数', value: data.totalOrders ?? 0 },
      { label: '今日订单数', value: data.todayOrders ?? 0 },
      { label: '总销售额', value: `￥${formatMoney(data.totalSales)}` },
      { label: '未处理反馈数', value: data.pendingFeedbackCount ?? 0 },
    ]
    recentFeedbacks.value = data.recentFeedbacks || []
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const go = (path) => {
  router.push(path)
}

onMounted(load)
</script>

<style scoped>
.page {
  display: grid;
  gap: 12px;
}
.kpi-row {
  margin-bottom: 4px;
}
.kpi-card {
  border-radius: 8px;
}
.kpi-label {
  color: #909399;
  font-size: 12px;
}
.kpi-value {
  font-size: 20px;
  font-weight: 600;
  margin-top: 6px;
}
.card {
  border-radius: 8px;
}
.title {
  font-weight: 600;
}
.list {
  display: grid;
  gap: 10px;
}
.list-item {
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}
.list-item:last-child {
  border-bottom: none;
}
.list-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.list-content {
  color: #606266;
  margin-bottom: 4px;
}
.list-time {
  color: #909399;
  font-size: 12px;
}
.quick-actions {
  display: grid;
  gap: 10px;
}
</style>
