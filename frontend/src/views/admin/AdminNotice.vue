<template>
  <el-card class="card">
    <template #header>
      <div class="title">
        公告管理
        <el-button type="primary" size="small" @click="openAdd">新增公告</el-button>
      </div>
    </template>

    <el-table :data="notices">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="content" label="内容" />
      <el-table-column label="操作" width="180">
        <template #default="{ row }">
          <el-button size="small" @click="openEdit(row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑公告' : '新增公告'" width="520px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="form.content" type="textarea" />
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
  addAdminNotice,
  deleteAdminNotice,
  getAdminNotices,
  updateAdminNotice,
} from '../../api/admin'

const notices = ref([])
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  title: '',
  content: '',
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const load = async () => {
  notices.value = unwrapList(await getAdminNotices())
}

const openAdd = () => {
  Object.assign(form, { id: null, title: '', content: '' })
  dialogVisible.value = true
}

const openEdit = (row) => {
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSave = async () => {
  const res = form.id ? await updateAdminNotice(form) : await addAdminNotice(form)
  if (res.code !== 200) {
    ElMessage.error(res.message || '保存失败')
    return
  }
  ElMessage.success('保存成功')
  dialogVisible.value = false
  await load()
}

const handleDelete = async (id) => {
  const res = await deleteAdminNotice(id)
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
