package com.example.entity;

import java.time.LocalDateTime;

public class StudentBehaviorEvent {
    private Integer id;
    private Integer studentId;
    private Integer courseId;
    private String behaviorType;
    private String relatedId;

    private Double score;
    private Boolean isLate;
    private Integer attemptNo;

    private Double behaviorValue;     // 新增
    private String behaviorExtra;     // JSON 字符串（数据库列是 JSON）

    private LocalDateTime behaviorTime;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getStudentId() { return studentId; }
    public void setStudentId(Integer studentId) { this.studentId = studentId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getBehaviorType() { return behaviorType; }
    public void setBehaviorType(String behaviorType) { this.behaviorType = behaviorType; }

    public String getRelatedId() { return relatedId; }
    public void setRelatedId(String relatedId) { this.relatedId = relatedId; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Boolean getIsLate() { return isLate; }
    public void setIsLate(Boolean isLate) { this.isLate = isLate; }

    public Integer getAttemptNo() { return attemptNo; }
    public void setAttemptNo(Integer attemptNo) { this.attemptNo = attemptNo; }

    public Double getBehaviorValue() { return behaviorValue; }
    public void setBehaviorValue(Double behaviorValue) { this.behaviorValue = behaviorValue; }

    public String getBehaviorExtra() { return behaviorExtra; }
    public void setBehaviorExtra(String behaviorExtra) { this.behaviorExtra = behaviorExtra; }

    public LocalDateTime getBehaviorTime() { return behaviorTime; }
    public void setBehaviorTime(LocalDateTime behaviorTime) { this.behaviorTime = behaviorTime; }
}
