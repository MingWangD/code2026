package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mapper.SystemMetricMapper;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    private static final Logger log = LoggerFactory.getLogger(HealthController.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SystemMetricMapper systemMetricMapper;

    /**
     * 系统健康检查接口
     */
    @GetMapping("/check")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("service", "教育风险预警系统");
        result.put("version", "1.0.0");

        try {
            // 1. 数据库连接检查
            try (Connection conn = dataSource.getConnection()) {
                result.put("database", "CONNECTED");

                DatabaseMetaData metaData = conn.getMetaData();
                result.put("database_name", metaData.getDatabaseProductName());
                result.put("database_version", metaData.getDatabaseProductVersion());
                result.put("url", metaData.getURL());
            }

            // 2. 定时任务状态检查
            try {
                List<Map<String, Object>> metrics = systemMetricMapper.getDailyMetrics();
                result.put("daily_metrics", metrics);

                // 修复：安全处理类型转换
                long totalTasks = metrics.stream()
                        .mapToLong(m -> {
                            Object value = m.get("total_executions");
                            return safeConvertToLong(value);
                        })
                        .sum();
                result.put("daily_task_count", totalTasks);
            } catch (Exception e) {
                log.debug("指标查询失败（可能表不存在）: {}", e.getMessage());
                result.put("daily_metrics", "指标表未初始化");
            }

            // 3. 系统资源状态
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memory = new HashMap<>();
            memory.put("total_memory", runtime.totalMemory() / 1024 / 1024 + "MB");
            memory.put("free_memory", runtime.freeMemory() / 1024 / 1024 + "MB");
            memory.put("max_memory", runtime.maxMemory() / 1024 / 1024 + "MB");
            memory.put("available_processors", runtime.availableProcessors());
            result.put("system_resources", memory);

            // 4. 应用状态
            result.put("status", "HEALTHY");
            result.put("message", "系统运行正常");

        } catch (Exception e) {
            log.error("❌ 健康检查失败: {}", e.getMessage());
            result.put("status", "UNHEALTHY");
            result.put("message", "系统检查异常: " + e.getMessage());
        }

        return result;
    }

    /**
     * 定时任务执行状态
     */
    @GetMapping("/scheduler/status")
    public Map<String, Object> getSchedulerStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().toString());

        try {
            List<Map<String, Object>> metrics = systemMetricMapper.getDailyMetrics();

            long totalExecutions = 0;
            long successCount = 0;

            for (Map<String, Object> metric : metrics) {
                totalExecutions += safeConvertToLong(metric.get("total_executions"));
                successCount += safeConvertToLong(metric.get("success_count"));
            }

            result.put("daily_tasks", totalExecutions);
            result.put("success_rate", totalExecutions == 0 ? "0%" : (successCount * 100 / totalExecutions) + "%");
            result.put("metrics", metrics);
            result.put("status", "OK");

        } catch (Exception e) {
            result.put("status", "ERROR");
            result.put("message", e.getMessage());
            result.put("suggestion", "请确保system_metrics表已创建");
        }

        return result;
    }

    /**
     * 简单ping接口
     */
    @GetMapping("/ping")
    public Map<String, String> ping() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "OK");
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("message", "pong");
        return result;
    }

    /**
     * 安全转换Object到Long
     */
    private long safeConvertToLong(Object value) {
        if (value == null) {
            return 0L;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            log.warn("无法转换为Long: {}", value);
            return 0L;
        }
    }
}