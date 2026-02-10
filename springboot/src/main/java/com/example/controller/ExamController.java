package com.example.controller;

import com.example.common.Result;
import com.example.service.ExamService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/exam")
public class ExamController {

    @Resource
    private ExamService examService;

    @GetMapping("/available")
    public Result available(@RequestParam Integer courseId) {
        return Result.success(examService.listAvailable(courseId));
    }

    @GetMapping("/paper/{examId}")
    public Result paper(@PathVariable Integer examId) {
        return Result.success(examService.getPaper(examId));
    }

    @PostMapping("/submit")
    public Result submit(@RequestBody ExamService.SubmitDTO dto) {
        return Result.success(examService.submit(dto));
    }
}
