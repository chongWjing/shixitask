<template>
  <div>
    <el-card shadow="never" style="margin-bottom:16px;border-radius:12px">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <div style="display:flex;align-items:center;gap:8px">
          <span style="font-size:14px;color:#666">状态筛选：</span>
          <el-select v-model="statusFilter" placeholder="全部" style="width:160px" @change="filterOrders">
            <el-option label="全部" value="" /><el-option label="待付款" :value="0" /><el-option label="已付款" :value="1" /><el-option label="已发货" :value="2" /><el-option label="已完成" :value="3" /><el-option label="已取消" :value="4" />
          </el-select>
        </div>
        <el-button style="background:#C71526;border-color:#C71526;color:#fff" @click="loadOrders"><el-icon><Refresh /></el-icon> 刷新</el-button>
      </div>
    </el-card>
    <el-card shadow="never" style="border-radius:12px">
      <el-table :data="pagedOrders" stripe>
        <el-table-column prop="orderNo" label="订单号" width="200" />
        <el-table-column prop="productName" label="商品名" min-width="180" />
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column label="总价" width="100"><template #default="{row}">¥{{ row.totalPrice }}</template></el-table-column>
        <el-table-column label="状态" width="90"><template #default="{row}"><el-tag :type="statusTypeMap[row.status]" size="small">{{ statusTextMap[row.status]||'未知' }}</el-tag></template></el-table-column>
        <el-table-column prop="receiverName" label="收货人" width="90" />
        <el-table-column label="时间" width="160"><template #default="{row}">{{ formatTime(row.createTime) }}</template></el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{row}">
            <el-button v-if="row.status===1" size="small" type="primary" link @click="handleUpdateStatus(row,2)">发货</el-button>
            <el-button v-if="row.status===2" size="small" type="success" link @click="handleUpdateStatus(row,3)">完成</el-button>
            <span v-if="row.status!==1&&row.status!==2" style="color:#999;font-size:12px">--</span>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-top:16px">
        <span style="font-size:13px;color:#666">共 {{ filteredOrders.length }} 条</span>
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="filteredOrders.length" layout="prev,pager,next" small />
      </div>
    </el-card>
  </div>
</template>

<script>
import { getAdminOrderList, updateOrderStatus } from '../../api/admin'
import { ElMessage } from 'element-plus'

export default {
  name: 'OrderManage',
  data() { return { allOrders:[], statusFilter:'', filteredOrders:[], currentPage:1, pageSize:10, statusTextMap:{0:'待付款',1:'已付款',2:'已发货',3:'已完成',4:'已取消'}, statusTypeMap:{0:'warning',1:'primary',2:'danger',3:'success',4:'info'} } },
  computed: { pagedOrders() { const s=(this.currentPage-1)*this.pageSize; return this.filteredOrders.slice(s,s+this.pageSize) } },
  methods: {
    async loadOrders() { try{const r=await getAdminOrderList(); if(r.code===200){const d=r.data; this.allOrders=(d.list||d||[]).sort((a,b)=>new Date(b.createTime)-new Date(a.createTime));this.filterOrders()}}catch(e){} },
    filterOrders() { this.filteredOrders=this.statusFilter===''?[...this.allOrders]:this.allOrders.filter(o=>o.status===Number(this.statusFilter)); this.currentPage=1 },
    formatTime(t) { if(!t)return'--'; const d=new Date(t); return isNaN(d)?t:d.getFullYear()+'-'+String(d.getMonth()+1).padStart(2,'0')+'-'+String(d.getDate()).padStart(2,'0')+' '+String(d.getHours()).padStart(2,'0')+':'+String(d.getMinutes()).padStart(2,'0') },
    async handleUpdateStatus(row,status) { const txt=status===2?'发货':'完成'; try{await this.$confirm(`确定要${txt}该订单？`,'确认',{type:'warning'}); const r=await updateOrderStatus({id:row.id,status}); if(r.code===200){ElMessage.success(`${txt}成功`);this.loadOrders()}}catch(e){} }
  },
  created() { this.loadOrders() }
}
</script>
