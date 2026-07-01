<template>
  <div class="shop-home">
    <!-- ==================== 2. 轮播图区域（全宽） ==================== -->
    <div class="banner-section full-width">
      <el-carousel
        :interval="5000"
        height="420px"
        indicator-position="outside"
        arrow="always"
      >
        <el-carousel-item v-for="(slide, i) in bannerSlides" :key="i">
          <div
            class="banner-slide"
            :style="{ background: slide.bg }"
          >
            <div class="banner-slide__text">
              <h2 class="banner-slide__title">{{ slide.title }}</h2>
              <p class="banner-slide__desc">{{ slide.desc }}</p>
              <el-button
                type="danger"
                round
                size="large"
                class="banner-slide__btn"
                @click="$router.push(slide.link)"
              >
                {{ slide.btnText }}
              </el-button>
            </div>
            <span class="banner-slide__emoji">{{ slide.emoji }}</span>
          </div>
        </el-carousel-item>
      </el-carousel>
    </div>

    <!-- ==================== 3. 分类导航（全宽） ==================== -->
    <div class="category-nav full-width">
      <div class="category-nav__inner">
        <div
          v-for="cat in categoryList"
          :key="cat.id"
          class="category-item"
          :class="{ 'category-item--active': activeCategory === cat.name || (!activeCategory && cat.name === '鲜花') }"
          @click="handleCategoryClick(cat)"
        >
          <div
            class="category-item__icon"
            :style="{ background: cat.bgColor || '#F5F5F5' }"
          >
            <span class="category-item__emoji">{{ cat.emoji || '🌸' }}</span>
          </div>
          <span class="category-item__name">{{ cat.name }}</span>
        </div>
      </div>
    </div>

    <!-- ==================== 4. 搜索结果提示（全宽，条件显示） ==================== -->
    <div v-if="keyword" class="search-hint full-width">
      <div class="search-hint__inner">
        <span class="search-hint__text">
          搜索 '<em>{{ keyword }}</em>' 的结果，共 <strong>{{ productTotal }}</strong> 件商品
        </span>
        <span class="search-hint__clear" @click="clearSearch">清除搜索</span>
      </div>
    </div>

    <!-- ==================== 5. 鲜花推荐区 ==================== -->
    <div class="recommend-section centered">
      <div class="section-title-row">
        <div class="section-title-row__left">
          <h2 class="section-main-title">鲜花推荐</h2>
          <p class="section-main-sub">适合各场合的鲜花佳选</p>
        </div>
        <span class="section-more-link">更多 &gt;</span>
      </div>

      <div class="recommend-grid">
        <!-- 送恋人 - 大卡片 跨2行 -->
        <div
          class="rec-card rec-card--large"
          :style="{ background: '#FFF0F3' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('送恋人'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">送恋人</h3>
            <p class="rec-card__subtitle">你是我的心跳的源泉</p>
            <span class="rec-card__more">更多 &gt;</span>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/lover/556/440" alt="送恋人" />
          </div>
        </div>

        <!-- 送长辈 -->
        <div
          class="rec-card"
          :style="{ background: '#FFF8E6' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('送长辈'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">送长辈</h3>
            <span class="rec-card__more">更多 &gt;</span>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/elder/556/420" alt="送长辈" />
          </div>
        </div>

        <!-- 生日祝福 -->
        <div
          class="rec-card"
          :style="{ background: '#FFE8EE' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('生日祝福'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">生日祝福</h3>
            <span class="rec-card__more">更多 &gt;</span>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/birthday2/556/420" alt="生日祝福" />
          </div>
        </div>

        <!-- 创意DIY -->
        <div
          class="rec-card"
          :style="{ background: '#F5F5F5' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('创意DIY'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">创意DIY</h3>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/diy/556/420" alt="创意DIY" />
          </div>
        </div>

        <!-- 送朋友 -->
        <div
          class="rec-card"
          :style="{ background: '#E8F4FD' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('送朋友'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">送朋友</h3>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/friend/556/420" alt="送朋友" />
          </div>
        </div>

        <!-- 浪漫求婚 -->
        <div
          class="rec-card"
          :style="{ background: '#FFE8E8' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('浪漫求婚'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">浪漫求婚</h3>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/propose/556/420" alt="浪漫求婚" />
          </div>
        </div>

        <!-- 开业大吉 -->
        <div
          class="rec-card"
          :style="{ background: '#E8F8F0' }"
          @click="$router.push('/shop?category=' + encodeURIComponent('开业大吉'))"
        >
          <div class="rec-card__head">
            <h3 class="rec-card__title">开业大吉</h3>
          </div>
          <div class="rec-card__img-box">
            <img class="rec-card__img" src="https://picsum.photos/seed/opening/556/420" alt="开业大吉" />
          </div>
        </div>
      </div>
    </div>

    <!-- ==================== 6. 商品展示区 ==================== -->
    <div class="product-section centered">
      <div class="section-title-row">
        <div class="section-title-row__left">
          <h2 class="section-main-title">全部鲜花</h2>
          <p class="section-main-sub">精选优质鲜花，新鲜直达</p>
        </div>
      </div>

      <!-- 骨架屏 -->
      <div v-if="productLoading" class="product-grid">
        <div v-for="n in 10" :key="'sk-' + n" class="product-card-skeleton">
          <el-skeleton animated>
            <template #template>
              <div class="sk-img"></div>
              <div style="padding: 12px">
                <el-skeleton-item variant="text" style="width: 80%" />
                <el-skeleton-item variant="text" style="width: 50%" />
                <el-skeleton-item variant="text" style="width: 40%" />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else-if="!productLoading && productList.length === 0"
        description="暂无商品数据"
      />

      <!-- 商品网格 -->
      <div v-else class="product-grid">
        <div
          v-for="product in productList"
          :key="product.id"
          class="product-card"
          @click="$router.push('/shop/detail/' + product.id)"
        >
          <div class="product-card__img-box">
            <img
              class="product-card__img"
              :src="product.image || 'https://picsum.photos/seed/' + product.id + '/260/260'"
              :alt="product.name"
            />
          </div>
          <div class="product-card__info">
            <p class="product-card__name">{{ product.name }}</p>
            <p class="product-card__category">{{ product.category }}</p>
            <div class="product-card__bottom">
              <span class="product-card__price">¥{{ product.price }}</span>
              <span v-if="product.originalPrice" class="product-card__orig-price">¥{{ product.originalPrice }}</span>
              <span class="product-card__sales">已售 {{ product.sales || 0 }}件</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div v-if="productTotal > pageSize" class="pagination-box">
        <span
          v-for="p in totalPages"
          :key="p"
          class="pagination-item"
          :class="{ 'pagination-item--active': currentPage === p }"
          @click="handlePageChange(p)"
        >{{ p }}</span>
      </div>
    </div>

    <!-- ==================== 7. 晒单评价区 ==================== -->
    <div class="review-section centered">
      <div class="section-title-row">
        <div class="section-title-row__left">
          <h2 class="section-main-title">晒单评价</h2>
          <p class="section-main-sub">用户实拍晒单</p>
        </div>
        <span class="section-more-link" @click="$router.push('/shop/review')">更多 &gt;</span>
      </div>

      <!-- 骨架屏 -->
      <div v-if="reviewLoading" class="review-grid">
        <div v-for="n in 4" :key="'rsk-' + n" class="review-card-skeleton">
          <el-skeleton animated>
            <template #template>
              <div style="display:flex;align-items:center;gap:10px;margin-bottom:12px">
                <el-skeleton-item variant="circle" style="width:30px;height:30px" />
                <div>
                  <el-skeleton-item variant="text" style="width:40px;height:14px" />
                  <el-skeleton-item variant="text" style="width:60px;height:12px;margin-top:4px" />
                </div>
              </div>
              <el-skeleton-item variant="text" style="width:100%" />
              <el-skeleton-item variant="text" style="width:70%" />
              <div style="margin-top:10px">
                <el-skeleton-item variant="image" style="width:100%;height:140px" />
              </div>
            </template>
          </el-skeleton>
        </div>
      </div>

      <!-- 空状态 -->
      <el-empty
        v-else-if="!reviewLoading && reviewList.length === 0"
        description="暂无用户评价"
      />

      <!-- 评价网格 -->
      <div v-else class="review-grid">
        <div
          v-for="review in reviewList"
          :key="review.id"
          class="review-card"
        >
          <div class="review-card__header">
            <div
              class="review-card__avatar"
              :style="{ background: avatarColor(review.username) }"
            >
              {{ (review.username || '?')[0] }}
            </div>
            <div class="review-card__user-info">
              <span class="review-card__user">{{ review.username }}</span>
              <span class="review-card__date">{{ formatDate(review.createTime) }}</span>
            </div>
          </div>
          <p class="review-card__content">{{ review.content }}</p>
          <div v-if="review.image" class="review-card__img-box">
            <img
              class="review-card__img"
              :src="review.image"
              alt="晒单图片"
            />
          </div>
          <span v-if="review.location" class="review-card__location">{{ review.location }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getProductList, searchProduct, getCategoryList, getReviews } from '../../api/product'

