import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './router'
import {ElMessage,ElMessageBox} from 'element-plus'
import 'element-plus/dist/index.css'

const app=createApp(App)
app.use(router)
app.config.globalProperties.$message=ElMessage
app.config.globalProperties.$confirm=ElMessageBox.confirm
app.mount('#app')
