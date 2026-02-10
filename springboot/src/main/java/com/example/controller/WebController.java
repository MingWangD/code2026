package com.example.controller;

import com.example.common.Result;
import com.example.entity.Account;
import com.example.service.AdminService;
import com.example.service.StudentService;
import com.example.service.TeacherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
public class WebController {

    @Resource
    private AdminService adminService;

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    /**
     * 默认请求接口
     */
    @GetMapping("/")
    public Result hello() {
        return Result.success("学情智能预警系统后端服务运行中");
    }

    /**
     * 多角色登录
     */
    @PostMapping("/login")
    public Result login(@RequestBody Account account) {
        Object user = null;

        if ("ADMIN".equals(account.getRole())) {
            // 管理员登录
            user = adminService.login(account);
        } else if ("TEACHER".equals(account.getRole())) {
            // 教师登录
            user = teacherService.login(account);
        } else if ("STUDENT".equals(account.getRole())) {
            // 学生登录
            user = studentService.login(account);
        } else {
            return Result.error("角色类型错误");
        }

        return Result.success(user);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public Result register(@RequestBody Account account) {
        // 根据角色调用不同的注册服务
        if ("ADMIN".equals(account.getRole())) {
            // 管理员注册（通常不开放）
            return Result.error("管理员注册请联系超级管理员");
        } else if ("TEACHER".equals(account.getRole())) {
            // 教师注册（需要审核）
            return Result.error("教师注册请联系管理员");
        } else if ("STUDENT".equals(account.getRole())) {
            // 学生注册（需要审核）
            return Result.error("学生注册请联系管理员");
        } else {
            return Result.error("角色类型错误");
        }
    }

    /**
     * 修改密码
     */
    @PutMapping("/updatePassword")
    public Result updatePassword(@RequestBody Account account) {
        if ("ADMIN".equals(account.getRole())) {
            adminService.updatePassword(account);
        } else if ("TEACHER".equals(account.getRole())) {
            teacherService.updatePassword(account);
        } else if ("STUDENT".equals(account.getRole())) {
            studentService.updatePassword(account);
        } else {
            return Result.error("角色类型错误");
        }
        return Result.success();
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userInfo")
    public Result getUserInfo(@RequestParam String role, @RequestParam Integer id) {
        if ("ADMIN".equals(role)) {
            return Result.success(adminService.selectById(id));
        } else if ("TEACHER".equals(role)) {
            return Result.success(teacherService.selectById(id));
        } else if ("STUDENT".equals(role)) {
            return Result.success(studentService.selectById(id));
        } else {
            return Result.error("角色类型错误");
        }
    }

    /**
     * 退出登录（前端清除token即可）
     */
    @PostMapping("/logout")
    public Result logout() {
        return Result.success("退出成功");
    }
}