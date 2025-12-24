package cn.edu.ctbu.scoremg.dao;

import cn.edu.ctbu.scoremg.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {

}