<template>
  <el-container class="layout">
    <el-header class="header">
      <div class="logo">校园二手平台</div>
      <div class="nav">
        <el-link :underline="false" @click="go('/home')">首页</el-link>
        <el-link :underline="false" @click="go('/products')">商品浏览</el-link>
        <el-link :underline="false" @click="go('/community')">社区</el-link>
        <el-link :underline="false" @click="go('/wanted')">求购</el-link>
        <el-link :underline="false" @click="go('/my/wanted')">我的求购</el-link>
        <el-link :underline="false" @click="go('/publish')">发布商品</el-link>
        <el-link :underline="false" @click="go('/my/products')">我的商品</el-link>
        <el-link :underline="false" @click="go('/my/favorites')">我的收藏</el-link>
        <el-link :underline="false" @click="go('/orders')">我的订单</el-link>
        <el-link :underline="false" @click="go('/chat')">聊天</el-link>
      </div>
      <div class="actions">
        <template v-if="isLoggedIn">
          <el-avatar :size="28" :src="userAvatar" class="avatar" />
          <span class="welcome">你好，{{ userName }}</span>
          <el-dropdown split-button size="small" @click="openProfile">
            个人中心
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="go('/address')">收货地址</el-dropdown-item>
                <el-dropdown-item @click="go('/feedback')">我的反馈</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button size="small" @click="handleLogout">退出</el-button>
        </template>
        <template v-else>
          <el-button size="small" @click="go('/login')">登录</el-button>
          <el-button size="small" type="primary" @click="go('/register')">注册</el-button>
        </template>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </el-container>

  <el-dialog v-model="profileVisible" title="个人信息编辑" width="520px">
    <el-form :model="profileForm" label-width="90px">
      <el-form-item label="用户名">
        <el-input v-model="profileForm.username" disabled />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="profileForm.phone" />
      </el-form-item>
      <el-form-item label="头像">
        <div class="avatar-upload">
          <el-upload
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
            :on-success="handleAvatarSuccess"
          >
            <el-button size="small">选择头像</el-button>
          </el-upload>
          <el-avatar :size="48" :src="profileForm.avatar || userAvatar" />
        </div>
        <el-input v-model="profileForm.avatar" placeholder="头像URL" />
      </el-form-item>
      <el-form-item label="签名">
        <el-input v-model="profileForm.signature" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="profileVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSaveProfile">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'
import { updateProfile } from '../api/user'

const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => !!authStore.token)
const isAdmin = computed(() => authStore.user?.role === 1)
const userName = computed(() => authStore.user?.username || '用户')
const userAvatar = computed(
  () => authStore.user?.avatar || 'https://via.placeholder.com/40x40?text=U',
)

const profileVisible = ref(false)
const uploadUrl = '/api/user/uploadAvatar'
const uploadHeaders = computed(() => {
  if (authStore.token) {
    return { Authorization: authStore.token }
  }
  return {}
})
const profileForm = reactive({
  username: '',
  phone: '',
  avatar: '',
  signature: '',
})

const go = (path) => {
  router.push(path)
}

const handleLogout = () => {
  authStore.logout()
  router.push('/login')
}

const openProfile = () => {
  const user = authStore.user || {}
  Object.assign(profileForm, {
    username: user.username || '',
    phone: user.phone || '',
    avatar: user.avatar || '',
    signature: user.signature || '',
  })
  profileVisible.value = true
}

const handleSaveProfile = async () => {
  try {
    const res = await updateProfile({
      phone: profileForm.phone,
      avatar: profileForm.avatar,
      signature: profileForm.signature,
    })
    if (res.code !== 200) {
      ElMessage.error(res.message || '更新失败')
      return
    }
    if (res.data) {
      authStore.setUser(res.data)
    }
    ElMessage.success('更新成功')
    profileVisible.value = false
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

const handleAvatarSuccess = (response) => {
  const url = response?.data || response?.url || ''
  if (!url) {
    ElMessage.error('上传失败')
    return
  }
  profileForm.avatar = url
  ElMessage.success('头像上传成功')
}
</script>

<style scoped>
.layout {
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.logo {
  font-weight: 600;
}
.nav {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
}
.actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.avatar {
  border: 1px solid #eee;
}
.avatar-upload {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}
.welcome {
  color: #606266;
}
.main {
  background: #f7f8fa;
}
</style>
