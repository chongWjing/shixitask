<template>
  <div class="shop-layout">
    <!-- ==================== 头部：顶部欢迎条 32px #A01020 ==================== -->
    <div class="header-top">
      <div class="header-top__inner">
        <span class="header-top__welcome">欢迎来到花之恋鲜花电商平台！</span>
        <div class="header-top__user">
          <template v-if="isLoggedIn">
            <span class="header-top__greeting">Hi, {{ username }}</span>
            <span class="header-top__sep">|</span>
            <span class="header-top__logout" @click="handleLogout">退出</span>
          </template>
          <template v-else>
            <span class="header-top__login" @click="$router.push('/login')">登录</span>
            <span class="header-top__sep">|</span>
            <span class="header-top__register" @click="$router.push('/login')">注册</span>
          </template>
        </div>
      </div>
    </div>

    <!-- ==================== 头部：主导航 72px #C71526 ==================== -->
    <div class="header-main">
      <div class="header-main__inner">
        <div class="header-logo" @click="$router.push('/shop')">
          <span class="header-logo__icon">🌸</span>
          <div class="header-logo__text">
            <span class="header-logo__cn">花之恋</span>
            <span class="header-logo__en">FLORA LOVE</span>
          </div>
        </div>

        <div class="header-nav">
          <span v-for="nav in headerNavs" :key="nav.name" class="header-nav__item"
            :class="{ 'header-nav__item--active': isNavActive(nav) }" @click="handleNavClick(nav)">{{ nav.name }}</span>
        </div>

        <div class="header-search">
          <input v-model="searchInput" class="header-search__input" placeholder="搜索鲜花、花束..."
            @keyup.enter="handleSearch" />
        </div>

        <div class="header-actions">
          <span class="header-actions__item" @click="$router.push('/shop/cart')">🛒 购物车</span>
          <span class="header-actions__item" @click="$router.push('/shop/orders')">📄 我的订单</span>
          <span class="header-actions__item" @click="$router.push('/shop/profile')">👤 {{ username }}</span>
        </div>
      </div>
    </div>

    <!-- ==================== 信任标签栏 ==================== -->
    <div class="trust-bar">
      <div class="trust-bar__inner">
        <span v-for="item in trustBadges" :key="item.label" class="trust-badge">
          <span class="trust-badge__icon">{{ item.emoji }}</span>
          <span class="trust-badge__text">{{ item.label }}</span>
        </span>
      </div>
    </div>

    <!-- ==================== 主内容区：子页面在此渲染 ==================== -->
    <main class="layout-main">
      <router-view />
    </main>

    <!-- ==================== 服务保障栏 ==================== -->
    <div class="service-bar">
      <div class="service-bar__inner">
        <div v-for="svc in serviceFeatures" :key="svc.title" class="service-item">
          <span class="service-item__icon">{{ svc.emoji }}</span>
          <div class="service-item__text">
            <span class="service-item__title">{{ svc.title }}</span>
            <span class="service-item__desc">{{ svc.desc }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- ==================== 页脚 ==================== -->
    <div class="footer">
      <div class="footer__inner">
        <div class="footer__main">
          <div class="footer-logo">
            <span class="footer-logo__icon">🌸</span>
            <span class="footer-logo__cn">花之恋</span>
            <span class="footer-logo__en">FLORA LOVE</span>
          </div>
          <div v-for="col in footerColumns" :key="col.title" class="footer-col">
            <h4 class="footer-col__title">{{ col.title }}</h4>
            <span v-for="link in col.links" :key="link" class="footer-col__link">{{ link }}</span>
          </div>
        </div>
        <div class="footer__bottom">
          <p class="footer__copyright">© 2026 花之恋鲜花电商 - 基于SpringBoot AI电商平台</p>
          <p class="footer__credit">华杉科技(河南)有限公司 × 河南工业大学 Java实训项目</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCategoryList } from '../../api/product'

