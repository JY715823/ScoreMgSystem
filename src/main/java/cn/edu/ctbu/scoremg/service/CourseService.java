package cn.edu.ctbu.scoremg.service;

import cn.edu.ctbu.scoremg.dao.CourseRepository;
import cn.edu.ctbu.scoremg.entity.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> findAll(){
        return courseRepository.findAll();
    }

    public Course findById(Long id){
        return courseRepository.findById(id).orElse(null);
    }

    public Course save(Course course){
        return courseRepository.save(course); // 新增和修改都用 save
    }

    public void delete(Long id){
        courseRepository.deleteById(id);
    }

    public void deleteBatch(List<Long> ids){
        courseRepository.deleteAllById(ids);
    }

    public Page<Course> getAll(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public Page<Course> getAll(Example<Course> example, Pageable pageable) {
        return courseRepository.findAll(example, pageable);
    }
}