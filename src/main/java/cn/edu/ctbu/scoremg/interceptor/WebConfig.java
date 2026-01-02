package cn.edu.ctbu.scoremg.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        // 登录与错误页
                        "/login",
                        "/index.html",
                        "/api/login/**",
                        "/error",

                        // 核心静态资源
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jpeg",
                        "/**/*.gif",
                        "/**/*.woff2",
                        "/**/*.svg",

                        // 静态资源目录
                        "/component/**",
                        "/admin/**",
                        "/css/**",
                        "/config/**",
                        "/view/**",
                        "/pagejs/**",
                        "/static/**",
                        "/favicon.ico"
                );
    }
}