export default {
  name: 'ShopLayout',
  computed: {
    isLoggedIn() {
      return !!localStorage.getItem('token')
    }
  },
  data() {
    return {
      username: '',
      searchInput: '',

      headerNavs: [
        { name: '首页', link: '/shop', active: true },
        { name: '鲜花', link: '/shop?category=' + encodeURIComponent('鲜花'), active: false },
        { name: '永生花', link: '/shop?category=' + encodeURIComponent('永生花'), active: false },
        { name: '花束', link: '/shop?category=' + encodeURIComponent('花束'), active: false },
        { name: '花篮', link: '/shop?category=' + encodeURIComponent('花篮'), active: false },
        { name: '绿植', link: '/shop?category=' + encodeURIComponent('绿植'), active: false }
      ],

      trustBadges: [
        { emoji: '🏆', label: '优秀企业' },
        { emoji: '🏅', label: '20年品牌' },
        { emoji: '🚚', label: '全国送花' },
        { emoji: '🛡️', label: '退赔承诺' },
        { emoji: '📷', label: '最近99+条评价' }
      ],

      serviceFeatures: [
        { emoji: '✅', title: '正品保障', desc: '品质鲜花 假一赔十' },
        { emoji: '🚚', title: '免费配送', desc: '指定区域 免费送达' },
        { emoji: '🎨', title: '专业花艺', desc: '花艺师亲手制作' },
        { emoji: '💝', title: '贴心售后', desc: '售后无忧 全程呵护' }
      ],

      footerColumns: [
        { title: '购物指南', links: ['购物流程', '会员介绍', '常见问题'] },
        { title: '配送方式', links: ['上门自提', '快递运输', '配送范围'] },
        { title: '售后服务', links: ['退换政策', '退换流程', '价格保护'] },
        { title: '关于我们', links: ['平台介绍', '联系我们', '招贤纳士'] }
      ]
    }
  },
  watch: {
    $route() {
      this.updateUserInfo()
    }
  },

  methods: {
    updateUserInfo() {
      const stored = localStorage.getItem('username')
      this.username = stored || ''
    },
    // 从 API 加载分类，动态更新导航链接
    async loadNavCategories() {
      try {
        const res = await getCategoryList()
        const list = Array.isArray(res.data) ? res.data : []
        if (list.length > 0) {
          this.headerNavs = [
            { name: '首页', link: '/shop', active: true },
            ...list.map(cat => ({
              name: cat.name,
              link: '/shop?category=' + encodeURIComponent(cat.name),
              active: false
            }))
          ]
        }
      } catch {
        // 降级：保持 data 中已定义的硬编码导航
      }
    },

    // 判断导航项是否激活
    isNavActive(nav) {
      if (nav.name === '首页') {
        return this.$route.path === '/shop' && !this.$route.query.category
      }
      const navCat = nav.link.includes('category=')
        ? decodeURIComponent(nav.link.split('category=')[1])
        : ''
      return this.$route.query.category === navCat
    },

    // 导航点击
    handleNavClick(nav) {
      this.$router.push(nav.link)
    },

    // 搜索：回车触发，通过 query 传递关键词到首页
    handleSearch() {
      const q = this.searchInput.trim()
      const query = {}
      if (q) query.keyword = q
      this.$router.push({ path: '/shop', query })
    },

    // 退出登录
    handleLogout() {
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      this.$router.push('/')
    }
  },

  created() {
    this.updateUserInfo()
    this.loadNavCategories()
  }
}
</script>

<style scoped>
/* ==================== 全局 ==================== */
.shop-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f5;
}

.layout-main {
  flex: 1;
}

/* ==================== 顶部欢迎条 32px #A01020 ==================== */
.header-top {
  background: #A01020;
  height: 32px;
}

.header-top__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 20px;
}

.header-top__welcome {
  font-size: 12px;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
}

.header-top__user {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
}

.header-top__greeting {
  color: #fff;
}

.header-top__sep {
  color: rgba(255, 255, 255, 0.5);
}

.header-top__logout {
  color: #66B1FF;
  cursor: pointer;
  text-decoration: underline;
}

.header-top__logout:hover {
  color: #fff;
}

/* ==================== 主导航 72px #C71526 ==================== */
.header-main {
  background: #C71526;
  height: 72px;
  box-shadow: 0 2px 12px rgba(199, 21, 38, 0.3);
}

.header-main__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  height: 100%;
  padding: 0 20px;
  gap: 20px;
}

.header-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  flex-shrink: 0;
}

.header-logo__icon {
  font-size: 24px;
  line-height: 1;
}

.header-logo__text {
  display: flex;
  flex-direction: column;
}

.header-logo__cn {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
  font-family: 'Microsoft YaHei', sans-serif;
}

.header-logo__en {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.8);
  font-family: 'Microsoft YaHei', sans-serif;
}

.header-nav {
  display: flex;
  align-items: center;
  gap: 18px;
  flex-shrink: 0;
}

.header-nav__item {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  text-decoration: underline;
  white-space: nowrap;
  font-family: 'Microsoft YaHei', sans-serif;
}

