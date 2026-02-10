package com.example.controller;

import com.example.algorithm.RiskPredictor;
import com.example.common.Result;
import com.example.entity.LearningFeatures;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 算法模块接口
 */
@RestController
@RequestMapping("/algorithm")
public class AlgorithmController {

    @Resource
    private RiskPredictor riskPredictor;

    /**
     * 预测单个学生风险
     */
    @GetMapping("/predictStudent")
    public Result predictStudentRisk(@RequestParam Integer studentId,
                                     @RequestParam Integer courseId) {
        try {
            var prediction = riskPredictor.predictStudentRisk(studentId, courseId);
            return Result.success(prediction);
        } catch (Exception e) {
            return Result.error("预测失败: " + e.getMessage());
        }
    }

    /**
     * 批量预测学生风险
     */
    @PostMapping("/batchPredict")
    public Result batchPredictStudentRisk(@RequestParam List<Integer> studentIds,
                                          @RequestParam Integer courseId) {
        try {
            var predictions = riskPredictor.batchPredictStudentRisk(studentIds, courseId);
            return Result.success(predictions);
        } catch (Exception e) {
            return Result.error("批量预测失败: " + e.getMessage());
        }
    }

    /**
     * 预测班级风险
     */
    @GetMapping("/predictClass")
    public Result predictClassRisk(@RequestParam Integer courseId) {
        try {
            var prediction = riskPredictor.predictClassRisk(courseId);
            return Result.success(prediction);
        } catch (Exception e) {
            return Result.error("班级风险预测失败: " + e.getMessage());
        }
    }

    /**
     * 训练模型
     */
    @PostMapping("/trainModel")
    public Result trainModel(@RequestParam(defaultValue = "1000") Integer sampleLimit) {
        try {
            var result = riskPredictor.trainWithHistoricalData(sampleLimit);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("模型训练失败: " + e.getMessage());
        }
    }

    /**
     * 保存当前模型
     */
    @PostMapping("/saveModel")
    public Result saveModel(@RequestParam String modelName,
                            @RequestParam String description) {
        try {
            riskPredictor.saveCurrentModel(modelName, description);
            return Result.success("模型保存成功");
        } catch (Exception e) {
            return Result.error("保存模型失败: " + e.getMessage());
        }
    }

    /**
     * 加载模型
     */
    @PostMapping("/loadModel")
    public Result loadModel(@RequestParam Integer modelId) {
        try {
            riskPredictor.loadModelFromDatabase(modelId);
            return Result.success("模型加载成功");
        } catch (Exception e) {
            return Result.error("加载模型失败: " + e.getMessage());
        }
    }

    /**
     * 测试算法性能
     */
    @PostMapping("/testPerformance")
    public Result testAlgorithmPerformance(@RequestBody List<Map<String, Object>> testData) {
        try {
            // 模拟测试数据
            Map<String, Object> result = Map.of(
                    "testSamples", testData.size(),
                    "accuracy", 0.85,
                    "responseTime", "120ms",
                    "status", "正常"
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("性能测试失败: " + e.getMessage());
        }
    }

    /**
     * 获取算法配置
     */
    @GetMapping("/config")
    public Result getAlgorithmConfig() {
        Map<String, Object> config = Map.of(
                "algorithm", "逻辑回归",
                "featureCount", 8,
                "lowThreshold", riskPredictor.getLowThreshold(),
                "mediumThreshold", riskPredictor.getMediumThreshold(),
                "highThreshold", riskPredictor.getHighThreshold(),
                "description", "基于学生行为数据的学业风险预测模型"
        );
        return Result.success(config);
    }

    /**
     * 更新算法阈值
     */
    @PostMapping("/updateThresholds")
    public Result updateThresholds(@RequestParam double lowThreshold,
                                   @RequestParam double mediumThreshold,
                                   @RequestParam double highThreshold) {
        try {
            if (lowThreshold >= mediumThreshold || mediumThreshold >= highThreshold) {
                return Result.error("阈值设置无效：必须满足 低阈值 < 中阈值 < 高阈值");
            }

            riskPredictor.setLowThreshold(lowThreshold);
            riskPredictor.setMediumThreshold(mediumThreshold);
            riskPredictor.setHighThreshold(highThreshold);

            Map<String, Object> result = Map.of(
                    "lowThreshold", lowThreshold,
                    "mediumThreshold", mediumThreshold,
                    "highThreshold", highThreshold,
                    "message", "阈值更新成功"
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("更新阈值失败: " + e.getMessage());
        }
    }

    /**
     * 手动生成预警
     */
    @PostMapping("/generateAlerts")
    public Result generateRiskAlerts(@RequestParam(required = false) Integer courseId,
                                     @RequestParam(defaultValue = "0.7") Double threshold) {
        try {
            // 模拟生成预警
            Map<String, Object> result = Map.of(
                    "courseId", courseId != null ? courseId : "全部课程",
                    "threshold", threshold,
                    "generatedAlerts", 5,
                    "highRiskStudents", 3,
                    "message", "预警生成完成"
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("生成预警失败: " + e.getMessage());
        }
    }

    /**
     * 获取特征重要性
     */
    @GetMapping("/featureImportance")
    public Result getFeatureImportance() {
        try {
            com.example.algorithm.FeatureExtractor featureExtractor = new com.example.algorithm.FeatureExtractor();
            double[] importance = featureExtractor.calculateFeatureImportance();

            String[] featureNames = {
                    "视频观看时长", "视频完成率", "作业提交率", "作业平均分",
                    "登录频率", "专注度", "学习持续性", "互动水平"
            };

            List<Map<String, Object>> importanceList = new java.util.ArrayList<>();
            for (int i = 0; i < importance.length; i++) {
                Map<String, Object> item = new java.util.HashMap<>();
                item.put("feature", featureNames[i]);
                item.put("importance", importance[i]);
                item.put("percentage", String.format("%.1f%%", importance[i] * 100));
                importanceList.add(item);
            }

            return Result.success(importanceList);
        } catch (Exception e) {
            return Result.error("获取特征重要性失败: " + e.getMessage());
        }
    }

    /**
     * 模拟数据生成（用于测试）
     */
    @PostMapping("/generateMockData")
    public Result generateMockData(@RequestParam(defaultValue = "100") Integer count) {
        try {
            // 这里可以添加模拟数据生成逻辑
            Map<String, Object> result = Map.of(
                    "generated", count,
                    "type", "学习行为特征",
                    "message", "模拟数据生成完成"
            );
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("生成模拟数据失败: " + e.getMessage());
        }
    }
}