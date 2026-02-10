package com.example.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.mapper.SystemMetricMapper;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/monitor")
public class MonitorController {

    private static final Logger log = LoggerFactory.getLogger(MonitorController.class);

    @Autowired
    private SystemMetricMapper systemMetricMapper;

    /**
     * 获取系统性能指标
     */
    @GetMapping("/performance")
    public Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().toString());

        try {
            // 1. JVM内存信息
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            Map<String, Object> memory = new HashMap<>();
            memory.put("heap_used", memoryBean.getHeapMemoryUsage().getUsed() / 1024 / 1024 + "MB");
            memory.put("heap_max", memoryBean.getHeapMemoryUsage().getMax() / 1024 / 1024 + "MB");
            memory.put("non_heap_used", memoryBean.getNonHeapMemoryUsage().getUsed() / 1024 / 1024 + "MB");
            result.put("memory", memory);

            // 2. 线程信息
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            Map<String, Object> threads = new HashMap<>();
            threads.put("total_threads", threadBean.getThreadCount());
            threads.put("daemon_threads", threadBean.getDaemonThreadCount());
            threads.put("peak_threads", threadBean.getPeakThreadCount());
            result.put("threads", threads);

            // 3. 操作系统信息
            Map<String, Object> os = new HashMap<>();
            os.put("name", System.getProperty("os.name"));
            os.put("version", System.getProperty("os.version"));
            os.put("arch", System.getProperty("os.arch"));
            os.put("available_processors", Runtime.getRuntime().availableProcessors());
            result.put("os", os);

            // 4. 定时任务指标
            try {
                List<Map<String, Object>> metrics = systemMetricMapper.getDailyMetrics();
                result.put("scheduler_metrics", metrics);
            } catch (Exception e) {
                result.put("scheduler_metrics", "暂无数据");
            }

            result.put("status", "success");

        } catch (Exception e) {
            log.error("获取性能指标失败: {}", e.getMessage());
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 获取系统健康度评分
     */
    @GetMapping("/health-score")
    public Map<String, Object> getHealthScore() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().toString());

        try {
            int score = 100; // 基础分数

            // 检查内存使用率
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            double heapUsageRatio = (double) memoryBean.getHeapMemoryUsage().getUsed() /
                    memoryBean.getHeapMemoryUsage().getMax();

            if (heapUsageRatio > 0.8) {
                score -= 20;
                result.put("memory_warning", "内存使用率过高: " + String.format("%.1f%%", heapUsageRatio * 100));
            }

            // 检查线程数
            ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
            if (threadBean.getThreadCount() > 100) {
                score -= 10;
                result.put("thread_warning", "线程数过多: " + threadBean.getThreadCount());
            }

            result.put("health_score", score);
            result.put("health_level", score >= 80 ? "HEALTHY" : score >= 60 ? "WARNING" : "CRITICAL");
            result.put("status", "success");

        } catch (Exception e) {
            result.put("health_score", 0);
            result.put("health_level", "UNKNOWN");
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }

    /**
     * 清理系统指标数据
     */
    @GetMapping("/cleanup-metrics")
    public Map<String, Object> cleanupMetrics() {
        Map<String, Object> result = new HashMap<>();
        result.put("timestamp", LocalDateTime.now().toString());

        try {
            int deletedCount = systemMetricMapper.cleanupOldMetrics();
            result.put("deleted_records", deletedCount);
            result.put("message", "清理成功");
            result.put("status", "success");

            log.info("清理系统指标数据: {} 条", deletedCount);

        } catch (Exception e) {
            log.error("清理指标数据失败: {}", e.getMessage());
            result.put("status", "error");
            result.put("message", e.getMessage());
        }

        return result;
    }
}