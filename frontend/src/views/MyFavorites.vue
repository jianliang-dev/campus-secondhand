<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">我的收藏</div>
      </template>
      <el-button size="small" @click="load">刷新</el-button>
      <div class="list" v-loading="loading">
        <div v-for="item in favorites" :key="item.favoriteId" class="item">
          <div class="cover-wrap" @click="goDetail(item)">
            <img class="cover" :src="item.coverImg || placeholder" alt="cover" />
            <div v-if="showMask(item)" class="mask">
              <el-tag type="info" size="small">{{ maskLabel(item) }}</el-tag>
            </div>
          </div>
          <div class="info">
            <div class="title-row" @click="goDetail(item)">
              {{ item.title }}
            </div>
            <div class="price">￥{{ item.price }}</div>
            <div class="meta">
              卖家：{{ item.sellerName || '未知' }} · 发布时间：{{ formatDateTime(item.createTime) }}
            </div>
            <div class="status">
              <el-tag :type="statusTagType(item)" size="small">{{ statusLabel(item) }}</el-tag>
            </div>
            <div class="actions">
              <el-button size="small" @click="goDetail(item)">查看详情</el-button>
              <el-button
                size="small"
                type="primary"
                :disabled="disableOrder(item)"
                @click="handleOrder(item)"
              >
                立即下单
              </el-button>
              <el-button size="small" type="danger" @click="handleCancelFavorite(item)">
                取消收藏
              </el-button>
            </div>
          </div>
        </div>
        <el-empty v-if="!favorites.length && !loading" description="暂无收藏商品" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { createOrder } from '../api/order'
import { getFavorites, toggleFavorite } from '../api/favorite'
import { formatDateTime } from '../utils/time'

const router = useRouter()
const favorites = ref([])
const loading = ref(false)
const placeholder = 'https://via.placeholder.com/240x180?text=Product'

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  loading.value = true
  try {
    const res = await getFavorites()
    favorites.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message)
  } finally {
    loading.value = false
  }
}

const goDetail = (item) => {
  router.push(`/product/${item.productId}`)
}

const disableOrder = (item) => item.soldFlag === 1 || item.status !== 1

const handleOrder = async (item) => {
  if (disableOrder(item)) {
    return
  }
  const res = await createOrder(item.productId)
  if (typeof res === 'string') {
    ElMessage.success(res)
    return
  }
  if (res.code && res.code !== 200) {
    ElMessage.error(res.message || '下单失败')
    return
  }
  ElMessage.success('下单成功')
}

const handleCancelFavorite = async (item) => {
  const res = await toggleFavorite(item.productId)
  if (typeof res === 'string' && res.includes('未登录')) {
    ElMessage.error('请先登录')
    return
  }
  ElMessage.success('已取消收藏')
  await load()
}

const showMask = (item) => disableOrder(item)

const maskLabel = (item) => {
  if (item.soldFlag === 1) return '已售出'
  return '已下架'
}

const statusLabel = (item) => {
  if (item.soldFlag === 1) return '已售出'
  return item.status === 1 ? '上架' : '下架'
}

const statusTagType = (item) => {
  if (item.soldFlag === 1) return 'info'
  return item.status === 1 ? 'success' : 'info'
}

onMounted(load)
</script>

<style scoped>
.page {
  padding: 12px;
}
.card {
  border-radius: 8px;
}
.title {
  font-weight: 600;
}
.list {
  margin-top: 12px;
  display: grid;
  gap: 12px;
}
.item {
  display: grid;
  grid-template-columns: 240px 1fr;
  gap: 16px;
  padding: 12px;
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  background: #fff;
}
.cover-wrap {
  position: relative;
  width: 240px;
  height: 180px;
  cursor: pointer;
}
.cover {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
}
.mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.35);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
}
.info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.title-row {
  font-weight: 600;
  cursor: pointer;
}
.price {
  color: #f56c6c;
  font-size: 18px;
}
.meta {
  color: #909399;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}
@media (max-width: 960px) {
  .item {
    grid-template-columns: 1fr;
  }
  .cover-wrap {
    width: 100%;
    height: 200px;
  }
}
</style>
