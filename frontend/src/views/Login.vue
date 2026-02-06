<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">用户登录</div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin">登录</el-button>
          <el-button @click="goRegister">注册</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../store/auth'
import { login } from '../api/user'

const router = useRouter()
const authStore = useAuthStore()

const form = reactive({
  username: '',
  password: '',
})

const handleLogin = async () => {
  try {
    const res = await login(form.username, form.password)
    if (res.code !== 200) {
      ElMessage.error(res.message || '登录失败')
      return
    }
    authStore.setToken(res.token)
    authStore.setUser(res.user)
    ElMessage.success('登录成功')
    if (res.user?.role === 1) {
      router.push('/admin')
    } else {
      router.push('/home')
    }
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const goRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.page {
  display: flex;
  justify-content: center;
  padding-top: 40px;
}
.card {
  width: 420px;
}
.title {
  font-weight: 600;
}
</style>
