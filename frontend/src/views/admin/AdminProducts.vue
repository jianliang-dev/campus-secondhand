<template>
  <el-card class="card">
    <template #header>
      <div class="title">商品管理</div>
    </template>
    <el-table :data="products">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" min-width="220" />
      <el-table-column prop="price" label="价格" width="120" />
      <el-table-column prop="userId" label="发布者ID" width="120" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button
            v-if="row.status === 0"
            size="small"
            type="success"
            @click="setStatus(row, 1)"
          >
            上架
          </el-button>
          <el-button v-else size="small" type="warning" @click="setStatus(row, 0)">
            下架
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!products.length" description="暂无商品" />
  </el-card>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getAdminProducts, updateAdminProductStatus } from '../../api/admin'

const products = ref([])

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  try {
    const res = await getAdminProducts()
    products.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message || '加载失败')
  }
}

const setStatus = async (row, status) => {
  const res = await updateAdminProductStatus(row.id, status)
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
