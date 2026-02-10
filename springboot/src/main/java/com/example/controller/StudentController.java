package com.example.controller;

import com.example.common.Result;
import com.example.entity.Student;
import com.example.service.StudentService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学生前端操作接口
 */
@RestController
@RequestMapping("/student")
public class StudentController {

    @Resource
    private StudentService studentService;

    /**
     * 新增学生
     */
    @PostMapping("/add")
    public Result add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        studentService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改学生
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Student student) {
        studentService.updateById(student);
        return Result.success();
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Student student = studentService.selectById(id);
        return Result.success(student);
    }

    /**
     * 根据学号查询学生
     */
    @GetMapping("/selectByStudentNo/{studentNo}")
    public Result selectByStudentNo(@PathVariable String studentNo) {
        Student student = studentService.selectByStudentNo(studentNo);
        return Result.success(student);
    }

    /**
     * 查询所有学生
     */
    @GetMapping("/selectAll")
    public Result selectAll(Student student) {
        List<Student> list = studentService.selectAll(student);
        return Result.success(list);
    }

    /**
     * 分页查询学生
     */
    @GetMapping("/selectPage")
    public Result selectPage(Student student,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Student> page = studentService.selectPage(student, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 批量导入学生
     */
    @PostMapping("/batchImport")
    public Result batchImport(@RequestBody List<Student> students) {
        studentService.batchImport(students);
        return Result.success("导入成功");
    }

    /**
     * 根据班级查询学生
     */
    @GetMapping("/selectByClass/{classId}")
    public Result selectByClassId(@PathVariable Integer classId) {
        List<Student> list = studentService.selectByClassId(classId);
        return Result.success(list);
    }

    /**
     * 更新学业状态
     */
    @PutMapping("/updateAcademicStatus")
    public Result updateAcademicStatus(@RequestParam Integer id, @RequestParam String academicStatus) {
        studentService.updateAcademicStatus(id, academicStatus);
        return Result.success();
    }

    /**
     * 统计学生数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) String grade,
                        @RequestParam(required = false) String major,
                        @RequestParam(required = false) String academicStatus) {
        Integer count = studentService.count(grade, major, academicStatus);
        return Result.success(count);
    }

    /**
     * 搜索学生
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Student> list = studentService.search(keyword);
        return Result.success(list);
    }

    /**
     * 启用/禁用学生
     */
    @PutMapping("/toggleStatus/{id}")
    public Result toggleStatus(@PathVariable Integer id) {
        studentService.toggleStatus(id);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Integer id) {
        studentService.resetPassword(id);
        return Result.success();
    }

    /**
     * 获取学生个人中心信息
     */
    @GetMapping("/profile/{id}")
    public Result getProfile(@PathVariable Integer id) {
        Student student = studentService.selectById(id);
        // 可以在这里添加更多个人信息
        return Result.success(student);
    }

    /**
     * 学生修改个人信息
     */
    @PutMapping("/updateProfile")
    public Result updateProfile(@RequestBody Student student) {
        // 不允许修改用户名和学号
        student.setUsername(null);
        student.setStudentNo(null);
        student.setRole(null); // 防止角色被修改
        studentService.updateById(student);
        return Result.success();
    }
}