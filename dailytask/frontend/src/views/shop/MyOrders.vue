<template>
  <div class="my-orders-page">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="order-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>我的订单</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="orders-card">
      <div class="orders-header">
        <h2 class="card-title">❀ 我的订单 ❀</h2>
        <el-text type="info" size="small">共 {{ filteredOrders.length }} 个订单</el-text>
      </div>

      <!-- 状态 Tab 筛选 -->
      <el-tabs v-model="activeStatus" @tab-change="onTabChange" class="order-tabs">
        <el-tab-pane label="全部" name="-1" />
        <el-tab-pane label="待付款" name="0" />
        <el-tab-pane label="已付款" name="1" />
        <el-tab-pane label="已发货" name="2" />
        <el-tab-pane label="已完成" name="3" />
        <el-tab-pane label="已取消" name="4" />
      </el-tabs>

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-wrapper">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空状态 -->
      <div v-else-if="filteredOrders.length === 0" class="empty-wrapper">
        <div class="empty-icon">🌸</div>
        <p class="empty-text">暂无订单</p>
        <el-button type="danger" round @click="$router.push('/shop')">去选购鲜花</el-button>
      </div>

      <!-- 订单列表 - 卡片式 -->
      <div v-else class="order-list">
        <div v-for="order in pagedOrders" :key="order.id" class="order-item">
          <!-- 订单头部：订单号 + 时间 + 状态标签 -->
          <div class="order-item-header">
            <div class="order-info-left">
              <span class="order-no">订单号：{{ order.orderNo }}</span>
              <span class="order-time">{{ order.createTime }}</span>
            </div>
            <el-tag :type="getStatusTagType(order.status)" size="small" effect="dark" round>
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>

          <!-- 订单内容：商品信息 -->
          <div class="order-item-body">
            <el-image :src="order.productImage" fit="cover" class="order-product-img">
              <template #error><div class="img-placeholder">🌸</div></template>
            </el-image>
            <div class="order-product-info">
              <div class="order-product-name">{{ order.productName }}</div>
              <div class="order-product-qty">× {{ order.quantity }}</div>
            </div>
            <div class="order-product-price">
              <span class="price-label">实付金额</span>
              <span class="price-value">¥{{ order.totalPrice }}</span>
            </div>
          </div>

          <!-- 操作按钮区：根据状态显示不同按钮 -->
          <div class="order-item-actions">
            <!-- 待付款：去付款 + 取消订单 -->
            <template v-if="order.status === 0">
              <el-button type="danger" size="small" @click="onPay(order)">去付款</el-button>
              <el-button size="small" @click="onCancel(order)">取消订单</el-button>
            </template>
            <!-- 已付款：等待发货 + 可取消 -->
            <template v-else-if="order.status === 1">
              <span class="action-hint">商家正在备货中，请耐心等待发货</span>
              <el-button size="small" @click="onCancel(order)">取消订单</el-button>
            </template>
            <!-- 已发货：确认收货 -->
            <template v-else-if="order.status === 2">
              <el-button type="danger" size="small" @click="onConfirm(order)">确认收货</el-button>
            </template>
            <!-- 已完成：再次购买 -->
            <template v-else-if="order.status === 3">
              <el-button type="danger" size="small" @click="onRebuy(order)">再次购买</el-button>
            </template>
            <!-- 已取消：重新下单 -->
            <template v-else-if="order.status === 4">
              <el-button type="danger" size="small" @click="onRebuy(order)">重新下单</el-button>
            </template>
          </div>
        </div>
      </div>

      <!-- 前端分页 -->
      <div v-if="filteredOrders.length > pageSize" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="filteredOrders.length"
          layout="prev, pager, next"
          background
          @current-change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>
<script>
import { getMyOrders, cancelOrder, payOrder, confirmOrder } from '../../api/order'
import { getStatusText, getStatusTagType } from '../../utils/orderStatus'

