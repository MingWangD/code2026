package com.example.controller;

import com.example.common.Result;
import com.example.service.AuthService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @PostMapping("/login")
    public Result login(@RequestBody LoginRequest req) {
        return Result.success(authService.login(req));
    }

    // 内部静态类，少建文件（最小改动）
    public static class LoginRequest {
        public String username;
        public String password;
        public String role; // ADMIN / STUDENT / TEACHER
    }
}
