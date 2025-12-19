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
    private String name;
    private Short age;
    @Column(name = "s_no")
    private String sNo;
    private Short sex;
    private Short score;
    // 密码
    private String password;

}
