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

import javax.swing.plaf.RootPaneUI;
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

    @GetMapping("/validateUser")
    public R validateSnoAndPassword(String sNo, String password, HttpServletRequest request) throws Exception{
        Student student = studentService.validateUsernameAndPassword(sNo, password);

        request.getSession().setAttribute("userInfo", student);

        return RUtil.success();
    }

    @PostMapping("/getbypage")
    public R<Page<Student>> getByPage(@RequestBody QueryObj<Student> qObj) {
        // 按id排倒序
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Integer pageIndex = 0;
        Integer pageSize = 10;

        if (qObj == null) {
            // student为空就直接调用分页
            Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);
            Page<Student> students = studentService.getAll(pageable);
            return RUtil.success(students.getContent(),students.getTotalElements());
        } else {
            pageIndex = qObj.getPage() - 1;
            pageSize = qObj.getLimit();

            if (qObj.getData() instanceof Student) {
                Student student = (Student) qObj.getData();
                Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

                ExampleMatcher matcher = ExampleMatcher.matching()
                        .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withIgnoreNullValues();

                Example<Student> example = Example.of(student, matcher);
                Page<Student> studentPage = studentService.getAll(example, pageable);
                return RUtil.success(studentPage.getContent(),studentPage.getTotalElements());

            } else {
                throw new RException(REnum.QUERY_ERR);
            }
        }
    }
}
