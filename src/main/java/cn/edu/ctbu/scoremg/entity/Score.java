package cn.edu.ctbu.scoremg.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="tb_score")
public class Score {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    // 多对一关联：多个成绩属于一个学生
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // 多对一关联：多个成绩属于一门课程
    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private Integer score; // 分数
}