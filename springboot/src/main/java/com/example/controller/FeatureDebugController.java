package com.example.controller;

import com.example.common.Result;
import com.example.service.LearningFeaturesService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/debug/feature")
public class FeatureDebugController {

    @Resource
    private LearningFeaturesService learningFeaturesService;

    /**
     * 手动触发：从 student_behavior 计算 learning_features
     * 示例：
     * http://localhost:9090/debug/feature/calc?days=7
     */
    @GetMapping("/calc")
    public Result calculateFeatures(@RequestParam(defaultValue = "7") Integer days) {
        int count = learningFeaturesService.batchCalculateFeatures(days);
        return Result.success("生成/更新特征记录数：" + count);
    }
}
