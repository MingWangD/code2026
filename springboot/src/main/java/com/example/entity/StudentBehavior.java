package com.example.entity;

import java.time.LocalDateTime;

/**
 * 学生学习行为事件（原始日志）
 */
public class StudentBehavior {

    private Integer id;

    /** 学生ID */
    private Integer studentId;

    /** 课程ID */
    private Integer courseId;

    /** 行为类型：HOMEWORK_SUBMIT / VIDEO_WATCH / EXAM / LOGIN */
    private String behaviorType;

    /** 关联业务ID（作业ID / 视频ID / 考试ID） */
    private String relatedId;

    /** 得分（作业 / 考试，视频可为null） */
    private Double score;

    /** 是否迟交：0否 1是 */
    private Integer isLate;

    /** 第几次提交 */
    private Integer attemptNo;

    /** 行为发生时间 */
    private String behaviorTime;

    /** 创建时间 */
    private String createTime;

    public StudentBehavior() {
        this.createTime = LocalDateTime.now().toString();
    }

    // ===== Getter / Setter =====

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(String behaviorType) {
        this.behaviorType = behaviorType;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getIsLate() {
        return isLate;
    }

    public void setIsLate(Integer isLate) {
        this.isLate = isLate;
    }

    public Integer getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(Integer attemptNo) {
        this.attemptNo = attemptNo;
    }

    public String getBehaviorTime() {
        return behaviorTime;
    }

    public void setBehaviorTime(String behaviorTime) {
        this.behaviorTime = behaviorTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateTime() {
        return createTime;
    }


}
