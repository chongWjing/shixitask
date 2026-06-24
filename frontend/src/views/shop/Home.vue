<!-- 商城首页 -->
<template>
  <div class="shop-home">
    <!-- Hero 轮播区 -->
    <div class="hero-banner">
      <div class="hero-content">
        <h1 class="hero-title">🌸 花之恋商城</h1>
        <p class="hero-desc">用AI为你的生活增添一份浪漫</p>
        <p class="hero-sub">Spring AI + Vue3 智能鲜花电商平台</p>
      </div>
    </div>

    <!-- 分类导航 -->
    <section class="section">
      <h2 class="section-title">🏷️ 鲜花分类</h2>
      <div class="category-grid">
        <div v-for="cat in categories" :key="cat.name" class="category-card" @click="$router.push('/shop/cart')">
          <span class="cat-icon">{{ cat.icon }}</span>
          <span class="cat-name">{{ cat.name }}</span>
        </div>
      </div>
    </section>

    <!-- 热门商品 -->
    <section class="section">
      <h2 class="section-title">🔥 热门推荐</h2>
      <div class="product-grid">
        <el-card v-for="p in products" :key="p.id" class="product-card" shadow="hover"
                 @click="$router.push(`/shop/product/${p.id}`)">
          <div class="product-img">{{ p.icon }}</div>
          <div class="product-info">
            <h3 class="product-name">{{ p.name }}</h3>
            <p class="product-desc">{{ p.desc }}</p>
            <div class="product-footer">
              <span class="product-price">¥{{ p.price }}</span>
              <el-button type="danger" size="small" @click.stop="$router.push('/shop/cart')">加入购物车</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </section>

    <!-- 用户评价 -->
    <section class="section">
      <h2 class="section-title">💬 用户评价</h2>
      <div class="review-list">
        <el-card v-for="r in reviews" :key="r.user" class="review-card">
          <div class="review-header">
            <span class="review-avatar">{{ r.avatar }}</span>
            <span class="review-user">{{ r.user }}</span>
            <span class="review-stars">{{ '⭐'.repeat(r.stars) }}</span>
          </div>
          <p class="review-text">{{ r.text }}</p>
        </el-card>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: 'ShopHome',
  data() {
    return {
      categories: [
        { name: '玫瑰', icon: '🌹' },
        { name: '百合', icon: '🌷' },
        { name: '向日葵', icon: '🌻' },
        { name: '康乃馨', icon: '🌺' },
        { name: '满天星', icon: '✨' },
        { name: '绣球花', icon: '💠' }
      ],
      products: [
        { id: 1, name: '热恋红玫瑰', desc: '11朵红玫瑰，代表一心一意', price: 99, icon: '🌹' },
        { id: 2, name: '阳光向日葵', desc: '6朵向日葵，传递正能量', price: 79, icon: '🌻' },
        { id: 3, name: '纯洁百合', desc: '10朵白百合，象征纯洁', price: 129, icon: '🌷' },
        { id: 4, name: '感恩康乃馨', desc: '19朵康乃馨，感恩母亲', price: 89, icon: '🌺' },
        { id: 5, name: '浪漫满天星', desc: '一大束满天星，梦幻浪漫', price: 69, icon: '✨' },
        { id: 6, name: '混搭花束', desc: '玫瑰+百合+满天星混搭', price: 159, icon: '💐' }
      ],
      reviews: [
        { user: '张同学', avatar: '👩', stars: 5, text: '花很新鲜，包装也很精美！女朋友特别喜欢，下次还会回购 🌸' },
        { user: '李同学', avatar: '👨', stars: 5, text: '配送很快，下单后2小时就收到了，品质超出预期！' },
        { user: '王同学', avatar: '👩', stars: 4, text: '花束搭配很好看，就是价格可以再便宜一点点～' }
      ]
    }
  }
}
</script>

<style scoped>
.shop-home {
  padding-bottom: 20px;
}

/* Hero 区 */
.hero-banner {
  background: linear-gradient(135deg, #FFE0E6, #FFB6C1, #C71526);
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  margin-bottom: 30px;
}
.hero-title {
  font-size: 36px;
  color: #fff;
  margin: 0 0 12px;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
}
.hero-desc {
  font-size: 18px;
  color: rgba(255,255,255,0.9);
  margin: 0 0 8px;
}
.hero-sub {
  font-size: 14px;
  color: rgba(255,255,255,0.7);
  margin: 0;
}

/* 通用 Section */
.section {
  margin-bottom: 30px;
}
.section-title {
  font-size: 22px;
  color: #333;
  margin: 0 0 16px;
  padding-left: 12px;
  border-left: 4px solid #C71526;
}

/* 分类卡片 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
}
.category-card {
  background: #fff;
  border: 1px solid #eee;
  border-radius: 12px;
  padding: 20px 12px;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s;
}
.category-card:hover {
  border-color: #C71526;
  box-shadow: 0 4px 12px rgba(199, 21, 38, 0.15);
  transform: translateY(-2px);
}
.cat-icon {
  font-size: 32px;
  display: block;
  margin-bottom: 8px;
}
.cat-name {
  font-size: 14px;
  color: #333;
}

/* 商品卡片 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.product-card {
  cursor: pointer;
  transition: transform 0.2s;
}
.product-card:hover {
  transform: translateY(-4px);
}
.product-card :deep(.el-card__body) {
  padding: 16px;
}
.product-img {
  font-size: 64px;
  text-align: center;
  padding: 20px 0;
  background: #FFF5F5;
  border-radius: 8px;
  margin-bottom: 12px;
}
.product-name {
  margin: 0 0 6px;
  font-size: 16px;
  color: #333;
}
.product-desc {
  margin: 0 0 12px;
  font-size: 13px;
  color: #999;
}
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #C71526;
}

/* 评价 */
.review-list {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}
.review-card :deep(.el-card__body) {
  padding: 16px;
}
.review-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}
.review-avatar {
  font-size: 24px;
}
.review-user {
  font-weight: 500;
  color: #333;
}
.review-stars {
  margin-left: auto;
  font-size: 12px;
}
.review-text {
  margin: 0;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

@media (max-width: 768px) {
  .category-grid { grid-template-columns: repeat(3, 1fr); }
  .product-grid { grid-template-columns: 1fr; }
  .review-list { grid-template-columns: 1fr; }
}
</style>