export default {
  name: 'ShopHome',

  data() {
    return {
      keyword: '',

      // ---- 轮播图 ----
      bannerSlides: [
        {
          title: '浪漫玫瑰花束',
          desc: '精选A级玫瑰，传递你的爱意',
          btnText: '立即选购',
          link: '/shop?category=' + encodeURIComponent('送恋人'),
          emoji: '🌹',
          bg: 'linear-gradient(135deg, #FFF0F3 0%, #FFD6DC 100%)'
        },
        {
          title: '感恩康乃馨',
          desc: '温馨祝福，传递深情厚意',
          btnText: '去挑选',
          link: '/shop?category=' + encodeURIComponent('送长辈'),
          emoji: '🌸',
          bg: 'linear-gradient(135deg, #FFF5F0 0%, #FFE8D6 100%)'
        },
        {
          title: '生日庆典花篮',
          desc: '精美花篮，为生日增添喜悦',
          btnText: '去看看',
          link: '/shop?category=' + encodeURIComponent('生日祝福'),
          emoji: '🎂',
          bg: 'linear-gradient(135deg, #FFF8F0 0%, #FFECD6 100%)'
        },
        {
          title: '告白鲜花速递',
          desc: '当日直达，让爱意准时抵达',
          btnText: '立即下单',
          link: '/shop',
          emoji: '💐',
          bg: 'linear-gradient(135deg, #FDF0FF 0%, #F5D6FF 100%)'
        }
      ],

      // ---- 分类 ----
      categoryList: [],
      activeCategory: '',

      fallbackCategories: [
        { id: 'c1', name: '鲜花', emoji: '🌹', bgColor: '#FFE0E6' },
        { id: 'c2', name: '永生花', emoji: '💐', bgColor: '#F5E6FF' },
        { id: 'c3', name: '花束', emoji: '🌷', bgColor: '#FFF0E0' },
        { id: 'c4', name: '花篮', emoji: '🧺', bgColor: '#FFF8E1' },
        { id: 'c5', name: '绿植', emoji: '🌿', bgColor: '#E8F5E9' },
        { id: 'c6', name: '礼品花', emoji: '🎁', bgColor: '#FFE8E8' },
        { id: 'c7', name: '全部', emoji: '📋', bgColor: '#F5F5F5' }
      ],

      // ---- 商品 ----
      productList: [],
      productTotal: 0,
      currentPage: 1,
      pageSize: 10,
      productLoading: false,

      // ---- 评价 ----
      reviewList: [],
      reviewLoading: false,

      // ---- 头像颜色池 ----
      avatarColors: [
        '#FF8A80', '#FFD180', '#EA80FC', '#82B1FF',
        '#80CBC4', '#FF80AB', '#B388FF', '#8C9EFF'
      ]
    }
  },

  computed: {
    totalPages() {
      return Math.max(1, Math.ceil(this.productTotal / this.pageSize))
    }
  },

  watch: {
    '$route.query.keyword': {
      immediate: true,
      handler(val) {
        this.keyword = val || ''
        this.searchInput = val || ''
        this.currentPage = 1
        this.loadProducts()
      }
    },
    '$route.query.category': {
      immediate: true,
      handler(val) {
        this.activeCategory = val || ''
        this.currentPage = 1
        if (!this.keyword) {
          this.loadProducts()
        }
      }
    }
  },

  created() {
    this.loadCategories()
    this.loadReviews()
  },

  methods: {
    clearSearch() {
      const query = { ...this.$route.query }
      delete query.keyword
      this.$router.push({ path: '/shop', query })
    },

    async loadCategories() {
      try {
        const res = await getCategoryList()
        if (res && res.data && res.data.length > 0) {
          const catColors = [
            '#FFE0E6', '#F5E6FF', '#FFF0E0', '#FFF8E1',
            '#E8F5E9', '#FFE8E8', '#F5F5F5'
          ]
          const catEmojis = ['🌹', '💐', '🌷', '🧺', '🌿', '🎁', '📋']
          this.categoryList = res.data.map((c, i) => ({
            ...c,
            emoji: catEmojis[i % catEmojis.length],
            bgColor: catColors[i % catColors.length]
          }))
        } else {
          this.categoryList = [...this.fallbackCategories]
        }
      } catch {
        this.categoryList = [...this.fallbackCategories]
      }
    },

    async loadProducts() {
      this.productLoading = true
      this.productList = []
      try {
        let res
        if (this.keyword) {
          res = await searchProduct(this.keyword, this.currentPage, this.pageSize)
        } else if (this.activeCategory) {
          res = await getProductList({
            page: this.currentPage,
            size: this.pageSize,
            category: this.activeCategory
          })
        } else {
          res = await getProductList({
            page: this.currentPage,
            size: this.pageSize
          })
        }

        if (res && res.data) {
          this.productList = res.data.list || []
          this.productTotal = res.data.total || 0
        }
      } catch {
        this.productList = []
        this.productTotal = 0
      } finally {
        this.productLoading = false
      }
    },

    async loadReviews() {
      this.reviewLoading = true
      try {
        const res = await getReviews(8)
        if (res && res.data) {
          this.reviewList = Array.isArray(res.data) ? res.data : []
        }
      } catch {
        this.reviewList = []
      } finally {
        this.reviewLoading = false
      }
    },

    handleCategoryClick(cat) {
      const query = { ...this.$route.query }
      if (cat.name === '全部') {
        delete query.category
      } else {
        query.category = cat.name
      }
      delete query.keyword
      this.$router.push({ path: '/shop', query })
    },

    handlePageChange(page) {
      this.currentPage = page
      this.loadProducts()
      const el = document.querySelector('.product-section')
      if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' })
    },

    avatarColor(username) {
      if (!username) return this.avatarColors[0]
      let hash = 0
      for (let i = 0; i < username.length; i++) {
        hash = username.charCodeAt(i) + ((hash << 5) - hash)
      }
      return this.avatarColors[Math.abs(hash) % this.avatarColors.length]
    },

    formatDate(dateStr) {
      if (!dateStr) return ''
      const d = new Date(dateStr)
      if (isNaN(d.getTime())) return dateStr
      const y = d.getFullYear()
      const m = String(d.getMonth() + 1).padStart(2, '0')
      const day = String(d.getDate()).padStart(2, '0')
      return `${y}-${m}-${day}`
    }
  }
}
</script>

