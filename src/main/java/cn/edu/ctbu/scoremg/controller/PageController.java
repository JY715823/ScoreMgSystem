package cn.edu.ctbu.scoremg.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 统一页面路由控制器
 * 专门负责跳转 HTML 视图
 */
@Controller
public class PageController {

    // 1. 学生管理页面
    @GetMapping("/student/list")
    public String studentList(){
        return "student/list";
    }

    // 2. 课程管理页面
    @GetMapping("/course/list")
    public String courseList(){
        return "course/list";
    }

    // 3. 成绩管理页面 (为下一步做准备)
    @GetMapping("/score/list")
    public String scoreList(){
        return "score/list";
    }
}