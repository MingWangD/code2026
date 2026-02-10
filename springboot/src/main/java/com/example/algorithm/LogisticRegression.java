package com.example.algorithm;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 逻辑回归算法实现类
 * 用于学生学业风险预测
 */
@Component
public class LogisticRegression {

    private double[] weights;      // 权重向量
    private double bias;           // 偏置项
    private double learningRate = 0.01;  // 学习率
    private int maxIterations = 1000;    // 最大迭代次数
    private int featureSize;       // 特征维度

    /**
     * 初始化模型
     */
    public LogisticRegression() {
        // 默认8个特征：视频时长、完成率、作业提交率、作业均分、登录频率、专注度、学习持续性、互动水平
        this.featureSize = 8;
        this.weights = new double[featureSize];
        this.bias = 0.0;
        initializeWeights();
    }

    /**
     * 初始化模型（指定特征维度）
     */
    public LogisticRegression(int featureSize) {
        this.featureSize = featureSize;
        this.weights = new double[featureSize];
        this.bias = 0.0;
        initializeWeights();
    }

    /**
     * 初始化权重
     */
    private void initializeWeights() {
        for (int i = 0; i < featureSize; i++) {
            weights[i] = Math.random() * 0.01 - 0.005; // 小随机数
        }
        bias = Math.random() * 0.01 - 0.005;
    }

    /**
     * 训练模型
     * @param features 特征矩阵，每行是一个样本的特征向量
     * @param labels 标签列表，0表示低风险，1表示高风险
     */
    public void train(List<double[]> features, List<Integer> labels) {
        if (features == null || labels == null || features.size() != labels.size()) {
            throw new IllegalArgumentException("特征和标签数量不匹配");
        }

        int m = features.size();

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            double[] weightGradients = new double[featureSize];
            double biasGradient = 0.0;

            // 计算梯度
            for (int i = 0; i < m; i++) {
                double[] x = features.get(i);
                double predicted = predictProbability(x);
                double error = predicted - labels.get(i);

                // 累积梯度
                for (int j = 0; j < featureSize; j++) {
                    weightGradients[j] += error * x[j];
                }
                biasGradient += error;
            }

            // 更新权重和偏置
            for (int j = 0; j < featureSize; j++) {
                weights[j] -= learningRate * weightGradients[j] / m;
            }
            bias -= learningRate * biasGradient / m;

            // 每100次迭代计算一次损失
            if (iteration % 100 == 0) {
                double loss = calculateLoss(features, labels);
                System.out.printf("迭代 %d, 损失: %.4f%n", iteration, loss);
            }
        }

