package cn.edu.ctbu.scoremg.service;


import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.dao.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;

    public List<Student> findAll(){
        return studentRepository.findAll();
    }

    public Student findById(Long id){
        return studentRepository.findById(id).get();
    }

    /**
     * 新增学生
     * @param student
     * @return
     */
    public Student add(Student student){
        // 设置默认密码 123456
        if(student.getPassword() == null || student.getPassword().isEmpty()){
            student.setPassword("123456");
        }
        return studentRepository.save(student);
    }

    /**
     * 更新学生
     * @param student
     * @return
     */
    public Student update(Student student){
        return studentRepository.save(student);
    }

    /**
     * 删除学生
     * @param id
     */
    public void delete(Long id){
        studentRepository.deleteById(id);
    }

    /**
     * 批量删除学生
     * @param ids ID列表
     */
    public void deleteBatch(List<Long> ids){
        studentRepository.deleteAllById(ids);
    }


    public Student validateUsernameAndPassword(String sNo, String password) throws Exception {

        // 在验证方法的第一行加上这个：
        System.out.println("========== 数据库环境自检 ==========");
        List<Student> all = studentRepository.findAll();
        for (Student s : all) {
            System.out.println("学号:" + s.getSNo() + " | 密码:" + s.getPassword());
        }
        System.out.println("==================================");
        System.out.println("正在验证登录 - 学号: [" + sNo + "], 密码: [" + password + "]");

        List<Student> students = studentRepository.findBysNo(sNo);

        if (students.size() > 0) {
            Student student = students.get(0);
            System.out.println("数据库中的密码: [" + student.getPassword() + "]");

            // 【核心修复】防止 NPE：先判断数据库密码是否为 null
            if (student.getPassword() == null) {
                System.out.println("验证失败：该账号未设置密码");
                throw new RException(REnum.LOGIN_ERR);
            }

            if (student.getPassword().equals(password)) {
                return student;
            } else {
                throw new RException(REnum.LOGIN_ERR);
            }
        } else {
            throw new RException(REnum.LOGIN_ERR);
        }
    }

    public Page<Student> getAll(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Page<Student> getAll(Example<Student> example, Pageable pageable) {
        return studentRepository.findAll(example, pageable);
    }
}
