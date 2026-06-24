<!-- 商城登录/注册页 - 鲜花电商"花之恋"主题 -->
<template>
  <div class="login-page">
    <div class="login-bg-decoration">
      <span class="deco-flower deco-1">🌸</span>
      <span class="deco-flower deco-2">🌹</span>
      <span class="deco-flower deco-3">🌷</span>
      <span class="deco-flower deco-4">💐</span>
    </div>
    <div class="login-container">
      <!-- Logo区域 -->
      <div class="login-logo">
        <span class="logo-icon">🌸</span>
        <h2 class="login-title">花之恋</h2>
        <p class="login-subtitle">每一束花，都是爱的告白</p>
      </div>

      <!-- 登录/注册卡片 -->
      <el-card class="login-card" shadow="always">
        <el-tabs v-model="activeTab" class="login-tabs">
          <!-- 登录Tab -->
          <el-tab-pane label="登录" name="login">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              label-position="top"
              class="login-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="danger"
                  size="large"
                  class="submit-btn"
                  :loading="loginLoading"
                  @click="onLogin"
                >
                  登 录
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 注册Tab -->
          <el-tab-pane label="注册" name="register">
            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              label-position="top"
              class="login-form"
            >
              <el-form-item label="用户名" prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名"
                  prefix-icon="User"
                  size="large"
                />
              </el-form-item>
              <el-form-item label="密码" prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码（至少6位）"
                  prefix-icon="Lock"
                  size="large"
                  show-password
                />
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="请输入手机号"
                  prefix-icon="Phone"
                  size="large"
                />
              </el-form-item>
              <el-form-item>
                <el-button
                  type="danger"
                  size="large"
                  class="submit-btn"
                  :loading="registerLoading"
                  @click="onRegister"
                >
                  注 册
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-card>
    </div>
  </div>
</template>

<script>
import { login, register } from '../../api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'ShopLogin',
  data() {
    return {
      activeTab: 'login',
      loginLoading: false,
      registerLoading: false,
      /* 登录表单 */
      loginForm: {
        username: '',
        password: ''
      },
      /* 注册表单 */
      registerForm: {
        username: '',
        password: '',
        phone: ''
      },
      /* 登录表单验证规则 */
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      /* 注册表单验证规则 */
      registerRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码不少于6位', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    /** 登录 */
    async onLogin() {
      try {
        await this.$refs.loginFormRef.validate()
      } catch {
        return
      }
      this.loginLoading = true
      try {
        const res = await login(this.loginForm)
        if (res.data && res.data.token) {
          const user = res.data.user || {}
          localStorage.setItem('token', res.data.token)
          localStorage.setItem('username', user.username || this.loginForm.username)
          localStorage.setItem('role', String(user.role != null ? user.role : 0))
          ElMessage.success('登录成功，欢迎回来！')
          const redirect = this.$route.query.redirect
          this.$router.push(redirect || '/shop')
        }
      } catch (e) {
        // 错误由拦截器处理
      } finally {
        this.loginLoading = false
      }
    },
    /** 注册 */
    async onRegister() {
      try {
        await this.$refs.registerFormRef.validate()
      } catch {
        return
      }
      this.registerLoading = true
      try {
        await register(this.registerForm)
        ElMessage.success('注册成功，请登录')
        this.activeTab = 'login'
        this.loginForm.username = this.registerForm.username
        this.loginForm.password = ''
      } catch (e) {
        // 错误由拦截器处理
      } finally {
        this.registerLoading = false
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #FFE0E6 0%, #FFB6C1 30%, #C71526 100%);
  padding: 40px 20px;
  position: relative;
  overflow: hidden;
}

/* 背景装饰花朵 */
.login-bg-decoration {
  position: absolute;
  width: 100%;
  height: 100%;
  top: 0;
  left: 0;
  pointer-events: none;
}

.deco-flower {
  position: absolute;
  font-size: 48px;
  opacity: 0.3;
  animation: float 6s ease-in-out infinite;
}

.deco-1 { top: 10%; left: 8%; animation-delay: 0s; }
.deco-2 { top: 60%; left: 5%; animation-delay: 2s; font-size: 64px; }
.deco-3 { top: 15%; right: 10%; animation-delay: 1s; font-size: 56px; }
.deco-4 { bottom: 15%; right: 8%; animation-delay: 3s; font-size: 40px; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(10deg); }
}

.login-container {
  width: 100%;
  max-width: 420px;
  position: relative;
  z-index: 1;
}

/* Logo区域 */
.login-logo {
  text-align: center;
  margin-bottom: 28px;
}

.logo-icon {
  font-size: 56px;
  display: inline-block;
  filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
}

.login-title {
  margin: 12px 0 6px;
  font-size: 32px;
  color: #fff;
  font-weight: bold;
  letter-spacing: 4px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.login-subtitle {
  color: rgba(255, 255, 255, 0.85);
  font-size: 14px;
  margin: 0;
}

/* 登录卡片 */
.login-card {
  border-radius: 16px;
}

.login-card :deep(.el-card__body) {
  padding: 28px 32px 16px;
}

.login-tabs :deep(.el-tabs__nav) {
  width: 100%;
}

.login-tabs :deep(.el-tabs__item) {
  width: 50%;
  text-align: center;
  font-size: 16px;
  font-weight: 500;
}

.login-tabs :deep(.el-tabs__active-bar) {
  background-color: #C71526;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #C71526;
}

.login-form {
  margin-top: 12px;
}

.submit-btn {
  width: 100%;
  margin-top: 8px;
  background: #C71526;
  border-color: #C71526;
  font-size: 16px;
  letter-spacing: 4px;
}

.submit-btn:hover {
  background: #A01020;
  border-color: #A01020;
}
</style>
