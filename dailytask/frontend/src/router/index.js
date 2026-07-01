import { createRouter, createWebHashHistory } from 'vue-router'

import ShopHome from '../views/shop/Home.vue'
import Login from '../views/shop/Login.vue'
import ShopLayout from '../views/shop/ShopLayout.vue'
import ProductDetail from '../views/shop/ProductDetail.vue'
import ReviewList from '../views/shop/ReviewList.vue'
import ShopCart from '../views/shop/ShopCart.vue'
import ShopOrder from '../views/shop/Orders.vue'
import ShopMyOrders from '../views/shop/MyOrders.vue'

// Admin pages
import AdminLayout from '../views/admin/Layout.vue'
import AdminLogin from '../views/admin/Login.vue'
import AdminDashboard from '../views/admin/Dashboard.vue'
import AdminProductManage from '../views/admin/ProductManage.vue'
import AdminOrderManage from '../views/admin/OrderManage.vue'
import AdminAiQuery from '../views/admin/AiQuery.vue'

// Shop pages
import Profile from '../views/shop/Profile.vue'

const routes = [
  {
    path: '/shop',
    component: ShopLayout,
    children: [
      {
        path: '',
        component: ShopHome
      },
      {
        path: 'detail/:id',
        component: ProductDetail
      },
      {
        path: 'review',
        component: ReviewList
      },
      {
        path: 'cart',
        component: ShopCart,
        meta: { requiresAuth: true }
      },
      { path: 'order', name: 'ShopOrder', component: ShopOrder, meta: { requiresAuth: true } },
      { path: 'orders', name: 'ShopMyOrders', component: ShopMyOrders, meta: { requiresAuth: true } },
      { path: 'myorder', name: 'ShopMyOrders2', component: ShopMyOrders, meta: { requiresAuth: true } },
      {
        path: 'profile',
        component: Profile,
        meta: { requiresAuth: true }
      },
      {
        path: 'login',
        component: Login
      }
    ]
  },
  {
    path: '/admin/login',
    component: AdminLogin
  },
  {
    path: '/admin',
    component: AdminLayout,
    meta: { requiresAdmin: true },
    children: [
      { path: 'dashboard', component: AdminDashboard },
      { path: 'product', component: AdminProductManage },
      { path: 'order', component: AdminOrderManage },
      { path: 'ai', component: AdminAiQuery }
    ]
  },
  {
    path: '/',
    redirect: '/shop'
  }
]


const router = createRouter({
  history: createWebHashHistory(),
  routes
})


router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const role = localStorage.getItem('role')
  const requiresAuth = to.matched.some(r => r.meta.requiresAuth)
  const requiresAdmin = to.matched.some(r => r.meta.requiresAdmin)

  if (requiresAdmin && (!token || role !== '1')) {
    next({ path: '/admin/login' })
    return
  }
  if (requiresAuth && !token) {
    next({ path: '/shop/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})


export default router
