package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.service.StudentService;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileApiController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/me")
    public R<Map<String, Object>> currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("userInfo");
        Map<String, Object> data = new HashMap<>();

        if (user instanceof Student student) {
            data.put("userType", "student");
            data.put("id", student.getId());
            data.put("sNo", student.getSNo());
            data.put("name", student.getName());
            data.put("sex", student.getSex());
            data.put("age", student.getAge());
        } else if (user != null) {
            data.put("userType", "admin");
            data.put("name", "管理员");
        } else {
            return RUtil.error(REnum.LOGIN_ERR.getCode(), "未登录");
        }

        return RUtil.success(data);
    }

    @PutMapping("/update")
    public R<Student> updateProfile(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object user = session.getAttribute("userInfo");

        if (!(user instanceof Student)) {
            return RUtil.error(REnum.LOGIN_ERR.getCode(), "只有学生账户可以修改个人信息");
        }

        Student sessionStudent = (Student) user;
        Student student = studentService.findById(sessionStudent.getId());

        // 更新基础信息
        student.setName((String) payload.get("name"));
        Object sex = payload.get("sex");
        if (sex != null) {
            student.setSex(Short.valueOf(sex.toString()));
        }
        Object age = payload.get("age");
        if (age != null) {
            student.setAge(Short.valueOf(age.toString()));
        }

        // 更新密码（可选）
        String newPassword = (String) payload.get("password");
        if (StringUtils.hasText(newPassword)) {
            student.setPassword(newPassword);
        }

        Student saved = studentService.update(student);
        session.setAttribute("userInfo", saved);
        return RUtil.success(saved);
    }
}
