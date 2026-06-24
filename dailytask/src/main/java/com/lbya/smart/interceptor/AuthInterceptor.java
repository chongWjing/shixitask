package com.lbya.smart.interceptor;

import com.lbya.smart.model.User;
import com.lbya.smart.service.impl.UserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Token 认证拦截器
 *
 * 职责：
 * 1. 从 Authorization 头提取 Bearer token
 * 2. 验证 token 是否有效
 * 3. 将用户信息设置到 request.setAttribute("currentUser", user)
 *
 * 不拦截的路径（在 WebConfig 中配置）：
 * - /user/login、/user/register
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        // OPTIONS 预检请求放行
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 从 Authorization 头提取 token
        String auth = request.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            User user = TokenStore.getUser(token);
            if (user != null) {
                // token 有效，将用户信息放入 request
                request.setAttribute("currentUser", user);
                return true;
            }
        }

        // token 无效或不存在 → 返回 401
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}");
        return false;
    }
}
