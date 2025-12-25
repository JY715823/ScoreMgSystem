package cn.edu.ctbu.scoremg.dao;

import cn.edu.ctbu.scoremg.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long>, JpaSpecificationExecutor<Score> {

    /**
     * 根据学生ID查询所有成绩 (用于学生端 "我的成绩")
     */
    List<Score> findByStudentId(Long studentId);

    /**
     * 检查是否已存在某学生某门课的成绩 (用于录入查重)
     */
    boolean existsByStudentIdAndCourseId(Long studentId, Long courseId);
}