package com.example.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface SystemMetricMapper {

    /**
     * 初始化系统指标表（如果不存在则创建）
     */
    @Update("CREATE TABLE IF NOT EXISTS system_metrics (" +
            "    id BIGINT AUTO_INCREMENT PRIMARY KEY," +
            "    metric_name VARCHAR(50) NOT NULL COMMENT '指标名称'," +
            "    task_id VARCHAR(100) NOT NULL COMMENT '任务ID'," +
            "    execution_time BIGINT NOT NULL COMMENT '执行耗时(ms)'," +
            "    status VARCHAR(20) NOT NULL COMMENT '执行状态: SUCCESS/FAILED/NO_DATA'," +
            "    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'," +
            "    INDEX idx_metric_name (metric_name)," +
            "    INDEX idx_created_time (created_time)," +
            "    INDEX idx_status (status)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统指标监控表'")
    void createMetricsTableIfNotExists();

    /**
     * 插入定时任务执行指标
     */
    @Insert("INSERT INTO system_metrics " +
            "(metric_name, task_id, execution_time, status, created_time) " +
            "VALUES (#{metricName}, #{taskId}, #{executionTime}, #{status}, #{createdTime})")
    void insertMetric(String metricName, String taskId, long executionTime,
                      String status, LocalDateTime createdTime);

    /**
     * 查询最近24小时的任务执行情况
     */
    @Select("SELECT metric_name, " +
            "       COUNT(*) as total_executions, " +
            "       AVG(execution_time) as avg_time, " +
            "       SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count " +
            "FROM system_metrics " +
            "WHERE created_time >= DATE_SUB(NOW(), INTERVAL 24 HOUR) " +
            "GROUP BY metric_name")
    List<Map<String, Object>> getDailyMetrics();

    /**
     * 清理30天前的指标数据
     */
    @Update("DELETE FROM system_metrics WHERE created_time < DATE_SUB(NOW(), INTERVAL 30 DAY)")
    int cleanupOldMetrics();
}