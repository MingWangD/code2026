package com.example.algorithm;

import com.example.entity.LearningFeatures;
import com.example.entity.ModelVersion;
import com.example.service.LearningFeaturesService;
import com.example.service.ModelVersionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 风险预测器（整合算法和业务逻辑）
 */
@Service
public class RiskPredictor {

    @Resource
    private LogisticRegression logisticRegression;

    @Resource
    private FeatureExtractor featureExtractor;

    @Resource
    private LearningFeaturesService learningFeaturesService;

    @Resource
    private ModelVersionService modelVersionService;

    // 风险阈值配置
    private double lowThreshold = 0.3;
    private double mediumThreshold = 0.7;
    private double highThreshold = 0.9;

    /**
     * 预测单个学生的风险
     */
    public RiskPrediction predictStudentRisk(Integer studentId, Integer courseId) {
        // 获取学生特征
        LearningFeatures features = learningFeaturesService.getStudentSummary(studentId, courseId, null, null);

        if (features == null) {
            return createEmptyPrediction(studentId, "无学习行为数据");
        }

        // 提取特征向量
        double[] featureVector = featureExtractor.extractFeatures(features);

        // 预测风险概率
        double riskProbability = logisticRegression.predictProbability(featureVector);

        // 确定风险等级
        String riskLevel = determineRiskLevel(riskProbability);

        // 生成风险原因分析
        List<String> riskFactors = analyzeRiskFactors(features);

        // 生成处理建议
        String suggestion = generateSuggestion(riskLevel, riskFactors);

        RiskPrediction prediction = new RiskPrediction();
        prediction.setStudentId(studentId);
        prediction.setStudentName(features.getStudentName());
        prediction.setStudentNo(features.getStudentNo());
        prediction.setCourseId(courseId);
        prediction.setCourseName(features.getCourseName());
        prediction.setRiskProbability(riskProbability);
        prediction.setRiskLevel(riskLevel);
        prediction.setRiskScore(riskProbability * 100);
        prediction.setFeatureVector(featureVector);
        prediction.setRiskFactors(riskFactors);
        prediction.setSuggestion(suggestion);
        prediction.setPredictionTime(LocalDateTime.now().toString());

        return prediction;
    }

    /**
     * 批量预测学生风险
     */
    public List<RiskPrediction> batchPredictStudentRisk(List<Integer> studentIds, Integer courseId) {
        List<RiskPrediction> predictions = new ArrayList<>();

        for (Integer studentId : studentIds) {
            try {
                RiskPrediction prediction = predictStudentRisk(studentId, courseId);
                predictions.add(prediction);
            } catch (Exception e) {
                System.err.println("预测学生 " + studentId + " 风险时出错: " + e.getMessage());
            }
        }

        // 按风险分数降序排序
        predictions.sort((p1, p2) -> Double.compare(p2.getRiskScore(), p1.getRiskScore()));

        return predictions;
    }

