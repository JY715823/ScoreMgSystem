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
        // 设置默认密码 123456
        if(student.getPassword() == null || student.getPassword().isEmpty()){
            student.setPassword("123456");
        }
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


    public Student validateUsernameAndPassword(String sNo, String password) throws Exception {

        // 在验证方法的第一行加上这个：
        System.out.println("========== 数据库环境自检 ==========");
        List<Student> all = studentReposity.findAll();
        for (Student s : all) {
            System.out.println("学号:" + s.getSNo() + " | 密码:" + s.getPassword());
        }
        System.out.println("==================================");
        System.out.println("正在验证登录 - 学号: [" + sNo + "], 密码: [" + password + "]");

        // 你说 findBysNo 能用，那就用它，只要能查出来就行
        List<Student> students = studentReposity.findBysNo(sNo);

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
        return studentReposity.findAll(pageable);
    }

    public Page<Student> getAll(Example<Student> example, Pageable pageable) {
        return studentReposity.findAll(example, pageable);
    }
}
