<!-- 商城布局组件 - 顶栏 + 内容区 + Footer -->
<template>
  <div class="shop-layout">
    <!-- 顶部公告栏 -->
    <div class="top-bar">
      <div class="top-bar-inner">
        <span class="announce-text">🌸 花之恋商城 · 鲜花预定 · 浪漫到家</span>
        <div class="user-area" v-if="isLoggedIn">
          <span>Hi, {{ username }}</span>
          <span class="divider">|</span>
          <a class="logout-link" @click="handleLogout">退出</a>
        </div>
        <div class="user-area" v-else>
          <a class="logout-link" @click="goLogin">登录</a>
          <span class="divider">|</span>
          <a class="logout-link" @click="goLogin">注册</a>
        </div>
      </div>
    </div>

    <!-- 头部导航 -->
    <header class="shop-header">
      <div class="header-inner">
        <div class="logo" @click="$router.push('/shop')">
          <span class="logo-icon">🌸</span>
          <span class="logo-text">花之恋</span>
        </div>
        <div class="nav-links">
          <router-link to="/shop" class="nav-link">首页</router-link>
          <router-link to="/shop/cart" class="nav-link" v-if="isLoggedIn">🛒 购物车</router-link>
          <router-link to="/shop/my-orders" class="nav-link" v-if="isLoggedIn">📦 我的订单</router-link>
        </div>
        <div class="header-actions">
          <div class="search-box">
            <el-input placeholder="搜索鲜花..." prefix-icon="Search" size="default" />
          </div>
          <a v-if="isLoggedIn" class="action-link user-link" @click="$router.push('/shop/user')">👤 {{ username }}</a>
          <a v-else class="action-link user-link" @click="goLogin">👤 登录</a>
        </div>
      </div>
    </header>

    <!-- 主内容区 -->
    <main class="shop-main">
      <router-view />
    </main>

    <!-- 底部 -->
    <footer class="shop-footer">
      <div class="footer-inner">
        <p>🌸 花之恋商城 © 2025 | 用 Spring AI + Vue3 打造的智能鲜花电商平台</p>
        <p class="footer-links">
          <a href="#">关于我们</a> · <a href="#">服务条款</a> · <a href="#">隐私政策</a> · <a href="#">联系客服</a>
        </p>
      </div>
    </footer>
  </div>
</template>

<script>
import { logout } from '../../api/user'
import { ElMessage } from 'element-plus'

export default {
  name: 'ShopLayout',
  data() {
    return {
      isLoggedIn: false,
      username: ''
    }
  },
  mounted() {
    this.loadUser()
  },
  methods: {
    loadUser() {
      const token = localStorage.getItem('token')
      const username = localStorage.getItem('username')
      if (token) {
        this.isLoggedIn = true
        this.username = username || '用户'
      } else {
        this.isLoggedIn = false
        this.username = ''
      }
    },
    goLogin() {
      this.$router.push({ path: '/shop/login', query: { redirect: '/shop' } })
    },
    async handleLogout() {
      try {
        await logout()
      } catch (e) {
        // 忽略错误，继续清理本地状态
      }
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      this.isLoggedIn = false
      this.username = ''
      ElMessage.success('已退出登录')
      this.goLogin()
    }
  }
}
</script>

<style scoped>
.shop-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* 顶部公告栏 */
.top-bar {
  background: #C71526;
  color: #fff;
  font-size: 13px;
  line-height: 36px;
  height: 36px;
}
.top-bar-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.announce-text {
  opacity: 0.9;
}
.user-area {
  display: flex;
  align-items: center;
  gap: 8px;
}
.divider {
  opacity: 0.5;
}
.logout-link {
  cursor: pointer;
  text-decoration: none;
  color: #fff;
}
.logout-link:hover {
  text-decoration: underline;
}

/* 头部导航 */
.shop-header {
  background: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.header-inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  height: 64px;
  display: flex;
  align-items: center;
  gap: 40px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  flex-shrink: 0;
}
.logo-icon {
  font-size: 28px;
}
.logo-text {
  font-size: 20px;
  font-weight: bold;
  color: #C71526;
  letter-spacing: 2px;
}
.nav-links {
  display: flex;
  gap: 24px;
}
.nav-link {
  text-decoration: none;
  color: #333;
  font-size: 15px;
  padding: 4px 0;
  transition: color 0.2s;
}
.nav-link:hover,
.nav-link.router-link-exact-active {
  color: #C71526;
}
.header-actions {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 20px;
}
.search-box {
  width: 240px;
}
.action-link {
  text-decoration: none;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  white-space: nowrap;
}
.action-link:hover {
  color: #C71526;
}

/* 主内容区 */
.shop-main {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
}

/* 底部 */
.shop-footer {
  background: #2c2c2c;
  color: #999;
  text-align: center;
  padding: 30px 20px;
  margin-top: 40px;
}
.footer-inner p {
  margin: 6px 0;
  font-size: 13px;
}
.footer-links a {
  color: #ccc;
  text-decoration: none;
  font-size: 12px;
}
.footer-links a:hover {
  color: #C71526;
}
</style>
