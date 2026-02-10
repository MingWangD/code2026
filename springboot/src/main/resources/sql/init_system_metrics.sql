-- 创建系统指标监控表
CREATE TABLE IF NOT EXISTS system_metrics (
                                              id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                              metric_name VARCHAR(50) NOT NULL COMMENT '指标名称',
    task_id VARCHAR(100) NOT NULL COMMENT '任务ID',
    execution_time BIGINT NOT NULL COMMENT '执行耗时(ms)',
    status VARCHAR(20) NOT NULL COMMENT '执行状态: SUCCESS/FAILED/NO_DATA',
    created_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_metric_name (metric_name),
    INDEX idx_created_time (created_time),
    INDEX idx_status (status)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统指标监控表';

-- 创建健康检查视图
CREATE OR REPLACE VIEW system_health_view AS
SELECT
    DATE(created_time) as check_date,
    metric_name,
    COUNT(*) as execution_count,
    AVG(execution_time) as avg_execution_time,
    SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) as success_count,
    SUM(CASE WHEN status = 'FAILED' THEN 1 ELSE 0 END) as failed_count
FROM system_metrics
WHERE created_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
GROUP BY DATE(created_time), metric_name
ORDER BY check_date DESC;