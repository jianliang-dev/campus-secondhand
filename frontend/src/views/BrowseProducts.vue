<template>
  <div class="page">
    <el-card class="toolbar">
      <el-select v-model="activeCategoryId" placeholder="选择分类" class="input">
        <el-option label="全部" value="all" />
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-input v-model="keyword" placeholder="搜索商品" class="input" clearable />
      <el-select v-model="sortKey" placeholder="排序" class="input">
        <el-option label="最新发布" value="latest" />
        <el-option label="价格从低到高" value="priceAsc" />
        <el-option label="价格从高到低" value="priceDesc" />
      </el-select>
      <el-button @click="refresh">刷新</el-button>
    </el-card>

    <div v-if="pagedProducts.length" class="grid">
      <ProductCard v-for="item in pagedProducts" :key="item.id" :product="item" :show-time="true" />
    </div>
    <el-empty v-else description="暂无商品" />

    <el-pagination
      v-if="total > 0"
      class="pager"
      background
      layout="total, sizes, prev, pager, next"
      :total="total"
      :current-page="currentPage"
      :page-size="pageSize"
      :page-sizes="[8, 12, 16, 24]"
      @current-change="handlePageChange"
      @size-change="handleSizeChange"
    />
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import ProductCard from '../components/ProductCard.vue'
import { getProducts, searchProducts } from '../api/product'
import { getCategories } from '../api/category'

const categories = ref([])
const products = ref([])
const activeCategoryId = ref('all')
const keyword = ref('')
const sortKey = ref('latest')
const currentPage = ref(1)
const pageSize = ref(12)

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const loadCategories = async () => {
  try {
    categories.value = unwrapList(await getCategories())
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const loadProducts = async () => {
  try {
    const key = keyword.value.trim()
    const res = key ? await searchProducts(key) : await getProducts()
    products.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const filteredProducts = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  if (activeCategoryId.value === 'all') {
    return key
      ? products.value.filter((item) =>
          String(item.title || '').toLowerCase().includes(key),
        )
      : products.value
  }
  return products.value.filter((item) => {
    const matchCategory = String(item.categoryId) === String(activeCategoryId.value)
    if (!key) {
      return matchCategory
    }
    return matchCategory && String(item.title || '').toLowerCase().includes(key)
  })
})

const getTimeValue = (item) => {
  if (!item) {
    return 0
  }
  const time = Date.parse(item.createTime)
  if (!Number.isNaN(time)) {
    return time
  }
  return item.id ? Number(item.id) : 0
}

const getPriceValue = (item) => {
  if (!item || item.price == null) {
    return 0
  }
  const price = Number(item.price)
  return Number.isNaN(price) ? 0 : price
}

const sortedProducts = computed(() => {
  const list = [...filteredProducts.value]
  switch (sortKey.value) {
    case 'priceAsc':
      return list.sort((a, b) => getPriceValue(a) - getPriceValue(b))
    case 'priceDesc':
      return list.sort((a, b) => getPriceValue(b) - getPriceValue(a))
    case 'latest':
    default:
      return list.sort((a, b) => getTimeValue(b) - getTimeValue(a))
  }
})

const total = computed(() => sortedProducts.value.length)

const pagedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  return sortedProducts.value.slice(start, start + pageSize.value)
})

const refresh = async () => {
  await loadProducts()
}

const handlePageChange = (page) => {
  currentPage.value = page
}

const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
}

watch([activeCategoryId, sortKey], () => {
  currentPage.value = 1
})

let keywordTimer = null
watch(keyword, () => {
  currentPage.value = 1
  if (keywordTimer) {
    clearTimeout(keywordTimer)
  }
  keywordTimer = setTimeout(() => {
    loadProducts()
  }, 300)
})

const route = useRoute()

onMounted(async () => {
  await loadCategories()
  await loadProducts()
  if (route.query.keyword) {
    keyword.value = String(route.query.keyword)
  }
})
</script>

<style scoped>
.page {
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
.grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 12px;
}
.pager {
  display: flex;
  justify-content: flex-end;
}
</style>
