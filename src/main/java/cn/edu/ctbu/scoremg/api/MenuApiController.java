package cn.edu.ctbu.scoremg.api;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
public class MenuApiController {

    @GetMapping("")
    public List<Map<String, Object>> getMenu(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userTypeObj = session.getAttribute("userType");

        if (userTypeObj == null) {
            return new ArrayList<>();
        }

        Integer userType = (Integer) userTypeObj;
        List<Map<String, Object>> menuList = new ArrayList<>();

        // ==========================
        // 1. 【首页】
        // ==========================
        Map<String, Object> homeItem = new HashMap<>();
        homeItem.put("id", "root_home");
        homeItem.put("title", "首页");
        homeItem.put("icon", "layui-icon layui-icon-home");
        homeItem.put("type", 1);
        homeItem.put("openType", "_iframe");
        homeItem.put("href", "view/console/index.html");
        menuList.add(homeItem);

        // ==========================
        // 2. 【业务菜单】(全部扁平化，不要父目录)
        // ==========================
        if (userType == 1) {
            // ----------- 管理员 -----------

            // 1. 学生管理 (直接放第一层)
            Map<String, Object> studentItem = new HashMap<>();
            studentItem.put("id", "node_student");
            studentItem.put("title", "学生管理");
            studentItem.put("icon", "layui-icon layui-icon-user");
            studentItem.put("type", 1);
            studentItem.put("openType", "_iframe");
            studentItem.put("href", "/student/list");
            menuList.add(studentItem);

            // 2. 课程管理 (直接放第一层)
            Map<String, Object> courseItem = new HashMap<>();
            courseItem.put("id", "node_course");
            courseItem.put("title", "课程管理");
            courseItem.put("icon", "layui-icon layui-icon-read");
            courseItem.put("type", 1);
            courseItem.put("openType", "_iframe");
            courseItem.put("href", "/course/list");
            menuList.add(courseItem);

            // 3. 成绩管理 (直接放第一层)
            Map<String, Object> scoreItem = new HashMap<>();
            scoreItem.put("id", "node_score");
            scoreItem.put("title", "成绩管理");
            scoreItem.put("icon", "layui-icon layui-icon-form");
            scoreItem.put("type", 1);
            scoreItem.put("openType", "_iframe");
            scoreItem.put("href", "/score/list");
            menuList.add(scoreItem);

        } else if (userType == 2) {
            // ----------- 学生 -----------
            Map<String, Object> myScoreItem = new HashMap<>();
            myScoreItem.put("id", "root_score_mine");
            myScoreItem.put("title", "我的成绩");
            myScoreItem.put("icon", "layui-icon layui-icon-chart-screen");
            myScoreItem.put("type", 1);
            myScoreItem.put("openType", "_iframe");
            myScoreItem.put("href", "/score/mine");
            menuList.add(myScoreItem);
        }

        return menuList;
    }
}