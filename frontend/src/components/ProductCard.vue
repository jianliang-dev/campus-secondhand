<template>
  <el-card class="card" shadow="hover" @click="goDetail">
    <img class="cover" :src="product.coverImg || placeholder" alt="cover" />
    <div class="title">{{ product.title }}</div>
    <div class="price">￥{{ product.price }}</div>
    <div v-if="showTime" class="time">发布时间：{{ formatDateTime(product.createTime) }}</div>
  </el-card>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { formatDateTime } from '../utils/time'

const props = defineProps({
  product: {
    type: Object,
    required: true,
  },
  showTime: {
    type: Boolean,
    default: false,
  },
})

const router = useRouter()
const placeholder =
  'https://via.placeholder.com/240x180?text=Product'

const goDetail = () => {
  router.push(`/product/${props.product.id}`)
}
</script>

<style scoped>
.card {
  cursor: pointer;
}
.cover {
  width: 100%;
  height: 140px;
  object-fit: cover;
  border-radius: 6px;
}
.title {
  margin-top: 8px;
  font-weight: 600;
  color: #303133;
}
.price {
  margin-top: 4px;
  color: #f56c6c;
}
.time {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
}
</style>
