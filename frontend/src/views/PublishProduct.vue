<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">发布商品</div>
      </template>
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="form.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImg" placeholder="图片URL" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handlePublish">发布</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { publishProduct } from '../api/product'
import { getCategories } from '../api/category'

const form = reactive({
  title: '',
  description: '',
  price: 0,
  categoryId: null,
  coverImg: '',
})

const categories = ref([])

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const loadCategories = async () => {
  try {
    categories.value = unwrapList(await getCategories())
    if (!form.categoryId && categories.value.length) {
      form.categoryId = categories.value[0].id
    }
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handlePublish = async () => {
  try {
    if (!form.categoryId) {
      ElMessage.error('请选择分类')
      return
    }
    const res = await publishProduct(form)
    if (typeof res === 'string' && res.includes('未登录')) {
      ElMessage.error('请先登录')
      return
    }
    ElMessage.success('发布成功')
  } catch (err) {
    ElMessage.error(err.message)
  }
}

onMounted(loadCategories)
</script>

<style scoped>
.page {
  display: flex;
  justify-content: center;
  padding-top: 20px;
}
.card {
  width: 520px;
}
.title {
  font-weight: 600;
}
</style>
