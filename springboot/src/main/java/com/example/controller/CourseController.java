package com.example.controller;

import com.example.common.Result;
import com.example.entity.Course;
import com.example.service.CourseService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程前端操作接口
 */
@RestController
@RequestMapping("/course")
public class CourseController {

    @Resource
    private CourseService courseService;

    /**
     * 新增课程
     */
    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        courseService.add(course);
        return Result.success();
    }

    /**
     * 删除课程
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        courseService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改课程
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Course course) {
        courseService.updateById(course);
        return Result.success();
    }

    /**
     * 根据ID查询课程
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Course course = courseService.selectById(id);
        return Result.success(course);
    }

    /**
     * 根据课程编号查询课程
     */
    @GetMapping("/selectByCourseNo/{courseNo}")
    public Result selectByCourseNo(@PathVariable String courseNo) {
        Course course = courseService.selectByCourseNo(courseNo);
        return Result.success(course);
    }

    /**
     * 查询所有课程
     */
    @GetMapping("/selectAll")
    public Result selectAll(Course course) {
        List<Course> list = courseService.selectAll(course);
        return Result.success(list);
    }

    /**
     * 分页查询课程
     */
    @GetMapping("/selectPage")
    public Result selectPage(Course course,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Course> page = courseService.selectPage(course, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据教师ID查询课程
     */
    @GetMapping("/selectByTeacher/{teacherId}")
    public Result selectByTeacherId(@PathVariable Integer teacherId) {
        List<Course> list = courseService.selectByTeacherId(teacherId);
        return Result.success(list);
    }

    /**
     * 根据学期查询课程
     */
    @GetMapping("/selectBySemester")
    public Result selectBySemester(@RequestParam String semester,
                                   @RequestParam(required = false) Integer year) {
        List<Course> list = courseService.selectBySemester(semester, year);
        return Result.success(list);
    }

    /**
     * 根据状态查询课程
     */
    @GetMapping("/selectByStatus/{status}")
    public Result selectByStatus(@PathVariable String status) {
        List<Course> list = courseService.selectByStatus(status);
        return Result.success(list);
    }

    /**
     * 更新课程状态
     */
    @PutMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer id, @RequestParam String status) {
        courseService.updateStatus(id, status);
        return Result.success();
    }

    /**
     * 统计课程数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) Integer teacherId,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String semester) {
        Integer count = courseService.count(teacherId, status, semester);
        return Result.success(count);
    }

    /**
     * 搜索课程
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Course> list = courseService.search(keyword);
        return Result.success(list);
    }

    /**
     * 增加课程学生人数
     */
    @PutMapping("/incrementStudentCount/{id}")
    public Result incrementStudentCount(@PathVariable Integer id) {
        courseService.incrementStudentCount(id);
        return Result.success();
    }

    /**
     * 增加课程作业数量
     */
    @PutMapping("/incrementHomeworkCount/{id}")
    public Result incrementHomeworkCount(@PathVariable Integer id) {
        courseService.incrementHomeworkCount(id);
        return Result.success();
    }

    /**
     * 获取课程详情（包含统计信息）
     */
    @GetMapping("/detail/{id}")
    public Result getCourseDetail(@PathVariable Integer id) {
        Course course = courseService.selectById(id);
        // 可以在这里添加更多统计信息
        return Result.success(course);
    }
}