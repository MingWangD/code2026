package com.example.entity;

import java.time.LocalDateTime;

/**
 * 作业实体类
 */
public class Homework {

    private Integer id;

    /** 课程ID */
    private Integer courseId;

    /** 课程名称 */
    private String courseName;

    /** 作业标题 */
    private String title;

    /** 作业描述 */
    private String description;

    /** 作业要求 */
    private String requirements;

    /** 作业附件地址 */
    private String attachmentUrl;

    /** 开始时间 */
    private String startTime;

    /** 截止时间 */
    private String deadline;

    /** 总分 */
    private Double totalScore;

    /** 提交人数 */
    private Integer submitCount;

    /** 批改人数 */
    private Integer gradedCount;

    /** 平均分 */
    private Double averageScore;

    /** 作业状态：未开始/进行中/已截止/已批改 */
    private String status;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;

    // 构造方法
    public Homework() {
        this.submitCount = 0;
        this.gradedCount = 0;
        this.averageScore = 0.0;
        this.status = "未开始";
        this.createTime = LocalDateTime.now().toString();
        this.updateTime = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getAttachmentUrl() {
        return attachmentUrl;
    }

    public void setAttachmentUrl(String attachmentUrl) {
        this.attachmentUrl = attachmentUrl;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Double totalScore) {
        this.totalScore = totalScore;
    }

    public Integer getSubmitCount() {
        return submitCount;
    }

    public void setSubmitCount(Integer submitCount) {
        this.submitCount = submitCount;
    }

    public Integer getGradedCount() {
        return gradedCount;
    }

    public void setGradedCount(Integer gradedCount) {
        this.gradedCount = gradedCount;
    }

    public Double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(Double averageScore) {
        this.averageScore = averageScore;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}