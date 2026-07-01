import request from './request'

//用户登录  /api/user/login
export function login(data){
    return request.post('/user/login',data)
}

//用户注册  /api/user/register
export function register(data){
    return request.post('/user/register',data)
}

//获取当前用户信息  /api/user/info
export function getUserInfo(){
    return request.get('/user/info')
}

//退出登录  /api/user/logout
export function logout(){
    return request.post('/user/logout')
}