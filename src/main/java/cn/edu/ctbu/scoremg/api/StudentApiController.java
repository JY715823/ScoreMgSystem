package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Student;
import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.service.StudentService;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.QueryObj;
import cn.edu.ctbu.scoremg.vo.R;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.util.StringUtils;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentApiController {
    @Autowired
    private StudentService studentService;


    @GetMapping("/list")
    public R<List<Student>> findAll(){
        List<Student> students = studentService.findAll();
        return RUtil.success(students);
    }

    @GetMapping("/{id}")
    public R<Student> findById(@PathVariable Long id){
        Student student = studentService.findById(id);
        return RUtil.success(student);
    }

    @PostMapping("/add")
    public R<Student> add(Student student){
        return RUtil.success(studentService.add(student));
    }

    @PutMapping("/update")
    public R<Student> update(Student  student){
        return RUtil.success(studentService.update(student));
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id){
        studentService.delete(id);
        return RUtil.success();
    }

    @DeleteMapping("/deletebatch")
    public R deleteBatch(@RequestBody List<Long> ids){
        studentService.deleteBatch(ids);
        return RUtil.success();
    }

    // 分页
    @PostMapping("/getbypage")
    public R<Page<Student>> getByPage(@RequestBody QueryObj<Student> qObj) {
        // 1. 处理排序逻辑
        Sort sort = Sort.by(Sort.Direction.DESC, "id"); // 默认按ID倒序

        if (qObj != null && StringUtils.hasText(qObj.getSortField()) && StringUtils.hasText(qObj.getSortOrder())) {
            String field = qObj.getSortField();
            String order = qObj.getSortOrder();

            // 简单的防止SQL注入处理
            Sort.Direction direction = "asc".equalsIgnoreCase(order) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, field);
        }

        // 2. 处理分页
        Integer pageIndex = 0;
        Integer pageSize = 10;

        if (qObj != null) {
            pageIndex = qObj.getPage() - 1; // JPA页码从0开始
            pageSize = qObj.getLimit();
        }

        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        // 3. 处理查询
        if (qObj == null || qObj.getData() == null) {
            Page<Student> students = studentService.getAll(pageable);
            return RUtil.success(students.getContent(), students.getTotalElements());
        } else {
            if (qObj.getData() instanceof Student) {
                Student student = (Student) qObj.getData();

                // 模糊查询匹配器
                ExampleMatcher matcher = ExampleMatcher.matching()
                        .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withMatcher("sNo", ExampleMatcher.GenericPropertyMatchers.contains()) // 学号也模糊
                        .withIgnoreNullValues();

                Example<Student> example = Example.of(student, matcher);
                Page<Student> studentPage = studentService.getAll(example, pageable);

                return RUtil.success(studentPage.getContent(), studentPage.getTotalElements());
            } else {
                throw new RException(REnum.QUERY_ERR);
            }
        }
    }
}