        System.out.println("模型训练完成");
        System.out.println("权重: " + Arrays.toString(weights));
        System.out.println("偏置: " + bias);
    }

    /**
     * 预测单个样本的风险概率
     * @param features 特征向量
     * @return 风险概率(0-1)
     */
    public double predictProbability(double[] features) {
        if (features.length != featureSize) {
            throw new IllegalArgumentException("特征维度不匹配，期望: " + featureSize + ", 实际: " + features.length);
        }

        double z = bias;
        for (int i = 0; i < featureSize; i++) {
            z += weights[i] * features[i];
        }

        return sigmoid(z);
    }

    /**
     * 预测风险等级
     * @param features 特征向量
     * @param thresholds 阈值数组 [低风险阈值, 中风险阈值, 高风险阈值]
     * @return 风险等级 LOW/MEDIUM/HIGH
     */
    public String predictRiskLevel(double[] features, double[] thresholds) {
        double probability = predictProbability(features);

        if (probability < thresholds[0]) {
            return "LOW";
        } else if (probability < thresholds[1]) {
            return "MEDIUM";
        } else {
            return "HIGH";
        }
    }

    /**
     * 批量预测
     */
    public double[] predictBatch(List<double[]> featuresList) {
        double[] predictions = new double[featuresList.size()];
        for (int i = 0; i < featuresList.size(); i++) {
            predictions[i] = predictProbability(featuresList.get(i));
        }
        return predictions;
    }

    /**
     * 计算损失函数（交叉熵损失）
     */
    private double calculateLoss(List<double[]> features, List<Integer> labels) {
        double loss = 0.0;
        int m = features.size();

        for (int i = 0; i < m; i++) {
            double predicted = predictProbability(features.get(i));
            int y = labels.get(i);

            // 交叉熵损失
            loss += y * Math.log(predicted + 1e-10) + (1 - y) * Math.log(1 - predicted + 1e-10);
        }

        return -loss / m;
    }

    /**
     * Sigmoid函数
     */
    private double sigmoid(double z) {
        return 1.0 / (1.0 + Math.exp(-z));
    }

    /**
     * 获取模型参数
     */
    public ModelParameters getModelParameters() {
        ModelParameters params = new ModelParameters();
        params.setWeights(Arrays.copyOf(weights, weights.length));
        params.setBias(bias);
        params.setFeatureSize(featureSize);
        return params;
    }

    /**
     * 加载模型参数
     */
    public void loadModelParameters(ModelParameters params) {
        if (params.getFeatureSize() != featureSize) {
            throw new IllegalArgumentException("特征维度不匹配");
        }

        this.weights = Arrays.copyOf(params.getWeights(), params.getWeights().length);
        this.bias = params.getBias();
    }

    /**
     * 评估模型性能
     */
    public ModelMetrics evaluate(List<double[]> testFeatures, List<Integer> testLabels, double threshold) {
        int truePositive = 0;  // 真正例
        int falsePositive = 0; // 假正例
        int trueNegative = 0;  // 真负例
        int falseNegative = 0; // 假负例

        for (int i = 0; i < testFeatures.size(); i++) {
            double probability = predictProbability(testFeatures.get(i));
            boolean predictedPositive = probability >= threshold;
            boolean actualPositive = testLabels.get(i) == 1;

            if (predictedPositive && actualPositive) truePositive++;
            else if (predictedPositive && !actualPositive) falsePositive++;
            else if (!predictedPositive && !actualPositive) trueNegative++;
            else falseNegative++;
        }

        ModelMetrics metrics = new ModelMetrics();
        metrics.setTruePositive(truePositive);
        metrics.setFalsePositive(falsePositive);
        metrics.setTrueNegative(trueNegative);
        metrics.setFalseNegative(falseNegative);

        // 计算各项指标
        int total = testFeatures.size();
        metrics.setAccuracy((double) (truePositive + trueNegative) / total);

        double precisionDenominator = truePositive + falsePositive;
        metrics.setPrecision(precisionDenominator > 0 ? (double) truePositive / precisionDenominator : 0);

        double recallDenominator = truePositive + falseNegative;
        metrics.setRecall(recallDenominator > 0 ? (double) truePositive / recallDenominator : 0);

        double f1Denominator = metrics.getPrecision() + metrics.getRecall();
        metrics.setF1Score(f1Denominator > 0 ? 2 * metrics.getPrecision() * metrics.getRecall() / f1Denominator : 0);

        return metrics;
    }

    // Getters and Setters
    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double getBias() {
        return bias;
    }

    public void setBias(double bias) {
        this.bias = bias;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(double learningRate) {
        this.learningRate = learningRate;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public int getFeatureSize() {
        return featureSize;
    }
}

/**
 * 模型参数类
 */
class ModelParameters {
    private double[] weights;
    private double bias;
    private int featureSize;

    // Getters and Setters
    public double[] getWeights() { return weights; }
    public void setWeights(double[] weights) { this.weights = weights; }

    public double getBias() { return bias; }
    public void setBias(double bias) { this.bias = bias; }

    public int getFeatureSize() { return featureSize; }
    public void setFeatureSize(int featureSize) { this.featureSize = featureSize; }
}

/**
 * 模型评估指标类
 */
class ModelMetrics {
    private int truePositive;
    private int falsePositive;
    private int trueNegative;
    private int falseNegative;
    private double accuracy;
    private double precision;
    private double recall;
    private double f1Score;

    // Getters and Setters
    public int getTruePositive() { return truePositive; }
    public void setTruePositive(int truePositive) { this.truePositive = truePositive; }

    public int getFalsePositive() { return falsePositive; }
    public void setFalsePositive(int falsePositive) { this.falsePositive = falsePositive; }

    public int getTrueNegative() { return trueNegative; }
    public void setTrueNegative(int trueNegative) { this.trueNegative = trueNegative; }

    public int getFalseNegative() { return falseNegative; }
    public void setFalseNegative(int falseNegative) { this.falseNegative = falseNegative; }

    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double accuracy) { this.accuracy = accuracy; }

    public double getPrecision() { return precision; }
    public void setPrecision(double precision) { this.precision = precision; }

    public double getRecall() { return recall; }
    public void setRecall(double recall) { this.recall = recall; }

    public double getF1Score() { return f1Score; }
    public void setF1Score(double f1Score) { this.f1Score = f1Score; }

    @Override
    public String toString() {
        return String.format(
                "准确率: %.2f%%, 精确率: %.2f%%, 召回率: %.2f%%, F1分数: %.2f%%",
                accuracy * 100, precision * 100, recall * 100, f1Score * 100
        );
    }
}