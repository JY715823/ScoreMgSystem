package cn.edu.ctbu.scoremg.api;

import cn.edu.ctbu.scoremg.constant.REnum;
import cn.edu.ctbu.scoremg.entity.Course;
import cn.edu.ctbu.scoremg.exception.RException;
import cn.edu.ctbu.scoremg.service.CourseService;
import cn.edu.ctbu.scoremg.util.RUtil;
import cn.edu.ctbu.scoremg.vo.QueryObj;
import cn.edu.ctbu.scoremg.vo.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/course")
public class CourseApiController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/{id}")
    public R<Course> findById(@PathVariable Long id){
        return RUtil.success(courseService.findById(id));
    }

    @PostMapping("/add")
    public R<Course> add(Course course){
        return RUtil.success(courseService.save(course));
    }

    @PutMapping("/update")
    public R<Course> update(Course course){
        return RUtil.success(courseService.save(course));
    }

    @DeleteMapping("/delete/{id}")
    public R delete(@PathVariable Long id){
        courseService.delete(id);
        return RUtil.success();
    }

    @DeleteMapping("/deletebatch")
    public R deleteBatch(@RequestBody List<Long> ids){
        courseService.deleteBatch(ids);
        return RUtil.success();
    }

    @PostMapping("/getbypage")
    public R<Page<Course>> getByPage(@RequestBody QueryObj<Course> qObj) {
        // 1. 排序逻辑
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        if (qObj != null && StringUtils.hasText(qObj.getSortField()) && StringUtils.hasText(qObj.getSortOrder())) {
            Sort.Direction direction = "asc".equalsIgnoreCase(qObj.getSortOrder()) ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(direction, qObj.getSortField());
        }

        // 2. 分页参数
        Integer pageIndex = (qObj != null) ? qObj.getPage() - 1 : 0;
        Integer pageSize = (qObj != null) ? qObj.getLimit() : 10;
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        // 3. 查询逻辑
        if (qObj == null || qObj.getData() == null) {
            Page<Course> page = courseService.getAll(pageable);
            return RUtil.success(page.getContent(), page.getTotalElements());
        } else {
            if (qObj.getData() instanceof Course) {
                Course course = (Course) qObj.getData();
                // 模糊查询匹配器：只对 name (课程名) 和 teacher (老师) 进行模糊匹配
                ExampleMatcher matcher = ExampleMatcher.matching()
                        .withMatcher("name", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withMatcher("teacher", ExampleMatcher.GenericPropertyMatchers.contains())
                        .withIgnoreNullValues();

                Example<Course> example = Example.of(course, matcher);
                Page<Course> page = courseService.getAll(example, pageable);
                return RUtil.success(page.getContent(), page.getTotalElements());
            } else {
                throw new RException(REnum.QUERY_ERR);
            }
        }
    }
}