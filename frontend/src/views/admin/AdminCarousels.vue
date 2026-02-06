<template>
  <el-card class="card">
    <template #header>
      <div class="title">
        轮播图管理
        <el-button size="small" type="primary" @click="openAdd">新增轮播图</el-button>
      </div>
    </template>
    <el-table :data="carousels">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column label="图片" width="140">
        <template #default="{ row }">
          <el-image
            v-if="row.imageUrl"
            :src="row.imageUrl"
            fit="cover"
            style="width: 100px; height: 56px"
          />
        </template>
      </el-table-column>
      <el-table-column prop="imageUrl" label="图片地址" min-width="220" />
      <el-table-column prop="linkUrl" label="跳转链接" min-width="200" />
      <el-table-column prop="sortOrder" label="排序" width="100" />
      <el-table-column label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-empty v-if="!carousels.length" description="暂无轮播图" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑轮播图' : '新增轮播图'" width="560px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="图片地址">
          <el-input v-model="form.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>
        <el-form-item label="跳转链接">
          <el-input v-model="form.linkUrl" placeholder="请输入跳转链接" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width: 140px">
            <el-option :value="1" label="启用" />
            <el-option :value="0" label="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import {
  addAdminCarousel,
  deleteAdminCarousel,
  getAdminCarousels,
  updateAdminCarousel,
} from '../../api/admin'

const carousels = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  imageUrl: '',
  linkUrl: '',
  sortOrder: 0,
  status: 1,
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  try {
    const res = await getAdminCarousels()
    carousels.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message || '加载失败')
  }
}

const openAdd = () => {
  Object.assign(form, { id: null, imageUrl: '', linkUrl: '', sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  const res = form.id ? await updateAdminCarousel(form) : await addAdminCarousel(form)
  if (res.code !== 200) {
    ElMessage.error(res.message || '保存失败')
    return
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

const handleDelete = async (id) => {
  const res = await deleteAdminCarousel(id)
  if (res.code !== 200) {
    ElMessage.error(res.message || '删除失败')
    return
  }
  ElMessage.success('删除成功')
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
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
