import request from './request'

//获取购物车列表
export function getCartList() {
  return request.get('/cart/list')
}

//添加购物车
export function addToCart(data) {
  return request.post('/cart/add', data)
}

//更新购物车数量
export function updateCartItem(data) {
  return request.put('/cart/update', data)
}

//删除购物车项
export function deleteCartItem(id) {
  return request.delete(`/cart/delete/${id}`)
}

//清空购物车
export function clearCart() {
  return request.delete('/cart/clear')
}
