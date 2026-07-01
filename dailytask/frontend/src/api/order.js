import request from './request'

//创建订单
export function createOrder(data) {
  return request.post("/order/create",data)
}

//获取订单列表
export function getMyOrders(){
    return request.get("/order/my")
}

//取消订单
export function cancelOrder(id){
    return request.put(`/order/cancel/${id}`)
}

//付款put
export function payOrder(id){
    return request.put(`/order/pay/${id}`)
}

//确认收货
export function confirmOrder(id){
    return request.put(`/order/confirm/${id}`)
}