<style scoped>
/* ==================== 全局重置 ==================== */
.shop-home {
  min-height: 100vh;
}

/* ==================== 全宽 / 居中 容器 ==================== */
.full-width {
  margin-left: calc(-50vw + 50%);
}
.centered {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 0;
}

/* ==================== 区块标题行 ==================== */
.section-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;
  padding: 0 20px;
}
.section-title-row__left {
  display: flex;
  align-items: baseline;
  gap: 12px;
}
.section-main-title {
  font-size: 24px;
  font-weight: 700;
  color: #333;
  margin: 0;
  line-height: 1.3;
}
.section-main-sub {
  font-size: 14px;
  color: #999;
  margin: 0;
}
.section-more-link {
  font-size: 14px;
  color: #C71526;
  cursor: pointer;
  user-select: none;
}
.section-more-link:hover {
  text-decoration: underline;
}


/* ==================== 2. 轮播图 ==================== */
.banner-section {
  overflow: hidden;
}
.banner-section :deep(.el-carousel__indicators--outside) {
  margin-top: 8px;
}
.banner-section :deep(.el-carousel__indicators--outside .el-carousel__button) {
  background-color: #ddd;
  width: 38px;
  height: 4px;
  border-radius: 2px;
}
.banner-section :deep(.el-carousel__indicator.is-active .el-carousel__button) {
  background-color: #C71526;
}
.banner-section :deep(.el-carousel__arrow) {
  background-color: rgba(255,255,255,0.7);
  color: #C71526;
  border-radius: 50%;
  width: 40px;
  height: 40px;
}
.banner-section :deep(.el-carousel__arrow:hover) {
  background-color: rgba(255,255,255,0.95);
}

