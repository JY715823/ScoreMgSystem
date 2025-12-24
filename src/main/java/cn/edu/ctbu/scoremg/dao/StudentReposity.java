package cn.edu.ctbu.scoremg.dao;

import cn.edu.ctbu.scoremg.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentReposity extends JpaRepository<Student,Long> {
    public List<Student> findBysNo(String sNo);
}