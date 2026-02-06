<template>
  <div class="page">
    <el-card class="section" v-if="carousels.length">
      <el-carousel height="240px">
        <el-carousel-item v-for="item in carousels" :key="item.id">
          <img
            class="carousel-img clickable"
            :src="item.imageUrl"
            alt="banner"
            @click="handleCarouselClick(item)"
          />
        </el-carousel-item>
      </el-carousel>
    </el-card>

    <el-card class="search-card">
      <el-input v-model="keyword" placeholder="搜索商品" @keyup.enter="handleSearch">
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="section-title">系统公告</div>
      </template>
      <el-empty v-if="!notices.length" description="暂无公告" />
      <el-timeline v-else>
        <el-timeline-item
          v-for="item in notices"
          :key="item.id"
          :timestamp="formatDateTime(item.createTime)"
        >
          <div class="notice-title">{{ item.title }}</div>
          <div class="notice-content">{{ item.content }}</div>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="section-title">猜你喜欢</div>
      </template>
      <div v-if="recommendations.length" class="grid">
        <ProductCard v-for="item in recommendations" :key="item.id" :product="item" />
      </div>
      <el-empty v-else description="暂无推荐" />
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="section-title">最新商品</div>
      </template>
      <div v-if="products.length" class="grid">
        <ProductCard v-for="item in products" :key="item.id" :product="item" />
      </div>
      <el-empty v-else description="暂无商品" />
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="section-title">社区帖子</div>
      </template>
      <el-empty v-if="!posts.length" description="暂无帖子" />
      <el-row v-else :gutter="12">
        <el-col v-for="item in posts" :key="item.id" :span="12">
          <el-card class="mini-card" shadow="hover">
            <div class="mini-title">{{ item.title }}</div>
            <div class="mini-content">{{ item.content }}</div>
            <div class="mini-meta">
              {{ item.username || '匿名' }} · {{ formatDateTime(item.createTime) }}
            </div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="section">
      <template #header>
        <div class="section-title">求购信息</div>
      </template>
      <el-empty v-if="!wantedList.length" description="暂无求购" />
      <el-row v-else :gutter="12">
        <el-col v-for="item in wantedList" :key="item.id" :span="12">
          <el-card class="mini-card" shadow="hover">
            <div class="mini-title">{{ item.title }}</div>
            <div class="mini-content">{{ item.description }}</div>
            <div class="mini-meta">预算：￥{{ item.budget }} · {{ formatDateTime(item.createTime) }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import { getProducts, getRecommend } from '../api/product'
import { getCarouselList } from '../api/carousel'
import { getNotices } from '../api/notice'
import { getPosts } from '../api/post'
import { getWantedList } from '../api/wanted'
import { formatDateTime } from '../utils/time'

const keyword = ref('')
const products = ref([])
const router = useRouter()
const recommendations = ref([])
const carousels = ref([])
const notices = ref([])
const posts = ref([])
const wantedList = ref([])

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const loadProducts = async () => {
  products.value = unwrapList(await getProducts())
}

const loadRecommend = async () => {
  recommendations.value = unwrapList(await getRecommend(10))
}

const loadCarousel = async () => {
  carousels.value = unwrapList(await getCarouselList())
}

const loadNotices = async () => {
  notices.value = unwrapList(await getNotices(5))
}

const loadPosts = async () => {
  const res = await getPosts()
  posts.value = unwrapList(res).slice(0, 4)
}

const loadWanted = async () => {
  const res = await getWantedList()
  wantedList.value = unwrapList(res).slice(0, 4)
}

const handleSearch = async () => {
  if (!keyword.value.trim()) {
    await loadProducts()
    return
  }
  router.push({ path: '/products', query: { keyword: keyword.value.trim() } })
}

const handleCarouselClick = (item) => {
  const link = item?.linkUrl
  if (!link) {
    return
  }
  if (link.startsWith('http://') || link.startsWith('https://')) {
    window.open(link, '_blank')
    return
  }
  router.push(link)
}

onMounted(async () => {
  await loadCarousel()
  await loadNotices()
  await loadRecommend()
  await loadProducts()
  await loadPosts()
  await loadWanted()
})
</script>

<style scoped>
.page {
  display: grid;
  gap: 16px;
}
.search-card {
  border-radius: 8px;
}
.section {
  border-radius: 8px;
}
.section-title {
  font-weight: 600;
}
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.carousel-img {
  width: 100%;
  height: 240px;
  object-fit: cover;
}
.clickable {
  cursor: pointer;
}
.mini-card {
  margin-bottom: 12px;
}
.mini-title {
  font-weight: 600;
  margin-bottom: 6px;
}
.mini-content {
  color: #606266;
  font-size: 13px;
  margin-bottom: 6px;
}
.mini-meta {
  color: #909399;
  font-size: 12px;
}
.notice-title {
  font-weight: 600;
  margin-bottom: 4px;
}
.notice-content {
  color: #606266;
}
</style>
