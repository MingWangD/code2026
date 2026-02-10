package com.example.service;

import com.example.controller.AuthController;
import com.example.entity.Admin;
import com.example.entity.Student;
import com.example.entity.Teacher;
import com.example.exception.CustomException;
import com.example.mapper.AdminMapper;
import com.example.mapper.StudentMapper;
import com.example.mapper.TeacherMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Resource
    private AdminMapper adminMapper;
    @Resource
    private StudentMapper studentMapper;
    @Resource
    private TeacherMapper teacherMapper;

    public Map<String, Object> login(AuthController.LoginRequest req) {
        if (req == null) throw new CustomException("参数不能为空");
        if (isBlank(req.username) || isBlank(req.password) || isBlank(req.role)) {
            throw new CustomException("账号/密码/角色不能为空");
        }

        String role = req.role.trim().toUpperCase();

        switch (role) {
            case "ADMIN": {
                Admin u = adminMapper.selectByUsername(req.username);
                checkUser(u == null ? null : u.getPassword(), u == null ? null : u.getStatus(), req.password);

                return toLoginUser(u.getId(), u.getName(), "ADMIN", u.getAvatar());
            }
            case "STUDENT": {
                Student u = studentMapper.selectByUsername(req.username);
                checkUser(u == null ? null : u.getPassword(), u == null ? null : u.getStatus(), req.password);

                return toLoginUser(u.getId(), u.getName(), "STUDENT", u.getAvatar());
            }
            case "TEACHER": {
                Teacher u = teacherMapper.selectByUsername(req.username);
                checkUser(u == null ? null : u.getPassword(), u == null ? null : u.getStatus(), req.password);

                return toLoginUser(u.getId(), u.getName(), "TEACHER", u.getAvatar());
            }
            default:
                throw new CustomException("不支持的角色：" + req.role);
        }
    }

    private void checkUser(String dbPwd, String status, String inputPwd) {
        if (dbPwd == null) throw new CustomException("账号不存在");
        if (!dbPwd.equals(inputPwd)) throw new CustomException("密码错误");
        if (status != null && "禁用".equals(status)) throw new CustomException("账号已被禁用");
    }

    private Map<String, Object> toLoginUser(Integer id, String name, String role, String avatar) {
        Map<String, Object> m = new HashMap<>();
        m.put("id", id);
        m.put("name", name);
        m.put("role", role);
        m.put("avatar", avatar);
        return m;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
