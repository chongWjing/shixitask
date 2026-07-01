<template>
  <div>
    <el-card shadow="never" style="margin-bottom:16px;border-radius:12px">
      <div style="display:flex;justify-content:space-between;align-items:center">
        <el-input v-model="searchKeyword" placeholder="搜索鲜花商品..." style="width:240px" clearable @input="filterProducts"><template #prefix><el-icon><Search /></el-icon></template></el-input>
        <el-button type="primary" style="background:#C71526;border-color:#C71526" @click="showAddDialog"><el-icon><Plus /></el-icon> 新增商品</el-button>
      </div>
    </el-card>
    <el-card shadow="never" style="border-radius:12px">
      <el-table :data="pagedProducts" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="商品名" min-width="200" />
        <el-table-column label="价格" width="100"><template #default="{row}">¥{{ row.price }}</template></el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="category" label="分类" width="90" />
        <el-table-column label="状态" width="80"><template #default="{row}"><el-tag :type="row.status===1?'success':'info'" size="small">{{ row.status===1?'上架':'下架' }}</el-tag></template></el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{row}">
            <el-button size="small" type="primary" link @click="showEditDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
            <el-button v-if="row.status===1" size="small" type="warning" link @click="handleToggleStatus(row,0)">下架</el-button>
            <el-button v-else size="small" type="success" link @click="handleToggleStatus(row,1)">上架</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex;justify-content:space-between;align-items:center;margin-top:16px">
        <span style="font-size:13px;color:#666">共 {{ filteredProducts.length }} 条</span>
        <el-pagination v-model:current-page="currentPage" :page-size="pageSize" :total="filteredProducts.length" layout="prev, pager,next" small />
      </div>
    </el-card>
    <el-dialog v-model="dialogVisible" :title="isEdit?'编辑商品':'新增商品'" width="500px">
      <el-form ref="productFormRef" :model="productForm" :rules="productRules" label-width="100px">
        <el-form-item label="商品名称" prop="name"><el-input v-model="productForm.name" /></el-form-item>
        <el-form-item label="商品价格" prop="price"><el-input-number v-model="productForm.price" :min="0.01" :precision="2" style="width:100%" /></el-form-item>
        <el-form-item label="库存数量" prop="stock"><el-input-number v-model="productForm.stock" :min="0" style="width:100%" /></el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" style="width:100%">
            <el-option v-for="c in ['鲜花','永生花','花束','花篮','绿植','礼品花']" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片URL"><el-input v-model="productForm.image" /></el-form-item>
        <el-form-item label="商品描述"><el-input v-model="productForm.description" type="textarea" :rows="3" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible=false">取消</el-button>
        <el-button type="primary" style="background:#C71526;border-color:#C71526" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { getAdminProductList, addProduct, updateProduct, deleteProduct, updateProductStatus } from '../../api/admin'
import { ElMessage } from 'element-plus'

export default {
  name: 'ProductManage',
  data() { return { allProducts:[], searchKeyword:'', filteredProducts:[], currentPage:1, pageSize:10, dialogVisible:false, isEdit:false, productForm:{id:null,name:'',price:null,stock:null,category:'鲜花',image:'',description:''}, productRules:{name:[{required:true,message:'请输入',trigger:'blur'}],price:[{required:true,message:'请输入',trigger:'blur'}],stock:[{required:true,message:'请输入',trigger:'blur'}],category:[{required:true,message:'请选择',trigger:'change'}]} } },
  computed: { pagedProducts() { const s=(this.currentPage-1)*this.pageSize; return this.filteredProducts.slice(s,s+this.pageSize) } },
  methods: {
    async loadProducts() { try { const r=await getAdminProductList(); if(r.code===200){const d=r.data; this.allProducts=d.list||d||[];this.filterProducts()} } catch(e){} },
    filterProducts() { const k=this.searchKeyword.trim().toLowerCase(); this.filteredProducts=k?this.allProducts.filter(p=>p.name&&p.name.toLowerCase().includes(k)):[...this.allProducts]; this.currentPage=1 },
    showAddDialog() { this.isEdit=false; this.productForm={id:null,name:'',price:null,stock:null,category:'鲜花',image:'',description:''}; this.dialogVisible=true },
    showEditDialog(row) { this.isEdit=true; this.productForm={...row}; this.dialogVisible=true },
    async handleSave() { try{await this.$refs.productFormRef.validate()}catch{return} try{const r=this.isEdit?await updateProduct(this.productForm):await addProduct(this.productForm); if(r.code===200){ElMessage.success(this.isEdit?'修改成功':'添加成功');this.dialogVisible=false;this.loadProducts()}}catch(e){} },
    async handleDelete(row) { try{await this.$confirm(`确定删除"${row.name}"？`,'确认',{type:'warning'}); const r=await deleteProduct(row.id); if(r.code===200){ElMessage.success('删除成功');this.loadProducts()}}catch(e){} },
    async handleToggleStatus(row,status) { try{const r=await updateProductStatus({id:row.id,status}); if(r.code===200){ElMessage.success(status===1?'上架成功':'下架成功');this.loadProducts()}}catch(e){} }
  },
  created() { this.loadProducts() }
}
</script>
