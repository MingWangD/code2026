package com.example.controller;

import com.example.common.Result;
import com.example.mapper.StudentBehaviorEventMapper;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/teacher/video")
public class TeacherVideoStatsController {

    @Resource
    private StudentBehaviorEventMapper studentBehaviorEventMapper;

    /**
     * 查询某学生某课程近 N 天观看秒数（默认 7 天）
     */
    @GetMapping("/watchSeconds")
    public Result watchSeconds(@RequestParam Integer studentId,
                               @RequestParam Integer courseId,
                               @RequestParam(defaultValue = "7") Integer days) {
        LocalDateTime from = LocalDateTime.now().minusDays(days);
        Double seconds = studentBehaviorEventMapper.sumWatchSecondsFrom(studentId, courseId, from);
        return Result.success(seconds == null ? 0 : seconds);
    }
}
