package com.example.entity;

import java.time.LocalDateTime;

/**
 * 学习行为特征实体类（对应learning_features表）
 * 用于逻辑回归算法的特征存储
 */
public class LearningFeatures {

    private Integer id;

    // 学生基本信息
    private Integer studentId;
    private String studentName;
    private String studentNo;
    private Integer courseId;
    private String courseName;

    // 时间维度
    private String featureDate;  // 特征日期
    private Integer weekOfSemester; // 学期周数(1-20)

    // 视频学习特征
    private Double videoWatchTime;      // 视频观看时长(小时)
    private Double videoCompletionRate; // 视频完成率(0-100)
    private Double videoEngagement;     // 视频互动度(0-100)

    // 作业特征
    private Double homeworkSubmitRate;   // 作业提交率(0-100)
    private Double homeworkAvgScore;     // 作业平均分
    private Integer homeworkDelayCount;  // 延迟提交次数
    private Double homeworkQualityScore; // 作业质量评分(0-100)

    // 登录行为特征
    private Integer loginFrequency;      // 登录频率(次/周)
    private Double loginRegularity;      // 登录规律性(0-100)
    private Integer lastLoginDays;       // 上次登录距今天数

    // 综合学习特征
    private Double studyConsistency;     // 学习持续性(0-100)
    private Double studyIntensity;       // 学习强度(0-100)
    private Double interactionLevel;     // 互动水平(0-100)
    private Double focusScore;           // 专注度评分(0-100)

    // 算法结果
    private Double riskScore;            // 风险评分(0-100)
    private String riskLevel;            // 风险等级(LOW/MEDIUM/HIGH)
    private Double riskProbability;      // 风险概率(0-1)

    // 特征向量（JSON格式）
    private String featureVector;        // JSON字符串存储特征向量

    // 时间戳
    private String createdTime;
    private String updatedTime;

    // 构造方法
    public LearningFeatures() {
        this.videoWatchTime = 0.0;
        this.videoCompletionRate = 0.0;
        this.videoEngagement = 0.0;
        this.homeworkSubmitRate = 0.0;
        this.homeworkAvgScore = 0.0;
        this.homeworkDelayCount = 0;
        this.homeworkQualityScore = 0.0;
        this.loginFrequency = 0;
        this.loginRegularity = 0.0;
        this.lastLoginDays = 999;
        this.studyConsistency = 0.0;
        this.studyIntensity = 0.0;
        this.interactionLevel = 0.0;
        this.focusScore = 0.0;
        this.riskScore = 0.0;
        this.riskLevel = "LOW";
        this.riskProbability = 0.0;
        this.createdTime = LocalDateTime.now().toString();
        this.updatedTime = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public String getFeatureDate() { return featureDate; }
    public void setFeatureDate(String featureDate) { this.featureDate = featureDate; }

    public Integer getWeekOfSemester() { return weekOfSemester; }
    public void setWeekOfSemester(Integer weekOfSemester) { this.weekOfSemester = weekOfSemester; }

    public Double getVideoWatchTime() { return videoWatchTime; }
    public void setVideoWatchTime(Double videoWatchTime) { this.videoWatchTime = videoWatchTime; }

    public Double getVideoCompletionRate() { return videoCompletionRate; }
    public void setVideoCompletionRate(Double videoCompletionRate) { this.videoCompletionRate = videoCompletionRate; }

    public Double getVideoEngagement() { return videoEngagement; }
    public void setVideoEngagement(Double videoEngagement) { this.videoEngagement = videoEngagement; }

    public Double getHomeworkSubmitRate() { return homeworkSubmitRate; }
    public void setHomeworkSubmitRate(Double homeworkSubmitRate) { this.homeworkSubmitRate = homeworkSubmitRate; }

    public Double getHomeworkAvgScore() { return homeworkAvgScore; }
    public void setHomeworkAvgScore(Double homeworkAvgScore) { this.homeworkAvgScore = homeworkAvgScore; }

    public Integer getHomeworkDelayCount() { return homeworkDelayCount; }
    public void setHomeworkDelayCount(Integer homeworkDelayCount) { this.homeworkDelayCount = homeworkDelayCount; }

    public Double getHomeworkQualityScore() { return homeworkQualityScore; }
    public void setHomeworkQualityScore(Double homeworkQualityScore) { this.homeworkQualityScore = homeworkQualityScore; }

    public Integer getLoginFrequency() { return loginFrequency; }
    public void setLoginFrequency(Integer loginFrequency) { this.loginFrequency = loginFrequency; }

    public Double getLoginRegularity() { return loginRegularity; }
    public void setLoginRegularity(Double loginRegularity) { this.loginRegularity = loginRegularity; }

    public Integer getLastLoginDays() { return lastLoginDays; }
    public void setLastLoginDays(Integer lastLoginDays) { this.lastLoginDays = lastLoginDays; }

    public Double getStudyConsistency() { return studyConsistency; }
    public void setStudyConsistency(Double studyConsistency) { this.studyConsistency = studyConsistency; }

    public Double getStudyIntensity() { return studyIntensity; }
    public void setStudyIntensity(Double studyIntensity) { this.studyIntensity = studyIntensity; }

    public Double getInteractionLevel() { return interactionLevel; }
    public void setInteractionLevel(Double interactionLevel) { this.interactionLevel = interactionLevel; }

    public Double getFocusScore() { return focusScore; }
    public void setFocusScore(Double focusScore) { this.focusScore = focusScore; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public Double getRiskProbability() { return riskProbability; }
    public void setRiskProbability(Double riskProbability) { this.riskProbability = riskProbability; }

    public String getFeatureVector() { return featureVector; }
    public void setFeatureVector(String featureVector) { this.featureVector = featureVector; }

    public String getCreatedTime() { return createdTime; }
    public void setCreatedTime(String createdTime) { this.createdTime = createdTime; }

    public String getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(String updatedTime) { this.updatedTime = updatedTime; }

    @Override
    public String toString() {
        return "LearningFeatures{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", courseId=" + courseId +
                ", courseName='" + courseName + '\'' +
                ", featureDate='" + featureDate + '\'' +
                ", riskScore=" + riskScore +
                ", riskLevel='" + riskLevel + '\'' +
                '}';
    }
}