package com.example.algorithm;

import com.example.entity.LearningFeatures;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 特征提取器
 * 从学习行为数据中提取特征向量
 *
 * 关键修复：
 * 1) 兼容字段既可能是 0~1，也可能是 0~100（自动归一到 0~1）
 * 2) videoWatchTime 兼容分钟/小时：大于 24 认为是“分钟”，自动转小时
 */
@Component
public class FeatureExtractor {

    /**
     * 从LearningFeatures实体提取特征向量
     * 输出固定8维，并且每一维都在 0~1
     */
    public double[] extractFeatures(LearningFeatures features) {
        if (features == null) {
            throw new IllegalArgumentException("特征数据不能为空");
        }

        double[] featureVector = new double[8];

        // 1. 视频观看时间（你的库里常见是“分钟”例如 90/173）
        //    这里自动识别：>24 视为分钟，转为小时；然后按 0~10 小时归一化
        double watchTimeHours = toHoursSmart(features.getVideoWatchTime());
        featureVector[0] = normalizeRange(watchTimeHours, 0.0, 10.0);

        // 2. 视频完成率（可能是 0~1 或 0~100）
        featureVector[1] = norm01Smart(features.getVideoCompletionRate());

        // 3. 作业提交率（可能是 0~1 或 0~100）
        featureVector[2] = norm01Smart(features.getHomeworkSubmitRate());

        // 4. 作业平均分（常见 0~100）
        featureVector[3] = normalizeRange(safeDouble(features.getHomeworkAvgScore()), 0.0, 100.0);

        // 5. 登录频率（次/周），按 0~50 归一化（你也可以改成 0~20 更敏感）
        featureVector[4] = normalizeRange(safeDouble(features.getLoginFrequency()), 0.0, 50.0);

        // 6. 专注度评分（可能是 0~1 或 0~100）
        featureVector[5] = norm01Smart(features.getFocusScore());

        // 7. 学习持续性（可能是 0~1 或 0~100）
        featureVector[6] = norm01Smart(features.getStudyConsistency());

        // 8. 互动水平（可能是 0~1 或 0~100）
        featureVector[7] = norm01Smart(features.getInteractionLevel());

        return featureVector;
    }

    /**
     * 批量提取特征向量
     */
    public List<double[]> batchExtractFeatures(List<LearningFeatures> featuresList) {
        List<double[]> featureVectors = new ArrayList<>();
        if (featuresList == null) return featureVectors;

        for (LearningFeatures features : featuresList) {
            featureVectors.add(extractFeatures(features));
        }
        return featureVectors;
    }

    /**
     * 从原始行为数据计算特征
     * 注意：这里我也做了“0~1/0~100兼容”，以及 watchTime 的分钟/小时处理
     */
    public double[] extractFromRawData(
            double videoWatchTime,          // 可能分钟 or 小时
            double videoCompletionRate,     // 可能 0~1 or 0~100
            double homeworkSubmitRate,      // 可能 0~1 or 0~100
            double homeworkAvgScore,        // 0~100
            int loginFrequency,             // 次/周
            double focusScore,              // 可能 0~1 or 0~100
            double studyConsistency,         // 可能 0~1 or 0~100
            double interactionLevel          // 可能 0~1 or 0~100
    ) {
        double[] featureVector = new double[8];

        double watchHours = toHoursSmart(videoWatchTime);
        featureVector[0] = normalizeRange(watchHours, 0.0, 10.0);

        featureVector[1] = norm01Smart(videoCompletionRate);
        featureVector[2] = norm01Smart(homeworkSubmitRate);
        featureVector[3] = normalizeRange(homeworkAvgScore, 0.0, 100.0);
        featureVector[4] = normalizeRange(loginFrequency, 0.0, 50.0);
        featureVector[5] = norm01Smart(focusScore);
        featureVector[6] = norm01Smart(studyConsistency);
        featureVector[7] = norm01Smart(interactionLevel);

        return featureVector;
    }

