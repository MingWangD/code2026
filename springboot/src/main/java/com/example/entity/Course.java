package com.example.entity;

import java.time.LocalDateTime;

/**
 * 课程实体类
 */
public class Course {

    private Integer id;

    /** 课程名称 */
    private String courseName;

    /** 课程编号 */
    private String courseNo;

    /** 教师ID */
    private Integer teacherId;

    /** 教师姓名 */
    private String teacherName;

    /** 课程描述 */
    private String description;

    /** 学分 */
    private Double credit;

    /** 总课时 */
    private Integer totalHours;

    /** 开课学期 */
    private String semester;

    /** 开课年份 */
    private Integer year;

    /** 课程封面图片 */
    private String coverImage;

    /** 课程视频地址 */
    private String videoUrl;

    /** 课程资料地址 */
    private String materialUrl;

    /** 课程状态：进行中/已结束/未开始 */
    private String status;

    /** 学生人数 */
    private Integer studentCount;

    /** 作业数量 */
    private Integer homeworkCount;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private String updateTime;

    // 构造方法
    public Course() {
        this.status = "未开始";
        this.studentCount = 0;
        this.homeworkCount = 0;
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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseNo() {
        return courseNo;
    }

    public void setCourseNo(String courseNo) {
        this.courseNo = courseNo;
    }

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public Integer getTotalHours() {
        return totalHours;
    }

    public void setTotalHours(Integer totalHours) {
        this.totalHours = totalHours;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getMaterialUrl() {
        return materialUrl;
    }

    public void setMaterialUrl(String materialUrl) {
        this.materialUrl = materialUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public Integer getHomeworkCount() {
        return homeworkCount;
    }

    public void setHomeworkCount(Integer homeworkCount) {
        this.homeworkCount = homeworkCount;
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