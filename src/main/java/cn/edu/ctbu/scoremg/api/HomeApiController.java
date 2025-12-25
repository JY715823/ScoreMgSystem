package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.dao.CourseRepository;
import cn.edu.ctbu.scoremg.dao.ScoreRepository;
import cn.edu.ctbu.scoremg.dao.StudentRepository;
import cn.edu.ctbu.scoremg.entity.Score;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/home")
public class HomeApiController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private ScoreRepository scoreRepository;

    @GetMapping("/data")
    public R getHomeData(HttpServletRequest request) {
        Map<String, Object> data = new HashMap<>();

        // 1. 获取当前登录用户信息 (用于欢迎语)
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute("userInfo");
        String displayName = "访客";

        if (userObj instanceof String) {
            // 管理员
            displayName = "管理员";
        } else if (userObj instanceof Student) {
            // 学生
            displayName = ((Student) userObj).getName();
        }
        data.put("username", displayName);

        // 2. 基础统计
        long studentCount = studentRepository.count();
        long courseCount = courseRepository.count();
        data.put("studentCount", studentCount);
        data.put("courseCount", courseCount);

        // 3. 计算全校平均绩点
        // 逻辑：先查出所有成绩 -> 按学生分组 -> 算每个学生的GPA -> 再求所有学生GPA的平均值
        List<Score> allScores = scoreRepository.findAll();

        // 按学生ID分组
        Map<Long, List<Score>> studentScoreMap = allScores.stream()
                .collect(Collectors.groupingBy(s -> s.getStudent().getId()));

        double totalGpaSum = 0.0;
        int validStudentCount = 0;

        for (List<Score> scores : studentScoreMap.values()) {
            double totalPoints = 0;
            double totalCredits = 0;

            for (Score s : scores) {
                // 防止空指针
                if (s.getCourse() == null) continue;

                int credit = (s.getCourse().getCredit() == null) ? 1 : s.getCourse().getCredit();
                int score = (s.getScore() == null) ? 0 : s.getScore();

                // 绩点计算：(分数-50)/10，不及格(60以下)为0
                double point = 0;
                if (score >= 60) {
                    point = (score - 50) / 10.0;
                }

                totalPoints += point * credit;
                totalCredits += credit;
            }

            if (totalCredits > 0) {
                double userGpa = totalPoints / totalCredits;
                totalGpaSum += userGpa;
                validStudentCount++;
            }
        }

        // 避免除以0
        double schoolAvgGpa = (validStudentCount > 0) ? (totalGpaSum / validStudentCount) : 0.0;
        // 保留2位小数
        data.put("schoolGpa", String.format("%.2f", schoolAvgGpa));

        return RUtil.success(data);
    }
}