.header-nav__item--active {
  font-weight: 700;
  color: #fff;
}

.header-nav__item:hover {
  color: #fff;
}

.header-search {
  flex: 1;
  max-width: 200px;
}

.header-search__input {
  width: 100%;
  height: 34px;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: rgba(255, 255, 255, 0.2);
  padding: 0 11px;
  font-size: 14px;
  color: #fff;
  outline: none;
  box-sizing: border-box;
  font-family: 'Arial', sans-serif;
}

.header-search__input::placeholder {
  color: rgba(255, 255, 255, 0.6);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}

.header-actions__item {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.85);
  cursor: pointer;
  text-decoration: underline;
  white-space: nowrap;
  padding: 8px 16px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.header-actions__item:hover {
  color: #fff;
}

/* ==================== 信任标签栏 ==================== */
.trust-bar {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}

.trust-bar__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 49px;
  gap: 48px;
  padding: 0 20px;
}

.trust-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  white-space: nowrap;
}

.trust-badge__icon {
  font-size: 14px;
  line-height: 1;
}

.trust-badge__text {
  font-size: 14px;
  color: #666;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 服务保障栏 ==================== */
.service-bar {
  background: #FFF5F5;
  border-top: 1px solid #FFE0E0;
  border-bottom: 1px solid #FFE0E0;
}

.service-bar__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 107px;
  padding: 0 20px;
}

.service-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.service-item__icon {
  font-size: 27px;
  line-height: 1;
}

.service-item__text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.service-item__title {
  font-size: 16px;
  font-weight: 700;
  color: #C71526;
  font-family: 'Microsoft YaHei', sans-serif;
}

.service-item__desc {
  font-size: 12px;
  color: #999;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 页脚 ==================== */
.footer {
  background: #2B2B2B;
  padding: 32px 0 20px;
}

.footer__inner {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer__main {
  display: flex;
  gap: 80px;
  margin-bottom: 30px;
}

.footer-logo {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  flex-shrink: 0;
}

.footer-logo__icon {
  font-size: 28px;
  margin-bottom: 6px;
}

.footer-logo__cn {
  font-size: 26px;
  font-weight: 700;
  color: #fff;
  font-family: 'Microsoft YaHei', sans-serif;
}

.footer-logo__en {
  font-size: 10px;
  color: rgba(255, 255, 255, 0.6);
  margin-top: 2px;
  font-family: 'Microsoft YaHei', sans-serif;
}

.footer-col {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.footer-col__title {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 4px 0;
  font-family: 'Microsoft YaHei', sans-serif;
  line-height: 2.5;
}

.footer-col__link {
  font-size: 12px;
  color: #999;
  cursor: pointer;
  font-family: 'Microsoft YaHei', sans-serif;
}

.footer-col__link:hover {
  color: #fff;
}

.footer__bottom {
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  padding-top: 20px;
  text-align: center;
}

.footer__copyright {
  font-size: 11px;
  color: #666;
  margin: 0 0 4px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}

.footer__credit {
  font-size: 12px;
  color: #666;
  margin: 0;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1024px) {
  .header-nav {
    gap: 10px;
  }

  .header-nav__item {
    font-size: 13px;
  }

  .header-search {
    max-width: 140px;
  }

  .header-actions__item {
    font-size: 12px;
    padding: 4px 8px;
  }

  .header-logo__cn {
    font-size: 20px;
  }

  .header-logo__en {
    font-size: 8px;
  }

  .trust-bar__inner {
    gap: 24px;
  }

  .service-bar__inner {
    height: auto;
    padding: 24px 20px;
    flex-wrap: wrap;
    gap: 24px;
  }

  .footer__main {
    gap: 40px;
    flex-wrap: wrap;
  }
}

@media (max-width: 768px) {
  .header-main__inner {
    flex-wrap: wrap;
    height: auto;
    padding: 12px 20px;
    gap: 12px;
  }

  .header-nav {
    order: 3;
    width: 100%;
    justify-content: center;
  }

  .header-search {
    order: 4;
    max-width: 100%;
    width: 100%;
  }

  .header-actions {
    order: 2;
  }

  .header-top__welcome {
    display: none;
  }

  .trust-bar__inner {
    flex-wrap: wrap;
    gap: 16px;
    height: auto;
    padding: 12px 10px;
  }

  .service-bar__inner {
    flex-direction: column;
    align-items: flex-start;
  }

  .footer__main {
    flex-direction: column;
    gap: 24px;
  }
}
</style>