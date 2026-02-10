package com.example.controller;

import com.example.common.Result;
import com.example.entity.Admin;
import com.example.service.AdminService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员前端操作接口
 **/
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody Admin admin) {
        adminService.add(admin);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        adminService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody Admin admin) {
        adminService.updateById(admin);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        Admin admin = adminService.selectById(id);
        return Result.success(admin);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(Admin admin) {
        List<Admin> list = adminService.selectAll(admin);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(Admin admin,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<Admin> page = adminService.selectPage(admin, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 统计管理员数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) String status,
                        @RequestParam(required = false) String department) {
        Integer count = adminService.count(status, department);
        return Result.success(count);
    }

    /**
     * 重置密码
     */
    @PutMapping("/resetPassword/{id}")
    public Result resetPassword(@PathVariable Integer id) {
        adminService.resetPassword(id);
        return Result.success();
    }

    /**
     * 启用/禁用管理员
     */
    @PutMapping("/toggleStatus/{id}")
    public Result toggleStatus(@PathVariable Integer id) {
        adminService.toggleStatus(id);
        return Result.success();
    }

    /**
     * 检查用户名是否存在
     */
    @GetMapping("/checkUsername/{username}")
    public Result checkUsername(@PathVariable String username) {
        boolean exists = adminService.checkUsername(username);
        return Result.success(exists);
    }

    /**
     * 搜索管理员
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<Admin> list = adminService.searchAdmins(keyword);
        return Result.success(list);
    }

    /**
     * 获取所有管理员（无参）
     */
    @GetMapping("/listAll")
    public Result listAll() {
        List<Admin> list = adminService.getAllAdmins();
        return Result.success(list);
    }

    /**
     * 获取管理员个人中心信息
     */
    @GetMapping("/profile/{id}")
    public Result getProfile(@PathVariable Integer id) {
        Admin admin = adminService.selectById(id);
        return Result.success(admin);
    }

    /**
     * 管理员修改个人信息
     */
    @PutMapping("/updateProfile")
    public Result updateProfile(@RequestBody Admin admin) {
        // 不允许修改用户名和角色
        admin.setUsername(null);
        admin.setRole(null);
        adminService.updateById(admin);
        return Result.success();
    }
}