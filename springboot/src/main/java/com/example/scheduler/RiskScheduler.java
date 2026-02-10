package com.example.scheduler;

import com.example.algorithm.RiskPredictor;
import com.example.mapper.SystemMetricMapper;
import com.example.service.LearningFeaturesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 教育风险预警系统定时任务
 */
@Component
@EnableScheduling
public class RiskScheduler {

    private static final Logger log = LoggerFactory.getLogger(RiskScheduler.class);

    @Autowired
    private LearningFeaturesService learningFeaturesService;

    @Autowired
    private RiskPredictor riskPredictor;

    @Autowired(required = false)
    private SystemMetricMapper systemMetricMapper;

    private final AtomicInteger concurrentTasks = new AtomicInteger(0);

    /**
     * 🧪 临时验证：每分钟刷新一次 learning_features 风险字段（验证通过后可删/改回）
     * 目的：你一启动，1分钟内就能看到它有没有执行、有没有更新。
     */
    @Scheduled(cron = "0 * * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void refreshRiskEveryMinuteForDebug() {
        String taskId = "RISK_REFRESH_" + System.currentTimeMillis();
        log.info("🧪 [定时任务-{}] 开始刷新最近7天风险字段 - {}", taskId, LocalDateTime.now());

        try {
            int refreshed = riskPredictor.refreshRecentLearningFeaturesRisk(7);
            log.info("✅ [定时任务-{}] 风险刷新完成，刷新 {} 条", taskId, refreshed);
        } catch (Exception e) {
            log.error("❌ [定时任务-{}] 风险刷新失败: {}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 🚨 风险预警扫描任务 - 每30分钟执行
     * （目前只是扫描高风险特征数量，后续再加：自动生成 risk_alerts）
     */
    @Scheduled(cron = "0 0/30 * * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void scanAndGenerateRiskAlerts() {
        if (concurrentTasks.get() >= 3) {
            log.warn("⚠️ 并发任务过多，跳过本次风险扫描");
            return;
        }

        concurrentTasks.incrementAndGet();
        long startTime = System.currentTimeMillis();
        String taskId = "RISK_SCAN_" + System.currentTimeMillis();

        log.info("🔄 [定时任务-{}] 开始风险预警扫描 - {}", taskId, LocalDateTime.now());

        try {
            // 先刷新最近7天风险字段
            int refreshed = riskPredictor.refreshRecentLearningFeaturesRisk(7);
            log.info("🔧 [定时任务-{}] 已刷新最近7天风险字段 {} 条", taskId, refreshed);

            // 再扫描高风险特征（risk_probability >= 0.7）
            var highRiskFeatures = learningFeaturesService.getHighRiskFeatures(0.7, 50);

            int highRiskCount = highRiskFeatures != null ? highRiskFeatures.size() : 0;
            long costTime = System.currentTimeMillis() - startTime;

            log.info("✅ [定时任务-{}] 风险扫描完成，高风险特征 {} 条，耗时 {}ms",
                    taskId, highRiskCount, costTime);

            if (systemMetricMapper != null) {
                systemMetricMapper.insertMetric(
                        "risk_scan",
                        taskId,
                        costTime,
                        highRiskCount > 0 ? "SUCCESS" : "NO_DATA",
                        LocalDateTime.now()
                );
            }

        } catch (Exception e) {
            log.error("❌ [定时任务-{}] 风险扫描失败: {}", taskId, e.getMessage(), e);

            if (systemMetricMapper != null) {
                systemMetricMapper.insertMetric(
                        "risk_scan",
                        taskId,
                        System.currentTimeMillis() - startTime,
                        "FAILED",
                        LocalDateTime.now()
                );
            }
        } finally {
            concurrentTasks.decrementAndGet();
        }
    }

    /**
     * 📊 学习特征计算任务 - 每天凌晨2点执行
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void calculateDailyLearningFeatures() {
        long startTime = System.currentTimeMillis();
        String taskId = "FEATURE_CALC_" + System.currentTimeMillis();

        log.info("🔄 [定时任务-{}] 开始计算学习特征 - {}", taskId, LocalDateTime.now());

        try {
            // ① 从 student_behavior 生成/更新 learning_features
            int generated = learningFeaturesService.batchCalculateFeatures(7);
            log.info("✅ [定时任务-{}] 特征生成完成，本次生成/更新 {} 条", taskId, generated);

            // ② 刷新风险字段
            int refreshed = riskPredictor.refreshRecentLearningFeaturesRisk(7);
            log.info("✅ [定时任务-{}] 风险刷新完成，本次刷新 {} 条", taskId, refreshed);

            long costTime = System.currentTimeMillis() - startTime;

            if (systemMetricMapper != null) {
                systemMetricMapper.insertMetric(
                        "feature_calculation",
                        taskId,
                        costTime,
                        (generated > 0 || refreshed > 0) ? "SUCCESS" : "NO_DATA",
                        LocalDateTime.now()
                );
            }

        } catch (Exception e) {
            log.error("❌ [定时任务-{}] 学习特征计算失败: {}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 🧹 数据清理任务 - 每周日凌晨3点执行
     */
    @Scheduled(cron = "0 0 3 ? * SUN")
    @Transactional(rollbackFor = Exception.class)
    public void cleanupOldData() {
        String taskId = "DATA_CLEANUP_" + System.currentTimeMillis();
        log.info("🧹 [定时任务-{}] 开始清理过期数据 - {}", taskId, LocalDateTime.now());

        try {
            if (systemMetricMapper != null) {
                int cleanedMetrics = systemMetricMapper.cleanupOldMetrics();
                log.info("🧹 清理 system_metrics: {} 条", cleanedMetrics);

                systemMetricMapper.insertMetric(
                        "data_cleanup",
                        taskId,
                        0L,
                        "SUCCESS",
                        LocalDateTime.now()
                );
            } else {
                log.warn("⚠️ systemMetricMapper 未注入，跳过指标清理/记录");
            }

        } catch (Exception e) {
            log.error("❌ [定时任务-{}] 数据清理失败: {}", taskId, e.getMessage(), e);
        }
    }

    /**
     * 🎯 测试任务 - 每分钟执行（仅用于调试）
     * 注意：这里用 INFO，避免你看不到日志
     */
    @Scheduled(cron = "30 * * * * ?")
    public void testScheduler() {
        log.info("⏰ 定时任务测试 - {}", LocalDateTime.now());
    }
}
