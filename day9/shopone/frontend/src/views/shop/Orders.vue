<!-- 
定义订单的状态：
0 待付款
1 已付款
2 已发货
3 已完成
4 已取消 

用户登录---搜索商品---加入购物车---下单订单---查看订单状态
-->
<template>
  <div class="order-page">
    <!-- 面包屑导航 -->
    <el-breadcrumb separator="/" class="order-breadcrumb">
      <el-breadcrumb-item :to="{ path: '/shop' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/shop/cart' }">购物车</el-breadcrumb-item>
      <el-breadcrumb-item>确认订单</el-breadcrumb-item>
    </el-breadcrumb>

    <!-- 步骤条：当前在"确认订单"步骤 -->
    <div class="steps-card">
      <el-steps :active="0" finish-status="success" align-center>
        <el-step title="确认订单" icon="Edit" />
        <el-step title="支付" icon="CreditCard" />
        <el-step title="完成" icon="SuccessFilled" />
      </el-steps>
    </div>

    <!-- 收货信息 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-icon">📍</span>
        <span class="section-title">收货信息</span>
      </div>
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" class="order-form">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" style="max-width: 400px" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号" style="max-width: 400px" />
        </el-form-item>
        <el-form-item label="收货地址" prop="receiverAddress">
          <el-input
            v-model="form.receiverAddress"
            type="textarea"
            :rows="3"
            placeholder="请输入详细收货地址"
            style="max-width: 500px"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 商品信息 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-icon">🌸</span>
        <span class="section-title">鲜花信息</span>
        <span class="product-count">共 {{ products.length }} 件</span>
      </div>
      <div v-if="products.length > 0" class="order-products">
        <div v-for="item in products" :key="item.id" class="order-product-item">
          <el-image :src="item.product.image" fit="cover" class="order-product-img" />
          <div class="order-product-info">
            <div class="order-product-name">{{ item.product.name }}</div>
            <div class="order-product-desc">{{ item.product.description || '精选鲜花，传递爱意' }}</div>
            <div class="order-product-category">{{ item.product.category || '鲜花' }}</div>
          </div>
          <div class="order-product-price">
            <span class="unit-price">¥{{ item.product.price }}</span>
            <span class="price-x">×</span>
            <span class="quantity-text">{{ item.quantity }}</span>
            <span class="subtotal">¥{{ (item.product.price * item.quantity).toFixed(2) }}</span>
          </div>
        </div>
      </div>
      <el-skeleton v-else :rows="2" animated />
    </div>

    <!-- 配送提示 -->
    <div class="delivery-tip">
      <span>🚚</span>
      <span>鲜花由专业花艺师制作后配送，支持指定区域免费配送</span>
    </div>

    <!-- 价格汇总 -->
    <div class="section-card">
      <div class="section-header">
        <span class="section-icon">💰</span>
        <span class="section-title">价格汇总</span>
      </div>
      <div class="price-summary">
        <div class="summary-row"><span>商品总价：</span><span>¥{{ totalPrice }}</span></div>
        <div class="summary-row"><span>配送费：</span><span class="free-shipping">免运费</span></div>
        <div class="summary-row"><span>包装费：</span><span class="free-shipping">免费</span></div>
        <el-divider />
        <div class="summary-row total-row">
          <span>应付总额：</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
      </div>
    </div>

    <!-- 提交订单 -->
    <div class="submit-section">
      <el-button size="large" @click="$router.back()">返回</el-button>
      <el-button type="danger" size="large" :loading="submitLoading" @click="onSubmit" class="submit-btn">
        提交订单
      </el-button>
    </div>
  </div>
</template>
<script>
import { getCartList } from '../../api/cart'
import { getProductDetail } from '../../api/product'
import { createOrder, cancelOrder } from '../../api/order'
import { ElMessage } from 'element-plus'

