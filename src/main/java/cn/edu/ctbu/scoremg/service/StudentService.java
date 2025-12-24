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

    /**
     * 批量删除学生
     * @param ids ID列表
     */
    public void deleteBatch(List<Long> ids){
        studentReposity.deleteAllById(ids);
    }


    // 【重点修改这个验证方法】
    public Student validateUsernameAndPassword(String sNo, String password) throws Exception {
        // 1. 打印日志，看看前端传进来的到底是啥 (排除空格干扰)
        System.out.println("正在验证登录 - 学号: [" + sNo + "], 密码: [" + password + "]");

        // 2. 调用修改后的方法 findBySNo
        List<Student> students = studentReposity.findBysNo(sNo);

        System.out.println("数据库查询结果数量: " + students.size());

        if (students.size() > 0) {
            Student student = students.get(0);

            // 3. 打印数据库里的真实密码，看看是不是数据库里存的数据有问题
            System.out.println("数据库中的密码: [" + student.getPassword() + "]");

            if (student.getPassword().equals(password)) {
                System.out.println("验证成功！");
                return student;
            } else {
                System.out.println("验证失败：密码不匹配");
                throw new RException(REnum.LOGIN_ERR);
            }
        } else {
            System.out.println("验证失败：未找到该学号的学生");
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
