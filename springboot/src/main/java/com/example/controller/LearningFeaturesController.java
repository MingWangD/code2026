package com.example.controller;

import com.example.common.Result;
import com.example.entity.LearningFeatures;
import com.example.service.LearningFeaturesService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 学习行为特征管理接口
 */
@RestController
@RequestMapping("/learning-features")
public class LearningFeaturesController {

    @Resource
    private LearningFeaturesService learningFeaturesService;

    /**
     * 新增特征记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody LearningFeatures features) {
        learningFeaturesService.add(features);
        return Result.success();
    }

    /**
     * 删除特征记录
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        learningFeaturesService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改特征记录
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody LearningFeatures features) {
        learningFeaturesService.updateById(features);
        return Result.success();
    }

    /**
     * 根据ID查询特征记录
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        LearningFeatures features = learningFeaturesService.selectById(id);
        return Result.success(features);
    }

    /**
     * 查询所有特征记录
     */
    @GetMapping("/selectAll")
    public Result selectAll(LearningFeatures features) {
        List<LearningFeatures> list = learningFeaturesService.selectAll(features);
        return Result.success(list);
    }

    /**
     * 分页查询特征记录
     */
    @GetMapping("/selectPage")
    public Result selectPage(LearningFeatures features,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<LearningFeatures> page = learningFeaturesService.selectPage(features, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据学生ID查询特征记录
     */
    @GetMapping("/selectByStudent/{studentId}")
    public Result selectByStudentId(@PathVariable Integer studentId) {
        List<LearningFeatures> list = learningFeaturesService.selectByStudentId(studentId);
        return Result.success(list);
    }

    /**
     * 根据课程ID查询特征记录
     */
    @GetMapping("/selectByCourse/{courseId}")
    public Result selectByCourseId(@PathVariable Integer courseId) {
        List<LearningFeatures> list = learningFeaturesService.selectByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 获取学生的学习特征汇总
     */
    @GetMapping("/summary")
    public Result getStudentSummary(@RequestParam Integer studentId,
                                    @RequestParam(required = false) Integer courseId,
                                    @RequestParam(required = false) String startDate,
                                    @RequestParam(required = false) String endDate) {
        LearningFeatures summary = learningFeaturesService.getStudentSummary(studentId, courseId, startDate, endDate);
        return Result.success(summary);
    }

    /**
     * 更新风险信息
     */
    @PutMapping("/updateRiskInfo")
    public Result updateRiskInfo(@RequestParam Integer id,
                                 @RequestParam Double riskScore,
                                 @RequestParam String riskLevel,
                                 @RequestParam Double riskProbability) {
        learningFeaturesService.updateRiskInfo(id, riskScore, riskLevel, riskProbability);
        return Result.success();
    }

    /**
     * 获取高风险学生特征
     */
    @GetMapping("/highRisk")
    public Result getHighRiskFeatures(@RequestParam(defaultValue = "0.7") Double threshold,
                                      @RequestParam(defaultValue = "50") Integer limit) {
        List<LearningFeatures> list = learningFeaturesService.getHighRiskFeatures(threshold, limit);
        return Result.success(list);
    }

    /**
     * 统计特征记录数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) Integer studentId,
                        @RequestParam(required = false) Integer courseId,
                        @RequestParam(required = false) String riskLevel) {
        Integer count = learningFeaturesService.count(studentId, courseId, riskLevel);
        return Result.success(count);
    }

    /**
     * 批量插入特征记录
     */
    @PostMapping("/batchInsert")
    public Result batchInsert(@RequestBody List<LearningFeatures> featuresList) {
        learningFeaturesService.batchInsert(featuresList);
        return Result.success();
    }

    /**
     * 获取模型训练数据
     */
    @GetMapping("/trainingData")
    public Result getTrainingData(@RequestParam(required = false) Integer limit) {
        List<LearningFeatures> list = learningFeaturesService.getTrainingData(limit);
        return Result.success(list);
    }

    /**
     * 计算特征向量
     */
    @PostMapping("/calculateFeatureVector")
    public Result calculateFeatureVector(@RequestBody LearningFeatures features) {
        double[] featureVector = learningFeaturesService.calculateFeatureVector(features);
        return Result.success(featureVector);
    }
    /**
     * 批量计算学习特征
     */
    @PostMapping("/batchCalculate")
    public Result batchCalculate(@RequestParam(required = false) Integer days) {
        int count = learningFeaturesService.batchCalculateFeatures(days);
        return Result.success("批量计算完成，共生成 " + count + " 条特征记录");
    }
}