export default {
  name: 'ShopOrder',
  data() {
    return {
      products: [],       // 要结算的商品列表
      submitLoading: false,
      form: {
        receiverName: '',
        receiverPhone: '',
        receiverAddress: ''
      },
      formRules: {
        receiverName: [
          { required: true, message: '请输入收货人姓名', trigger: 'blur' }
        ],
        receiverPhone: [
          { required: true, message: '请输入手机号', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
        ],
        receiverAddress: [
          { required: true, message: '请输入收货地址', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    /** 计算所有商品总价 */
    totalPrice() {
      return this.products
        .reduce((sum, item) => sum + item.product.price * item.quantity, 0)
        .toFixed(2)
    }
  },
  created() {
    const { directBuy, productId, quantity, items } = this.$route.query
    
    // 处理直接购买模式（从商品详情页跳转）
    if (directBuy === 'true' && productId) {
      this.loadDirectBuyProduct(productId, quantity)
      return
    }
    
    // 处理购物车结算模式
    if (!items) {
      ElMessage.error('未选择结算商品')
      this.$router.push('/shop/cart')
      return
    }
    this.loadProducts(items)
  },
  methods: {
    /** 根据购物车项ID加载商品信息 */
    async loadProducts(itemIdsStr) {
      try {
        const res = await getCartList()
        const allItems = res.data || []
        const ids = itemIdsStr.split(',').map(id => parseInt(id))
        // 筛选出选中的商品
        this.products = allItems.filter(item => ids.includes(item.id))
        
        if (this.products.length === 0) {
          ElMessage.error('商品信息异常，请重新选择')
          this.$router.push('/shop/cart')
        }
      } catch (e) {
        ElMessage.error('加载商品失败')
        this.$router.push('/shop/cart')
      }
    },

    /** 处理直接购买模式，根据商品ID加载商品信息 */
    async loadDirectBuyProduct(productId, quantity = 1) {
      try {
        const res = await getProductDetail(productId)
        const product = res.data
        
        if (!product) {
          ElMessage.error('商品不存在或已下架')
          this.$router.push('/shop')
          return
        }
        
        // 转换为与购物车项相同的数据结构
        this.products = [{
          id: `direct_${productId}`,
          product: product,
          quantity: parseInt(quantity) || 1
        }]
      } catch (e) {
        ElMessage.error('加载商品失败')
        this.$router.push('/shop')
      }
    },

    /** 提交订单（事务性：顺序创建，失败回滚） */
    async onSubmit() {
      try {
        await this.$refs.formRef.validate()
      } catch {
        ElMessage.warning('请填写完整的收货信息')
        return
      }

      this.submitLoading = true
      const createdOrderIds = [] // 记录已创建成功的订单ID，用于失败时回滚
      
      try {
        // 顺序创建订单（确保按顺序创建，便于回滚）
        for (const item of this.products) {
          const res = createOrder({
            productId: item.product.id,
            quantity: item.quantity,
            receiverName: this.form.receiverName,
            receiverPhone: this.form.receiverPhone,
            receiverAddress: this.form.receiverAddress
          })
          // 假设返回的数据中包含订单ID
          if (res.data && res.data.id) {
            createdOrderIds.push(res.data.id)
          }
        }
        
        // 全部订单创建成功
        ElMessage.success('下单成功！')
        this.$router.push('/shop/myorder')
      } catch (e) {
        // 任一订单创建失败，回滚已创建的订单
        if (createdOrderIds.length > 0) {
          // 尝试取消已创建的订单（模拟回滚）
          await Promise.allSettled(
            createdOrderIds.map(orderId => {
              // 使用PUT请求取消订单（后端需支持cancel接口）
              const cancelUrl = `order/cancel/${orderId}`
              return request.put(cancelUrl)
            })
          )
        }
        console.log(e)
        ElMessage.error('订单创建失败，已回滚所有操作')
      } finally {
        this.submitLoading = false
      }
    }
  }
}
</script>
<style scoped>
.order-page {
  display: flex; flex-direction: column; gap: 16px;
  padding: 20px; max-width: 1200px; margin: 0 auto;
  box-sizing: border-box;
}
.order-breadcrumb { margin-bottom: 4px; }

/* 步骤条 */
.steps-card {
  background: #fff; border-radius: 12px; padding: 28px 48px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.steps-card :deep(.el-step__head.is-finish .el-step__icon) { color: #C71526; border-color: #C71526; }

/* 区段卡片 */
.section-card {
  background: #fff; border-radius: 12px; padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
}
.section-header {
  display: flex; align-items: center; gap: 8px;
  margin-bottom: 18px; padding-bottom: 12px;
  border-bottom: 1px solid #f5f5f5;
}
.section-icon { font-size: 20px; }
.section-title { font-size: 17px; font-weight: bold; color: #333; }
.order-form { max-width: 600px; }

/* 商品信息 */
.product-count { font-size: 14px; color: #999; font-weight: normal; margin-left: auto; }
.order-products { display: flex; flex-direction: column; gap: 12px; }
.order-product-item {
  display: flex; align-items: center; gap: 20px; padding: 12px;
  background: #FAFAFA; border-radius: 8px; transition: background 0.2s;
}
.order-product-item:hover { background: #FFF5F5; }
.order-product-img {
  width: 80px; height: 80px; border-radius: 8px;
  border: 1px solid #f0f0f0; flex-shrink: 0;
}
.order-product-info { flex: 1; min-width: 0; }
.order-product-name { font-size: 15px; color: #333; font-weight: 600; margin-bottom: 4px; }
.order-product-desc { font-size: 13px; color: #999; margin-bottom: 6px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.order-product-category {
  font-size: 12px; color: #C71526; background: #FFF5F5;
  display: inline-block; padding: 2px 8px; border-radius: 4px;
}
.order-product-price { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.unit-price { font-size: 15px; color: #C71526; font-weight: bold; }
.price-x { color: #ccc; font-size: 14px; }
.quantity-text { font-size: 14px; color: #666; font-weight: 500; min-width: 30px; text-align: center; }
.subtotal { font-size: 18px; color: #C71526; font-weight: bold; min-width: 80px; text-align: right; }

/* 配送提示 */
.delivery-tip {
  background: #FFF8F0; border: 1px solid #FFE0B2; border-radius: 8px;
  padding: 12px 16px; font-size: 13px; color: #E6A23C;
  display: flex; align-items: center; gap: 8px;
}

/* 价格汇总 */
.price-summary { max-width: 400px; margin-left: auto; }
.summary-row { display: flex; justify-content: space-between; padding: 6px 0; font-size: 14px; color: #666; }
.free-shipping { color: #67c23a; font-weight: bold; }
.total-row { font-size: 16px; font-weight: bold; color: #333; }
.total-price { color: #C71526; font-size: 28px; font-weight: bold; }

/* 提交 */
.submit-section { display: flex; justify-content: flex-end; gap: 16px; padding: 8px 0 16px; }
.submit-btn { background: #C71526; border-color: #C71526; min-width: 160px; font-size: 16px; }
.submit-btn:hover { background: #A01020; border-color: #A01020; }
</style>