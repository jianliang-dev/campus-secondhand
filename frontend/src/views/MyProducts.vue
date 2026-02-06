<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">我的商品</div>
      </template>
      <el-table :data="products" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" />
        <el-table-column prop="price" label="价格" width="120" />
        <el-table-column label="分类" width="160">
          <template #default="{ row }">
            {{ categoryMap[row.categoryId] || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="更新时间" width="180">
          <template #default="{ row }">
            {{ formatDateTime(row.updateTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button size="small" @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            <el-button size="small" type="warning" @click="toggleStatus(row)">
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑商品" width="520px">
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="editForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="editForm.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="editForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="item in categories"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="封面图">
          <div class="cover-editor">
            <el-upload
              :show-file-list="false"
              :http-request="handleCoverUpload"
              :before-upload="beforeCoverUpload"
              accept="image/*"
            >
              <el-button size="small" :loading="uploading">选择文件</el-button>
            </el-upload>
            <el-input v-model="editForm.coverImg" placeholder="图片URL" />
          </div>
          <img v-if="editForm.coverImg" class="cover-preview" :src="editForm.coverImg" alt="cover" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleUpdate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  deleteProduct,
  getMyProducts,
  updateProduct,
  updateProductStatus,
  uploadProductCover,
} from '../api/product'
import { getCategories } from '../api/category'
import { formatDateTime } from '../utils/time'

const products = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const uploading = ref(false)
const editForm = reactive({
  id: null,
  title: '',
  description: '',
  price: 0,
  categoryId: null,
  coverImg: '',
})

const categoryMap = computed(() => {
  const map = {}
  categories.value.forEach((item) => {
    map[item.id] = item.name
  })
  return map
})

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

const load = async () => {
  try {
    products.value = await getMyProducts()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const openEdit = (row) => {
  Object.assign(editForm, row)
  dialogVisible.value = true
}

const handleUpdate = async () => {
  try {
    const res = await updateProduct(editForm)
    if (res.code !== 200) {
      ElMessage.error(res.message || '更新失败')
      return
    }
    dialogVisible.value = false
    ElMessage.success('更新成功')
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const beforeCoverUpload = (file) => {
  if (!file.type.startsWith('image/')) {
    ElMessage.error('仅支持图片文件')
    return false
  }
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    ElMessage.error('图片大小不能超过 5MB')
    return false
  }
  return true
}

const handleCoverUpload = async ({ file, onSuccess, onError }) => {
  uploading.value = true
  try {
    const res = await uploadProductCover(file)
    if (res.code !== 200) {
      ElMessage.error(res.message || '上传失败')
      onError?.(new Error(res.message || '上传失败'))
      return
    }
    editForm.coverImg = res.data
    ElMessage.success('上传成功')
    onSuccess?.(res)
  } catch (err) {
    ElMessage.error(err.message)
    onError?.(err)
  } finally {
    uploading.value = false
  }
}

const handleDelete = async (id) => {
  try {
    const res = await deleteProduct(id)
    if (res.code !== 200) {
      ElMessage.error(res.message || '删除失败')
      return
    }
    ElMessage.success('删除成功')
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const toggleStatus = async (row) => {
  const nextStatus = row.status === 1 ? 0 : 1
  try {
    const res = await updateProductStatus(row.id, nextStatus)
    if (res.code !== 200) {
      ElMessage.error(res.message || '更新失败')
      return
    }
    ElMessage.success('状态更新成功')
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

onMounted(async () => {
  await loadCategories()
  await load()
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
}
.cover-editor {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 8px;
  width: 100%;
  align-items: center;
}
.cover-preview {
  margin-top: 8px;
  width: 160px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
  border: 1px solid #eee;
}
</style>
