package com.example.controller;

import com.example.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取预警趋势数据
     * @param days 天数，默认30天
     * @return 趋势数据
     */
    @GetMapping("/warning-trend")
    public Map<String, Object> getWarningTrend(
            @RequestParam(defaultValue = "30") int days) {

        // 参数验证
        if (days <= 0 || days > 365) {
            days = 30;
        }

        return dashboardService.getWarningTrendData(days);
    }

    /**
     * 获取Dashboard总览数据
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        return dashboardService.getDashboardOverview();
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of(
                "status", "UP",
                "service", "dashboard-service",
                "timestamp", System.currentTimeMillis()
        );
    }
}