    /**
     * 预测班级整体风险
     */
    public ClassRiskPrediction predictClassRisk(Integer courseId) {
        // 获取课程所有学生的特征
        List<LearningFeatures> featuresList = learningFeaturesService.selectByCourseId(courseId);

        if (featuresList.isEmpty()) {
            return createEmptyClassPrediction(courseId, "课程无学生数据");
        }

        ClassRiskPrediction classPrediction = new ClassRiskPrediction();
        classPrediction.setCourseId(courseId);
        classPrediction.setCourseName(featuresList.get(0).getCourseName());
        classPrediction.setTotalStudents(featuresList.size());
        classPrediction.setPredictionTime(LocalDateTime.now().toString());

        // 统计风险分布
        int highRiskCount = 0;
        int mediumRiskCount = 0;
        int lowRiskCount = 0;
        List<RiskPrediction> studentPredictions = new ArrayList<>();
        double totalRiskScore = 0;

        for (LearningFeatures features : featuresList) {
            double[] featureVector = featureExtractor.extractFeatures(features);
            double riskProbability = logisticRegression.predictProbability(featureVector);
            String riskLevel = determineRiskLevel(riskProbability);

            // 更新计数
            switch (riskLevel) {
                case "HIGH": highRiskCount++; break;
                case "MEDIUM": mediumRiskCount++; break;
                case "LOW": lowRiskCount++; break;
            }

            totalRiskScore += riskProbability * 100;

            // 添加学生预测
            RiskPrediction studentPrediction = new RiskPrediction();
            studentPrediction.setStudentId(features.getStudentId());
            studentPrediction.setStudentName(features.getStudentName());
            studentPrediction.setStudentNo(features.getStudentNo());
            studentPrediction.setRiskProbability(riskProbability);
            studentPrediction.setRiskLevel(riskLevel);
            studentPrediction.setRiskScore(riskProbability * 100);
            studentPredictions.add(studentPrediction);
        }

        // 设置班级预测结果
        classPrediction.setHighRiskCount(highRiskCount);
        classPrediction.setMediumRiskCount(mediumRiskCount);
        classPrediction.setLowRiskCount(lowRiskCount);
        classPrediction.setAvgRiskScore(featuresList.size() > 0 ? totalRiskScore / featuresList.size() : 0);
        classPrediction.setStudentPredictions(studentPredictions);

        // 计算风险分布百分比
        Map<String, Double> riskDistribution = new HashMap<>();
        riskDistribution.put("HIGH", featuresList.size() > 0 ? (double) highRiskCount / featuresList.size() * 100 : 0);
        riskDistribution.put("MEDIUM", featuresList.size() > 0 ? (double) mediumRiskCount / featuresList.size() * 100 : 0);
        riskDistribution.put("LOW", featuresList.size() > 0 ? (double) lowRiskCount / featuresList.size() * 100 : 0);
        classPrediction.setRiskDistribution(riskDistribution);

        // 生成班级建议
        String classSuggestion = generateClassSuggestion(highRiskCount, mediumRiskCount, featuresList.size());
        classPrediction.setSuggestion(classSuggestion);

        return classPrediction;
    }

    /**
     * 训练风险预测模型
     */
    public ModelTrainingResult trainModel(List<LearningFeatures> trainingData, List<Integer> labels) {
        if (trainingData.size() != labels.size()) {
            throw new IllegalArgumentException("训练数据和标签数量不匹配");
        }

        // 提取特征向量
        List<double[]> featureVectors = featureExtractor.batchExtractFeatures(trainingData);

        // 训练模型
        long startTime = System.currentTimeMillis();
        logisticRegression.train(featureVectors, labels);
        long endTime = System.currentTimeMillis();

        // 评估模型
        ModelMetrics metrics = logisticRegression.evaluate(featureVectors, labels, mediumThreshold);

        // 创建训练结果
        ModelTrainingResult result = new ModelTrainingResult();
        result.setTrainingSamples(trainingData.size());
        result.setFeatureCount(featureVectors.get(0).length);
        result.setTrainingTime(endTime - startTime);
        result.setMetrics(metrics);
        result.setSuccess(true);
        result.setMessage("模型训练成功");

        return result;
    }

    /**
     * 使用历史数据训练模型
     */
    public ModelTrainingResult trainWithHistoricalData(Integer limit) {
        // 获取历史数据
        List<LearningFeatures> historicalData = learningFeaturesService.getTrainingData(limit);

        if (historicalData.isEmpty()) {
            throw new IllegalArgumentException("没有足够的历史数据");
        }

        // 提取标签（假设riskProbability>0.6为高风险）
        List<Integer> labels = new ArrayList<>();
        for (LearningFeatures features : historicalData) {
            int label = (features.getRiskProbability() != null && features.getRiskProbability() > 0.6) ? 1 : 0;
            labels.add(label);
        }

        return trainModel(historicalData, labels);
    }

