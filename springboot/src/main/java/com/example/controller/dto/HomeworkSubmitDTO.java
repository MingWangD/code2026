package com.example.controller.dto;



import java.time.LocalDateTime;

/**
 * 学生提交作业请求体
 */

public class HomeworkSubmitDTO {
    private Integer studentId;
    private Integer courseId;
    private Integer homeworkId;

    /** 本次提交得分（如果是提交后老师批改，可先传null） */
    private Double score;

    /** 提交时间（不传就用服务器时间） */
    private LocalDateTime submitTime;

    /** 可选：第几次提交（你以后支持多次提交就用得上） */
    private Integer attemptNo;

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

    public Integer getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(Integer homeworkId) {
        this.homeworkId = homeworkId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getAttemptNo() {
        return attemptNo;
    }

    public void setAttemptNo(Integer attemptNo) {
        this.attemptNo = attemptNo;
    }
}
