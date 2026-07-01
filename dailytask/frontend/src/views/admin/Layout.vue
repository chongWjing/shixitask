<template>
  <el-container style="height: 100vh">
    <el-aside width="220px" style="background-color: #8B0A1A">
      <div class="sidebar-logo">🌸 花之恋后台</div>
      <el-menu :default-active="activeMenu" background-color="#8B0A1A" text-color="#F5C6CB" active-text-color="#FF6B81" router>
        <el-menu-item index="/admin/dashboard"><el-icon><DataBoard /></el-icon><span>首页概览</span></el-menu-item>
        <el-menu-item index="/admin/product"><el-icon><Goods /></el-icon><span>商品管理</span></el-menu-item>
        <el-menu-item index="/admin/order"><el-icon><List /></el-icon><span>订单管理</span></el-menu-item>
        <el-menu-item index="/admin/ai"><el-icon><ChatDotRound /></el-icon><span>智能查询</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:linear-gradient(135deg,#C71526,#8B0A1A);display:flex;align-items:center;justify-content:space-between;padding:0 20px">
        <div style="display:flex;align-items:center;color:#fff">
          <span style="font-size:20px;font-weight:700">🌸 花之恋后台</span>
        </div>
        <div style="display:flex;align-items:center;gap:16px;color:#fff">
          <span>{{ adminName }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main style="background:#FFF8F8;overflow-y:auto"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script>
export default {
  name: 'AdminLayout',
  computed: {
    activeMenu() { return this.$route.path },
    adminName() { return localStorage.getItem('username') || '管理员' }
  },
  methods: {
    handleLogout() {
      this.$confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
        localStorage.removeItem('token'); localStorage.removeItem('username'); localStorage.removeItem('role')
        this.$router.push('/admin/login')
        this.$message.success('已退出登录')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.sidebar-logo { padding: 20px; font-size: 18px; font-weight: 700; color: #fff; text-align: center; background: #5A0612; }
.el-menu { border-right: none; }
</style>