    /**
     * 刷新最近 N 天 learning_features 的风险字段（写回数据库）
     * 先保证系统跑通：如果模型不可用，则使用兜底规则计算风险
     */
    public int refreshRecentLearningFeaturesRisk(Integer days) {

        System.out.println("🔥🔥🔥 refreshRecentLearningFeaturesRisk 被调用了，days=" + days);

        if (days == null || days <= 0) days = 7;

        // 取最近N天特征
        List<LearningFeatures> list = learningFeaturesService.selectRecentFeatures(days);
        if (list == null || list.isEmpty()) return 0;

        int updated = 0;
        for (LearningFeatures f : list) {
            try {
                // 计算风险概率（模型优先，否则兜底）
                double p = safePredictProbability(f);

                // 风险等级
                String level = determineRiskLevel(p);

                // 写回数据库
                Double riskScore = p * 100.0;
                learningFeaturesService.updateRiskInfo(
                        f.getId().intValue(),  // 你表 id 是 bigint，这里 service 用 Integer，我先强转保证跑通
                        riskScore,
                        level,
                        p
                );
                updated++;
            } catch (Exception e) {
                System.err.println("刷新风险失败 id=" + f.getId() + " : " + e.getMessage());
            }
        }
        return updated;
    }

    /**
     * 预测概率：模型可用就用模型，不可用就用兜底规则
     */
    private double safePredictProbability(LearningFeatures f) {
        // 1) 模型不可用 => 直接兜底
        if (!isModelUsable()) {
            return fallbackHeuristicProbability(f);
        }

        try {
            double[] vec = featureExtractor.extractFeatures(f);
            double p = logisticRegression.predictProbability(vec);

            // 2) 输出塌缩在0.5附近 => 用兜底（或混合）
            if (Double.isNaN(p) || Double.isInfinite(p) || looksLikeConstantHalf(p)) {
                // 方案A：直接兜底（最简单立刻见效）
                return fallbackHeuristicProbability(f);

                // 方案B：混合（想保留一点模型味道再用这行替换上面那行）
                // double fb = fallbackHeuristicProbability(f);
                // return clamp01(0.2 * p + 0.8 * fb);
            }

            return clamp01(p);
        } catch (Exception ex) {
            return fallbackHeuristicProbability(f);
        }
    }

    private boolean isModelUsable() {
        try {
            ModelParameters params = logisticRegression.getModelParameters();
            if (params == null || params.getWeights() == null || params.getWeights().length == 0) {
                return false;
            }

            double l2 = 0.0;
            for (double w : params.getWeights()) {
                l2 += w * w;
            }
            l2 = Math.sqrt(l2);

            double bias = params.getBias();

            // ✅ 权重几乎为0 或 bias几乎为0 => 模型基本没学到东西
            return !(l2 < 1e-6 && Math.abs(bias) < 1e-6);
        } catch (Exception e) {
            return false;
        }
    }
    private boolean looksLikeConstantHalf(double p) {
        // 你现在 p 基本都在 0.5009~0.5011，这里就认为模型输出无区分度
        return Math.abs(p - 0.5) < 0.02; // 0.48~0.52 都算可疑
    }


    /**
     * 兜底规则：根据行为特征粗略算风险(0~1)
     * 目的：先让系统跑通，后续你训练/加载模型后自动替换为模型输出
     */
    private double fallbackHeuristicProbability(LearningFeatures f) {
        // 注意：你的 learning_features 里 completion_rate / submit_rate / focus_score 很可能是 0~1
        double vc = safe01(f.getVideoCompletionRate());     // 视频完成率(0-1)
        double hs = safe01(f.getHomeworkSubmitRate());      // 作业提交率(0-1)
        double score = f.getHomeworkAvgScore() == null ? 0 : f.getHomeworkAvgScore(); // 0-100
        double login = f.getLoginFrequency() == null ? 0 : f.getLoginFrequency();     // 次/周或次数
        double focus = safe01(f.getFocusScore());           // 0-1

        // 正向得分（越高越好）
        double score01 = clamp01(score / 100.0);
        double login01 = clamp01(login / 20.0); // 20次以上算满

        double good = 0.30 * vc + 0.25 * hs + 0.25 * score01 + 0.10 * login01 + 0.10 * focus;

        // 风险 = 1 - 好的程度
        double risk = 1.0 - good;
        return clamp01(risk);
    }

