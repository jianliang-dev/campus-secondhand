<template>
  <div class="page">
    <el-card v-loading="loading" class="card">
      <div class="title">{{ post?.title || '帖子详情' }}</div>
      <div class="author">
        <el-avatar :size="40" :src="post?.avatar || placeholder" />
        <div class="author-info">
          <div class="name">{{ post?.username || '匿名' }}</div>
          <div class="meta">
            {{ formatDateTime(post?.createTime) }} · 浏览 {{ post?.viewCount || 0 }} · 点赞
            {{ post?.likeCount || 0 }} · 评论 {{ post?.commentCount || 0 }}
          </div>
        </div>
        <div class="actions">
          <el-button size="small" type="success" @click="handleChat">联系楼主</el-button>
          <el-button
            size="small"
            :type="liked ? 'success' : 'default'"
            @click="toggleLikeStatus"
          >
            {{ liked ? '已点赞' : '点赞' }}
          </el-button>
        </div>
      </div>
      <div class="content">{{ post?.content }}</div>
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
        <div
          v-for="item in sortedComments"
          :key="item.id"
          class="comment-item"
          :class="{ author: isAuthorComment(item) }"
        >
          <div class="comment-header">
            <div class="comment-name">
              {{ item.username || '匿名' }}
              <el-tag v-if="isAuthorComment(item)" size="small" type="success" class="author-tag">楼主</el-tag>
            </div>
            <el-button
              v-if="canDeleteComment(item)"
              text
              type="danger"
              size="small"
              @click="handleDeleteComment(item)"
            >
              删除
            </el-button>
          </div>
          <div class="comment-content">{{ item.content }}</div>
          <div class="comment-time">{{ formatDateTime(item.createTime) }}</div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPostDetail } from '../api/post'
import { addComment, deleteComment, getComments } from '../api/comment'
import { checkPostLike, togglePostLike } from '../api/postLike'
import { formatDateTime } from '../utils/time'
import { useAuthStore } from '../store/auth'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const post = ref(null)
const comments = ref([])
const commentText = ref('')
const liked = ref(false)
const placeholder = 'https://via.placeholder.com/80x80?text=U'
const authStore = useAuthStore()

const sortedComments = computed(() => {
  const authorId = post.value?.userId
  if (!authorId) {
    return comments.value
  }
  const authorComments = []
  const others = []
  comments.value.forEach((item) => {
    if (Number(item?.userId) === Number(authorId)) {
      authorComments.push(item)
    } else {
      others.push(item)
    }
  })
  return [...authorComments, ...others]
})

const loadDetail = async () => {
  loading.value = true
  try {
    const res = await getPostDetail(route.params.id)
    post.value = res.data || res
  } catch (err) {
    ElMessage.error(err.message)
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  const res = await getComments(route.params.id, 2)
  comments.value = res.data || res || []
}

const handleComment = async () => {
  if (!commentText.value.trim()) {
    ElMessage.warning('请输入评论内容')
    return
  }
  const res = await addComment(route.params.id, 2, commentText.value.trim())
  if (res.code !== 200) {
    ElMessage.error(res.message || '评论失败')
    return
  }
  commentText.value = ''
  await loadComments()
  await loadDetail()
}

const isAuthorComment = (item) => {
  return Number(item?.userId) === Number(post.value?.userId)
}

const canDeleteComment = (item) => {
  const currentId = authStore.user?.id
  return currentId && Number(currentId) === Number(item?.userId)
}

const handleDeleteComment = async (item) => {
  try {
    await ElMessageBox.confirm('确认删除这条评论吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  const res = await deleteComment(item.id)
  if (res.code !== 200) {
    ElMessage.error(res.message || '删除失败')
    return
  }
  ElMessage.success('删除成功')
  await loadComments()
  await loadDetail()
}

const loadStatus = async () => {
  liked.value = await checkPostLike(route.params.id)
}

const toggleLikeStatus = async () => {
  const res = await togglePostLike(route.params.id)
  if (typeof res === 'string' && res.includes('未登录')) {
    ElMessage.error('请先登录')
    return
  }
  await loadStatus()
  await loadDetail()
}

const handleChat = () => {
  if (!post.value?.userId) {
    return
  }
  const currentId = authStore.user?.id
  if (currentId && Number(currentId) === Number(post.value.userId)) {
    ElMessageBox.alert('不能和自己聊天', '提示', { type: 'warning' })
    return
  }
  router.push({ path: '/chat', query: { userId: String(post.value.userId) } })
}

onMounted(async () => {
  await loadDetail()
  await loadComments()
  await loadStatus()
})
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
  font-size: 20px;
  margin-bottom: 12px;
}
.author {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
}
.author-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}
.meta {
  color: #909399;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 8px;
}
.content {
  color: #606266;
  line-height: 1.6;
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
.comment-item.author {
  background: #f6ffed;
  border-left: 3px solid #67c23a;
  padding: 10px 12px;
  border-radius: 6px;
}
.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 4px;
}
.comment-name {
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 6px;
}
.author-tag {
  margin-left: 4px;
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
