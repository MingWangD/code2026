package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Admin;
import com.example.entity.Account;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Resource
    private AdminMapper adminMapper;

    /**
     * 新增管理员
     */
    public void add(Admin admin) {
        // 检查用户名是否存在
        Admin dbAdmin = adminMapper.selectByUsername(admin.getUsername());
        if (ObjectUtil.isNotNull(dbAdmin)) {
            throw new CustomException("用户名已存在");
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(admin.getPassword())) {
            admin.setPassword("123456"); // 默认密码
        }
        if (ObjectUtil.isEmpty(admin.getName())) {
            admin.setName(admin.getUsername());
        }
        if (ObjectUtil.isEmpty(admin.getAdminNo())) {
            admin.setAdminNo("ADM" + System.currentTimeMillis());
        }

        admin.setRole("ADMIN");
        admin.setStatus("正常");
        admin.setPermissionLevel("普通管理员");
        admin.setCreateTime(LocalDateTime.now().toString());
        admin.setUpdateTime(LocalDateTime.now().toString());
        admin.setLoginCount(0);

        adminMapper.insert(admin);
    }

    /**
     * 删除管理员
     */
    public void deleteById(Integer id) {
        // 检查管理员是否存在
        Admin admin = adminMapper.selectById(id);
        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("管理员不存在");
        }

        // 不能删除超级管理员
        if ("超级管理员".equals(admin.getPermissionLevel())) {
            throw new CustomException("不能删除超级管理员");
        }

        adminMapper.deleteById(id);
    }

    /**
     * 修改管理员
     */
    public void updateById(Admin admin) {
        // 检查管理员是否存在
        Admin dbAdmin = adminMapper.selectById(admin.getId());
        if (ObjectUtil.isNull(dbAdmin)) {
            throw new CustomException("管理员不存在");
        }

        // 检查用户名是否重复（如果修改了用户名）
        if (ObjectUtil.isNotEmpty(admin.getUsername()) &&
                !dbAdmin.getUsername().equals(admin.getUsername())) {
            Admin existAdmin = adminMapper.selectByUsername(admin.getUsername());
            if (ObjectUtil.isNotNull(existAdmin) && !existAdmin.getId().equals(admin.getId())) {
                throw new CustomException("用户名已存在");
            }
        }

        // 保留原有的一些字段
        admin.setRole("ADMIN");
        admin.setUpdateTime(LocalDateTime.now().toString());

        adminMapper.updateById(admin);
    }

    /**
     * 根据ID查询管理员
     */
    public Admin selectById(Integer id) {
        Admin admin = adminMapper.selectById(id);
        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("管理员不存在");
        }
        return admin;
    }

    /**
     * 查询所有管理员
     */
    public List<Admin> selectAll(Admin admin) {
        return adminMapper.selectAll(admin);
    }

    /**
     * 分页查询管理员
     */
    public PageInfo<Admin> selectPage(Admin admin, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Admin> list = adminMapper.selectAll(admin);
        return PageInfo.of(list);
    }

    /**
     * 管理员登录（Account参数版本）
     */
    public Admin login(Account account) {
        // 验证参数
        if (ObjectUtil.isEmpty(account.getUsername()) ||
                ObjectUtil.isEmpty(account.getPassword())) {
            throw new CustomException("用户名或密码不能为空");
        }

        // 查询管理员
        Admin admin = adminMapper.selectByCondition(
                account.getUsername(),
                account.getPassword(),
                "正常",
                "ADMIN"
        );

        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("用户名或密码错误");
        }

        // 更新登录信息
        String now = LocalDateTime.now().toString();
        adminMapper.updateLoginInfo(admin.getId(), now, now);
        admin.setLastLoginTime(now);
        admin.setLoginCount(admin.getLoginCount() + 1);

        // 清除密码再返回
        admin.setPassword(null);
        admin.setNewPassword(null);

        return admin;
    }

    /**
     * 管理员登录（Admin参数版本 - 重载）
     */
    public Admin login(Admin admin) {
        // 调用Account版本的login方法
        Account account = new Account();
        account.setUsername(admin.getUsername());
        account.setPassword(admin.getPassword());
        return login(account);
    }

    /**
     * 修改密码（Account参数版本）
     */
    public void updatePassword(Account account) {
        // 验证参数
        if (ObjectUtil.isEmpty(account.getUsername()) ||
                ObjectUtil.isEmpty(account.getPassword()) ||
                ObjectUtil.isEmpty(account.getNewPassword())) {
            throw new CustomException("参数不完整");
        }

        // 查询管理员
        Admin admin = adminMapper.selectByCondition(
                account.getUsername(),
                account.getPassword(),
                "正常",
                "ADMIN"
        );

        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("原密码错误");
        }

        // 更新密码
        admin.setPassword(account.getNewPassword());
        admin.setUpdateTime(LocalDateTime.now().toString());
        adminMapper.updateById(admin);
    }

    /**
     * 修改密码（Admin参数版本 - 重载）
     */
    public void updatePassword(Admin admin) {
        // 调用Account版本的updatePassword方法
        Account account = new Account();
        account.setUsername(admin.getUsername());
        account.setPassword(admin.getPassword());
        account.setNewPassword(admin.getNewPassword());
        updatePassword(account);
    }

    /**
     * 统计管理员数量
     */
    public Integer count(String status, String department) {
        return adminMapper.count(status, department);
    }

    /**
     * 重置密码
     */
    public void resetPassword(Integer id) {
        Admin admin = adminMapper.selectById(id);
        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("管理员不存在");
        }

        admin.setPassword("123456");
        admin.setUpdateTime(LocalDateTime.now().toString());
        adminMapper.updateById(admin);
    }

    /**
     * 启用/禁用管理员
     */
    public void toggleStatus(Integer id) {
        Admin admin = adminMapper.selectById(id);
        if (ObjectUtil.isNull(admin)) {
            throw new CustomException("管理员不存在");
        }

        // 不能操作超级管理员
        if ("超级管理员".equals(admin.getPermissionLevel())) {
            throw new CustomException("不能操作超级管理员");
        }

        String newStatus = "正常".equals(admin.getStatus()) ? "禁用" : "正常";
        admin.setStatus(newStatus);
        admin.setUpdateTime(LocalDateTime.now().toString());
        adminMapper.updateById(admin);
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsername(String username) {
        Admin admin = adminMapper.selectByUsername(username);
        return ObjectUtil.isNotNull(admin);
    }

    /**
     * 获取所有管理员（无参数）
     */
    public List<Admin> getAllAdmins() {
        Admin query = new Admin();
        return adminMapper.selectAll(query);
    }

    /**
     * 搜索管理员
     */
    public List<Admin> searchAdmins(String keyword) {
        Admin query = new Admin();
        query.setName(keyword);
        query.setUsername(keyword);
        query.setDepartment(keyword);
        return adminMapper.selectAll(query);
    }
}