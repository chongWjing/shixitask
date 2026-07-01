<template>
  <div class="login-page">
    <span class="deco deco-1">🌸</span>
    <span class="deco deco-2">🌹</span>
    <span class="deco deco-3">🌷</span>
    <span class="deco deco-4">💐</span>

    <div class="login-wrapper">
      <div class="brand">
        <span class="brand-icon">🌸</span>
        <div class="brand-title">花之恋</div>
        <div class="brand-sub">每一束花，都是爱的告白</div>
      </div>

      <el-card class="login-card" :shadow="'always'">
        <el-tabs v-model="activeTab" class="login-tabs">
          <el-tab-pane label="登录" name="login">
            <el-form
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              :label-position="'top'"
              size="large"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="用户名"
                  :prefix-icon="'User'"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  placeholder="密码"
                  type="password"
                  show-password
                  :prefix-icon="'Lock'"
                />
              </el-form-item>
              <el-button
                type="danger"
                size="large"
                class="submit-btn"
                :loading="loginLoading"
                @click="handleLogin"
              >
                登 录
              </el-button>
            </el-form>
          </el-tab-pane>

          <el-tab-pane label="注册" name="register">
            <el-form
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              :label-position="'top'"
              size="large"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="用户名"
                  :prefix-icon="'User'"
                />
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  placeholder="密码（不少于6位）"
                  type="password"
                  show-password
                  :prefix-icon="'Lock'"
                />
              </el-form-item>
              <el-form-item prop="phone">
                <el-input
                  v-model="registerForm.phone"
                  placeholder="手机号"
                  :prefix-icon="'Phone'"
                />
              </el-form-item>
              <el-button
                type="danger"
                size="large"
                class="submit-btn"
                :loading="registerLoading"
                @click="handleRegister"
              >
                注 册
              </el-button>
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
      loginForm: {
        username: '',
        password: ''
      },
      registerForm: {
        username: '',
        password: '',
        phone: ''
      },
      loginRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ]
      },
      registerRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' },
          { min: 6, message: '密码不少于6位', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: '请输入正确的手机号',
            trigger: 'blur'
          }
        ]
      }
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginFormRef.validate(async (valid) => {
        if (!valid) return
        this.loginLoading = true
        try {
          const res = await login(this.loginForm)
          const data = res.data || {}
          const user = data.user || {}
          if (data.token) {
            localStorage.setItem('token', data.token)
          }
          if (user.username) {
            localStorage.setItem('username', user.username)
          }
          if (user.role !== undefined && user.role !== null) {
            localStorage.setItem('role', String(user.role))
          }
          ElMessage.success('登录成功，欢迎回来！')
          const redirect = this.$route.query.redirect || '/shop'
          this.$router.replace(redirect)
        } catch (e) {
          ElMessage.error(e.message || '操作失败，请重试')
        } finally {
          this.loginLoading = false
        }
      })
    },
    handleRegister() {
      this.$refs.registerFormRef.validate(async (valid) => {
        if (!valid) return
        this.registerLoading = true
        try {
          await register(this.registerForm)
          ElMessage.success('注册成功，请登录')
          this.loginForm.username = this.registerForm.username
          this.loginForm.password = ''
          this.registerForm = { username: '', password: '', phone: '' }
          this.activeTab = 'login'
        } catch (e) {
          ElMessage.error(e.message || '操作失败，请重试')
        } finally {
          this.registerLoading = false
        }
      })
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
  position: relative;
  overflow: hidden;
  padding: 24px;
  box-sizing: border-box;
}

.deco {
  position: absolute;
  font-size: 56px;
  opacity: 0.3;
  animation: float 6s ease-in-out infinite;
  pointer-events: none;
  user-select: none;
}

.deco-1 { top: 32px; left: 32px; animation-delay: 0s; }
.deco-2 { top: 32px; right: 32px; animation-delay: 1.5s; }
.deco-3 { bottom: 32px; left: 32px; animation-delay: 3s; }
.deco-4 { bottom: 32px; right: 32px; animation-delay: 4.5s; }

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(-8deg); }
  50% { transform: translateY(-16px) rotate(8deg); }
}

.login-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 2;
  width: 100%;
  max-width: 460px;
}

.brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.brand-icon {
  font-size: 56px;
  display: inline-block;
  filter: drop-shadow(0 4px 10px rgba(0, 0, 0, 0.25));
  margin-bottom: 8px;
}

.brand-title {
  font-size: 32px;
  color: #fff;
  font-weight: 700;
  letter-spacing: 4px;
  line-height: 1.2;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.brand-sub {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.75);
  margin-top: 6px;
  letter-spacing: 2px;
}

.login-card {
  width: 100%;
  max-width: 420px;
  border-radius: 16px;
  padding: 28px 32px 16px;
}

.login-card :deep(.el-card__body) {
  padding: 0;
}

.login-tabs :deep(.el-tabs__item.is-active) {
  color: #C71526;
}
.login-tabs :deep(.el-tabs__active-bar) {
  background-color: #C71526;
}
.login-tabs :deep(.el-tabs__nav-wrap::after) {
  background-color: rgba(199, 21, 38, 0.15);
}

.submit-btn {
  width: 100%;
  letter-spacing: 4px;
  font-size: 16px;
}
</style>
