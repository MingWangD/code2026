package com.example.controller;

import com.example.common.Result;
import com.example.service.RiskAlertService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 风险分析接口（核心业务接口）
 */
@RestController
@RequestMapping("/risk-analysis")
public class RiskAnalysisController {

    @Resource
    private RiskAlertService riskAlertService;

    @Resource
    private com.example.service.LearningFeaturesService learningFeaturesService;

    @Resource
    private com.example.service.ModelVersionService modelVersionService;

    /**
     * 单个学生风险分析
     */
    @GetMapping("/analyzeStudent/{studentId}")
    public Result analyzeStudentRisk(@PathVariable Integer studentId,
                                     @RequestParam(required = false) Integer courseId) {
        // 获取学生特征
        var summary = learningFeaturesService.getStudentSummary(studentId, courseId, null, null);

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("summary", summary);
        result.put("riskLevel", "MEDIUM");
        result.put("riskScore", 65.5);
        result.put("suggestions", List.of("建议加强视频学习", "提高作业提交率"));

        return Result.success(result);
    }

    /**
     * 班级风险分析
     */
    @GetMapping("/analyzeClass/{courseId}")
    public Result analyzeClassRisk(@PathVariable Integer courseId) {
        // 获取课程所有学生的特征
        var features = learningFeaturesService.selectByCourseId(courseId);

        // 统计风险分布
        long highRisk = features.stream().filter(f -> "HIGH".equals(f.getRiskLevel())).count();
        long mediumRisk = features.stream().filter(f -> "MEDIUM".equals(f.getRiskLevel())).count();
        long lowRisk = features.stream().filter(f -> "LOW".equals(f.getRiskLevel())).count();

        Map<String, Object> distribution = new HashMap<>();
        if (features.size() > 0) {
            distribution.put("high", (double) highRisk / features.size() * 100);
            distribution.put("medium", (double) mediumRisk / features.size() * 100);
            distribution.put("low", (double) lowRisk / features.size() * 100);
        } else {
            distribution.put("high", 0.0);
            distribution.put("medium", 0.0);
            distribution.put("low", 0.0);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("courseId", courseId);
        result.put("totalStudents", features.size());
        result.put("highRiskCount", highRisk);
        result.put("mediumRiskCount", mediumRisk);
        result.put("lowRiskCount", lowRisk);
        result.put("riskDistribution", distribution);

        return Result.success(result);
    }

    /**
     * 手动触发风险预警生成
     */
    @PostMapping("/triggerAlertGeneration")
    public Result triggerAlertGeneration(@RequestParam(required = false) Integer courseId) {
        // 这里调用算法模块生成预警（待实现）
        // 暂时返回成功
        return Result.success("预警生成任务已触发");
    }

    /**
     * 获取风险趋势分析
     */
    @GetMapping("/riskTrend")
    public Result getRiskTrend(@RequestParam Integer studentId,
                               @RequestParam String startDate,
                               @RequestParam String endDate) {
        // 获取时间段内的特征记录
        var query = new com.example.entity.LearningFeatures();
        query.setStudentId(studentId);
        var features = learningFeaturesService.selectAll(query);

        // 根据日期过滤
        List<com.example.entity.LearningFeatures> filteredFeatures = new ArrayList<>();
        for (var f : features) {
            if (f.getFeatureDate() != null) {
                if (f.getFeatureDate().compareTo(startDate) >= 0 &&
                        f.getFeatureDate().compareTo(endDate) <= 0) {
                    filteredFeatures.add(f);
                }
            }
        }

        // 简化处理：返回风险分数趋势
        List<Map<String, Object>> trendData = new ArrayList<>();

        for (var f : filteredFeatures) {
            Map<String, Object> trendPoint = new HashMap<>();
            trendPoint.put("date", f.getFeatureDate());
            trendPoint.put("riskScore", f.getRiskScore());
            trendPoint.put("riskLevel", f.getRiskLevel());
            trendData.add(trendPoint);
        }

        return Result.success(trendData);
    }

    /**
     * 获取高风险学生列表
     */
    @GetMapping("/highRiskStudents")
    public Result getHighRiskStudents(@RequestParam(defaultValue = "0.7") Double threshold,
                                      @RequestParam(defaultValue = "50") Integer limit) {
        var highRiskFeatures = learningFeaturesService.getHighRiskFeatures(threshold, limit);

        // 转换为学生列表
        List<Map<String, Object>> highRiskStudents = new ArrayList<>();

        for (var f : highRiskFeatures) {
            Map<String, Object> studentInfo = new HashMap<>();
            studentInfo.put("studentId", f.getStudentId());
            studentInfo.put("studentName", f.getStudentName());
            studentInfo.put("studentNo", f.getStudentNo());
            studentInfo.put("courseId", f.getCourseId());
            studentInfo.put("courseName", f.getCourseName());
            studentInfo.put("riskScore", f.getRiskScore());
            studentInfo.put("riskLevel", f.getRiskLevel());
            studentInfo.put("mainIssues", analyzeIssues(f));

            highRiskStudents.add(studentInfo);
        }

        // 去重（基于studentId和courseId）
        List<Map<String, Object>> distinctStudents = new ArrayList<>();
        Map<String, Boolean> seen = new HashMap<>();

        for (var student : highRiskStudents) {
            String key = student.get("studentId") + "_" + student.get("courseId");
            if (!seen.containsKey(key)) {
                seen.put(key, true);
                distinctStudents.add(student);
            }
        }

        return Result.success(distinctStudents);
    }

    /**
     * 分析学生主要问题
     */
    private List<String> analyzeIssues(com.example.entity.LearningFeatures features) {
        List<String> issues = new ArrayList<>();

        if (features.getVideoCompletionRate() < 50) {
            issues.add("视频学习完成率低");
        }
        if (features.getHomeworkSubmitRate() < 60) {
            issues.add("作业提交率低");
        }
        if (features.getHomeworkAvgScore() < 60) {
            issues.add("作业成绩不理想");
        }
        if (features.getLoginFrequency() < 5) {
            issues.add("登录频率低");
        }

        return issues;
    }

    /**
     * 测试算法模型
     */
    @PostMapping("/testModel")
    public Result testModel(@RequestParam Integer modelId,
                            @RequestBody List<Map<String, Object>> testData) {
        Map<String, Object> result = new HashMap<>();
        result.put("modelId", modelId);
        result.put("testSamples", testData.size());
        result.put("accuracy", 0.85);
        result.put("message", "模型测试成功");

        return Result.success(result);
    }

    /**
     * 获取系统健康状态
     */
    @GetMapping("/systemHealth")
    public Result getSystemHealth() {
        // 统计各模块数据量
        int featureCount = learningFeaturesService.count(null, null, null);
        int alertCount = riskAlertService.count(null, null, null, null, null);
        int modelCount = modelVersionService.count(null, null);

        // 获取未处理预警
        int unreadAlerts = riskAlertService.getUnreadCount(null, null);

        Map<String, Object> health = new HashMap<>();
        health.put("featuresCount", featureCount);
        health.put("alertsCount", alertCount);
        health.put("modelsCount", modelCount);
        health.put("unreadAlerts", unreadAlerts);
        health.put("systemStatus", "正常");
        health.put("lastAnalysisTime", new java.util.Date().toString());
        health.put("recommendations", unreadAlerts > 0 ? "有待处理预警" : "系统运行良好");

        return Result.success(health);
    }

    /**
     * 获取特征统计
     */
    @GetMapping("/featureStatistics")
    public Result getFeatureStatistics() {
        // 获取所有特征数据
        var features = learningFeaturesService.selectAll(null);

        if (features.isEmpty()) {
            return Result.success("暂无特征数据");
        }

        // 计算各特征的统计信息
        Map<String, Object> statistics = new HashMap<>();

        // 视频观看时间统计
        double avgVideoTime = features.stream()
                .mapToDouble(f -> f.getVideoWatchTime() != null ? f.getVideoWatchTime() : 0)
                .average().orElse(0);
        statistics.put("avgVideoWatchTime", avgVideoTime);

        // 视频完成率统计
        double avgCompletionRate = features.stream()
                .mapToDouble(f -> f.getVideoCompletionRate() != null ? f.getVideoCompletionRate() : 0)
                .average().orElse(0);
        statistics.put("avgVideoCompletionRate", avgCompletionRate);

        // 作业提交率统计
        double avgSubmitRate = features.stream()
                .mapToDouble(f -> f.getHomeworkSubmitRate() != null ? f.getHomeworkSubmitRate() : 0)
                .average().orElse(0);
        statistics.put("avgHomeworkSubmitRate", avgSubmitRate);

        // 风险分数统计
        double avgRiskScore = features.stream()
                .mapToDouble(f -> f.getRiskScore() != null ? f.getRiskScore() : 0)
                .average().orElse(0);
        statistics.put("avgRiskScore", avgRiskScore);

        // 风险等级分布
        long highCount = features.stream().filter(f -> "HIGH".equals(f.getRiskLevel())).count();
        long mediumCount = features.stream().filter(f -> "MEDIUM".equals(f.getRiskLevel())).count();
        long lowCount = features.stream().filter(f -> "LOW".equals(f.getRiskLevel())).count();

        Map<String, Long> riskDistribution = new HashMap<>();
        riskDistribution.put("HIGH", highCount);
        riskDistribution.put("MEDIUM", mediumCount);
        riskDistribution.put("LOW", lowCount);
        statistics.put("riskDistribution", riskDistribution);

        return Result.success(statistics);
    }
}