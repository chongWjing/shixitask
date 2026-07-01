<template>
  <div class="product-detail-page">
    <el-breadcrumb separator="/" class="detail-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item v-if="product && product.category">{{ product.category }}</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div v-if="loading" class="detail-skeleton">
      <el-skeleton animated>
        <template #template>
          <div class="sk-layout">
            <el-skeleton-item variant="image" style="width:420px;height:420px" />
            <div style="flex:1;padding-left:40px">
              <el-skeleton-item variant="text" style="width:60%;height:32px" />
              <el-skeleton-item variant="text" style="width:40%;margin-top:16px" />
              <el-skeleton-item variant="text" style="width:30%;margin-top:12px" />
              <el-skeleton-item variant="text" style="width:80%;margin-top:32px" />
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <div v-else-if="product" class="detail-layout">
      <div class="detail-left">
        <el-image
          :src="product.image || 'https://picsum.photos/seed/' + product.id + '/420/420'"
          :preview-src-list="[product.image || 'https://picsum.photos/seed/' + product.id + '/800/800']"
          fit="cover" class="detail-image"
        />
      </div>
      <div class="detail-right">
        <h1 class="detail-name">{{ product.name }}</h1>
        <div class="detail-tags">
          <el-tag type="danger" size="small">鲜花速递</el-tag>
          <el-tag type="warning" size="small">免费配送</el-tag>
        </div>
        <div class="detail-price">
          <span class="detail-price__current">¥{{ product.price }}</span>
          <span v-if="product.originalPrice" class="detail-price__original">¥{{ product.originalPrice }}</span>
          <el-tag v-if="product.originalPrice" type="danger" size="small">{{ discountText }}</el-tag>
        </div>
        <div class="detail-meta">
          <div class="detail-meta__row">
            <span class="detail-meta__label">分类</span>
            <span class="detail-meta__value">{{ product.category || '-' }}</span>
          </div>
          <div class="detail-meta__row">
            <span class="detail-meta__label">库存</span>
            <span class="detail-meta__value" :class="{ 'text-danger': product.stock <= 10 }">
              {{ product.stock > 0 ? '有货（' + product.stock + '件）' : '暂时缺货' }}
            </span>
          </div>
          <div v-if="product.description" class="detail-meta__row">
            <span class="detail-meta__label">描述</span>
            <span class="detail-meta__value detail-meta__desc">{{ product.description }}</span>
          </div>
        </div>
        <div class="detail-quantity">
          <span class="detail-quantity__label">购买数量</span>
          <el-input-number v-model="quantity" :min="1" :max="product.stock || 99" size="large" />
        </div>
        <div class="detail-actions">
          <el-button type="danger" size="large" round @click="handleBuy">立即购买</el-button>
          <el-button size="large" round @click="handleAddCart">加入购物车</el-button>
        </div>
      </div>
    </div>

    <el-empty v-else description="商品不存在或已下架">
      <el-button type="primary" @click="$router.push('/shop')">返回首页</el-button>
    </el-empty>
  </div>
</template>

<script>
import { getProductDetail } from '../../api/product'
import { addToCart } from '../../api/cart'

export default {
  name: 'ProductDetail',
  data() {
    return { product: null, loading: true, quantity: 1 }
  },
  computed: {
    discountText() {
      if (!this.product || !this.product.originalPrice) return ''
      const pct = Math.round((this.product.price / this.product.originalPrice) * 100)
      return (100 - pct) + '% OFF'
    }
  },
  methods: {
    async loadProduct() {
      const id = this.$route.params.id
      if (!id) { this.$message.error('商品ID无效'); this.loading = false; return }
      try {
        const res = await getProductDetail(id)
        this.product = (res && res.data) ? res.data : null
      } catch { this.product = null }
      finally { this.loading = false }
    },
    handleBuy() {
      // 检查登录
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/shop/login?redirect=' + encodeURIComponent(this.$route.fullPath))
        return
      }
      // 检查库存
      if (this.product.stock <= 0) {
        this.$message.error('商品库存不足')
        return
      }
      // 跳转到下单页，携带直接购买参数
      this.$router.push({
        path: '/shop/order',
        query: {
          directBuy: 'true',
          productId: this.product.id,
          quantity: this.quantity
        }
      })
    },
    async handleAddCart() {
      const token = localStorage.getItem('token')
      if (!token) {
        this.$message.warning('请先登录')
        this.$router.push('/shop/login?redirect=' + encodeURIComponent(this.$route.fullPath))
        return
      }
      if (!this.product || !this.product.id){
        this.$message.error('商品不存在')
        return
      }
      if(this.product.stock <= 0){
        this.$message.error('商品库存不足')
        return
      }
      try{
        await addToCart({
          productId: this.product.id,
          quantity: this.quantity
        })
        this.$message.success('已加入购物车：' + this.product.name + ' × ' + this.quantity)
      }catch(err){
        this.$message.error(err.message || '加入购物车失败')
      }
    }
  },
  created() { this.loadProduct() }
}
</script>

<style scoped>
.product-detail-page { max-width: 1200px; margin: 0 auto; padding: 24px 20px 48px; background: #fff; min-height: 60vh; }
.detail-breadcrumb { margin-bottom: 24px; }
.detail-layout { display: flex; gap: 48px; }
.detail-left { flex-shrink: 0; width: 420px; }
.detail-image { width: 100%; aspect-ratio: 1/1; border-radius: 12px; overflow: hidden; }
.detail-right { flex: 1; display: flex; flex-direction: column; gap: 16px; }
.detail-name { font-size: 24px; font-weight: 700; color: #333; margin: 0; font-family: 'Microsoft YaHei', sans-serif; }
.detail-tags { display: flex; gap: 8px; }
.detail-price { display: flex; align-items: baseline; gap: 10px; padding: 16px; background: #FFF5F5; border-radius: 8px; }
.detail-price__current { font-size: 28px; font-weight: 700; color: #C71526; }
.detail-price__original { font-size: 16px; color: #bbb; text-decoration: line-through; }
.detail-meta { display: flex; flex-direction: column; gap: 10px; padding: 12px 0; border-top: 1px solid #f0f0f0; border-bottom: 1px solid #f0f0f0; }
.detail-meta__row { display: flex; gap: 12px; }
.detail-meta__label { font-size: 14px; color: #999; min-width: 48px; }
.detail-meta__value { font-size: 14px; color: #333; }
.text-danger { color: #C71526; font-weight: 600; }
.detail-quantity { display: flex; align-items: center; gap: 16px; }
.detail-quantity__label { font-size: 14px; color: #999; }
.detail-actions { display: flex; gap: 16px; margin-top: 8px; }
.detail-actions .el-button--danger { background: #C71526; border-color: #C71526; padding: 14px 40px; font-size: 16px; }
.detail-actions .el-button--default { padding: 14px 40px; font-size: 16px; }
@media (max-width: 768px) {
  .detail-layout { flex-direction: column; }
  .detail-left { width: 100%; }
  .detail-name { font-size: 20px; }
}
</style>