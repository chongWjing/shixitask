import request from './request'

// ===== 商品管理 =====
export const getAdminProductList = () => request.get('/admin/product/list')
export const addProduct = (data) => request.post('/admin/product/add', data)
export const updateProduct = (data) => request.put('/admin/product/update', data)
export const deleteProduct = (id) => request.delete(`/admin/product/delete/${id}`)
export const updateProductStatus = (data) => request.put('/admin/product/status', data)

// ===== 订单管理 =====
export const getAdminOrderList = () => request.get('/admin/order/list')
export const updateOrderStatus = (data) => request.put('/admin/order/status', data)

// ===== 统计数据 =====
export const getStatsOverview = () => request.get('/admin/stats/overview')
export const getOrderTrend = () => request.get('/admin/stats/orderTrend')
export const getCategorySales = () => request.get('/admin/stats/categorySales')

// ===== AI智能查询 =====
export const aiQuery = (data) => request.post('/ai/query', data)
