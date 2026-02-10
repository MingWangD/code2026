package com.example.entity;

/**
 * 学生实体类
 */
public class Student extends Account {

    /** 学号 */
    private String studentNo;

    /** 班级ID */
    private Integer classId;

    /** 班级名称 */
    private String className;

    /** 年级 */
    private String grade;

    /** 专业 */
    private String major;

    /** 入学年份 */
    private Integer enrollmentYear;

    /** 联系电话 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 学业状态：正常/关注/预警/退学 */
    private String academicStatus;

    /** 累计预警次数 */
    private Integer warningCount;

    // 构造方法
    public Student() {
        this.setRole("STUDENT");
        this.academicStatus = "正常";
        this.warningCount = 0;
    }

    // Getters and Setters
    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
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

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) {
        this.academicStatus = academicStatus;
    }

    public Integer getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(Integer warningCount) {
        this.warningCount = warningCount;
    }
}