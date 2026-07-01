package com.huashan.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
// 实现WebMvcConfigurer接口，用于自定义Spring MVC的配置
// 通过重写接口方法可以配置拦截器、跨域、视图解析器等
public class WebMvcConfig implements WebMvcConfigurer {
    // 自动注入LoginInterceptor拦截器实例
    // Spring会自动从容器中找到类型匹配的Bean并注入
    @Autowired
    private LoginInterceptor loginInterceptor;

    // 重写addInterceptors方法：配置拦截器的拦截规则
    // 参数registry：拦截器注册中心，用于添加和配置拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 向注册中心添加loginInterceptor拦截器，并开始配置拦截路径
        registry.addInterceptor(loginInterceptor)
                // 设置拦截路径模式："/**"表示拦截所有请求路径
                // 双星号**表示匹配任意多级路径，如/api/user/info/1
                .addPathPatterns("/**")
                // 设置排除路径：以下路径不会被拦截器拦截，直接放行
                .excludePathPatterns(
                        // 排除用户登录接口，未登录用户需要访问该接口获取Token
                        "/api/user/login",
                        // 排除用户注册接口，新用户需要访问该接口完成注册
                        "/api/user/register",
                        // 排除商品相关所有接口，商品列表/详情等无需登录即可查看
                        // /**表示匹配/api/product/下的任意路径
                        "/api/product/**",
                        // 排除评论相关所有接口，评论列表/详情等无需登录即可查看
                        "/api/review/**",
                        // 排除分类列表接口，获取全部分类无需登录
                        "/api/category/list",
                        // 排除单个分类详情接口，如/api/category/1
                        // /*只匹配一级路径，不匹配多级路径
                        "/api/category/*",
                        // 排除Spring Boot默认错误页面路径
                        "/error"
                );
    }

    // 重写addCorsMappings方法：配置跨域资源共享（CORS）规则
    // 参数registry：跨域注册中心，用于配置允许跨域访问的路径
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加跨域映射，对所有路径生效
        registry.addMapping("/**")
                // 设置允许的请求来源模式，"*"表示允许任意来源（域名）
                // allowedOriginPatterns支持通配符，比allowedOrigins更灵活
                .allowedOriginPatterns("*")
                // 设置允许的HTTP请求方法
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 设置允许的请求头，"*"表示允许任意请求头
                .allowedHeaders("*")
                // 允许携带凭证（如Cookie、Authorization头等）
                // 当allowCredentials为true时，allowedOriginPatterns不能为"*"
                // 实际生产环境应配置具体域名，此处为开发环境配置
                .allowCredentials(true)
                // 设置预检请求（OPTIONS）的缓存时间，单位为秒
                // 3600秒=1小时，浏览器在1小时内对同一请求不再重复发送预检
                .maxAge(3600);
    }
}