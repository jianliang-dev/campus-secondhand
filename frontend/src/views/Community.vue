<template>
  <div class="page">
    <el-card class="toolbar">
      <el-select v-model="circleName" placeholder="选择圈子" class="input" clearable>
        <el-option label="全部" value="" />
        <el-option v-for="item in circleCategories" :key="item.id" :label="item.name" :value="item.name" />
      </el-select>
      <el-select v-model="sortKey" placeholder="排序" class="input">
        <el-option label="最新发布" value="latest" />
        <el-option label="最新回复" value="reply" />
      </el-select>
      <el-button type="primary" @click="handleFilter">筛选</el-button>
      <el-button @click="resetFilter">重置</el-button>
      <el-button :type="viewMode === 'mine' ? 'primary' : 'default'" @click="handleMyPosts">
        我的帖子
      </el-button>
      <el-button type="success" @click="dialogVisible = true">发布帖子</el-button>
    </el-card>

    <div class="content">
      <div class="main">
        <el-card v-loading="loading" class="post-list">
          <el-card v-for="item in posts" :key="item.id" class="post-card" shadow="hover">
            <div class="post-title" @click="openDetail(item)">
              {{ item.title }}
            </div>
            <div class="post-content">{{ item.content }}</div>
            <div class="post-stats">
              <span>点赞 {{ item.likeCount || 0 }}</span>
              <span>评论 {{ item.commentCount || 0 }}</span>
              <span>浏览 {{ item.viewCount || 0 }}</span>
              <span>{{ formatDateTime(item.createTime) }}</span>
            </div>
            <div class="post-meta">
              圈子：{{ item.circleName || '默认' }} · {{ item.username || '匿名' }}
            </div>
          </el-card>
          <el-empty
            v-if="!posts.length && !loading"
            :description="viewMode === 'mine' ? '暂无我的帖子' : '暂无帖子'"
          />
        </el-card>

        <el-pagination
          v-if="viewMode === 'all' && total > 0"
          class="pager"
          background
          layout="total, prev, pager, next"
          :total="total"
          :current-page="page"
          :page-size="pageSize"
          @current-change="handlePageChange"
        />
      </div>

      <el-card v-loading="hotLoading" class="hot-card">
        <template #header>
          <div class="section-title">热门帖子</div>
        </template>
        <div v-for="item in hotPosts" :key="item.id" class="hot-item">
          <div class="hot-title" @click="openDetail(item)">{{ item.title }}</div>
          <div class="hot-meta">
            圈子：{{ item.circleName || '默认' }} · {{ item.username || '匿名' }}
          </div>
          <div class="hot-stats">
            <span>浏览 {{ item.viewCount || 0 }}</span>
            <span>评论 {{ item.commentCount || 0 }}</span>
            <span>点赞 {{ item.likeCount || 0 }}</span>
          </div>
          <div class="hot-time">{{ formatDateTime(item.createTime) }}</div>
        </div>
        <el-empty v-if="!hotPosts.length && !hotLoading" description="暂无热门帖子" />
      </el-card>
    </div>

    <el-dialog v-model="dialogVisible" title="发布帖子" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="圈子">
          <el-select v-model="form.circleName" placeholder="请选择圈子" style="width: 100%">
            <el-option v-for="item in circleCategories" :key="item.id" :label="item.name" :value="item.name" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handlePublish">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { getHotPosts, getMyPosts, getPosts, publishPost } from '../api/post'
import { getCircleCategories } from '../api/category'
import { formatDateTime } from '../utils/time'

const posts = ref([])
const circleName = ref('')
const sortKey = ref('latest')
const page = ref(1)
const pageSize = ref(8)
const total = ref(0)
const loading = ref(false)
const hotPosts = ref([])
const hotLoading = ref(false)
const circleCategories = ref([])
const dialogVisible = ref(false)
const viewMode = ref('all')
const form = reactive({
  circleName: '',
  title: '',
  content: '',
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const router = useRouter()

const load = async () => {
  loading.value = true
  try {
    const res = await getPosts({
      circleName: circleName.value,
      sort: sortKey.value,
      page: page.value,
      size: pageSize.value,
    })
    posts.value = unwrapList(res)
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const loadMyPosts = async () => {
  loading.value = true
  try {
    const res = await getMyPosts()
    if (res && typeof res === 'object' && 'code' in res && res.code !== 200) {
      ElMessage.error(res.message || '获取失败')
      posts.value = []
      total.value = 0
      return
    }
    posts.value = unwrapList(res)
    total.value = posts.value.length
  } finally {
    loading.value = false
  }
}

const loadCircleCategories = async () => {
  try {
    circleCategories.value = unwrapList(await getCircleCategories())
    if (!form.circleName && circleCategories.value.length) {
      form.circleName = circleCategories.value[0].name
    }
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const loadHotPosts = async () => {
  hotLoading.value = true
  try {
    const res = await getHotPosts({ limit: 10 })
    hotPosts.value = unwrapList(res)
  } finally {
    hotLoading.value = false
  }
}

const handleFilter = async () => {
  viewMode.value = 'all'
  if (!circleName.value.trim()) {
    await load()
    return
  }
  page.value = 1
  await load()
}

const resetFilter = async () => {
  viewMode.value = 'all'
  circleName.value = ''
  sortKey.value = 'latest'
  page.value = 1
  await load()
}

const handleMyPosts = async () => {
  viewMode.value = 'mine'
  page.value = 1
  await loadMyPosts()
}

const handlePublish = async () => {
  try {
    if (!form.circleName) {
      ElMessage.error('请选择圈子')
      return
    }
    const res = await publishPost(form)
    if (res.code !== 200) {
      ElMessage.error(res.message || '发布失败')
      return
    }
    ElMessage.success('发布成功')
    dialogVisible.value = false
    page.value = 1
    if (viewMode.value === 'mine') {
      await loadMyPosts()
    } else {
      await load()
    }
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handlePageChange = async (val) => {
  page.value = val
  await load()
}

const openDetail = (item) => {
  router.push(`/post/${item.id}`)
}

onMounted(async () => {
  await loadCircleCategories()
  await load()
  await loadHotPosts()
})
</script>

<style scoped>
.page {
  display: grid;
  gap: 12px;
}
.content {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 320px;
  gap: 12px;
  align-items: start;
}
.main {
  display: grid;
  gap: 12px;
}
.toolbar {
  display: flex;
  gap: 8px;
  align-items: center;
}
.input {
  max-width: 240px;
}
.post-card {
  border-radius: 8px;
}
.post-title {
  font-weight: 600;
  margin-bottom: 6px;
  cursor: pointer;
}
.post-content {
  color: #606266;
  margin-bottom: 8px;
}
.post-list {
  border-radius: 8px;
}
.hot-card {
  border-radius: 8px;
}
.section-title {
  font-weight: 600;
}
.hot-item {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}
.hot-item:last-child {
  border-bottom: none;
}
.hot-title {
  font-weight: 600;
  margin-bottom: 4px;
  cursor: pointer;
}
.hot-meta,
.hot-time {
  color: #909399;
  font-size: 12px;
}
.hot-stats {
  color: #606266;
  font-size: 12px;
  display: flex;
  gap: 8px;
  margin: 4px 0;
}
.post-stats {
  color: #909399;
  font-size: 12px;
  display: flex;
  gap: 12px;
  margin-bottom: 6px;
}
.post-meta {
  color: #909399;
  font-size: 12px;
}
.pager {
  display: flex;
  justify-content: flex-end;
}
@media (max-width: 960px) {
  .content {
    grid-template-columns: 1fr;
  }
}
</style>
