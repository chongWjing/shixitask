<template>
  <div>
    <el-card shadow="never" style="margin-bottom:20px;border-radius:12px">
      <h3 style="margin:0 0 8px;font-size:20px">欢迎回来，{{ adminName }}！</h3>
      <p style="color:#999;margin:0;font-size:14px">今天是 {{ todayStr }}，祝您工作愉快！</p>
    </el-card>
    <el-row :gutter="16" style="margin-bottom:20px">
      <el-col :span="6" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: card.bg }"><el-icon :size="24" color="#fff"><component :is="card.icon" /></el-icon></div>
          <div class="stat-info"><div class="stat-label">{{ card.label }}</div><div class="stat-value">{{ card.value }}</div></div>
        </el-card>
      </el-col>
    </el-row>
    <el-card shadow="never" style="border-radius:12px">
      <h5 style="margin:0 0 16px;color:#333">快捷操作</h5>
      <el-button type="primary" style="background:#C71526;border-color:#C71526" @click="$router.push('/admin/product')">商品管理</el-button>
      <el-button type="warning" @click="$router.push('/admin/order')">订单管理</el-button>
      <el-button type="info" @click="loadStats">刷新数据</el-button>
    </el-card>
  </div>
</template>

<script>
import { getStatsOverview } from '../../api/admin'

export default {
  name: 'AdminDashboard',
  data() { return { stats: { productCount: '--', todayOrderCount: '--', totalSales: '--', userCount: '--' } } },
  computed: {
    adminName() { return localStorage.getItem('username') || '管理员' },
    todayStr() {
      const d = new Date(), w = ['星期日','星期一','星期二','星期三','星期四','星期五','星期六']
      return d.getFullYear() + '年' + (d.getMonth()+1) + '月' + d.getDate() + '日 ' + w[d.getDay()]
    },
    statCards() {
      return [
        { label: '商品总数', value: this.stats.productCount, bg: 'linear-gradient(135deg,#C71526,#8B0A1A)', icon: 'Goods' },
        { label: '今日订单', value: this.stats.todayOrderCount, bg: 'linear-gradient(135deg,#E8475F,#C71526)', icon: 'List' },
        { label: '总销售额', value: '¥' + this.stats.totalSales, bg: 'linear-gradient(135deg,#FF6B81,#E8475F)', icon: 'Money' },
        { label: '用户总数', value: this.stats.userCount, bg: 'linear-gradient(135deg,#D4A574,#B8860B)', icon: 'User' }
      ]
    }
  },
  methods: {
    async loadStats() {
      try {
        const result = await getStatsOverview()
        if (result.code === 200) this.stats = result.data
      } catch (e) { console.error('加载统计失败', e) }
    }
  },
  created() { this.loadStats() }
}
</script>

<style scoped>
.stat-card { display:flex;align-items:center;gap:16px;padding:20px;border-radius:12px; }
.stat-icon { width:56px;height:56px;border-radius:12px;display:flex;align-items:center;justify-content:center;flex-shrink:0; }
.stat-label { font-size:13px;color:#999;margin-bottom:4px; }
.stat-value { font-size:28px;font-weight:700;color:#333; }
</style>
