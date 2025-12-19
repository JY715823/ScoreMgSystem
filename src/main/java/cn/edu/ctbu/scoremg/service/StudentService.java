package cn.edu.ctbu.scoremg.service;


import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.dao.StudentReposity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentReposity studentReposity;

    public List<Student> findAll(){
        return studentReposity.findAll();
    }

    public Student findById(Long id){
        return studentReposity.findById(id).get();
    }

    /**
     * 新增学生
     * @param student
     * @return
     */
    public Student add(Student student){
        return studentReposity.save(student);
    }

    /**
     * 更新学生
     * @param student
     * @return
     */
    public Student update(Student student){
        return studentReposity.save(student);
    }

    /**
     * 删除学生
     * @param id
     */
    public void delete(Long id){
        studentReposity.deleteById(id);
    }


    public Student validateUsernameAndPassword(String sNo, String password) throws Exception {
        List<Student> students = studentReposity.findBysNo(sNo);
        if (students.size() > 0) {
            // 可能对password加密，但我们暂时不做处理
            Student student = students.get(0);
            if (student.getPassword().equals(password)) {
                // 成功
                return student;
            } else {
                throw new RException(REnum.LOGIN_ERR);
            }
        } else {
            throw new RException(REnum.LOGIN_ERR);
        }
    }

    public Page<Student> getAll(Pageable pageable) {
        return studentReposity.findAll(pageable);
    }

    public Page<Student> getAll(Example<Student> example, Pageable pageable) {
        return studentReposity.findAll(example, pageable);
    }
}
