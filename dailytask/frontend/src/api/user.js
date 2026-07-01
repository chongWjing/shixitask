import request from './request'

//用户登录  /api/user/login
export function login(data){
    return request.post('/user/login',data)
}

//用户注册  /api/user/register
export function register(data){
    return request.post('/user/register',data)
}