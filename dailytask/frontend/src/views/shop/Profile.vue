<template>
  <div class="profile-page">
    <el-breadcrumb separator="/" class="profile-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>个人中心</el-breadcrumb-item>
    </el-breadcrumb>

    <el-card class="profile-card" shadow="never">
      <template #header>
        <span class="card-title">👤 个人中心</span>
      </template>

      <div class="profile-content">
        <div class="profile-avatar">
          <el-avatar :size="100" :style="{ backgroundColor: '#C71526' }">
            {{ username ? username.charAt(0).toUpperCase() : 'U' }}
          </el-avatar>
        </div>

        <el-descriptions :column="1" border class="profile-info">
          <el-descriptions-item label="用户名">{{ username }}</el-descriptions-item>
          <el-descriptions-item label="角色">{{ role === '1' ? '管理员' : '普通用户' }}</el-descriptions-item>
        </el-descriptions>

        <div class="profile-actions">
          <el-button type="primary" @click="$router.push('/shop/orders')">我的订单</el-button>
          <el-button type="danger" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { getUserInfo, logout } from '../../api/user'

export default {
  name: 'UserProfile',
  data() {
    return {
      username: '',
      role: '',
      phone: ''
    }
  },
  async created() {
    // 优先从后端获取最新用户信息
    const token = localStorage.getItem('token')
    if (token) {
      try {
        const res = await getUserInfo()
        if (res && res.data) {
          this.username = res.data.username || ''
          this.role = String(res.data.role ?? '0')
          this.phone = res.data.phone || ''
          // 同步更新localStorage
          localStorage.setItem('username', this.username)
          localStorage.setItem('role', this.role)
        }
      } catch {
        // 接口失败则从localStorage读取
        this.username = localStorage.getItem('username') || ''
        this.role = localStorage.getItem('role') || '0'
      }
    } else {
      this.username = localStorage.getItem('username') || ''
      this.role = localStorage.getItem('role') || '0'
    }
  },
  methods: {
    async handleLogout() {
      try {
        await logout()
      } catch { /* 忽略 */ }
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      this.$router.push('/shop')
    }
  }
}
</script>

<style scoped>
.profile-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-breadcrumb {
  margin-bottom: 20px;
}

.profile-card {
  background: #fff;
}

.card-title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.profile-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 30px;
  padding: 20px 0;
}

.profile-avatar {
  margin-bottom: 10px;
}

.profile-info {
  width: 100%;
  max-width: 400px;
}

.profile-actions {
  display: flex;
  gap: 16px;
  margin-top: 20px;
}
</style>
