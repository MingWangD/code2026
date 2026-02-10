package com.example.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.entity.RiskAlert;
import com.example.exception.CustomException;
import com.example.mapper.RiskAlertMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class RiskAlertService {

    @Resource
    private RiskAlertMapper riskAlertMapper;

    /**
     * 新增预警记录
     */
    public void add(RiskAlert alert) {
        // 生成预警编号
        if (ObjectUtil.isEmpty(alert.getAlertNo())) {
            alert.setAlertNo(generateAlertNo());
        }

        // 设置默认值
        if (ObjectUtil.isEmpty(alert.getStatus())) {
            alert.setStatus("UNREAD");
        }
        if (ObjectUtil.isEmpty(alert.getDetectedTime())) {
            alert.setDetectedTime(LocalDateTime.now().toString());
        }
        if (ObjectUtil.isEmpty(alert.getAlertTime())) {
            alert.setAlertTime(LocalDateTime.now().toString());
        }

        riskAlertMapper.insert(alert);
    }

    /**
     * 生成预警编号
     */
    private String generateAlertNo() {
        return "ALERT_" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") +
                "_" + IdUtil.fastSimpleUUID().substring(0, 6).toUpperCase();
    }

    /**
     * 删除预警记录
     */
    public void deleteById(Integer id) {
        RiskAlert alert = riskAlertMapper.selectById(id);
        if (ObjectUtil.isNull(alert)) {
            throw new CustomException("预警记录不存在");
        }
        riskAlertMapper.deleteById(id);
    }

    /**
     * 修改预警记录
     */
    public void updateById(RiskAlert alert) {
        RiskAlert dbAlert = riskAlertMapper.selectById(alert.getId());
        if (ObjectUtil.isNull(dbAlert)) {
            throw new CustomException("预警记录不存在");
        }

        riskAlertMapper.updateById(alert);
    }

    /**
     * 根据ID查询预警记录
     */
    public RiskAlert selectById(Integer id) {
        RiskAlert alert = riskAlertMapper.selectById(id);
        if (ObjectUtil.isNull(alert)) {
            throw new CustomException("预警记录不存在");
        }
        return alert;
    }

    /**
     * 根据预警编号查询
     */
    public RiskAlert selectByAlertNo(String alertNo) {
        RiskAlert alert = riskAlertMapper.selectByAlertNo(alertNo);
        if (ObjectUtil.isNull(alert)) {
            throw new CustomException("预警记录不存在");
        }
        return alert;
    }

    /**
     * 查询所有预警记录
     */
    public List<RiskAlert> selectAll(RiskAlert alert) {
        return riskAlertMapper.selectAll(alert);
    }

    /**
     * 分页查询预警记录
     */
    public PageInfo<RiskAlert> selectPage(RiskAlert alert, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<RiskAlert> list = riskAlertMapper.selectAll(alert);
        return PageInfo.of(list);
    }

    /**
     * 根据学生ID查询预警记录
     */
    public List<RiskAlert> selectByStudentId(Integer studentId) {
        return riskAlertMapper.selectByStudentId(studentId);
    }

    /**
     * 根据课程ID查询预警记录
     */
    public List<RiskAlert> selectByCourseId(Integer courseId) {
        return riskAlertMapper.selectByCourseId(courseId);
    }

    /**
     * 根据预警等级查询
     */
    public List<RiskAlert> selectByAlertLevel(String alertLevel) {
        return riskAlertMapper.selectByAlertLevel(alertLevel);
    }

    /**
     * 根据状态查询预警记录
     */
    public List<RiskAlert> selectByStatus(String status) {
        return riskAlertMapper.selectByStatus(status);
    }

    /**
     * 获取未读预警数量
     */
    public Integer getUnreadCount(Integer handlerId, String handlerRole) {
        return riskAlertMapper.countUnread(handlerId, handlerRole);
    }

    /**
     * 统计预警数量
     */
    public Integer count(Integer courseId, String alertLevel, String status, String startDate, String endDate) {
        return riskAlertMapper.count(courseId, alertLevel, status, startDate, endDate);
    }

    /**
     * 更新预警状态
     */
    public void updateStatus(Integer id, String status, Integer handlerId, String handlerName, String handlerRole) {
        RiskAlert alert = riskAlertMapper.selectById(id);
        if (ObjectUtil.isNull(alert)) {
            throw new CustomException("预警记录不存在");
        }

        riskAlertMapper.updateStatus(id, status, handlerId, handlerName, handlerRole);
    }

    /**
     * 标记为已读
     */
    public void markAsRead(Integer id, Integer handlerId, String handlerName, String handlerRole) {
        updateStatus(id, "READ", handlerId, handlerName, handlerRole);
    }

    /**
     * 开始处理预警
     */
    public void startProcessing(Integer id, Integer handlerId, String handlerName, String handlerRole) {
        updateStatus(id, "PROCESSING", handlerId, handlerName, handlerRole);
    }

    /**
     * 解决预警
     */
    public void resolveAlert(Integer id, Integer handlerId, String handlerName, String handlerRole, String processResult) {
        RiskAlert alert = selectById(id);
        alert.setProcessResult(processResult);
        updateStatus(id, "RESOLVED", handlerId, handlerName, handlerRole);
        riskAlertMapper.updateById(alert);
    }

    /**
     * 关闭预警
     */
    public void closeAlert(Integer id, Integer handlerId, String handlerName, String handlerRole) {
        updateStatus(id, "CLOSED", handlerId, handlerName, handlerRole);
    }

    /**
     * 批量更新预警状态
     */
    public void batchUpdateStatus(List<Integer> ids, String status) {
        if (ObjectUtil.isEmpty(ids)) {
            throw new CustomException("请选择要更新的预警记录");
        }

        for (Integer id : ids) {
            RiskAlert alert = riskAlertMapper.selectById(id);
            if (ObjectUtil.isNotNull(alert)) {
                riskAlertMapper.updateStatus(id, status, null, null, null);
            }
        }
    }

    /**
     * 获取最新的预警记录
     */
    public List<RiskAlert> getLatestAlerts(Integer limit) {
        if (ObjectUtil.isEmpty(limit)) limit = 10;
        return riskAlertMapper.selectLatestAlerts(limit);
    }

    /**
     * 搜索预警记录
     */
    public List<RiskAlert> search(String keyword) {
        return riskAlertMapper.search(keyword);
    }

    /**
     * 获取每日预警统计
     */
    public List<Map<String, Object>> getDailyStatistics(Integer days) {
        if (ObjectUtil.isEmpty(days)) days = 30;

        // 直接返回Mapper的结果
        return riskAlertMapper.selectDailyStatistics(days);
    }

    /**
     * 根据处理人查询预警记录
     */
    public List<RiskAlert> getByHandler(Integer handlerId, String handlerRole) {
        return riskAlertMapper.selectByHandler(handlerId, handlerRole);
    }

    /**
     * 生成预警（核心方法）
     */
    public RiskAlert generateAlert(Integer studentId, String studentName, String studentNo,
                                   Integer courseId, String courseName, String alertType,
                                   String alertLevel, String alertTitle, String alertContent,
                                   Double riskScore, Double riskProbability, String featureData) {

        RiskAlert alert = new RiskAlert();
        alert.setAlertNo(generateAlertNo());
        alert.setStudentId(studentId);
        alert.setStudentName(studentName);
        alert.setStudentNo(studentNo);
        alert.setCourseId(courseId);
        alert.setCourseName(courseName);
        alert.setAlertType(alertType);
        alert.setAlertLevel(alertLevel);
        alert.setAlertTitle(alertTitle);
        alert.setAlertContent(alertContent);
        alert.setRiskScore(riskScore);
        alert.setRiskProbability(riskProbability);
        alert.setFeatureData(featureData);
        alert.setStatus("UNREAD");
        alert.setDetectedTime(LocalDateTime.now().toString());
        alert.setAlertTime(LocalDateTime.now().toString());

        // 设置处理建议
        alert.setSuggestion(generateSuggestion(alertLevel, alertType));

        add(alert);
        return alert;
    }

    /**
     * 根据预警等级和类型生成处理建议
     */
    private String generateSuggestion(String alertLevel, String alertType) {
        StringBuilder suggestion = new StringBuilder();

        suggestion.append("建议处理措施：\n");

        if ("HIGH".equals(alertLevel) || "CRITICAL".equals(alertLevel)) {
            suggestion.append("1. 立即联系学生进行面对面沟通\n");
            suggestion.append("2. 通知辅导员关注学生情况\n");
            suggestion.append("3. 安排学习帮扶计划\n");
        } else if ("MEDIUM".equals(alertLevel)) {
            suggestion.append("1. 通过线上方式与学生沟通\n");
            suggestion.append("2. 提供针对性的学习资源\n");
            suggestion.append("3. 定期跟进学习进展\n");
        } else {
            suggestion.append("1. 发送提醒通知\n");
            suggestion.append("2. 关注后续学习行为变化\n");
        }

        if ("VIDEO".equals(alertType)) {
            suggestion.append("\n针对视频学习问题的具体建议：\n");
            suggestion.append("- 推荐重点视频片段\n");
            suggestion.append("- 提供学习笔记模板\n");
        } else if ("HOMEWORK".equals(alertType)) {
            suggestion.append("\n针对作业问题的具体建议：\n");
            suggestion.append("- 提供作业示例和解题思路\n");
            suggestion.append("- 安排作业答疑时间\n");
        } else if ("LOGIN".equals(alertType)) {
            suggestion.append("\n针对登录问题的具体建议：\n");
            suggestion.append("- 检查账号是否有问题\n");
            suggestion.append("- 发送学习提醒通知\n");
        }

        return suggestion.toString();
    }
}