.banner-slide {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  padding: 0 120px;
  box-sizing: border-box;
}
.banner-slide__text {
  flex: 1;
}
.banner-slide__title {
  font-size: 42px;
  font-weight: 700;
  color: #C71526;
  margin: 0 0 12px 0;
  line-height: 1.2;
  font-family: 'Microsoft YaHei', sans-serif;
}
.banner-slide__desc {
  font-size: 18px;
  color: #666;
  margin: 0 0 24px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.banner-slide__btn {
  font-size: 16px;
  padding: 12px 32px;
}
.banner-slide__emoji {
  font-size: 105px;
  line-height: 1;
  flex-shrink: 0;
  margin-left: 60px;
}

/* ==================== 3. 分类导航 ==================== */
.category-nav {
  background: #fff;
  border-bottom: 1px solid #f0f0f0;
}
.category-nav__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: center;
  flex-wrap: wrap;
  gap: 24px;
  padding: 25px 20px;
}
.category-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s;
  user-select: none;
  width: 80px;
}
.category-item:hover {
  transform: translateY(-4px);
}
.category-item--active .category-item__name {
  color: #C71526;
  font-weight: 700;
}
.category-item__icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.category-item__emoji {
  font-size: 23px;
  line-height: 1;
}
.category-item__name {
  font-size: 13px;
  color: #666;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* ==================== 4. 搜索提示 ==================== */
.search-hint {
  background: #FFF5F5;
  border-bottom: 1px solid #FFE0E0;
}
.search-hint__inner {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 44px;
  padding: 0 20px;
}
.search-hint__text {
  font-size: 14px;
  color: #666;
  font-family: 'Microsoft YaHei', sans-serif;
}
.search-hint__text em {
  font-style: normal;
  color: #C71526;
  font-weight: 600;
}
.search-hint__text strong {
  color: #C71526;
}
.search-hint__clear {
  font-size: 13px;
  color: #C71526;
  cursor: pointer;
  font-family: 'Microsoft YaHei', sans-serif;
}
.search-hint__clear:hover {
  text-decoration: underline;
}

/* ==================== 5. 推荐网格（4列，首列跨2行） ==================== */
.recommend-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  padding: 0 20px;
}

