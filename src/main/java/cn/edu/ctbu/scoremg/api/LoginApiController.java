package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.service.StudentService;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
public class LoginApiController {

    @Autowired
    private StudentService studentService;

    /**
     * 统一登录接口
     * 请求路径：POST /api/login
     */
    @PostMapping("") // 【修改】这里留空，直接映射类路径
    public R login(@RequestParam String username,
                   @RequestParam String password,
                   @RequestParam Integer type,
                   HttpServletRequest request) {

        HttpSession session = request.getSession();

        if (type == 1) {
            // === 管理员登录 ===
            if ("admin".equals(username) && "123456".equals(password)) {
                session.setAttribute("userInfo", "ADMIN");
                session.setAttribute("userType", 1);
                return RUtil.success();
            } else {
                return RUtil.error(REnum.LOGIN_ERR.getCode(), "管理员账号或密码错误");
            }

        } else if (type == 2) {
            // === 学生登录 ===
            try {
                Student student = studentService.validateUsernameAndPassword(username, password);

                session.setAttribute("userInfo", student);
                session.setAttribute("userType", 2);
                return RUtil.success();
            } catch (Exception e) {
                return RUtil.error(REnum.LOGIN_ERR.getCode(), "学号或密码错误");
            }
        }

        return RUtil.error(REnum.COMMON_ERR.getCode(), "未知的身份类型");
    }

    /**
     * 退出登录
     * 请求路径：/api/login/logout
     */
    @RequestMapping("/logout")
    public R logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return RUtil.success();
    }
}