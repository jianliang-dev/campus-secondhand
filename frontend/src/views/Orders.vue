<template>
  <div class="page">
    <el-card>
      <div class="toolbar">
        <el-button size="small" @click="load">刷新</el-button>
      </div>
      <el-tabs v-model="active">
        <el-tab-pane label="买家订单" name="buyer">
          <el-table :data="buyerOrders">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="productTitle" label="商品" />
            <el-table-column prop="totalPrice" label="金额" width="120" />
            <el-table-column label="下单时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="支付时间" width="180">
              <template #default="{ row }">
                {{ row.payTime ? formatDateTime(row.payTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="订单状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.orderStatus)">
                  {{ getOrderStatusLabel(row.orderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="支付状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getPayStatusType(row.payStatus)">
                  {{ getPayStatusLabel(row.payStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220">
              <template #default="{ row }">
                <el-button
                  v-if="row.payStatus === 0 && row.orderStatus === 0"
                  size="small"
                  type="primary"
                  @click="handlePay(row)"
                >
                  模拟支付
                </el-button>
                <el-button
                  v-if="row.orderStatus === 0"
                  size="small"
                  type="danger"
                  @click="handleCancel(row)"
                >
                  取消订单
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="卖家订单" name="seller">
          <el-table :data="sellerOrders">
            <el-table-column prop="orderNo" label="订单号" />
            <el-table-column prop="productTitle" label="商品" />
            <el-table-column prop="totalPrice" label="金额" width="120" />
            <el-table-column prop="buyerName" label="买家" width="120" />
            <el-table-column label="下单时间" width="180">
              <template #default="{ row }">
                {{ formatDateTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column label="支付时间" width="180">
              <template #default="{ row }">
                {{ row.payTime ? formatDateTime(row.payTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column label="订单状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getOrderStatusType(row.orderStatus)">
                  {{ getOrderStatusLabel(row.orderStatus) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="支付状态" width="120">
              <template #default="{ row }">
                <el-tag :type="getPayStatusType(row.payStatus)">
                  {{ getPayStatusLabel(row.payStatus) }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { cancelOrder, getBuyerOrders, getSellerOrders, mockPay } from '../api/order'
import { formatDateTime } from '../utils/time'

const active = ref('buyer')
const buyerOrders = ref([])
const sellerOrders = ref([])

const orderStatusLabelMap = {
  0: '待支付',
  1: '已支付',
  2: '已完成',
  3: '已取消',
}

const payStatusLabelMap = {
  0: '未支付',
  1: '已支付',
}

const getOrderStatusLabel = (status) => orderStatusLabelMap[status] || `未知(${status})`
const getPayStatusLabel = (status) => payStatusLabelMap[status] || `未知(${status})`

const getOrderStatusType = (status) => {
  if (status === 0) return 'warning'
  if (status === 1 || status === 2) return 'success'
  if (status === 3) return 'info'
  return 'info'
}

const getPayStatusType = (status) => {
  if (status === 0) return 'danger'
  if (status === 1) return 'success'
  return 'info'
}

const load = async () => {
  try {
    const resBuyer = await getBuyerOrders()
    buyerOrders.value = Array.isArray(resBuyer) ? resBuyer : resBuyer.data || []
    const resSeller = await getSellerOrders()
    sellerOrders.value = Array.isArray(resSeller) ? resSeller : resSeller.data || []
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handlePay = async (row) => {
  try {
    const res = await mockPay(row.id)
    if (res.code !== 200) {
      ElMessage.error(res.message || '支付失败')
      return
    }
    ElMessage.success('支付成功')
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handleCancel = async (row) => {
  try {
    const ok = window.confirm('确定要取消该订单吗？')
    if (!ok) {
      return
    }
    const res = await cancelOrder(row.id)
    if (res.code !== 200) {
      ElMessage.error(res.message || '取消失败')
      return
    }
    ElMessage.success('订单已取消')
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

onMounted(load)
</script>

<style scoped>
.page {
  padding: 12px;
}
.toolbar {
  margin-bottom: 10px;
  display: flex;
  justify-content: flex-end;
}
</style>
