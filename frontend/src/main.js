import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'element-plus/dist/index.css'
import { ElMessage, ElMessageBox } from 'element-plus'

const app = createApp(App)
app.use(router)
app.config.globalProperties.$message = ElMessage
app.config.globalProperties.$confirm = ElMessageBox.confirm
app.mount('#app')
