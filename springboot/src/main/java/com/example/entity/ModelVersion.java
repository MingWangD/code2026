package com.example.entity;

/**
 * 逻辑回归模型版本实体类
 */
public class ModelVersion {

    private Integer id;
    private String modelNo;          // 模型编号
    private String version;          // 版本号

    // 模型信息
    private String modelName;        // 模型名称
    private String description;      // 模型描述
    private String algorithmType;    // 算法类型

    // 训练数据
    private String trainingDataStart; // 训练数据开始日期
    private String trainingDataEnd;   // 训练数据结束日期
    private Integer sampleCount;      // 样本数量
    private Integer featureCount;     // 特征数量

    // 模型参数
    private String parameters;       // JSON格式模型参数
    private String weights;          // JSON格式权重向量
    private Double bias;             // 偏置项

    // 性能指标
    private Double accuracy;         // 准确率
    private Double precision;        // 精确率
    private Double recall;           // 召回率
    private Double f1Score;          // F1分数
    private Double auc;              // AUC值

    // 阈值配置
    private Double lowThreshold;     // 低风险阈值
    private Double mediumThreshold;  // 中风险阈值
    private Double highThreshold;    // 高风险阈值

    // 状态
    private Boolean isActive;        // 是否激活
    private String status;           // 状态(TRAINING/TRAINED/VALIDATED/DEPLOYED)

    // 文件路径
    private String modelFilePath;    // 模型文件路径
    private String featureScalerPath;// 特征标准化器路径

    // 时间戳
    private String trainingStartTime; // 训练开始时间
    private String trainingEndTime;   // 训练结束时间
    private String deployedTime;      // 部署时间
    private String createdTime;       // 创建时间
    private String updatedTime;       // 更新时间

    // 操作信息
    private String createdBy;        // 创建人
    private String updatedBy;        // 更新人

    // 构造方法
    public ModelVersion() {
        this.algorithmType = "LOGISTIC_REGRESSION";
        this.lowThreshold = 0.3;
        this.mediumThreshold = 0.7;
        this.highThreshold = 0.9;
        this.isActive = false;
        this.status = "TRAINED";
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getModelNo() { return modelNo; }
    public void setModelNo(String modelNo) { this.modelNo = modelNo; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }

    public String getTrainingDataStart() { return trainingDataStart; }
    public void setTrainingDataStart(String trainingDataStart) { this.trainingDataStart = trainingDataStart; }

    public String getTrainingDataEnd() { return trainingDataEnd; }
    public void setTrainingDataEnd(String trainingDataEnd) { this.trainingDataEnd = trainingDataEnd; }

    public Integer getSampleCount() { return sampleCount; }
    public void setSampleCount(Integer sampleCount) { this.sampleCount = sampleCount; }

    public Integer getFeatureCount() { return featureCount; }
    public void setFeatureCount(Integer featureCount) { this.featureCount = featureCount; }

    public String getParameters() { return parameters; }
    public void setParameters(String parameters) { this.parameters = parameters; }

    public String getWeights() { return weights; }
    public void setWeights(String weights) { this.weights = weights; }

    public Double getBias() { return bias; }
    public void setBias(Double bias) { this.bias = bias; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getPrecision() { return precision; }
    public void setPrecision(Double precision) { this.precision = precision; }

    public Double getRecall() { return recall; }
    public void setRecall(Double recall) { this.recall = recall; }

    public Double getF1Score() { return f1Score; }
    public void setF1Score(Double f1Score) { this.f1Score = f1Score; }

    public Double getAuc() { return auc; }
    public void setAuc(Double auc) { this.auc = auc; }

    public Double getLowThreshold() { return lowThreshold; }
    public void setLowThreshold(Double lowThreshold) { this.lowThreshold = lowThreshold; }

    public Double getMediumThreshold() { return mediumThreshold; }
    public void setMediumThreshold(Double mediumThreshold) { this.mediumThreshold = mediumThreshold; }

    public Double getHighThreshold() { return highThreshold; }
    public void setHighThreshold(Double highThreshold) { this.highThreshold = highThreshold; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getModelFilePath() { return modelFilePath; }
    public void setModelFilePath(String modelFilePath) { this.modelFilePath = modelFilePath; }

    public String getFeatureScalerPath() { return featureScalerPath; }
    public void setFeatureScalerPath(String featureScalerPath) { this.featureScalerPath = featureScalerPath; }

    public String getTrainingStartTime() { return trainingStartTime; }
    public void setTrainingStartTime(String trainingStartTime) { this.trainingStartTime = trainingStartTime; }

    public String getTrainingEndTime() { return trainingEndTime; }
    public void setTrainingEndTime(String trainingEndTime) { this.trainingEndTime = trainingEndTime; }

    public String getDeployedTime() { return deployedTime; }
    public void setDeployedTime(String deployedTime) { this.deployedTime = deployedTime; }

    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }

    public String getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public String getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(String updatedBy) { this.updatedBy = updatedBy; }
}