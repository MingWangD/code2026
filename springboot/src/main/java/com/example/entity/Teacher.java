package com.example.entity;

/**
 * 教师实体类
 */
public class Teacher extends Account {

    /** 工号 */
    private String teacherNo;

    /** 部门/学院 */
    private String department;

    /** 职称 */
    private String title;

    /** 研究方向 */
    private String researchArea;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 负责班级ID列表（JSON格式存储） */
    private String classIds;

    /** 负责课程数量 */
    private Integer courseCount;

    // 构造方法
    public Teacher() {
        this.setRole("TEACHER");
    }

    // Getters and Setters
    public String getTeacherNo() {
        return teacherNo;
    }

    public void setTeacherNo(String teacherNo) {
        this.teacherNo = teacherNo;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResearchArea() {
        return researchArea;
    }

    public void setResearchArea(String researchArea) {
        this.researchArea = researchArea;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClassIds() {
        return classIds;
    }

    public void setClassIds(String classIds) {
        this.classIds = classIds;
    }

    public Integer getCourseCount() {
        return courseCount;
    }

    public void setCourseCount(Integer courseCount) {
        this.courseCount = courseCount;
    }
}