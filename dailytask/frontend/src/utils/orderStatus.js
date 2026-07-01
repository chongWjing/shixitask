/** 订单状态常量 */
export const ORDER_STATUS = {
  PENDING: 0,    // 待付款
  PAID: 1,       // 已付款
  SHIPPED: 2,    // 已发货
  COMPLETED: 3,  // 已完成
  CANCELLED: 4   // 已取消
}

/** 状态文本映射 */
export const ORDER_STATUS_TEXT = {
  0: '待付款',
  1: '已付款',
  2: '已发货',
  3: '已完成',
  4: '已取消'
}

/** 状态标签颜色映射（el-tag 的 type 属性） */
export const ORDER_STATUS_TAG_TYPE = {
  0: 'warning',   // 黄色-待处理
  1: 'success',   // 绿色-已确认
  2: '',          // 默认蓝色-进行中
  3: 'info',      // 灰色-已完成
  4: 'danger'     // 红色-已取消
}

/** 获取状态文本 */
export function getStatusText(status) {
  return ORDER_STATUS_TEXT[status] || '未知'
}

/** 获取状态标签颜色 */
export function getStatusTagType(status) {
  return ORDER_STATUS_TAG_TYPE[status] || 'info'
}