    /**
     * 归一化到 0~1（线性缩放 + 截断）
     */
    private double normalizeRange(double value, double min, double max) {
        if (max <= min) return 0.0;
        double normalized = (value - min) / (max - min);
        return clamp01(normalized);
    }

    /**
     * 智能把 0~1 或 0~100 的值统一变成 0~1
     * 规则：>1.0 认为是百分比/分数，除以100
     */
    private double norm01Smart(Number n) {
        if (n == null) return 0.0;
        double v = n.doubleValue();
        if (v > 1.0) v = v / 100.0;
        return clamp01(v);
    }

    /**
     * videoWatchTime 智能转小时：
     * - 如果值 > 24：大概率是“分钟”，转小时
     * - 否则认为已经是“小时”
     */
    private double toHoursSmart(Number n) {
        if (n == null) return 0.0;
        double v = n.doubleValue();
        if (v > 24.0) {     // 例如 90, 173 这种
            return v / 60.0;
        }
        return v;           // 已经是小时
    }

    private double toHoursSmart(double v) {
        if (v > 24.0) return v / 60.0;
        return v;
    }

    private double safeDouble(Number n) {
        return n == null ? 0.0 : n.doubleValue();
    }

    private double clamp01(double x) {
        if (x < 0) return 0.0;
        if (x > 1) return 1.0;
        return x;
    }

    /**
     * 反归一化
     */
    public double denormalize(double normalizedValue, double min, double max) {
        return normalizedValue * (max - min) + min;
    }

    /**
     * 计算学习行为趋势特征
     * @param recentFeatures 近期特征列表（按时间顺序）
     * @return 趋势特征向量
     */
    public double[] calculateTrendFeatures(List<LearningFeatures> recentFeatures) {
        if (recentFeatures == null || recentFeatures.size() < 2) {
            return new double[8];
        }

        double[] trendFeatures = new double[8];

        int n = recentFeatures.size();
        LearningFeatures latest = recentFeatures.get(n - 1);

        double[] means = new double[8];
        for (LearningFeatures f : recentFeatures) {
            double[] vec = extractFeatures(f);
            for (int i = 0; i < 8; i++) {
                means[i] += vec[i];
            }
        }
        for (int i = 0; i < 8; i++) {
            means[i] /= n;
        }

        double[] latestFeatures = extractFeatures(latest);
        for (int i = 0; i < 8; i++) {
            trendFeatures[i] = latestFeatures[i] - means[i];
        }

        return trendFeatures;
    }

    /**
     * 计算风险特征重要性（模拟）
     */
    public double[] calculateFeatureImportance() {
        return new double[] {
                0.15,
                0.20,
                0.25,
                0.20,
                0.10,
                0.05,
                0.03,
                0.02
        };
    }

    /**
     * 生成特征描述
     */
    public String describeFeatures(double[] features) {
        if (features == null || features.length != 8) {
            return "特征数据异常";
        }

        String[] featureNames = {
                "视频观看时长(归一化)", "视频完成率(归一化)", "作业提交率(归一化)", "作业平均分(归一化)",
                "登录频率(归一化)", "专注度(归一化)", "学习持续性(归一化)", "互动水平(归一化)"
        };

        StringBuilder description = new StringBuilder("特征分析：\n");
        for (int i = 0; i < features.length; i++) {
            description.append(String.format("%s: %.4f\n", featureNames[i], features[i]));
        }
        return description.toString();
    }

    /**
     * 标准化特征向量（Z-score标准化）
     */
    public double[] standardizeFeatures(double[] features, double[] mean, double[] std) {
        if (features.length != mean.length || features.length != std.length) {
            throw new IllegalArgumentException("维度不匹配");
        }

        double[] standardized = new double[features.length];
        for (int i = 0; i < features.length; i++) {
            standardized[i] = (features[i] - mean[i]) / (std[i] + 1e-10);
        }
        return standardized;
    }
}
