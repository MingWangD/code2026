package com.example.controller;

import com.example.common.Result;
import com.example.entity.Teacher;
import com.example.service.TeacherService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师前端操作接口
 */
@RestController
@RequestMapping("/teacher")
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 新增教师
     */
    @PostMapping("/add")
    public Result add(@RequestBody Teacher teacher) {
        teacherService.add(teacher);
        return Result.success();
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        teacherService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改教师
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Teacher teacher) {
        teacherService.updateById(teacher);
        return Result.success();
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Teacher teacher = teacherService.selectById(id);
        return Result.success(teacher);
    }

    /**
     * 根据工号查询教师
     */
    @GetMapping("/selectByTeacherNo/{teacherNo}")
    public Result selectByTeacherNo(@PathVariable String teacherNo) {
        Teacher teacher = teacherService.selectByTeacherNo(teacherNo);
        return Result.success(teacher);
    }

    /**
     * 查询所有教师
     */
    @GetMapping("/selectAll")
    public Result selectAll(Teacher teacher) {
        List<Teacher> list = teacherService.selectAll(teacher);
        return Result.success(list);
    }

    /**
     * 分页查询教师
     */
    @GetMapping("/selectPage")
    public Result selectPage(Teacher teacher,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Teacher> page = teacherService.selectPage(teacher, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据院系查询教师
     */
    @GetMapping("/selectByDepartment/{department}")
    public Result selectByDepartment(@PathVariable String department) {
        List<Teacher> list = teacherService.selectByDepartment(department);
        return Result.success(list);
    }

    /**
     * 批量导入教师
     */
    @PostMapping("/batchImport")
    public Result batchImport(@RequestBody List<Teacher> teachers) {
        teacherService.batchImport(teachers);
        return Result.success("导入成功");
    }

    /**
     * 更新课程数量
     */
    @PutMapping("/updateCourseCount")
    public Result updateCourseCount(@RequestParam Integer id, @RequestParam Integer courseCount) {
        teacherService.updateCourseCount(id, courseCount);
        return Result.success();
    }

    /**
     * 统计教师数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) String department,
                        @RequestParam(required = false) String title) {
        Integer count = teacherService.count(department, title);
        return Result.success(count);
    }

    /**
     * 搜索教师
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Teacher> list = teacherService.search(keyword);
        return Result.success(list);
    }

    /**
     * 启用/禁用教师
     */
    @PutMapping("/toggleStatus/{id}")
    public Result toggleStatus(@PathVariable Integer id) {
        teacherService.toggleStatus(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Integer id) {
        teacherService.resetPassword(id);
        return Result.success();
    }

    /**
     * 获取教师个人中心信息
     */
    @GetMapping("/profile/{id}")
    public Result getProfile(@PathVariable Integer id) {
        Teacher teacher = teacherService.selectById(id);
        // 可以在这里添加更多个人信息
        return Result.success(teacher);
    }

    /**
     * 教师修改个人信息
     */
    @PutMapping("/updateProfile")
    public Result updateProfile(@RequestBody Teacher teacher) {
        // 不允许修改用户名和工号
        teacher.setUsername(null);
        teacher.setTeacherNo(null);
        teacher.setRole(null); // 防止角色被修改
        teacherService.updateById(teacher);
        return Result.success();
    }

    /**
     * 获取所有教师（简单列表，用于下拉选择）
     */
    @GetMapping("/listAll")
    public Result listAll() {
        List<Teacher> list = teacherService.getAllTeachers();
        return Result.success(list);
    }
}