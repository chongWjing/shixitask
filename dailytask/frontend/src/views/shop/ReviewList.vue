<template>
  <div class="review-list-page">
    <div class="page-header">
      <h1 class="page-header__title">晒单评价</h1>
      <p class="page-header__sub">共 {{ reviewList.length }} 条真实用户评价</p>
    </div>

    <div v-if="loading" class="review-grid">
      <div v-for="n in 8" :key="'rsk-' + n" class="review-card-skeleton">
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

    <el-empty v-else-if="reviewList.length === 0" description="暂无用户评价" />

    <div v-else class="review-grid">
      <div v-for="review in reviewList" :key="review.id" class="review-card">
        <div class="review-card__header">
          <div class="review-card__avatar" :style="{ background: avatarColor(review.username) }">
            {{ (review.username || '?')[0] }}
          </div>
          <div class="review-card__user-info">
            <span class="review-card__user">{{ review.username }}</span>
            <div class="review-card__rating-row">
              <el-rate :model-value="review.rating || 5" disabled show-score text-color="#ff9900" size="small" />
            </div>
            <span class="review-card__date">{{ formatDate(review.createTime) }}</span>
          </div>
        </div>
        <p class="review-card__content">{{ review.content }}</p>
        <div v-if="review.image" class="review-card__img-box">
          <img class="review-card__img" :src="review.image" alt="晒单图片" />
        </div>
        <span v-if="review.location" class="review-card__location">{{ review.location }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { getReviewList } from '../../api/product'

export default {
  name: 'ReviewList',
  data() {
    return {
      reviewList: [],
      loading: false,
      avatarColors: ['#FF8A80','#FFD180','#EA80FC','#82B1FF','#80CBC4','#FF80AB','#B388FF','#8C9EFF']
    }
  },
  methods: {
    async loadReviews() {
      this.loading = true
      try {
        const res = await getReviewList()
        this.reviewList = Array.isArray(res.data) ? res.data : []
      } catch { this.reviewList = [] }
      finally { this.loading = false }
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
  },
  created() { this.loadReviews() }
}
</script>

<style scoped>
.review-list-page { max-width: 1200px; margin: 0 auto; padding: 32px 20px 48px; background: #fff; min-height: 60vh; }
.page-header { text-align: center; margin-bottom: 28px; }
.page-header__title { font-size: 24px; font-weight: 700; color: #333; margin: 0 0 8px; }
.page-header__sub { font-size: 14px; color: #999; margin: 0; }
.review-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; }
.review-card { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 16px; transition: box-shadow 0.3s; }
.review-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,0.06); }
.review-card__header { display: flex; align-items: flex-start; gap: 10px; margin-bottom: 12px; }
.review-card__avatar { width: 30px; height: 30px; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 13px; font-weight: 700; flex-shrink: 0; }
.review-card__user-info { display: flex; flex-direction: column; }
.review-card__user { font-size: 13px; font-weight: 700; color: #333; }
.review-card__rating-row { margin: 2px 0; }
.review-card__date { font-size: 10px; color: #bbb; }
.review-card__content { font-size: 13px; color: #555; line-height: 1.6; margin: 0 0 10px; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.review-card__img-box { width: 100%; overflow: hidden; border-radius: 6px; margin-bottom: 4px; }
.review-card__img { width: 100%; aspect-ratio: 4/3; object-fit: cover; display: block; }
.review-card__location { font-size: 11px; color: #999; }
.review-card-skeleton { background: #fff; border: 1px solid #f0f0f0; border-radius: 10px; padding: 16px; }
@media (max-width: 1024px) { .review-grid { grid-template-columns: repeat(2, 1fr); } }
@media (max-width: 768px) { .review-grid { grid-template-columns: 1fr; } }
</style>