package com.example.service.init;

import com.example.mapper.SystemMetricMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SystemInitService implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SystemInitService.class);

    @Autowired
    private SystemMetricMapper systemMetricMapper;

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        log.info("🚀 系统初始化开始...");

        try {
            // 1. 创建系统指标表
            systemMetricMapper.createMetricsTableIfNotExists();
            log.info("✅ 系统指标表初始化完成");

            // 2. 创建健康检查视图（如果不存在）
            createHealthViewIfNotExists();

            // 3. 清理过期指标数据
            int cleaned = systemMetricMapper.cleanupOldMetrics();
            log.info("🧹 清理过期指标数据: {} 条", cleaned);

        } catch (Exception e) {
            log.warn("⚠️ 系统初始化过程中出现异常（可能表已存在）: {}", e.getMessage());
        }

        log.info("🚀 系统初始化完成");
    }

    private void createHealthViewIfNotExists() {
        try {
            if (jdbcTemplate != null) {
                String createViewSql =
                        "CREATE OR REPLACE VIEW system_health_view AS " +
                                "SELECT DATE(created_time) as check_date, " +
                                "       metric_name, " +
                                "       COUNT(*) as execution_count, " +
                                "       AVG(execution_time) as avg_execution_time, " +
                                "       SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count, " +
                                "       SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_count " +
                                "FROM system_metrics " +
                                "WHERE created_time >= DATE_SUB(NOW(), INTERVAL 7 DAY) " +
                                "GROUP BY DATE(created_time), metric_name " +
                                "ORDER BY check_date DESC";

                jdbcTemplate.execute(createViewSql);
                log.info("✅ 健康检查视图创建完成");
            }
        } catch (Exception e) {
            log.debug("健康检查视图可能已存在: {}", e.getMessage());
        }
    }
}