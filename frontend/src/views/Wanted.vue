<template>
  <div class="page">
    <el-card class="toolbar">
      <el-select v-model="filters.categoryId" placeholder="选择类别" class="input" clearable>
        <el-option label="全部" value="" />
        <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
      </el-select>
      <el-select v-model="filters.status" placeholder="选择状态" class="input" clearable>
        <el-option label="全部" value="" />
        <el-option label="进行中" :value="1" />
        <el-option label="已完成" :value="0" />
      </el-select>
      <el-select v-model="filters.sort" placeholder="排序" class="input">
        <el-option label="最新发布" value="latest" />
        <el-option label="预算从低到高" value="budgetAsc" />
        <el-option label="预算从高到低" value="budgetDesc" />
      </el-select>
      <el-button type="primary" @click="load">筛选</el-button>
      <el-button @click="resetFilter">重置</el-button>
      <el-button type="success" @click="dialogVisible = true">发布求购</el-button>
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
          类别：{{ item.categoryName || '未分类' }} · 预算：￥{{ item.budget }} ·
          {{ item.username || '匿名' }} · {{ formatDateTime(item.createTime) }}
        </div>
        <div class="wanted-extra">
          <div class="expected">期望：{{ item.expected || '未填写' }}</div>
          <el-button v-if="item?.userId" size="small" type="success" @click="handleChat(item)">
            联系TA
          </el-button>
        </div>
      </el-card>
      <el-empty v-if="!wantedList.length && !loading" description="暂无求购信息" />
    </el-card>

    <el-dialog v-model="dialogVisible" title="发布求购" width="520px">
      <el-form :model="form" label-width="90px">
        <el-form-item label="标题">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类别">
          <el-select v-model="form.categoryId" placeholder="请选择类别" style="width: 100%">
            <el-option v-for="item in categories" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" />
        </el-form-item>
        <el-form-item label="期望">
          <el-input v-model="form.expected" placeholder="成色/型号等" />
        </el-form-item>
        <el-form-item label="预算">
          <el-input-number v-model="form.budget" :min="0" :precision="2" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { getWantedList, publishWanted } from '../api/wanted'
import { getCategories } from '../api/category'
import { formatDateTime } from '../utils/time'
import { useAuthStore } from '../store/auth'

const wantedList = ref([])
const categories = ref([])
const dialogVisible = ref(false)
const loading = ref(false)
const form = reactive({
  title: '',
  categoryId: '',
  expected: '',
  description: '',
  budget: 0,
})
const filters = reactive({
  categoryId: '',
  status: '',
  sort: 'latest',
})

const unwrapList = (res) => {
  if (res && typeof res === 'object' && 'code' in res) {
    return res.data || []
  }
  return res || []
}

const router = useRouter()
const authStore = useAuthStore()

const load = async () => {
  loading.value = true
  try {
    const res = await getWantedList({
      categoryId: filters.categoryId || undefined,
      status: filters.status === '' ? undefined : filters.status,
      sort: filters.sort,
    })
    wantedList.value = unwrapList(res)
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

const resetFilter = async () => {
  filters.categoryId = ''
  filters.status = ''
  filters.sort = 'latest'
  await load()
}

const handlePublish = async () => {
  try {
    if (!form.title.trim()) {
      ElMessage.error('请输入标题')
      return
    }
    if (!form.categoryId) {
      ElMessage.error('请选择类别')
      return
    }
    const res = await publishWanted(form)
    if (res.code !== 200) {
      ElMessage.error(res.message || '发布失败')
      return
    }
    ElMessage.success('发布成功')
    dialogVisible.value = false
    await load()
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const handleChat = (item) => {
  if (!item?.userId) {
    return
  }
  const currentId = authStore.user?.id
  if (currentId && Number(currentId) === Number(item.userId)) {
    ElMessageBox.alert('不能和自己聊天', '提示', { type: 'warning' })
    return
  }
  router.push({ path: '/chat', query: { userId: String(item.userId) } })
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
  flex-wrap: wrap;
  gap: 8px;
  align-items: center;
}
.input {
  max-width: 240px;
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
</style>
