package com.huashan.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.huashan.backend.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.HashMap;
import java.util.Map;

@Component
public class LoginInterceptor  implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //放行OPTIONS的预检请求
        if("OPTIONS".equalsIgnoreCase(request.getMethod())) return  true;

        //从请求头中获取authorization字段，这个字段通常会携带token
        String token = request.getHeader("Authorization");
        if(token == null || token.isEmpty()) {
            //如果请求头中没有token，则返回401错误
            writeUnauthorized(response,"请先登录！");
            return false;
        }
        //去掉 token 的 Bearer 前缀
        if(token.startsWith("Bearer ")) token = token.substring(7);
        //调用jwtutil解析token
        Map<String, Object> claims = JwtUtil.parseToken(token);

        if (claims == null){
            writeUnauthorized(response,"登录已过期！");
            return false;
        }

        Object userIdObj=claims.get("userId");
        Integer userId;
        if(userIdObj instanceof Integer) userId=(Integer) userIdObj;
        else if (userIdObj instanceof Number) userId=((Number) userIdObj).intValue();
        else userId=Integer.parseInt(userIdObj.toString());
        request.setAttribute("userId",userId);
        request.setAttribute("username",claims.get("username"));
        return true;
    }


    //私有方法，封装返回401错误
    private void writeUnauthorized(HttpServletResponse response,String message) throws  Exception{
        response.setContentType("application/json;charset=UTF-8");
        //设置状态码为401
        response.setStatus(401);
        Map<String,Object> result=new HashMap<>();
        result.put("code",401);
        result.put("message",message);
        response.getWriter().write(new ObjectMapper().writeValueAsString( result));
    }

}
