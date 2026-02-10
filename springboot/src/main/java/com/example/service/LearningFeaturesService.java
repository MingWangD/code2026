package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.entity.Homework;
import com.example.entity.LearningFeatures;
import com.example.entity.StudentBehavior;
import com.example.exception.CustomException;
import com.example.mapper.HomeworkMapper;
import com.example.mapper.LearningFeaturesMapper;
import com.example.mapper.StudentBehaviorMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LearningFeaturesService {

    @Resource
    private LearningFeaturesMapper learningFeaturesMapper;

    // ✅ 新增：事件表 mapper（student_behavior_event）
    @Resource
    private StudentBehaviorMapper studentBehaviorMapper;

    // ✅ 新增：作业表 mapper（用于算提交率分母）
    @Resource
    private HomeworkMapper homeworkMapper;

    public List<LearningFeatures> selectRecentFeatures(Integer days) {
        if (days == null || days <= 0) days = 7;
        return learningFeaturesMapper.selectRecentFeatures(days);
    }

    /**
     * ✅ 核心：刷新“当天”learning_features（由事件触发）
     * 目前先只做：作业提交率、作业均分、迟交次数、质量分（=均分）
     *
     * 你后面再扩展：视频/登录/互动等特征
     */
    @Transactional(rollbackFor = Exception.class)
    public void refreshTodayFeatures(Integer studentId, Integer courseId) {
        if (studentId == null || courseId == null) {
            throw new CustomException("studentId/courseId 不能为空");
        }

        String today = LocalDate.now().toString();
        String now = LocalDateTime.now().toString();

        // 1) 查今天是否已有行（你已有这个 Mapper 方法）
        LearningFeatures row = learningFeaturesMapper.selectByStudentCourseDate(studentId, courseId, today);

        // 2) 查今天作业提交事件（需要你在 StudentBehaviorMapper 增加这个方法）
        List<StudentBehavior> submits =
                studentBehaviorMapper.selectHomeworkSubmitEventsByStudentCourseAndDate(studentId, courseId, today);

        // 3) 计算迟交次数、均分
        int delayCount = 0;
        double sumScore = 0.0;
        int scoredCnt = 0;

        Set<String> distinctHomeworkIds = new HashSet<>();

        if (submits != null) {
            for (StudentBehavior e : submits) {
                if (e == null) continue;

                if (e.getIsLate() != null && e.getIsLate() == 1) delayCount++;

                if (e.getScore() != null) {
                    sumScore += e.getScore();
                    scoredCnt++;
                }

                if (ObjectUtil.isNotEmpty(e.getRelatedId())) {
                    distinctHomeworkIds.add(e.getRelatedId());
                }
            }
        }

        double avgScore = (scoredCnt > 0) ? (sumScore / scoredCnt) : 0.0;
        double qualityScore = avgScore; // 先用均分代替质量分（后面可换规则）

        // 4) 提交率：distinct(homeworkId)/课程作业总数
        int totalHomework = 0;
        List<Homework> all = homeworkMapper.selectByCourseId(courseId);
        if (all != null) totalHomework = all.size();

        double submitRate = 0.0;
        if (totalHomework > 0) {
            submitRate = Math.min(1.0, distinctHomeworkIds.size() * 1.0 / totalHomework);
        }

        // 5) 写回 learning_features：没有则插入，有则更新
        if (row == null) {
            LearningFeatures nf = new LearningFeatures();
            nf.setStudentId(studentId);
            nf.setCourseId(courseId);
            nf.setFeatureDate(today);

            // ⚠️ 你的表里 student_name/student_no/course_name 可能是 NOT NULL
            // 如果是 NOT NULL，你必须在这里补齐：从 student/course 表查出来再 set
            // 目前先不强填（避免你项目里 Student/Course 字段不一致导致编译失败）

            nf.setHomeworkSubmitRate(submitRate);
            nf.setHomeworkAvgScore(avgScore);
            nf.setHomeworkDelayCount(delayCount);
            nf.setHomeworkQualityScore(qualityScore);

            nf.setCreatedTime(now);
            nf.setUpdatedTime(now);

            learningFeaturesMapper.insert(nf);
        } else {
            // ✅ 你现在 LearningFeaturesMapper 里没有 updateHomeworkPart 的话
            // 就用 updateById（只更新这几个字段）
            LearningFeatures upd = new LearningFeatures();
            upd.setId(row.getId());
            upd.setHomeworkSubmitRate(submitRate);
            upd.setHomeworkAvgScore(avgScore);
            upd.setHomeworkDelayCount(delayCount);
            upd.setHomeworkQualityScore(qualityScore);
            upd.setUpdatedTime(now);

            learningFeaturesMapper.updateById(upd);
        }
    }

    /**
     * 新增特征记录
     */
    public void add(LearningFeatures features) {
        if (ObjectUtil.isEmpty(features.getStudentId())) {
            throw new CustomException("学生ID不能为空");
        }
        if (ObjectUtil.isEmpty(features.getCourseId())) {
            throw new CustomException("课程ID不能为空");
        }
        if (ObjectUtil.isEmpty(features.getFeatureDate())) {
            features.setFeatureDate(LocalDateTime.now().toLocalDate().toString());
        }

        LearningFeatures exist = learningFeaturesMapper.selectByStudentCourseDate(
                features.getStudentId(), features.getCourseId(), features.getFeatureDate());
        if (ObjectUtil.isNotNull(exist)) {
            throw new CustomException("该学生当天特征记录已存在");
        }

        if (ObjectUtil.isEmpty(features.getVideoWatchTime())) features.setVideoWatchTime(0.0);
        if (ObjectUtil.isEmpty(features.getVideoCompletionRate())) features.setVideoCompletionRate(0.0);
        if (ObjectUtil.isEmpty(features.getHomeworkSubmitRate())) features.setHomeworkSubmitRate(0.0);
        if (ObjectUtil.isEmpty(features.getHomeworkAvgScore())) features.setHomeworkAvgScore(0.0);
        if (ObjectUtil.isEmpty(features.getRiskLevel())) features.setRiskLevel("LOW");

        features.setCreatedTime(LocalDateTime.now().toString());
        features.setUpdatedTime(LocalDateTime.now().toString());

        learningFeaturesMapper.insert(features);
    }

    public void deleteById(Integer id) {
        LearningFeatures features = learningFeaturesMapper.selectById(id);
        if (ObjectUtil.isNull(features)) {
            throw new CustomException("特征记录不存在");
        }
        learningFeaturesMapper.deleteById(id);
    }

    public void updateById(LearningFeatures features) {
        LearningFeatures dbFeatures = learningFeaturesMapper.selectById(features.getId());
        if (ObjectUtil.isNull(dbFeatures)) {
            throw new CustomException("特征记录不存在");
        }

        features.setUpdatedTime(LocalDateTime.now().toString());
        learningFeaturesMapper.updateById(features);
    }

    public LearningFeatures selectById(Integer id) {
        LearningFeatures features = learningFeaturesMapper.selectById(id);
        if (ObjectUtil.isNull(features)) {
            throw new CustomException("特征记录不存在");
        }
        return features;
    }

    public List<LearningFeatures> selectAll(LearningFeatures features) {
        return learningFeaturesMapper.selectAll(features);
    }

    public PageInfo<LearningFeatures> selectPage(LearningFeatures features, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<LearningFeatures> list = learningFeaturesMapper.selectAll(features);
        return PageInfo.of(list);
    }

    public List<LearningFeatures> selectByStudentId(Integer studentId) {
        return learningFeaturesMapper.selectByStudentId(studentId);
    }

    public List<LearningFeatures> selectByCourseId(Integer courseId) {
        return learningFeaturesMapper.selectByCourseId(courseId);
    }

    public List<LearningFeatures> selectByStudentAndCourse(Integer studentId, Integer courseId) {
        return learningFeaturesMapper.selectByStudentAndCourse(studentId, courseId);
    }

    public LearningFeatures getStudentSummary(Integer studentId, Integer courseId, String startDate, String endDate) {
        return learningFeaturesMapper.selectSummaryByStudent(studentId, courseId, startDate, endDate);
    }

    public void updateRiskInfo(Integer id, Double riskScore, String riskLevel, Double riskProbability) {
        LearningFeatures features = learningFeaturesMapper.selectById(id);
        if (ObjectUtil.isNull(features)) {
            throw new CustomException("特征记录不存在");
        }
        learningFeaturesMapper.updateRiskInfo(id, riskScore, riskLevel, riskProbability);
    }

    public List<LearningFeatures> getHighRiskFeatures(Double threshold, Integer limit) {
        if (ObjectUtil.isEmpty(threshold)) threshold = 0.7;
        if (ObjectUtil.isEmpty(limit)) limit = 50;
        return learningFeaturesMapper.selectHighRiskFeatures(threshold, limit);
    }

    public Integer count(Integer studentId, Integer courseId, String riskLevel) {
        return learningFeaturesMapper.count(studentId, courseId, riskLevel);
    }

    public void batchInsert(List<LearningFeatures> featuresList) {
        if (ObjectUtil.isEmpty(featuresList)) {
            throw new CustomException("数据不能为空");
        }

        for (LearningFeatures features : featuresList) {
            if (ObjectUtil.isEmpty(features.getCreatedTime())) {
                features.setCreatedTime(LocalDateTime.now().toString());
            }
            if (ObjectUtil.isEmpty(features.getUpdatedTime())) {
                features.setUpdatedTime(LocalDateTime.now().toString());
            }
        }

        learningFeaturesMapper.batchInsert(featuresList);
    }

    public List<LearningFeatures> getTrainingData(Integer limit) {
        return learningFeaturesMapper.selectTrainingData(limit);
    }

    public double[] calculateFeatureVector(LearningFeatures features) {
        if (features == null) {
            return new double[8];
        }

        return new double[]{
                safeNormalize(features.getVideoWatchTime(), 0.0, 10.0),
                safeNormalize(features.getVideoCompletionRate(), 0.0, 100.0),
                safeNormalize(features.getHomeworkSubmitRate(), 0.0, 100.0),
                safeNormalize(features.getHomeworkAvgScore(), 0.0, 100.0),
                safeNormalize(features.getLoginFrequency() != null ? features.getLoginFrequency().doubleValue() : 0.0, 0.0, 50.0),
                safeNormalize(features.getFocusScore(), 0.0, 100.0),
                safeNormalize(features.getStudyConsistency(), 0.0, 100.0),
                safeNormalize(features.getInteractionLevel(), 0.0, 100.0)
        };
    }

    private double safeNormalize(Double value, double min, double max) {
        if (value == null) return 0.0;
        if (max <= min) return 0.0;

        if (value < min) value = min;
        if (value > max) value = max;

        double normalized = (value - min) / (max - min);
        return Math.max(0.0, Math.min(1.0, normalized));
    }

    private double safeNormalize(Integer value, double min, double max) {
        if (value == null) return 0.0;
        return safeNormalize(value.doubleValue(), min, max);
    }

    public void generateFeaturesFromBehavior(Integer studentId, Integer courseId, String date) {
        // 暂时留空
    }

    public int batchCalculateFeatures(Integer days) {
        if (days == null) days = 7;

        try {
            List<LearningFeatures> calculatedFeatures = learningFeaturesMapper.calculateFeaturesFromBehavior(days);

            if (calculatedFeatures != null && !calculatedFeatures.isEmpty()) {
                learningFeaturesMapper.insertOrUpdateBatch(calculatedFeatures);
                return calculatedFeatures.size();
            }
            return 0;
        } catch (Exception e) {
            throw new CustomException("批量计算特征失败: " + e.getMessage());
        }
    }
}