export default {
  name: 'ShopMyOrders',
  data() {
    return {
      activeStatus: '-1',   // 当前选中的 Tab（-1 = 全部）
      loading: true,
      orderList: [],        // 所有订单（一次性加载）
      currentPage: 1,
      pageSize: 6           // 每页 6 条
    }
  },
  computed: {
    /** 按 Tab 筛选订单 */
    filteredOrders() {
      if (this.activeStatus === '-1') return this.orderList
      return this.orderList.filter(order => String(order.status) === this.activeStatus)
    },
    /** 当前页显示的订单 */
    pagedOrders() {
      const start = (this.currentPage - 1) * this.pageSize
      return this.filteredOrders.slice(start, start + this.pageSize)
    }
  },
  watch: {
    '$route'(to) {
      if (to.name === 'ShopOrders' || to.name === 'ShopMyOrders') {
        this.loadOrders()
      }
    }
  },
  created() {
    this.loadOrders()
    // 切换标签页回来时自动刷新订单
    this._onVisible = () => { if (!document.hidden) this.loadOrders() }
    document.addEventListener('visibilitychange', this._onVisible)
  },
  beforeUnmount() {
    document.removeEventListener('visibilitychange', this._onVisible)
  },
  methods: {
    /** 获取状态文本 */
    getStatusText(status) {
      return getStatusText(status)
    },
    /** 获取状态标签颜色 */
    getStatusTagType(status) {
      return getStatusTagType(status)
    },
    /** 加载全部订单 */
    async loadOrders() {
      this.loading = true
      try {
        const res = await getMyOrders()
        this.orderList = Array.isArray(res.data) ? res.data : []
      } catch (e) {
        this.orderList = []
      } finally {
        this.loading = false
      }
    },

    /** 切换 Tab → 重置页码 */
    onTabChange() { this.currentPage = 1 },

    /** 换页 → 回到顶部 */
    onPageChange() { window.scrollTo({ top: 0, behavior: 'smooth' }) },

    /** 付款操作（带确认弹窗） */
    async onPay(order) {
      try {
        await this.$confirm(`确认支付 ¥${order.totalPrice}？`, '确认付款', {
          confirmButtonText: '确认支付', cancelButtonText: '取消', type: 'info'
        })
        await payOrder(order.id)
        this.$message.success('付款成功')
        this.loadOrders()
      } catch (e) {
        if (e !== 'cancel') { /* 错误由拦截器处理 */ }
      }
    },

    /** 取消订单操作 */
    async onCancel(order) {
      try {
        await this.$confirm('确定要取消订单吗？', '取消订单', {
          confirmButtonText: '确定取消', cancelButtonText: '再想想', type: 'warning'
        })
        await cancelOrder(order.id)
        this.$message.success('取消成功')
        this.loadOrders()
      } catch (e) {
        if (e !== 'cancel') { /* 错误由拦截器处理 */ }
      }
    },

    /** 确认收货操作 */
    async onConfirm(order) {
      try {
        await this.$confirm('确认已收到商品？', '确认收货', {
          confirmButtonText: '确认收货', cancelButtonText: '取消', type: 'info'
        })
        await confirmOrder(order.id)
        this.$message.success('确认收货成功')
        this.loadOrders()
      } catch (e) {
        if (e !== 'cancel') { /* 错误由拦截器处理 */ }
      }
    },

    /** 再次购买 / 重新下单 → 跳转商品详情 */
    onRebuy(order) {
      if (order.productId) {
        this.$router.push(`/shop/detail/${order.productId}`)
      } else {
        this.$router.push('/shop')
      }
    }
  }
}
</script>
<style scoped>
.my-orders-page {
  display: flex; flex-direction: column; gap: 16px;
  padding: 20px; max-width: 1200px; margin: 0 auto;
  box-sizing: border-box;
}
.order-breadcrumb { margin-bottom: 4px; }

/* 订单卡片容器 */
.orders-card {
  background: #fff; border-radius: 12px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.orders-header {
  display: flex; align-items: center; justify-content: space-between;
  margin-bottom: 4px;
}
.card-title { font-size: 22px; font-weight: bold; color: #333; margin: 0; }

/* Tab 样式 */
.order-tabs :deep(.el-tabs__active-bar) { background-color: #C71526; }
.order-tabs :deep(.el-tabs__item.is-active) { color: #C71526; }

/* 空状态 */
.empty-wrapper { text-align: center; padding: 48px 0; }
.empty-icon { font-size: 64px; margin-bottom: 12px; }
.empty-text { color: #999; font-size: 14px; margin-bottom: 16px; }
.empty-wrapper :deep(.el-button--danger) { background: #C71526; border-color: #C71526; }

/* 订单列表 */
.order-list { display: flex; flex-direction: column; gap: 14px; }
.order-item {
  border: 1px solid #f0f0f0; border-radius: 10px; overflow: hidden;
  transition: box-shadow 0.3s;
}
.order-item:hover {
  box-shadow: 0 4px 16px rgba(199, 21, 38, 0.08);
  border-color: #FFE0E0;
}

/* 订单头部 */
.order-item-header {
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; background: #FAFAFA;
  border-bottom: 1px solid #f0f0f0;
}
.order-info-left { display: flex; align-items: center; gap: 16px; }
.order-no { font-size: 13px; color: #666; font-family: monospace; }
.order-time { font-size: 12px; color: #bbb; }

/* 订单内容 */
.order-item-body { display: flex; align-items: center; gap: 16px; padding: 16px; }
.order-product-img {
  width: 80px; height: 80px; border-radius: 8px;
  border: 1px solid #f0f0f0; flex-shrink: 0;
}
.img-placeholder {
  width: 80px; height: 80px; display: flex;
  align-items: center; justify-content: center;
  background: #FFF5F5; font-size: 28px;
}
.order-product-info { flex: 1; min-width: 0; }
.order-product-name {
  font-size: 15px; color: #333; font-weight: 500; margin-bottom: 4px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 400px;
}
.order-product-qty { font-size: 13px; color: #999; }
.order-product-price { text-align: right; flex-shrink: 0; }
.price-label { display: block; font-size: 12px; color: #999; margin-bottom: 2px; }
.price-value { font-size: 20px; color: #C71526; font-weight: bold; }

/* 操作按钮区 */
.order-item-actions {
  display: flex; align-items: center; justify-content: flex-end;
  gap: 10px; padding: 10px 16px;
  border-top: 1px solid #f5f5f5; background: #FAFAFA;
}
.order-item-actions :deep(.el-button--danger) {
  background-color: #C71526; border-color: #C71526;
}
.action-hint { font-size: 13px; color: #999; margin-right: auto; }

/* 分页 */
.loading-wrapper { padding: 20px; }
.pagination-wrapper { display: flex; justify-content: center; margin-top: 20px; padding-bottom: 8px; }
.pagination-wrapper :deep(.el-pagination.is-background .el-pager li.is-active) {
  background-color: #C71526;
}
</style>