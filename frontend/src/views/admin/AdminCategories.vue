<template>
  <el-card class="card">
    <template #header>
      <div class="title">
        商品分类管理
        <el-button size="small" type="primary" @click="openAdd">新增分类</el-button>
      </div>
    </template>
    <el-table :data="categories">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="name" label="分类名称" min-width="200" />
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
    <el-empty v-if="!categories.length" description="暂无分类" />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑分类' : '新增分类'" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
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
  addAdminCategory,
  deleteAdminCategory,
  getAdminCategories,
  updateAdminCategory,
} from '../../api/admin'

const categories = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  name: '',
  status: 1,
  sortOrder: 0,
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  try {
    const res = await getAdminCategories()
    categories.value = unwrapList(res)
  } catch (err) {
    ElMessage.error(err.message || '加载失败')
  }
}

const openAdd = () => {
  Object.assign(form, { id: null, name: '', status: 1, sortOrder: 0 })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  const res = form.id ? await updateAdminCategory(form) : await addAdminCategory(form)
  if (res.code !== 200) {
    ElMessage.error(res.message || '保存失败')
    return
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

const handleDelete = async (id) => {
  const res = await deleteAdminCategory(id)
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
