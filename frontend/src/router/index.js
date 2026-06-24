import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 商城页面
const ShopLayout = () => import('../views/shop/ShopLayout.vue')
const ShopHome = () => import('../views/shop/Home.vue')
const ShopLogin = () => import('../views/shop/Login.vue')
const ShopProductDetail = () => import('../views/shop/ProductDetail.vue')
const ShopOrder = () => import('../views/shop/Order.vue')
const ShopMyOrders = () => import('../views/shop/MyOrders.vue')
const ShopCart = () => import('../views/shop/Cart.vue')
const ShopUserCenter = () => import('../views/shop/UserCenter.vue')

const routes = [
  // 默认重定向到商城首页
  { path: '/', redirect: '/shop' },

  // 商城路由 - 使用ShopLayout包裹
  {
    path: '/shop',
    component: ShopLayout,
    children: [
      { path: '', name: 'ShopHome', component: ShopHome },
      { path: 'login', name: 'ShopLogin', component: ShopLogin },
      { path: 'product/:id', name: 'ShopProductDetail', component: ShopProductDetail },
      { path: 'order/:id', name: 'ShopOrder', component: ShopOrder, meta: { requiresAuth: true } },
      { path: 'cart', name: 'ShopCart', component: ShopCart, meta: { requiresAuth: true } },
      { path: 'user', name: 'ShopUserCenter', component: ShopUserCenter, meta: { requiresAuth: true } },
      { path: 'my-orders', name: 'ShopMyOrders', component: ShopMyOrders, meta: { requiresAuth: true } }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局路由守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 商城页面需要登录（检查所有匹配路由的meta）
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  if (requiresAuth && !token) {
    ElMessage.warning('请先登录后再访问该页面')
    next({ path: '/shop/login', query: { redirect: to.fullPath } })
    return
  }

  next()
})

export default router
