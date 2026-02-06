<template>
  <div class="page">
    <el-card class="card">
      <template #header>
        <div class="title">用户注册</div>
      </template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRegister">注册</el-button>
          <el-button @click="goLogin">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/user'

const router = useRouter()
const form = reactive({
  username: '',
  password: '',
  phone: '',
})

const handleRegister = async () => {
  try {
    const res = await register(form.username, form.password, form.phone)
    if (res.code !== 200) {
      ElMessage.error(res.message || '注册失败')
      return
    }
    ElMessage.success('注册成功')
    router.push('/login')
  } catch (err) {
    ElMessage.error(err.message)
  }
}

const goLogin = () => {
  router.push('/login')
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
