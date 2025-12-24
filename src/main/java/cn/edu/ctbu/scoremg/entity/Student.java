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
@Table(name="tb_student")
public class Student {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "s_no")
    private String sNo; // 学号

    private String name; // 姓名

    private Short age;   // 年龄

    private Short sex;   // 1男 2女

    private String password; // 密码
}