<template>
  <div class="page">
    <template v-if="product">
      <el-card class="card">
        <div class="content">
          <img class="cover" :src="product.coverImg || placeholder" alt="cover" />
          <div class="info">
            <h2>{{ product.title }}</h2>
            <div class="price">￥{{ product.price }}</div>
            <div class="time">发布时间：{{ formatDateTime(product.createTime) }}</div>
            <div class="seller" v-if="product.sellerName">
              <el-avatar :size="36" :src="product.sellerAvatar || sellerPlaceholder" />
              <div class="seller-info">
                <div class="seller-name">卖家：{{ product.sellerName }}</div>
                <div class="seller-id">ID：{{ product.sellerId }}</div>
              </div>
              <el-button
                v-if="canChat"
                size="small"
                type="success"
                @click="handleChat"
              >
                聊一聊
              </el-button>
            </div>
            <div class="desc">{{ product.description }}</div>
            <div class="actions">
              <el-button type="primary" @click="handleOrder">立即下单</el-button>
              <el-button :type="liked ? 'success' : 'default'" @click="toggleLikeStatus">
                {{ liked ? '已点赞' : '点赞' }}
              </el-button>
              <el-button :type="favorited ? 'warning' : 'default'" @click="toggleFavoriteStatus">
                {{ favorited ? '已收藏' : '收藏' }}
              </el-button>
            </div>
          </div>
        </div>
      </el-card>
      <el-card class="card" style="margin-top: 12px">
        <template #header>
          <div class="section-title">评论</div>
        </template>
        <div class="comment-input">
          <el-input v-model="commentText" placeholder="输入评论内容" />
          <el-button type="primary" @click="handleComment">发布</el-button>
        </div>
        <el-empty v-if="!comments.length" description="暂无评论" />
        <div v-else class="comment-list">
          <div v-for="item in comments" :key="item.id" class="comment-item">
            <div class="comment-name">{{ item.username || '匿名' }}</div>
            <div class="comment-content">{{ item.content }}</div>
            <div class="comment-time">{{ formatDateTime(item.createTime) }}</div>
          </div>
        </div>
      </el-card>
    </template>
    <el-empty v-else description="商品不存在" />
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getProductDetail } from '../api/product'
import { createOrder } from '../api/order'
import { addComment, getComments } from '../api/comment'
import { checkFavorite, toggleFavorite } from '../api/favorite'
import { checkLike, toggleLike } from '../api/like'
import { formatDateTime } from '../utils/time'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const product = ref(null)
const placeholder = 'https://via.placeholder.com/320x240?text=Product'
const sellerPlaceholder = 'https://via.placeholder.com/80x80?text=User'
const comments = ref([])
const commentText = ref('')
const liked = ref(false)
const favorited = ref(false)

const canChat = computed(() => {
  if (!product.value || !product.value.sellerId) {
    return false
  }
  const currentId = authStore.user?.id
  return currentId ? Number(currentId) !== Number(product.value.sellerId) : true
})

onMounted(async () => {
  try {
    product.value = await getProductDetail(route.params.id)
    await loadComments()
    await loadStatus()
  } catch (err) {
    ElMessage.error(err.message)
  }
})

const loadComments = async () => {
  const res = await getComments(route.params.id, 1)
  comments.value = res.data || res || []
}

const loadStatus = async () => {
  liked.value = await checkLike(route.params.id)
  favorited.value = await checkFavorite(route.params.id)
}

const handleComment = async () => {
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  const res = await addComment(route.params.id, 1, commentText.value.trim())
  if (res.code !== 200) {
    ElMessage.error(res.message || '评论失败')
    return
  }
  commentText.value = ''
  await loadComments()
}

const toggleLikeStatus = async () => {
  const res = await toggleLike(route.params.id)
  if (typeof res === 'string' && res.includes('未登录')) {
    ElMessage.error('请先登录')
    return
  }
  await loadStatus()
}

const toggleFavoriteStatus = async () => {
  const res = await toggleFavorite(route.params.id)
  if (typeof res === 'string' && res.includes('未登录')) {
    ElMessage.error('请先登录')
    return
  }
  await loadStatus()
}

const handleOrder = async () => {
  const res = await createOrder(route.params.id)
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

const handleChat = () => {
  if (!product.value?.sellerId) {
    return
  }
  router.push({ path: '/chat', query: { userId: String(product.value.sellerId) } })
}
</script>

<style scoped>
.page {
  padding: 12px;
}
.card {
  border-radius: 8px;
}
.content {
  display: flex;
  gap: 16px;
}
.cover {
  width: 320px;
  height: 240px;
  object-fit: cover;
  border-radius: 8px;
}
.price {
  color: #f56c6c;
  font-size: 20px;
  margin: 8px 0;
}
.time {
  color: #909399;
  font-size: 12px;
  margin-bottom: 6px;
}
.seller {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.seller-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.seller-name {
  font-weight: 600;
}
.seller-id {
  color: #909399;
  font-size: 12px;
}
.desc {
  color: #606266;
}
.actions {
  margin-top: 12px;
  display: flex;
  gap: 8px;
}
.section-title {
  font-weight: 600;
}
.comment-input {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
  margin-bottom: 10px;
}
.comment-item {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.comment-name {
  font-weight: 600;
  margin-bottom: 4px;
}
.comment-content {
  color: #606266;
}
.comment-time {
  color: #909399;
  font-size: 12px;
  margin-top: 4px;
}
</style>
