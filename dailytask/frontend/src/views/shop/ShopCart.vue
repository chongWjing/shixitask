<template>
  <div class="cart-page">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="cart-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item>购物车</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="cart-card">
      <div class="cart-header">
        <h2 class="card-title">🛒 我的购物车</h2>
        <el-text type="info" size="small">共 {{ cartList.length }} 件商品</el-text>
      </div>

      <!-- 加载状态：骨架屏 -->
      <div v-if="loading" class="loading-wrapper">
        <el-skeleton :rows="5" animated />
      </div>

      <!-- 空购物车 -->
      <div v-else-if="cartList.length === 0" class="empty-wrapper">
        <div class="empty-icon">🛒</div>
        <p class="empty-text">购物车空空如也</p>
        <p class="empty-sub">快去挑选心仪的鲜花吧！</p>
        <el-button type="danger" round @click="$router.push('/shop')">去选购鲜花</el-button>
      </div>

      <!-- 购物车列表 -->
      <div v-else>
        <!-- 全选栏 -->
        <div class="select-bar">
          <el-checkbox v-model="selectAll" @change="onSelectAll">全选</el-checkbox>
          <span class="select-count">已选 {{ selectedItems.length }} 件</span>
          <el-button text type="danger" @click="onDeleteSelected" :disabled="selectedItems.length === 0">
            删除选中
          </el-button>
        </div>

        <!-- 商品列表 -->
        <div class="cart-list">
          <div v-for="item in cartList" :key="item.id" class="cart-item">
            <el-checkbox v-model="item.checked" @change="onItemCheck" class="item-check" />
            <el-image
              :src="item.product.image"
              fit="cover"
              class="item-img"
              @click="goDetail(item.product.id)"
            >
              <template #error><div class="img-fallback">🌸</div></template>
            </el-image>
            <div class="item-info" @click="goDetail(item.product.id)">
              <div class="item-name">{{ item.product.name }}</div>
              <div class="item-price-single">¥{{ item.product.price }}/件</div>
            </div>
            <div class="item-quantity">
              <el-input-number
                v-model="item.quantity"
                :min="1" :max="99"
                size="small"
                @change="onQuantityChange(item)"
              />
            </div>
            <div class="item-subtotal">
              ¥{{ (item.product.price * item.quantity).toFixed(2) }}
            </div>
            <el-button text type="danger" @click="onDeleteItem(item)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>

        <!-- 底部结算栏 -->
        <div class="cart-footer">
          <div class="footer-left">
            <el-checkbox v-model="selectAll" @change="onSelectAll">全选</el-checkbox>
          </div>
          <div class="footer-right">
            <span class="total-info">
              合计：<span class="total-price">¥{{ totalPrice }}</span>
            </span>
            <el-button type="danger" size="large" :disabled="selectedItems.length === 0" @click="onCheckout">
              去结算 ({{ selectedItems.length }})
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { Delete } from '@element-plus/icons-vue'
import { getCartList, updateCartItem, deleteCartItem } from '../../api/cart'

