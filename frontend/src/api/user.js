import request from './request'

/**
 * 用户登录
 * @param {Object} data - { username, password }
 */
export function login(data) {
  return request.post('/user/login', data)
}

/**
 * 用户注册
 * @param {Object} data - { username, password, phone }
 */
export function register(data) {
  return request.post('/user/register', data)
}

/**
 * 获取当前登录用户信息
 */
export function getUserInfo() {
  return request.get('/user/info')
}

/**
 * 退出登录
 */
export function logout() {
  return request.post('/user/logout')
}

/**
 * 更新用户资料（手机号）
 * @param {Object} data - { phone }
 */
export function updateProfile(data) {
  return request.put('/user/profile', data)
}

/**
 * 修改密码
 * @param {Object} data - { oldPassword, newPassword }
 */
export function changePassword(data) {
  return request.put('/user/password', data)
}
