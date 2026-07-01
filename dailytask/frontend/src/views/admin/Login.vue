<template>
  <div class="login-container">
    <div class="login-left">
      <h1>花之恋鲜花电商</h1>
      <p>后台管理系统<br>高效管理商品与订单<br>数据驱动运营决策</p>
    </div>
    <div class="login-right">
      <el-card class="login-card" shadow="never">
        <h2>管理员登录</h2>
        <p class="subtitle">请输入管理员账号和密码</p>
        <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" @submit.prevent="handleLogin">
          <el-form-item prop="username">
            <el-input v-model="loginForm.username" placeholder="用户名" prefix-icon="User" size="large" />
          </el-form-item>
          <el-form-item prop="password">
            <el-input v-model="loginForm.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password @keyup.enter="handleLogin" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="large" :loading="loading" style="width:100%;background:#C71526;border-color:#C71526" @click="handleLogin">
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { login } from '../../api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminLogin',
  data() {
    return {
      loginForm: { username: '', password: '' },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      loading: false
    }
  },
  methods: {
    async handleLogin() {
      try { await this.$refs.loginFormRef.validate() } catch { return }
      this.loading = true
      try {
        const result = await login(this.loginForm)
        if (result.code === 200 && result.data.user.role === 1) {
          localStorage.setItem('token', result.data.token)
          localStorage.setItem('username', result.data.user.username)
          localStorage.setItem('role', String(result.data.user.role))
          this.$router.push('/admin/dashboard')
        } else if (result.code === 200) {
          ElMessage.error('您不是管理员，无法登录后台！')
        }
      } catch (e) { /* 拦截器处理 */ }
      finally { this.loading = false }
    }
  },
  created() {
    const token = localStorage.getItem('token'), role = localStorage.getItem('role')
    if (token && role === '1') this.$router.push('/admin/dashboard')
  }
}
</script>

<style scoped>
.login-container { display: flex; height: 100vh; }
.login-left { flex: 1; background: linear-gradient(135deg, #C71526, #8B0A1A); display: flex; flex-direction: column; justify-content: center; align-items: center; color: #fff; padding: 40px; }
.login-left h1 { font-size: 36px; font-weight: 700; margin: 20px 0; }
.login-left p { font-size: 18px; opacity: 0.9; text-align: center; line-height: 1.8; }
.login-right { flex: 1; display: flex; justify-content: center; align-items: center; padding: 40px; background: #FFF8F8; }
.login-card { width: 100%; max-width: 420px; border-radius: 8px; }
.login-card h2 { font-size: 28px; color: #333; margin-bottom: 8px; }
.subtitle { color: #999; margin-bottom: 30px; font-size: 14px; }
</style>
