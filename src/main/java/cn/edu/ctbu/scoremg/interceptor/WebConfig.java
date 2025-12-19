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
                        // 1. 登录相关
                        "/login",
                        "/api/student/validateUser",

                        // 2. 核心静态资源 (重点补充!)
                        "/**/*.css",    // 所有 CSS
                        "/**/*.js",     // 所有 JS
                        "/**/*.png",    // 所有 PNG
                        "/**/*.jpg",    // 所有 JPG
                        "/**/*.jpeg",   // 所有 JPEG
                        "/**/*.gif",    // 所有 GIF
                        "/**/*.woff2",  // 字体文件
                        "/**/*.svg",    // SVG 图片

                        // 3. 框架特定资源
                        "/layui/**",    // Layui 框架
                        "/pear/**",     // Pear Admin
                        "/component/**",
                        "/admin/**",
                        "/pagejs/**",
                        "/config/**",
                        "/view/**",

                        // 4. 其他公共资源
                        "/favicon.ico"
                );
    }
}