.rec-card {
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
}
.rec-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 32px rgba(0,0,0,0.1);
}
.rec-card--large {
  grid-row: span 2;
}
.rec-card__head {
  padding: 20px 16px 12px;
}
.rec-card__title {
  font-size: 18px;
  font-weight: 700;
  color: #333;
  margin: 0 0 4px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.rec-card__subtitle {
  font-size: 13px;
  color: #888;
  margin: 0 0 8px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.rec-card__more {
  font-size: 13px;
  color: #C71526;
  display: inline-block;
  font-family: 'Microsoft YaHei', sans-serif;
}
.rec-card__img-box {
  flex: 1;
  overflow: hidden;
  min-height: 150px;
}
.rec-card--large .rec-card__img-box {
  min-height: 300px;
}
.rec-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* ==================== 6. 商品展示区 ==================== */
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  padding: 0 20px;
}

.product-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}
.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(199,21,38,0.1);
}
.product-card__img-box {
  aspect-ratio: 1 / 1;
  overflow: hidden;
}
.product-card__img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.4s;
}
.product-card:hover .product-card__img {
  transform: scale(1.05);
}
.product-card__info {
  padding: 12px;
}
.product-card__name {
  font-size: 14px;
  color: #333;
  margin: 0 0 4px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-family: 'Microsoft YaHei', sans-serif;
}
.product-card__category {
  font-size: 12px;
  color: #999;
  margin: 0 0 8px 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.product-card__bottom {
  display: flex;
  align-items: baseline;
  gap: 6px;
}
.product-card__price {
  font-size: 15px;
  font-weight: 700;
  color: #C71526;
  font-family: 'Microsoft YaHei', sans-serif;
}
.product-card__orig-price {
  font-size: 11px;
  color: #bbb;
  text-decoration: line-through;
  font-family: 'Microsoft YaHei', sans-serif;
}
.product-card__sales {
  font-size: 12px;
  color: #999;
  margin-left: auto;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 骨架屏 */
.product-card-skeleton {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
}
.product-card-skeleton .sk-img {
  aspect-ratio: 1 / 1;
  background: #f5f5f5;
}

/* 分页 */
.pagination-box {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-top: 40px;
}
.pagination-item {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 4px;
  font-size: 14px;
  color: #303133;
  background: #f4f4f5;
  cursor: pointer;
  user-select: none;
  font-family: 'Microsoft YaHei', sans-serif;
  transition: all 0.2s;
}
.pagination-item:hover {
  color: #C71526;
}
.pagination-item--active {
  background: #C71526;
  color: #fff;
  font-weight: 700;
}

/* ==================== 7. 评价区 ==================== */
.review-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 0 20px;
}

.review-card {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 16px;
  transition: box-shadow 0.3s;
}
.review-card:hover {
  box-shadow: 0 4px 16px rgba(0,0,0,0.06);
}
.review-card__header {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 12px;
}
.review-card__avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  font-family: 'Microsoft YaHei', sans-serif;
}
.review-card__user-info {
  display: flex;
  flex-direction: column;
}
.review-card__user {
  font-size: 13px;
  font-weight: 700;
  color: #333;
  font-family: 'Microsoft YaHei', sans-serif;
}
.review-card__date {
  font-size: 10px;
  color: #bbb;
  margin-top: 2px;
  font-family: 'Microsoft YaHei', sans-serif;
}
.review-card__content {
  font-size: 13px;
  color: #555;
  line-height: 1.6;
  margin: 0 0 10px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-family: 'Microsoft YaHei', sans-serif;
}
.review-card__img-box {
  width: 100%;
  overflow: hidden;
  border-radius: 6px;
  margin-bottom: 4px;
}
.review-card__img {
  width: 100%;
  aspect-ratio: 4 / 3;
  object-fit: cover;
  display: block;
}
.review-card__location {
  font-size: 11px;
  color: #999;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 评价骨架屏 */
.review-card-skeleton {
  background: #fff;
  border: 1px solid #f0f0f0;
  border-radius: 10px;
  padding: 16px;
}


/* ==================== el-empty 定制 ==================== */
.centered :deep(.el-empty__description) {
  color: #999;
}

/* ==================== 响应式 ==================== */
@media (max-width: 1024px) {
  .header-nav { gap: 10px; }
  .header-nav__item { font-size: 13px; }
  .header-search { max-width: 140px; }
  .header-actions__item { font-size: 12px; padding: 4px 8px; }
  .header-logo__cn { font-size: 20px; }
  .header-logo__en { font-size: 8px; }

  .product-grid { grid-template-columns: repeat(3, 1fr); }
  .review-grid { grid-template-columns: repeat(2, 1fr); }
  .recommend-grid {
    grid-template-columns: 1fr 1fr;
  }
  .rec-card--large { grid-row: auto; }
  .banner-slide { padding: 0 40px; }
  .banner-slide__emoji { font-size: 70px; margin-left: 20px; }
  .banner-slide__title { font-size: 28px; }
  .trust-bar__inner { gap: 24px; }
  .service-bar__inner { height: auto; padding: 24px 20px; flex-wrap: wrap; gap: 24px; }
  .footer__main { gap: 40px; flex-wrap: wrap; }
}

@media (max-width: 768px) {
  .header-main__inner { flex-wrap: wrap; height: auto; padding: 12px 20px; gap: 12px; }
  .header-nav { order: 3; width: 100%; justify-content: center; }
  .header-search { order: 4; max-width: 100%; width: 100%; }
  .header-actions { order: 2; }
  .header-top__inner { padding: 0 10px; }
  .header-top__welcome { display: none; }

  .product-grid { grid-template-columns: repeat(2, 1fr); }
  .review-grid { grid-template-columns: 1fr; }
  .recommend-grid { grid-template-columns: 1fr; }

  .banner-slide { flex-direction: column; justify-content: center; text-align: center; padding: 0 24px; }
  .banner-slide__emoji { font-size: 50px; margin-left: 0; margin-top: 16px; }
  .banner-slide__title { font-size: 24px; }
  .banner-slide__desc { font-size: 14px; }

  .trust-bar__inner { flex-wrap: wrap; gap: 16px; height: auto; padding: 12px 10px; }
  .category-nav__inner { gap: 12px; padding: 16px 10px; }
  .category-item { width: 64px; }
  .category-item__icon { width: 48px; height: 48px; }
  .category-item__emoji { font-size: 18px; }

  .service-bar__inner { flex-direction: column; align-items: flex-start; }
  .footer__main { flex-direction: column; gap: 24px; }
}
</style>
