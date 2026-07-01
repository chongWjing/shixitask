import axios from 'axios'
import { ElMessage } from 'element-plus'
const request=axios.create({
    baseURL:'/api',
    timeout:10000
})


//请求拦截器
request.interceptors.request.use(
  config => {
    //在localStorage中获取token
    const token=localStorage.getItem('token')
    //判定toten是否存在
    if (token){
      //使用Bearer认证方案携带token
      config.headers['Authorization']=`Bearer ${token}`
    }
    //返回修改后的请求配置
    return config
  }
)

//拦截器
request.interceptors.response.use(
  response => {
    const { code, message } = response.data
    if (code !== 200) {
      ElMessage.error(message || '请求失败')
      return Promise.reject(new Error(message || '请求失败'))
    }
    return response.data
  },
  error => {
    const message = error.response?.data?.message || error.message || '网络错误'
    ElMessage.error(message)
    return Promise.reject(error)
  }
)
export default request