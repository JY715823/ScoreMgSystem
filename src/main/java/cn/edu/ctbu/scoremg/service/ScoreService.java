package cn.edu.ctbu.scoremg.service;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.dao.ScoreRepository;
import cn.edu.ctbu.scoremg.entity.Score;
import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.vo.QueryObj;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScoreService {

    @Autowired
    private ScoreRepository scoreRepository;

    // ... (add, update, delete 方法保持不变，这里省略以节省篇幅，请保留你原来的代码) ...
    public Score add(Score score) { /* ...保留原代码... */ return scoreRepository.save(score); }
    public Score update(Score score) { /* ...保留原代码... */ return scoreRepository.save(score); }
    public void delete(Long id) { scoreRepository.deleteById(id); }
    public void deleteBatch(List<Long> ids) { scoreRepository.deleteAllById(ids); }
    public List<Score> findByStudentId(Long studentId) { return scoreRepository.findByStudentId(studentId); }

    /**
     * 分页查询 (支持 全局排序 + 模糊搜索)
     */
    public Page<Score> getList(QueryObj queryObj) {
        // 1. 处理排序字段映射
        String sortField = "id"; // 默认按 ID 排
        if (StringUtils.hasText(queryObj.getSortField())) {
            String webField = queryObj.getSortField();
            // 映射前端列名 -> 后端实体属性路径
            if ("studentName".equals(webField)) {
                sortField = "student.name";
            } else if ("courseName".equals(webField)) {
                sortField = "course.name";
            } else if ("score".equals(webField)) {
                sortField = "score"; // 分数
            } else {
                sortField = "id"; // 其他情况默认 ID
            }
        }

        // 2. 处理排序方向 (ASC / DESC)
        Sort.Direction direction = Sort.Direction.DESC; // 默认降序
        if ("asc".equalsIgnoreCase(queryObj.getSortOrder())) {
            direction = Sort.Direction.ASC;
        }

        // 构建分页 + 排序对象
        Sort sort = Sort.by(direction, sortField);
        Pageable pageable = PageRequest.of(queryObj.getPage() - 1, queryObj.getLimit(), sort);

        // 3. 构建查询条件 (模糊搜索)
        Specification<Score> spec = (root, query, cb) -> {
            List<Predicate> list = new ArrayList<>();

            // 搜索逻辑：如果 keyword 有值，就去匹配 (学生姓名 OR 课程名)
            if (StringUtils.hasText(queryObj.getKeyword())) {
                String key = "%" + queryObj.getKeyword() + "%";

                // 这里的 root.get("student") 会自动做 Join 操作
                Predicate p1 = cb.like(root.get("student").get("name"), key);
                Predicate p2 = cb.like(root.get("course").get("name"), key);

                list.add(cb.or(p1, p2));
            }

            return cb.and(list.toArray(new Predicate[0]));
        };

        return scoreRepository.findAll(spec, pageable);
    }
}