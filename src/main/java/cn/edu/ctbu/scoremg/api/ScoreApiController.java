package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Score;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.service.ScoreService;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.QueryObj;
import cn.edu.ctbu.scoremg.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
public class ScoreApiController {

    @Autowired
    private ScoreService scoreService;

    /**
     * 管理员：获取成绩列表 (分页 + 搜索)
     */
    @GetMapping("")
    public R list(QueryObj queryObj) {
        Page<Score> page = scoreService.getList(queryObj);
        return RUtil.success(page.getContent(), page.getTotalElements());
    }

    /**
     * 学生：获取"我的成绩"
     */
    @GetMapping("/mine")
    public R myScores(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Student user = (Student) session.getAttribute("userInfo");

        if (user == null) {
            return RUtil.error(REnum.LOGIN_ERR.getCode(), "登录已过期");
        }

        List<Score> scores = scoreService.findByStudentId(user.getId());
        // Layui 表格通常需要 count，这里不分页，直接返回总数
        return RUtil.success(scores, (long) scores.size());
    }

    /**
     * 新增成绩
     */
    @PostMapping("")
    public R add(@RequestBody Score score) {
        try {
            scoreService.add(score);
            return RUtil.success();
        } catch (Exception e) {
            return RUtil.error(REnum.UNKNOWN_ERR.getCode(), e.getMessage());
        }
    }

    /**
     * 修改成绩
     */
    @PutMapping("")
    public R update(@RequestBody Score score) {
        try {
            scoreService.update(score);
            return RUtil.success();
        } catch (Exception e) {
            return RUtil.error(REnum.UNKNOWN_ERR.getCode(), e.getMessage());
        }
    }

    /**
     * 删除成绩
     */
    @DeleteMapping("/{id}")
    public R delete(@PathVariable Long id) {
        scoreService.delete(id);
        return RUtil.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/batch")
    public R deleteBatch(@RequestBody List<Long> ids) {
        scoreService.deleteBatch(ids);
        return RUtil.success();
    }
}