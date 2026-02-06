<template>
  <div class="page">
    <el-row :gutter="12" class="kpi-row">
      <el-col :xs="24" :sm="12" :md="6" v-for="item in kpis" :key="item.label">
        <el-card class="kpi-card">
          <div class="kpi-label">{{ item.label }}</div>
          <div class="kpi-value">{{ item.value }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="12" class="chart-row">
      <el-col :xs="24" :md="16">
        <el-card class="card">
          <template #header>
            <div class="title">订单与销售趋势</div>
          </template>
          <div ref="lineChartRef" class="chart" />
        </el-card>
      </el-col>
      <el-col :xs="24" :md="8">
        <el-card class="card">
          <template #header>
            <div class="title">订单状态分布</div>
          </template>
          <div ref="pieChartRef" class="chart" />
        </el-card>
      </el-col>
    </el-row>

  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getAdminDashboard, getAdminTrend } from '../../api/admin'

const lineChartRef = ref(null)
const pieChartRef = ref(null)
let lineChartInstance = null
let pieChartInstance = null
const kpis = ref([])

const formatMoney = (value) => {
  if (value == null) return '0.00'
  return Number(value).toFixed(2)
}

const renderLineChart = (data) => {
  if (!lineChartRef.value) return
  if (!lineChartInstance) {
    lineChartInstance = echarts.init(lineChartRef.value)
  }
  const option = {
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单数', '销售额'] },
    grid: { left: 40, right: 40, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: data.dates || [] },
    yAxis: [
      { type: 'value', name: '订单数' },
      { type: 'value', name: '销售额' },
    ],
    series: [
      {
        name: '订单数',
        type: 'line',
        smooth: true,
        data: data.orderCounts || [],
      },
      {
        name: '销售额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: data.salesAmounts || [],
      },
    ],
  }
  lineChartInstance.setOption(option)
}

const renderPieChart = (data) => {
  if (!pieChartRef.value) return
  if (!pieChartInstance) {
    pieChartInstance = echarts.init(pieChartRef.value)
  }
  const option = {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '70%'],
        data: [
          { name: '待支付', value: data.pending || 0 },
          { name: '已完成', value: data.completed || 0 },
          { name: '已取消', value: data.canceled || 0 },
        ],
      },
    ],
  }
  pieChartInstance.setOption(option)
}

const loadDashboard = async () => {
  const res = await getAdminDashboard()
  if (res.code !== 200) {
    ElMessage.error(res.message || '加载失败')
    return
  }
  const data = res.data || {}
  kpis.value = [
    { label: '总订单数', value: data.totalOrders ?? 0 },
    { label: '总销售额', value: `￥${formatMoney(data.totalSales)}` },
    { label: '今日订单数', value: data.todayOrders ?? 0 },
    { label: '今日销售额', value: `￥${formatMoney(data.todaySales)}` },
  ]
  renderPieChart(data.orderStatusCounts || {})
}

const loadTrend = async () => {
  try {
    const res = await getAdminTrend(7)
    if (res.code !== 200) {
      ElMessage.error(res.message || '加载失败')
      return
    }
    renderLineChart(res.data || {})
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handleResize = () => {
  if (lineChartInstance) {
    lineChartInstance.resize()
  }
  if (pieChartInstance) {
    pieChartInstance.resize()
  }
}

onMounted(async () => {
  await loadDashboard()
  await loadTrend()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (lineChartInstance) {
    lineChartInstance.dispose()
    lineChartInstance = null
  }
  if (pieChartInstance) {
    pieChartInstance.dispose()
    pieChartInstance = null
  }
})
</script>

<style scoped>
.page {
  padding: 12px;
  display: grid;
  gap: 12px;
}
.card {
  border-radius: 8px;
}
.title {
  font-weight: 600;
}
.chart {
  width: 100%;
  height: 360px;
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
.chart-row .card {
  height: 100%;
}
</style>
