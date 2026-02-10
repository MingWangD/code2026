package com.example.controller;

import com.example.common.Result;
import com.example.entity.ModelVersion;
import com.example.service.ModelVersionService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模型版本管理接口
 */
@RestController
@RequestMapping("/model-versions")
public class ModelVersionController {

    @Resource
    private ModelVersionService modelVersionService;

    /**
     * 新增模型版本
     */
    @PostMapping("/add")
    public Result add(@RequestBody ModelVersion modelVersion) {
        modelVersionService.add(modelVersion);
        return Result.success();
    }

    /**
     * 删除模型版本
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        modelVersionService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改模型版本
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody ModelVersion modelVersion) {
        modelVersionService.updateById(modelVersion);
        return Result.success();
    }

    /**
     * 根据ID查询模型版本
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ModelVersion modelVersion = modelVersionService.selectById(id);
        return Result.success(modelVersion);
    }

    /**
     * 根据模型编号查询
     */
    @GetMapping("/selectByModelNo/{modelNo}")
    public Result selectByModelNo(@PathVariable String modelNo) {
        ModelVersion modelVersion = modelVersionService.selectByModelNo(modelNo);
        return Result.success(modelVersion);
    }

    /**
     * 查询所有模型版本
     */
    @GetMapping("/selectAll")
    public Result selectAll(ModelVersion modelVersion) {
        List<ModelVersion> list = modelVersionService.selectAll(modelVersion);
        return Result.success(list);
    }

    /**
     * 分页查询模型版本
     */
    @GetMapping("/selectPage")
    public Result selectPage(ModelVersion modelVersion,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ModelVersion> page = modelVersionService.selectPage(modelVersion, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 获取当前激活的模型
     */
    @GetMapping("/active")
    public Result getActiveModel() {
        ModelVersion modelVersion = modelVersionService.getActiveModel();
        return Result.success(modelVersion);
    }

    /**
     * 激活指定模型
     */
    @PutMapping("/activate/{id}")
    public Result activateModel(@PathVariable Integer id) {
        modelVersionService.activateModel(id);
        return Result.success();
    }

    /**
     * 取消激活所有模型
     */
    @PutMapping("/deactivateAll")
    public Result deactivateAllModels() {
        modelVersionService.deactivateAllModels();
        return Result.success();
    }

    /**
     * 更新模型性能指标
     */
    @PutMapping("/updateMetrics")
    public Result updatePerformanceMetrics(@RequestParam Integer id,
                                           @RequestParam Double accuracy,
                                           @RequestParam Double precision,
                                           @RequestParam Double recall,
                                           @RequestParam Double f1Score,
                                           @RequestParam Double auc) {
        modelVersionService.updatePerformanceMetrics(id, accuracy, precision, recall, f1Score, auc);
        return Result.success();
    }

    /**
     * 更新模型文件路径
     */
    @PutMapping("/updatePaths")
    public Result updateModelFilePath(@RequestParam Integer id,
                                      @RequestParam String modelFilePath,
                                      @RequestParam String featureScalerPath) {
        modelVersionService.updateModelFilePath(id, modelFilePath, featureScalerPath);
        return Result.success();
    }

    /**
     * 获取最新的模型版本
     */
    @GetMapping("/latest/{modelNo}")
    public Result getLatestVersion(@PathVariable String modelNo) {
        ModelVersion modelVersion = modelVersionService.getLatestVersion(modelNo);
        return Result.success(modelVersion);
    }

    /**
     * 统计模型版本数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) String status,
                        @RequestParam(required = false) Boolean isActive) {
        Integer count = modelVersionService.count(status, isActive);
        return Result.success(count);
    }

    /**
     * 获取模型训练历史
     */
    @GetMapping("/trainingHistory")
    public Result getTrainingHistory(@RequestParam(defaultValue = "10") Integer limit) {
        List<ModelVersion> list = modelVersionService.getTrainingHistory(limit);
        return Result.success(list);
    }

    /**
     * 创建新的模型版本
     */
    @PostMapping("/createNew")
    public Result createNewVersion(@RequestParam String modelName,
                                   @RequestParam String description,
                                   @RequestParam String trainingDataStart,
                                   @RequestParam String trainingDataEnd,
                                   @RequestParam Integer sampleCount,
                                   @RequestParam Integer featureCount) {
        ModelVersion modelVersion = modelVersionService.createNewVersion(
                modelName, description, trainingDataStart, trainingDataEnd, sampleCount, featureCount
        );
        return Result.success(modelVersion);
    }

    /**
     * 完成模型训练
     */
    @PutMapping("/completeTraining")
    public Result completeTraining(@RequestParam Integer id,
                                   @RequestParam String weights,
                                   @RequestParam Double bias,
                                   @RequestParam Double accuracy,
                                   @RequestParam Double precision,
                                   @RequestParam Double recall,
                                   @RequestParam Double f1Score,
                                   @RequestParam Double auc,
                                   @RequestParam String modelFilePath) {
        modelVersionService.completeTraining(id, weights, bias, accuracy, precision, recall, f1Score, auc, modelFilePath);
        return Result.success();
    }
}