<template>
  <div style="padding:20px">
    <el-card shadow="never">
      <template #header><span>AI 智能查询</span></template>
      <el-row :gutter="12" align="middle">
        <el-col :span="18"><el-input v-model="question" placeholder="请输入您的问题，例如：今天卖了多少束花？" size="large" @keyup.enter="onQuery" clearable /></el-col>
        <el-col :span="6"><el-button type="primary" size="large" style="width:100%;background:#C71526;border-color:#C71526" :loading="loading" @click="onQuery">查询</el-button></el-col>
      </el-row>
      <div style="margin-top:16px;display:flex;align-items:center;flex-wrap:wrap;gap:8px">
        <span style="color:#999;font-size:14px">快捷提问：</span>
        <el-button v-for="q in quickQuestions" :key="q" size="small" round @click="question=q;onQuery()">{{ q }}</el-button>
      </div>
      <div v-if="result" style="margin-top:24px">
        <div v-if="result.sql" style="margin-bottom:20px">
          <div style="font-size:15px;font-weight:bold;color:#333;margin-bottom:10px">生成的 SQL</div>
          <div style="background:#1e1e1e;color:#d4d4d4;padding:16px;border-radius:6px;overflow-x:auto"><code style="font-family:Consolas,Monaco,monospace;font-size:14px;line-height:1.6;white-space:pre-wrap">{{ result.sql }}</code></div>
        </div>
        <div v-if="result.columns&&result.columns.length&&result.results&&result.results.length" style="margin-bottom:20px">
          <div style="font-size:15px;font-weight:bold;color:#333;margin-bottom:10px">查询结果（共 {{ result.results.length }} 条）</div>
          <el-table :data="result.results" stripe border max-height="400">
            <el-table-column v-for="col in result.columns" :key="col" :prop="col" :label="col" min-width="120" />
          </el-table>
        </div>
      </div>
      <el-empty v-else-if="!loading" description="请输入问题开始查询" />
    </el-card>
  </div>
</template>

<script>
import { aiQuery } from '../../api/admin'
import { ElMessage } from 'element-plus'

export default {
  name: 'AdminAiQuery',
  data() { return { question:'', result:null, loading:false, quickQuestions:['今天卖了多少束花？','销量最高的鲜花是什么？','本周的销售额是多少？','哪个鲜花分类最受欢迎？','最近7天的订单趋势如何？'] } },
  methods: {
    async onQuery() { if(!this.question.trim()){ElMessage.warning('请输入问题');return} this.loading=true; this.result=null; try{const r=await aiQuery({question:this.question}); if(r.code===200)this.result=r.data}catch(e){} finally{this.loading=false} }
  }
}
</script>
