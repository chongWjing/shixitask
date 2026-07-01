import { createRouter, createWebHashHistory } from 'vue-router'

import ShopHome from '../views/shop/Home.vue'
import Login from '../views/shop/Login.vue'
import ShopLayout from '../views/shop/ShopLayout.vue'
import ProductDetail from '../views/shop/ProductDetail.vue'
import ReviewList from '../views/shop/ReviewList.vue'
import ShopCart from '../views/shop/ShopCart.vue'
import ShopOrder from '../views/shop/Orders.vue'
import ShopMyOrders from '../views/shop/MyOrders.vue'

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
      { path: 'myorder', name: 'ShopMyOrders', component: ShopMyOrders, meta: { requiresAuth: true } },
      {
        path: 'login',
        component: Login
      }
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
  // 使用meta配置判断是否需要认证，覆盖嵌套路由场景
  const requiresAuth = to.matched.some(r => r.meta.requiresAuth)
  if (requiresAuth && !token) {
    next({ path: '/shop/login', query: { redirect: to.fullPath } })
    return
  }
  next()
})


export default router
