<template>
  <div class="page">
    <el-card class="toolbar">
      <el-button @click="load">刷新</el-button>
    </el-card>

    <el-card v-loading="loading" class="wanted-list">
      <el-card v-for="item in wantedList" :key="item.id" class="wanted-card">
        <div class="wanted-head">
          <div class="wanted-title">{{ item.title }}</div>
          <el-tag :type="item.status === 1 ? 'success' : 'info'" size="small">
            {{ item.status === 1 ? '进行中' : '已完成' }}
          </el-tag>
        </div>
        <div class="wanted-desc">{{ item.description }}</div>
        <div class="wanted-meta">
          类别：{{ categoryMap[item.categoryId] || '未分类' }} · 预算：￥{{ item.budget }} ·
          {{ formatDateTime(item.createTime) }}
        </div>
        <div class="wanted-extra">
          <div class="expected">期望：{{ item.expected || '未填写' }}</div>
          <div class="actions">
            <el-button size="small" @click="openEdit(item)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(item)">删除</el-button>
            <el-button
              size="small"
              type="success"
              :disabled="item.status !== 1"
              @click="markCompleted(item)"
            >
              标记完成
            </el-button>
          </div>
        </div>
      </el-card>
      <el-empty v-if="!wantedList.length && !loading" description="暂无求购信息" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="编辑求购" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.categoryId" placeholder="请选择类别" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="期望">
          <el-input v-model="form.expected" placeholder="成色/型号等" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="form.budget" :min="0" :precision="2" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategories } from '../api/category'
import { deleteWanted, getMyWanted, updateWanted, updateWantedStatus } from '../api/wanted'
import { formatDateTime } from '../utils/time'

const wantedList = ref([])
const categories = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const form = reactive({
  id: null,
  title: '',
  categoryId: '',
  expected: '',
  description: '',
  budget: 0,
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

const load = async () => {
  loading.value = true
  try {
    wantedList.value = unwrapList(await getMyWanted())
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    categories.value = unwrapList(await getCategories())
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const openEdit = (item) => {
  Object.assign(form, {
    id: item.id,
    title: item.title || '',
    categoryId: item.categoryId || '',
    expected: item.expected || '',
    description: item.description || '',
    budget: item.budget || 0,
  })
  dialogVisible.value = true
}

const handleUpdate = async () => {
  try {
    if (!form.title.trim()) {
      ElMessage.error('请输入标题')
      return
    }
    if (!form.categoryId) {
      ElMessage.error('请选择类别')
      return
    }
    const res = await updateWanted(form)
    if (res.code !== 200) {
      ElMessage.error(res.message || '更新失败')
      return
    }
    ElMessage.success('更新成功')
    dialogVisible.value = false
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handleDelete = async (item) => {
  try {
    await ElMessageBox.confirm('确定删除该求购信息吗？', '提示', { type: 'warning' })
    const res = await deleteWanted(item.id)
    if (res.code !== 200) {
      ElMessage.error(res.message || '删除失败')
      return
    }
    ElMessage.success('删除成功')
    await load()
  } catch (err) {
    if (err !== 'cancel') {
      ElMessage.error(err.message || '操作取消')
    }
  }
}

const markCompleted = async (item) => {
  if (item.status !== 1) {
    return
  }
  const res = await updateWantedStatus(item.id, 0)
  if (res.code !== 200) {
    ElMessage.error(res.message || '更新失败')
    return
  }
  ElMessage.success('已标记为完成')
  await load()
}

onMounted(async () => {
  await loadCategories()
  await load()
})
</script>

<style scoped>
.page {
  display: grid;
  gap: 12px;
}
.toolbar {
  display: flex;
  justify-content: flex-end;
}
.wanted-list {
  border-radius: 8px;
}
.wanted-card {
  border-radius: 8px;
}
.wanted-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-bottom: 6px;
}
.wanted-title {
  font-weight: 600;
}
.wanted-desc {
  color: #606266;
  margin-bottom: 8px;
}
.wanted-extra {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin-top: 6px;
}
.expected {
  color: #909399;
  font-size: 12px;
}
.wanted-meta {
  color: #909399;
  font-size: 12px;
}
.actions {
  display: flex;
  gap: 8px;
}
</style>