export default {
  name: 'ShopCart',
  components: { Delete },
  data() {
    return {
      loading: true,
      cartList: [],       // 购物车数据，每个 item 额外添加 checked 属性
      selectAll: false    // 全选状态
    }
  },
  computed: {
    /** 已选中的商品列表 */
    selectedItems() {
      return this.cartList.filter(item => item.checked)
    },
    /** 选中商品的总价（保留两位小数） */
    totalPrice() {
      return this.selectedItems
        .reduce((sum, item) => sum + item.product.price * item.quantity, 0)
        .toFixed(2)
    }
  },
  watch: {
    '$route'() { this.loadCart() }
  },
  created() {
    this.loadCart()
  },
  methods: {
    /** 加载购物车数据 */
    async loadCart() {
      this.loading = true
      try {
        const res = await getCartList()
        const data = res.data
        // 防御性编码：确保返回的是数组，为每项添加 checked 属性
        this.cartList = Array.isArray(data)
          ? data.map(item => ({ ...item, checked: false, _qtyReady: false }))
          : []
      } catch (e) {
        this.$message.error('加载购物车失败，请稍后重试')
        this.cartList = []
      } finally {
        this.loading = false
      }
    },

    /** 全选 / 取消全选 */
    onSelectAll(val) {
      this.cartList.forEach(item => { item.checked = val })
    },

    /** 单个商品选中变化 → 同步全选框状态 */
    onItemCheck() {
      this.selectAll = this.cartList.every(item => item.checked)
    },

    /** 修改数量 → 实时同步后端 */
    async onQuantityChange(item) {
      if (!item._qtyReady) { item._qtyReady = true; return }
      try {
        await updateCartItem({ id: item.id, quantity: item.quantity })
      } catch (e) {
        // 错误由拦截器统一处理
      }
    },

    /** 删除单个商品（带确认弹窗） */
    async onDeleteItem(item) {
      try {
        await this.$confirm('确定要从购物车中移除该商品吗？', '提示', { type: 'warning' })
        await deleteCartItem(item.id)
        this.$message.success('已移除')
        this.loadCart()  // 重新加载
      } catch (e) {
        if (e !== 'cancel') { /* 错误由拦截器处理 */ }
      }
    },

    /** 批量删除选中商品 */
    async onDeleteSelected() {
      if (this.selectedItems.length === 0) return
      try {
        await this.$confirm(
          `确定要删除选中的 ${this.selectedItems.length} 件商品吗？`,
          '提示',
          { type: 'warning' }
        )
        for (const item of this.selectedItems) {
          await deleteCartItem(item.id)
        }
        this.$message.success('已删除选中商品')
        this.loadCart()
      } catch (e) {
        if (e !== 'cancel') { this.$message.error('删除失败，请稍后重试') }
      }
    },

    /** 去结算：将选中的购物车项ID传递给订单页面 */
    onCheckout() {
      const items = this.selectedItems
      if (items.length === 0) return
      // 提取购物车项ID
      const cartItemIds = items.map(item => item.id).join(',')
      this.$router.push({
        path: '/shop/order',
        query: { items: cartItemIds }
      })
    },
    

    /** 点击商品图片或名称 → 跳转商品详情 */
    goDetail(productId) {
      this.$router.push(`/shop/detail/${productId}`)
    }
  }
}
</script>
<style scoped>
.cart-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  box-sizing: border-box;
}
.cart-breadcrumb { margin-bottom: 4px; }
.cart-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.cart-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.card-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin: 0;
}

/* 空状态 */
.empty-wrapper { text-align: center; padding: 60px 0; }
.empty-icon { font-size: 64px; margin-bottom: 12px; }
.empty-text { color: #333; font-size: 16px; font-weight: bold; margin-bottom: 8px; }
.empty-sub { color: #999; font-size: 14px; margin-bottom: 20px; }
.empty-wrapper :deep(.el-button--danger) { background: #C71526; border-color: #C71526; }

/* 全选栏 */
.select-bar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}
.select-count { font-size: 13px; color: #999; }

/* 商品列表 */
.cart-list { display: flex; flex-direction: column; }
.cart-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-bottom: 1px solid #f5f5f5;
  transition: background 0.2s;
}
.cart-item:hover { background: #FFF8F8; }
.item-check { flex-shrink: 0; }
.item-img {
  width: 80px; height: 80px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
  cursor: pointer;
  flex-shrink: 0;
}
.img-fallback {
  width: 80px; height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #FFF5F5;
  font-size: 28px;
}
.item-info { flex: 1; min-width: 0; cursor: pointer; }
.item-name {
  font-size: 15px; color: #333; font-weight: 500;
  margin-bottom: 6px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.item-price-single { font-size: 13px; color: #999; }
.item-quantity { flex-shrink: 0; }
.item-subtotal {
  font-size: 16px; font-weight: bold; color: #C71526;
  width: 100px; text-align: right; flex-shrink: 0;
}

/* 结算栏 */
.cart-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 0 0;
  margin-top: 8px;
}
.footer-right { display: flex; align-items: center; gap: 20px; }
.total-info { font-size: 14px; color: #666; }
.total-price { font-size: 24px; font-weight: bold; color: #C71526; }
.cart-footer :deep(.el-button--danger) {
  background: #C71526; border-color: #C71526;
  font-size: 16px; padding: 12px 32px;
}
.loading-wrapper { padding: 20px; }
</style>