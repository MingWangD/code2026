package com.example.controller;

import com.example.common.Result;
import com.example.entity.RiskAlert;
import com.example.service.RiskAlertService;
import com.github.pagehelper.PageInfo;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 风险预警管理接口
 */
@RestController
@RequestMapping("/risk-alerts")
public class RiskAlertController {

    @Resource
    private RiskAlertService riskAlertService;

    /**
     * 新增预警记录
     */
    @PostMapping("/add")
    public Result add(@RequestBody RiskAlert alert) {
        riskAlertService.add(alert);
        return Result.success();
    }

    /**
     * 删除预警记录
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        riskAlertService.deleteById(id);
        return Result.success();
    }

    /**
     * 修改预警记录
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody RiskAlert alert) {
        riskAlertService.updateById(alert);
        return Result.success();
    }

    /**
     * 根据ID查询预警记录
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        RiskAlert alert = riskAlertService.selectById(id);
        return Result.success(alert);
    }

    /**
     * 根据预警编号查询
     */
    @GetMapping("/selectByAlertNo/{alertNo}")
    public Result selectByAlertNo(@PathVariable String alertNo) {
        RiskAlert alert = riskAlertService.selectByAlertNo(alertNo);
        return Result.success(alert);
    }

    /**
     * 查询所有预警记录
     */
    @GetMapping("/selectAll")
    public Result selectAll(RiskAlert alert) {
        List<RiskAlert> list = riskAlertService.selectAll(alert);
        return Result.success(list);
    }

    /**
     * 分页查询预警记录
     */
    @GetMapping("/selectPage")
    public Result selectPage(RiskAlert alert,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<RiskAlert> page = riskAlertService.selectPage(alert, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 根据学生ID查询预警记录
     */
    @GetMapping("/selectByStudent/{studentId}")
    public Result selectByStudentId(@PathVariable Integer studentId) {
        List<RiskAlert> list = riskAlertService.selectByStudentId(studentId);
        return Result.success(list);
    }

    /**
     * 根据课程ID查询预警记录
     */
    @GetMapping("/selectByCourse/{courseId}")
    public Result selectByCourseId(@PathVariable Integer courseId) {
        List<RiskAlert> list = riskAlertService.selectByCourseId(courseId);
        return Result.success(list);
    }

    /**
     * 根据预警等级查询
     */
    @GetMapping("/selectByLevel/{alertLevel}")
    public Result selectByAlertLevel(@PathVariable String alertLevel) {
        List<RiskAlert> list = riskAlertService.selectByAlertLevel(alertLevel);
        return Result.success(list);
    }

    /**
     * 根据状态查询预警记录
     */
    @GetMapping("/selectByStatus/{status}")
    public Result selectByStatus(@PathVariable String status) {
        List<RiskAlert> list = riskAlertService.selectByStatus(status);
        return Result.success(list);
    }

    /**
     * 获取未读预警数量
     */
    @GetMapping("/unreadCount")
    public Result getUnreadCount(@RequestParam(required = false) Integer handlerId,
                                 @RequestParam(required = false) String handlerRole) {
        Integer count = riskAlertService.getUnreadCount(handlerId, handlerRole);
        return Result.success(count);
    }

    /**
     * 统计预警数量
     */
    @GetMapping("/count")
    public Result count(@RequestParam(required = false) Integer courseId,
                        @RequestParam(required = false) String alertLevel,
                        @RequestParam(required = false) String status,
                        @RequestParam(required = false) String startDate,
                        @RequestParam(required = false) String endDate) {
        Integer count = riskAlertService.count(courseId, alertLevel, status, startDate, endDate);
        return Result.success(count);
    }

    /**
     * 标记为已读
     */
    @PutMapping("/markAsRead/{id}")
    public Result markAsRead(@PathVariable Integer id,
                             @RequestParam Integer handlerId,
                             @RequestParam String handlerName,
                             @RequestParam String handlerRole) {
        riskAlertService.markAsRead(id, handlerId, handlerName, handlerRole);
        return Result.success();
    }

    /**
     * 开始处理预警
     */
    @PutMapping("/startProcessing/{id}")
    public Result startProcessing(@PathVariable Integer id,
                                  @RequestParam Integer handlerId,
                                  @RequestParam String handlerName,
                                  @RequestParam String handlerRole) {
        riskAlertService.startProcessing(id, handlerId, handlerName, handlerRole);
        return Result.success();
    }

    /**
     * 解决预警
     */
    @PutMapping("/resolve/{id}")
    public Result resolveAlert(@PathVariable Integer id,
                               @RequestParam Integer handlerId,
                               @RequestParam String handlerName,
                               @RequestParam String handlerRole,
                               @RequestParam String processResult) {
        riskAlertService.resolveAlert(id, handlerId, handlerName, handlerRole, processResult);
        return Result.success();
    }

    /**
     * 关闭预警
     */
    @PutMapping("/close/{id}")
    public Result closeAlert(@PathVariable Integer id,
                             @RequestParam Integer handlerId,
                             @RequestParam String handlerName,
                             @RequestParam String handlerRole) {
        riskAlertService.closeAlert(id, handlerId, handlerName, handlerRole);
        return Result.success();
    }

    /**
     * 批量更新预警状态
     */
    @PutMapping("/batchUpdateStatus")
    public Result batchUpdateStatus(@RequestParam List<Integer> ids,
                                    @RequestParam String status) {
        riskAlertService.batchUpdateStatus(ids, status);
        return Result.success();
    }

    /**
     * 获取最新的预警记录
     */
    @GetMapping("/latest")
    public Result getLatestAlerts(@RequestParam(defaultValue = "10") Integer limit) {
        List<RiskAlert> list = riskAlertService.getLatestAlerts(limit);
        return Result.success(list);
    }

    /**
     * 搜索预警记录
     */
    @GetMapping("/search")
    public Result search(@RequestParam String keyword) {
        List<RiskAlert> list = riskAlertService.search(keyword);
        return Result.success(list);
    }

    /**
     * 获取每日预警统计
     */
    @GetMapping("/dailyStatistics")
    public Result getDailyStatistics(@RequestParam(defaultValue = "30") Integer days) {
        List<Map<String, Object>> statistics = riskAlertService.getDailyStatistics(days);
        return Result.success(statistics);
    }

    /**
     * 根据处理人查询预警记录
     */
    @GetMapping("/byHandler")
    public Result getByHandler(@RequestParam Integer handlerId,
                               @RequestParam String handlerRole) {
        List<RiskAlert> list = riskAlertService.getByHandler(handlerId, handlerRole);
        return Result.success(list);
    }

    /**
     * 生成预警
     */
    @PostMapping("/generate")
    public Result generateAlert(@RequestParam Integer studentId,
                                @RequestParam String studentName,
                                @RequestParam String studentNo,
                                @RequestParam Integer courseId,
                                @RequestParam String courseName,
                                @RequestParam String alertType,
                                @RequestParam String alertLevel,
                                @RequestParam String alertTitle,
                                @RequestParam String alertContent,
                                @RequestParam Double riskScore,
                                @RequestParam Double riskProbability,
                                @RequestParam(required = false) String featureData) {
        RiskAlert alert = riskAlertService.generateAlert(
                studentId, studentName, studentNo, courseId, courseName,
                alertType, alertLevel, alertTitle, alertContent,
                riskScore, riskProbability, featureData
        );
        return Result.success(alert);
    }
}