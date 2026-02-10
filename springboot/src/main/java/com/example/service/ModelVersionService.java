package com.example.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.ModelVersion;
import com.example.exception.CustomException;
import com.example.mapper.ModelVersionMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ModelVersionService {

    @Resource
    private ModelVersionMapper modelVersionMapper;

    /**
     * 新增模型版本
     */
    public void add(ModelVersion modelVersion) {
        // 检查模型编号是否重复
        if (ObjectUtil.isNotEmpty(modelVersion.getModelNo())) {
            ModelVersion exist = modelVersionMapper.selectByModelNo(modelVersion.getModelNo());
            if (ObjectUtil.isNotNull(exist)) {
                throw new CustomException("模型编号已存在");
            }
        } else {
            // 生成模型编号
            modelVersion.setModelNo("MODEL_" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(modelVersion.getVersion())) {
            modelVersion.setVersion("1.0.0");
        }
        if (ObjectUtil.isEmpty(modelVersion.getAlgorithmType())) {
            modelVersion.setAlgorithmType("LOGISTIC_REGRESSION");
        }
        if (ObjectUtil.isEmpty(modelVersion.getLowThreshold())) {
            modelVersion.setLowThreshold(0.3);
        }
        if (ObjectUtil.isEmpty(modelVersion.getMediumThreshold())) {
            modelVersion.setMediumThreshold(0.7);
        }
        if (ObjectUtil.isEmpty(modelVersion.getHighThreshold())) {
            modelVersion.setHighThreshold(0.9);
        }
        if (ObjectUtil.isEmpty(modelVersion.getIsActive())) {
            modelVersion.setIsActive(false);
        }
        if (ObjectUtil.isEmpty(modelVersion.getStatus())) {
            modelVersion.setStatus("TRAINED");
        }

        modelVersion.setCreatedTime(LocalDateTime.now().toString());
        modelVersion.setUpdatedTime(LocalDateTime.now().toString());

        // 如果设置为激活，先取消其他激活模型
        if (modelVersion.getIsActive()) {
            modelVersionMapper.deactivateAllModels();
        }

        modelVersionMapper.insert(modelVersion);
    }

    /**
     * 删除模型版本
     */
    public void deleteById(Integer id) {
        ModelVersion modelVersion = modelVersionMapper.selectById(id);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }

        // 不能删除激活的模型
        if (modelVersion.getIsActive()) {
            throw new CustomException("不能删除激活的模型");
        }

        modelVersionMapper.deleteById(id);
    }

    /**
     * 修改模型版本
     */
    public void updateById(ModelVersion modelVersion) {
        ModelVersion dbModel = modelVersionMapper.selectById(modelVersion.getId());
        if (ObjectUtil.isNull(dbModel)) {
            throw new CustomException("模型版本不存在");
        }

        modelVersion.setUpdatedTime(LocalDateTime.now().toString());

        // 如果设置为激活，先取消其他激活模型
        if (modelVersion.getIsActive() != null && modelVersion.getIsActive()) {
            modelVersionMapper.deactivateAllModels();
        }

        modelVersionMapper.updateById(modelVersion);
    }

    /**
     * 根据ID查询模型版本
     */
    public ModelVersion selectById(Integer id) {
        ModelVersion modelVersion = modelVersionMapper.selectById(id);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }
        return modelVersion;
    }

    /**
     * 根据模型编号查询
     */
    public ModelVersion selectByModelNo(String modelNo) {
        ModelVersion modelVersion = modelVersionMapper.selectByModelNo(modelNo);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }
        return modelVersion;
    }

    /**
     * 查询所有模型版本
     */
    public List<ModelVersion> selectAll(ModelVersion modelVersion) {
        return modelVersionMapper.selectAll(modelVersion);
    }

    /**
     * 分页查询模型版本
     */
    public PageInfo<ModelVersion> selectPage(ModelVersion modelVersion, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ModelVersion> list = modelVersionMapper.selectAll(modelVersion);
        return PageInfo.of(list);
    }

    /**
     * 获取当前激活的模型
     */
    public ModelVersion getActiveModel() {
        ModelVersion modelVersion = modelVersionMapper.selectActiveModel();
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("没有激活的模型");
        }
        return modelVersion;
    }

    /**
     * 激活指定模型
     */
    public void activateModel(Integer id) {
        ModelVersion modelVersion = modelVersionMapper.selectById(id);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }

        // 取消所有模型的激活状态
        modelVersionMapper.deactivateAllModels();

        // 激活指定模型
        modelVersionMapper.updateActiveStatus(id, true);

        // 更新部署时间
        modelVersion.setDeployedTime(LocalDateTime.now().toString());
        modelVersion.setStatus("DEPLOYED");
        modelVersionMapper.updateById(modelVersion);
    }

    /**
     * 取消激活所有模型
     */
    public void deactivateAllModels() {
        modelVersionMapper.deactivateAllModels();
    }

    /**
     * 更新模型性能指标
     */
    public void updatePerformanceMetrics(Integer id, Double accuracy, Double precision,
                                         Double recall, Double f1Score, Double auc) {
        ModelVersion modelVersion = modelVersionMapper.selectById(id);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }

        modelVersionMapper.updatePerformanceMetrics(id, accuracy, precision, recall, f1Score, auc);
    }

    /**
     * 更新模型文件路径
     */
    public void updateModelFilePath(Integer id, String modelFilePath, String featureScalerPath) {
        ModelVersion modelVersion = modelVersionMapper.selectById(id);
        if (ObjectUtil.isNull(modelVersion)) {
            throw new CustomException("模型版本不存在");
        }

        modelVersionMapper.updateModelFilePath(id, modelFilePath, featureScalerPath);
    }

    /**
     * 获取最新的模型版本
     */
    public ModelVersion getLatestVersion(String modelNo) {
        return modelVersionMapper.selectLatestVersion(modelNo);
    }

    /**
     * 统计模型版本数量
     */
    public Integer count(String status, Boolean isActive) {
        return modelVersionMapper.count(status, isActive);
    }

    /**
     * 获取模型训练历史
     */
    public List<ModelVersion> getTrainingHistory(Integer limit) {
        if (ObjectUtil.isEmpty(limit)) limit = 10;
        return modelVersionMapper.selectTrainingHistory(limit);
    }

    /**
     * 创建新的模型版本
     */
    public ModelVersion createNewVersion(String modelName, String description,
                                         String trainingDataStart, String trainingDataEnd,
                                         Integer sampleCount, Integer featureCount) {

        ModelVersion modelVersion = new ModelVersion();
        String baseModelNo = "LR_STUDENT_RISK";
        modelVersion.setModelNo(baseModelNo + "_" + IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase());
        modelVersion.setVersion(generateNextVersion(baseModelNo));
        modelVersion.setModelName(modelName);
        modelVersion.setDescription(description);
        modelVersion.setTrainingDataStart(trainingDataStart);
        modelVersion.setTrainingDataEnd(trainingDataEnd);
        modelVersion.setSampleCount(sampleCount);
        modelVersion.setFeatureCount(featureCount);
        modelVersion.setAlgorithmType("LOGISTIC_REGRESSION");
        modelVersion.setStatus("TRAINING");
        modelVersion.setTrainingStartTime(LocalDateTime.now().toString());
        modelVersion.setCreatedBy("SYSTEM");

        add(modelVersion);
        return modelVersion;
    }

    /**
     * 生成下一个版本号（修正版）
     */
    private String generateNextVersion(String modelNo) {
        try {
            ModelVersion latest = getLatestVersion(modelNo);
            if (ObjectUtil.isNull(latest) || ObjectUtil.isEmpty(latest.getVersion())) {
                return "1.0.0";
            }

            String version = latest.getVersion();
            String[] parts = version.split("\\.");

            if (parts.length != 3) {
                return "1.0.0";
            }

            try {
                int major = Integer.parseInt(parts[0]);
                int minor = Integer.parseInt(parts[1]);
                int patch = Integer.parseInt(parts[2]);

                // 主版本+1，次版本和补丁版本归零
                return (major + 1) + ".0.0";
            } catch (NumberFormatException e) {
                return "1.0.0";
            }
        } catch (Exception e) {
            // 出现任何异常都返回默认版本
            return "1.0.0";
        }
    }

    /**
     * 完成模型训练
     */
    public void completeTraining(Integer id, String weights, Double bias,
                                 Double accuracy, Double precision, Double recall,
                                 Double f1Score, Double auc, String modelFilePath) {

        ModelVersion modelVersion = selectById(id);
        modelVersion.setWeights(weights);
        modelVersion.setBias(bias);
        modelVersion.setAccuracy(accuracy);
        modelVersion.setPrecision(precision);
        modelVersion.setRecall(recall);
        modelVersion.setF1Score(f1Score);
        modelVersion.setAuc(auc);
        modelVersion.setModelFilePath(modelFilePath);
        modelVersion.setStatus("TRAINED");
        modelVersion.setTrainingEndTime(LocalDateTime.now().toString());
        modelVersion.setUpdatedTime(LocalDateTime.now().toString());

        modelVersionMapper.updateById(modelVersion);
    }
}