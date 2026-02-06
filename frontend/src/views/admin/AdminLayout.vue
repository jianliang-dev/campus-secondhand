<template>
  <div class="admin-layout">
    <el-container>
      <el-aside width="200px" class="aside">
        <div class="aside-title">管理员后台</div>
        <el-menu :default-active="active" @select="handleSelect">
          <el-menu-item index="/admin">概览</el-menu-item>
          <el-menu-item index="/admin/products">商品管理</el-menu-item>
          <el-menu-item index="/admin/orders">订单管理</el-menu-item>
          <el-menu-item index="/admin/users">用户管理</el-menu-item>
          <el-menu-item index="/admin/categories">商品分类</el-menu-item>
          <el-menu-item index="/admin/circle-categories">社区分类</el-menu-item>
          <el-menu-item index="/admin/carousels">轮播图管理</el-menu-item>
          <el-menu-item index="/admin/notices">公告管理</el-menu-item>
          <el-menu-item index="/admin/feedback">
            <el-badge :value="pendingCount" :hidden="pendingCount === 0">反馈处理</el-badge>
          </el-menu-item>
          <el-menu-item index="/admin/statistics">数据统计</el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header class="header">
          <div class="header-actions">
            <el-button size="small" @click="goHome">返回前台</el-button>
            <el-button size="small" type="danger" @click="handleLogout">退出登录</el-button>
          </div>
        </el-header>
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../store/auth'
import { getAdminDashboard } from '../../api/admin'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

const active = computed(() => route.path)
const pendingCount = ref(0)

const handleSelect = (path) => {
  router.push(path)
}

const goHome = () => {
  router.push('/')
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const loadPendingCount = async () => {
  try {
    const res = await getAdminDashboard()
    if (res.code === 200) {
      pendingCount.value = res.data?.pendingFeedbackCount || 0
    }
  } catch (err) {
    pendingCount.value = 0
  }
}

onMounted(loadPendingCount)
</script>

<style scoped>
.admin-layout {
  min-height: 100vh;
  background: #f5f7fa;
}
.aside {
  background: #fff;
  border-right: 1px solid #eee;
}
.aside-title {
  padding: 16px;
  font-weight: 600;
}
.header {
  background: #fff;
  border-bottom: 1px solid #eee;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>