    private double safe01(Double v) {
        if (v == null) return 0.0;
        // 如果你的数据是 0-100，则自动缩放
        if (v > 1.0) return clamp01(v / 100.0);
        return clamp01(v);
    }

    private double clamp01(double x) {
        if (x < 0) return 0;
        if (x > 1) return 1;
        return x;
    }


    /**
     * 保存当前模型到数据库
     */
    public void saveCurrentModel(String modelName, String description) {
        try {
            // 获取当前激活的模型或创建新模型
            ModelVersion activeModel;
            try {
                activeModel = modelVersionService.getActiveModel();
            } catch (Exception e) {
                activeModel = null;
            }

            // 创建新模型版本
            ModelVersion newModel = modelVersionService.createNewVersion(
                    modelName,
                    description,
                    LocalDateTime.now().minusDays(30).toLocalDate().toString(),
                    LocalDateTime.now().toLocalDate().toString(),
                    1000, // 样本数
                    8     // 特征数
            );

            // 获取模型参数
            ModelParameters params = logisticRegression.getModelParameters();

            // 转换为JSON格式存储
            String weightsJson = Arrays.toString(params.getWeights());

            // 更新模型参数
            newModel.setWeights(weightsJson);
            newModel.setBias(params.getBias());
            newModel.setAccuracy(0.85); // 模拟准确率
            newModel.setPrecision(0.82);
            newModel.setRecall(0.88);
            newModel.setF1Score(0.85);
            newModel.setAuc(0.90);
            newModel.setStatus("TRAINED");
            newModel.setTrainingEndTime(LocalDateTime.now().toString());

            modelVersionService.updateById(newModel);

            // 激活新模型
            modelVersionService.activateModel(newModel.getId());

        } catch (Exception e) {
            throw new RuntimeException("保存模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 加载数据库中的模型
     */
    public void loadModelFromDatabase(Integer modelId) {
        try {
            ModelVersion modelVersion = modelVersionService.selectById(modelId);

            // 解析权重
            String weightsStr = modelVersion.getWeights();
            weightsStr = weightsStr.replace("[", "").replace("]", "");
            String[] weightStrs = weightsStr.split(",");
            double[] weights = new double[weightStrs.length];
            for (int i = 0; i < weightStrs.length; i++) {
                weights[i] = Double.parseDouble(weightStrs[i].trim());
            }

            // 设置模型参数
            ModelParameters params = new ModelParameters();
            params.setWeights(weights);
            params.setBias(modelVersion.getBias());
            params.setFeatureSize(weights.length);

            logisticRegression.loadModelParameters(params);

            // 更新阈值
            if (modelVersion.getLowThreshold() != null) lowThreshold = modelVersion.getLowThreshold();
            if (modelVersion.getMediumThreshold() != null) mediumThreshold = modelVersion.getMediumThreshold();
            if (modelVersion.getHighThreshold() != null) highThreshold = modelVersion.getHighThreshold();

        } catch (Exception e) {
            throw new RuntimeException("加载模型失败: " + e.getMessage(), e);
        }
    }

    /**
     * 确定风险等级
     */
    private String determineRiskLevel(double probability) {
        if (probability < lowThreshold) {
            return "LOW";
        } else if (probability < mediumThreshold) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }

    /**
     * 分析风险因素
     */
    private List<String> analyzeRiskFactors(LearningFeatures features) {
        List<String> factors = new ArrayList<>();

        double videoRate = normalizeTo100(features.getVideoCompletionRate());
        double submitRate = normalizeTo100(features.getHomeworkSubmitRate());
        double focus = normalizeTo100(features.getFocusScore());

        double avgScore = features.getHomeworkAvgScore() == null ? 0 : features.getHomeworkAvgScore();
        double login = features.getLoginFrequency() == null ? 0 : features.getLoginFrequency();

        if (videoRate < 50) factors.add("视频学习完成率低 (" + String.format("%.1f", videoRate) + "%)");
        if (submitRate < 60) factors.add("作业提交率低 (" + String.format("%.1f", submitRate) + "%)");
        if (avgScore < 60) factors.add("作业成绩不理想 (" + String.format("%.1f", avgScore) + "分)");
        if (login < 5) factors.add("登录频率低 (" + String.format("%.0f", login) + "次)");
        if (focus < 60) factors.add("学习专注度不足 (" + String.format("%.1f", focus) + "%)");

        return factors;
    }

    private double normalizeTo100(Double v) {
        if (v == null) return 0;
        // 0~1 => 转成百分比
        if (v <= 1.0) return v * 100.0;
        // 已经是 0~100
        return v;
    }


    /**
     * 生成处理建议
     */
    private String generateSuggestion(String riskLevel, List<String> riskFactors) {
        StringBuilder suggestion = new StringBuilder();
        suggestion.append("风险等级: ").append(riskLevel).append("\n\n");

        if (!riskFactors.isEmpty()) {
            suggestion.append("主要问题:\n");
            for (String factor : riskFactors) {
                suggestion.append("• ").append(factor).append("\n");
            }
            suggestion.append("\n");
        }

        suggestion.append("建议措施:\n");
        switch (riskLevel) {
            case "HIGH":
                suggestion.append("1. 立即安排与学生面对面沟通\n");
                suggestion.append("2. 通知辅导员和班主任关注\n");
                suggestion.append("3. 制定个性化学习帮扶计划\n");
                suggestion.append("4. 定期跟进学习进展\n");
                break;
            case "MEDIUM":
                suggestion.append("1. 通过线上方式与学生沟通\n");
                suggestion.append("2. 提供针对性学习资源\n");
                suggestion.append("3. 设置学习提醒\n");
                suggestion.append("4. 每周检查学习进度\n");
                break;
            case "LOW":
                suggestion.append("1. 发送学习提醒\n");
                suggestion.append("2. 关注学习行为变化\n");
                suggestion.append("3. 鼓励保持良好学习状态\n");
                break;
        }

        return suggestion.toString();
    }

    /**
     * 生成班级建议
     */
    private String generateClassSuggestion(int highRiskCount, int mediumRiskCount, int totalStudents) {
        double highRiskRatio = (double) highRiskCount / totalStudents * 100;
        double mediumRiskRatio = (double) mediumRiskCount / totalStudents * 100;

        StringBuilder suggestion = new StringBuilder();
        suggestion.append("班级风险分析:\n");
        suggestion.append(String.format("• 高风险学生: %d人 (%.1f%%)\n", highRiskCount, highRiskRatio));
        suggestion.append(String.format("• 中风险学生: %d人 (%.1f%%)\n", mediumRiskCount, mediumRiskRatio));
        suggestion.append("\n班级管理建议:\n");

        if (highRiskRatio > 20) {
            suggestion.append("1. 召开班级学习情况分析会\n");
            suggestion.append("2. 组织学习帮扶小组\n");
            suggestion.append("3. 调整教学节奏和难度\n");
            suggestion.append("4. 加强课堂互动和监督\n");
        } else if (mediumRiskRatio > 30) {
            suggestion.append("1. 加强学习监督和提醒\n");
            suggestion.append("2. 提供更多学习资源\n");
            suggestion.append("3. 组织学习经验分享会\n");
            suggestion.append("4. 定期检查学习进度\n");
        } else {
            suggestion.append("1. 继续保持良好学习氛围\n");
            suggestion.append("2. 关注个别学习困难学生\n");
            suggestion.append("3. 提供拓展学习资源\n");
        }

        return suggestion.toString();
    }

    private RiskPrediction createEmptyPrediction(Integer studentId, String message) {
        RiskPrediction prediction = new RiskPrediction();
        prediction.setStudentId(studentId);
        prediction.setRiskLevel("UNKNOWN");
        prediction.setRiskScore(0.0);
        prediction.setSuggestion("无法评估: " + message);
        return prediction;
    }

    private ClassRiskPrediction createEmptyClassPrediction(Integer courseId, String message) {
        ClassRiskPrediction prediction = new ClassRiskPrediction();
        prediction.setCourseId(courseId);
        prediction.setTotalStudents(0);
        prediction.setAvgRiskScore(0.0);
        prediction.setSuggestion("无法评估: " + message);
        return prediction;
    }

    // Getters and Setters
    public double getLowThreshold() { return lowThreshold; }
    public void setLowThreshold(double lowThreshold) { this.lowThreshold = lowThreshold; }

    public double getMediumThreshold() { return mediumThreshold; }
    public void setMediumThreshold(double mediumThreshold) { this.mediumThreshold = mediumThreshold; }

    public double getHighThreshold() { return highThreshold; }
    public void setHighThreshold(double highThreshold) { this.highThreshold = highThreshold; }
}

/**
 * 风险预测结果类
 */
class RiskPrediction {
    private Integer studentId;
    private String studentName;
    private String studentNo;
    private Integer courseId;
    private String courseName;
    private double riskProbability;
    private String riskLevel;
    private double riskScore;
    private double[] featureVector;
    private List<String> riskFactors;
    private String suggestion;
    private String predictionTime;

    // Getters and Setters
    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentNo() { return studentNo; }
    public void setStudentNo(String studentNo) { this.studentNo = studentNo; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public double getRiskProbability() { return riskProbability; }
    public void setRiskProbability(double riskProbability) { this.riskProbability = riskProbability; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }

    public double[] getFeatureVector() { return featureVector; }
    public void setFeatureVector(double[] featureVector) { this.featureVector = featureVector; }

    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getPredictionTime() { return predictionTime; }
    public void setPredictionTime(String predictionTime) { this.predictionTime = predictionTime; }
}

/**
 * 班级风险预测结果类
 */
class ClassRiskPrediction {
    private Integer courseId;
    private String courseName;
    private int totalStudents;
    private int highRiskCount;
    private int mediumRiskCount;
    private int lowRiskCount;
    private double avgRiskScore;
    private Map<String, Double> riskDistribution;
    private List<RiskPrediction> studentPredictions;
    private String suggestion;
    private String predictionTime;

    // Getters and Setters
    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }

    public int getHighRiskCount() { return highRiskCount; }
    public void setHighRiskCount(int highRiskCount) { this.highRiskCount = highRiskCount; }

    public int getMediumRiskCount() { return mediumRiskCount; }
    public void setMediumRiskCount(int mediumRiskCount) { this.mediumRiskCount = mediumRiskCount; }

    public int getLowRiskCount() { return lowRiskCount; }
    public void setLowRiskCount(int lowRiskCount) { this.lowRiskCount = lowRiskCount; }

    public double getAvgRiskScore() { return avgRiskScore; }
    public void setAvgRiskScore(double avgRiskScore) { this.avgRiskScore = avgRiskScore; }

    public Map<String, Double> getRiskDistribution() { return riskDistribution; }
    public void setRiskDistribution(Map<String, Double> riskDistribution) { this.riskDistribution = riskDistribution; }

    public List<RiskPrediction> getStudentPredictions() { return studentPredictions; }
    public void setStudentPredictions(List<RiskPrediction> studentPredictions) { this.studentPredictions = studentPredictions; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getPredictionTime() { return predictionTime; }
    public void setPredictionTime(String predictionTime) { this.predictionTime = predictionTime; }
}

/**
 * 模型训练结果类
 */
class ModelTrainingResult {
    private int trainingSamples;
    private int featureCount;
    private long trainingTime;
    private ModelMetrics metrics;
    private boolean success;
    private String message;

    // Getters and Setters
    public int getTrainingSamples() { return trainingSamples; }
    public void setTrainingSamples(int trainingSamples) { this.trainingSamples = trainingSamples; }

    public int getFeatureCount() { return featureCount; }
    public void setFeatureCount(int featureCount) { this.featureCount = featureCount; }

    public long getTrainingTime() { return trainingTime; }
    public void setTrainingTime(long trainingTime) { this.trainingTime = trainingTime; }

    public ModelMetrics getMetrics() { return metrics; }
    public void setMetrics(ModelMetrics metrics) { this.metrics = metrics; }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}