// 导入封装好的 axios 请求实例
import request from './request'

/**
 * 获取商品列表（分页，可按分类筛选）
 * 发送 GET 请求到 /api/product/list
 * @param {Object} params - 查询参数对象 { page, size, category }
 * @returns {Promise} 返回包含分页商品数据的响应
 */
export function getProductList(params) {
  return request.get('/product/list', { params })
}

/**
 * 获取商品详情
 * 发送 GET 请求到 /api/product/detail/:id
 * @param {Number} id - 商品ID
 * @returns {Promise} 返回包含商品详情的响应
 */
export function getProductDetail(id) {
  return request.get(`/product/${id}`)
}



/**
 * 搜索商品（支持分页）
 * 发送 GET 请求到 /api/product/search
 * @param {String} keyword - 搜索关键词
 * @param {Number} page - 页码，默认 1
 * @param {Number} size - 每页条数，默认 10
 * @returns {Promise} 返回包含搜索结果的响应
 */
export function searchProduct(keyword, page = 1, size = 10) {
  return request.get('/product/search', { params: { keyword, page, size } })
}

/**
 * 获取启用的分类列表
 * 发送 GET 请求到 /api/category/list
 * @returns {Promise} 返回包含分类列表的响应
 */
export function getCategoryList() {
  return request.get('/category/list')
}

/**
 * 获取最新评价列表
 * 发送 GET 请求到 /api/review/latest
 * @param {Number} limit - 返回条数，默认 8
 * @returns {Promise} 返回包含评价列表的响应
 */
export function getReviews(limit = 8) {
  return request.get('/review/latest', { params: { limit } })
}

/**
 * 获取商品评价列表
 * 发送 GET 请求到 /api/review/list
 */
export function getReviewList() {
  return request.get('/review/list')
}
