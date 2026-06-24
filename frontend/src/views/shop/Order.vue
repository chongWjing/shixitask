<!-- 订单详情页 - 占位 -->
<template>
  <div class="placeholder-page">
    <el-empty description="订单详情页（待开发）">
      <el-button type="danger" @click="$router.push('/shop')">返回首页</el-button>
    </el-empty>
  </div>
</template>
<script>
export default { name: 'ShopOrder' }
</script>
<style scoped>
.placeholder-page { padding: 60px 0; text-align: center; }
</style>
