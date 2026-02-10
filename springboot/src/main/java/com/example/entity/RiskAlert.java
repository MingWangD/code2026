package com.example.entity;

import java.time.LocalDateTime;

/**
 * 学业风险预警实体类（对应risk_alerts表）
 */
public class RiskAlert {

    private Integer id;

    // 预警基本信息
    private String alertNo;          // 预警编号
    private Integer studentId;       // 学生ID
    private String studentName;      // 学生姓名
    private String studentNo;        // 学号

    // 课程信息
    private Integer courseId;        // 课程ID
    private String courseName;       // 课程名称

    // 预警详情
    private String alertType;        // 预警类型(VIDEO/HOMEWORK/LOGIN/COMPREHENSIVE)
    private String alertLevel;       // 预警等级(LOW/MEDIUM/HIGH/CRITICAL)
    private String alertTitle;       // 预警标题
    private String alertContent;     // 预警详细内容

    // 风险数据
    private Double riskScore;        // 风险评分
    private Double riskProbability;  // 风险概率

    // 特征数据
    private String featureData;      // JSON格式特征数据
    private String suggestion;       // 处理建议

    // 状态管理
    private String status;           // 状态(UNREAD/READ/PROCESSING/RESOLVED/CLOSED)
    private Integer handlerId;       // 处理人ID
    private String handlerName;      // 处理人姓名
    private String handlerRole;      // 处理人角色(ADMIN/TEACHER)

    // 时间管理
    private String detectedTime;     // 检测时间
    private String alertTime;        // 预警生成时间
    private String readTime;         // 查阅时间
    private String processTime;      // 开始处理时间
    private String resolveTime;      // 解决时间
    private String closeTime;        // 关闭时间

    // 处理信息
    private String processMethod;    // 处理方式
    private String processResult;    // 处理结果
    private String feedback;         // 反馈信息

    // 构造方法
    public RiskAlert() {
        this.status = "UNREAD";
        this.alertTime = LocalDateTime.now().toString();
        this.detectedTime = LocalDateTime.now().toString();
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getAlertNo() { return alertNo; }
    public void setAlertNo(String alertNo) { this.alertNo = alertNo; }

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

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getAlertLevel() { return alertLevel; }
    public void setAlertLevel(String alertLevel) { this.alertLevel = alertLevel; }

    public String getAlertTitle() { return alertTitle; }
    public void setAlertTitle(String alertTitle) { this.alertTitle = alertTitle; }

    public String getAlertContent() { return alertContent; }
    public void setAlertContent(String alertContent) { this.alertContent = alertContent; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public Double getRiskProbability() { return riskProbability; }
    public void setRiskProbability(Double riskProbability) { this.riskProbability = riskProbability; }

    public String getFeatureData() { return featureData; }
    public void setFeatureData(String featureData) { this.featureData = featureData; }

    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getHandlerId() { return handlerId; }
    public void setHandlerId(Integer handlerId) { this.handlerId = handlerId; }

    public String getHandlerName() { return handlerName; }
    public void setHandlerName(String handlerName) { this.handlerName = handlerName; }

    public String getHandlerRole() { return handlerRole; }
    public void setHandlerRole(String handlerRole) { this.handlerRole = handlerRole; }

    public String getDetectedTime() { return detectedTime; }
    public void setDetectedTime(String detectedTime) { this.detectedTime = detectedTime; }

    public String getAlertTime() { return alertTime; }
    public void setAlertTime(String alertTime) { this.alertTime = alertTime; }

    public String getReadTime() { return readTime; }
    public void setReadTime(String readTime) { this.readTime = readTime; }

    public String getProcessTime() { return processTime; }
    public void setProcessTime(String processTime) { this.processTime = processTime; }

    public String getResolveTime() { return resolveTime; }
    public void setResolveTime(String resolveTime) { this.resolveTime = resolveTime; }

    public String getCloseTime() { return closeTime; }
    public void setCloseTime(String closeTime) { this.closeTime = closeTime; }

    public String getProcessMethod() { return processMethod; }
    public void setProcessMethod(String processMethod) { this.processMethod = processMethod; }

    public String getProcessResult() { return processResult; }
    public void setProcessResult(String processResult) { this.processResult = processResult; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    @Override
    public String toString() {
        return "RiskAlert{" +
                "id=" + id +
                ", alertNo='" + alertNo + '\'' +
                ", studentName='" + studentName + '\'' +
                ", courseName='" + courseName + '\'' +
                ", alertLevel='" + alertLevel + '\'' +
                ", alertTitle='" + alertTitle + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}