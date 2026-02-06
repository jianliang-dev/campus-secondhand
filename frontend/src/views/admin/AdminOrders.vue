<template>
  <el-card class="card">
    <template #header>
      <div class="title">订单管理</div>
    </template>
    <el-table :data="orders">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="orderNo" label="订单号" min-width="220" />
      <el-table-column prop="buyerId" label="买家ID" width="120" />
      <el-table-column prop="sellerId" label="卖家ID" width="120" />
      <el-table-column prop="productId" label="商品ID" width="120" />
      <el-table-column prop="totalPrice" label="金额" width="120" />
      <el-table-column label="订单状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.orderStatus === 1 ? 'success' : 'info'">
            {{ row.orderStatus === 1 ? '已支付' : '待支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="支付状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.payStatus === 1 ? 'success' : 'warning'">
            {{ row.payStatus === 1 ? '已支付' : '未支付' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="row.orderStatus === 0"
            size="small"
            type="success"
            @click="setStatus(row, 1)"
          >
            设为已支付
          </el-button>
          <el-button v-else size="small" type="warning" @click="setStatus(row, 0)">
            设为待支付
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!orders.length" description="暂无订单" />
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminOrders, updateAdminOrderStatus } from '../../api/admin'

const orders = ref([])

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  try {
    const res = await getAdminOrders()
    orders.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message || '加载失败')
  }
}

const setStatus = async (row, status) => {
  const res = await updateAdminOrderStatus(row.id, status)
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
</style>
