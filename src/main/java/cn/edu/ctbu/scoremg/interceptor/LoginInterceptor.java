package cn.edu.ctbu.scoremg.interceptor;

import cn.edu.ctbu.scoremg.entity.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 登录校验逻辑
        Object userInfo = request.getSession().getAttribute("userInfo");
        if (userInfo != null && userInfo instanceof Student) {
            return true; // 继续执行后续的拦截器或控制器
        }
        response.sendRedirect("/login"); // 401 未授权
        return false; // 拦截请求
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 执行控制器方法后，可以在此处理
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 请求完成后处理，比如资源清理
    }
}
