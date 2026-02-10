package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取预警趋势数据
     */
    public Map<String, Object> getWarningTrendData(int days) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 查询数据库获取趋势数据
            String sql = """
                SELECT 
                    DATE(alert_time) as date,
                    COUNT(*) as count
                FROM risk_alerts 
                WHERE alert_time >= DATE_SUB(NOW(), INTERVAL ? DAY)
                GROUP BY DATE(alert_time)
                ORDER BY date
                """;

            List<Map<String, Object>> dbData = jdbcTemplate.queryForList(sql, days);

            // 2. 处理数据 - 确保连续日期
            List<String> dates = new ArrayList<>();
            List<Integer> counts = new ArrayList<>();
            int total = 0;

            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusDays(days - 1);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

            // 创建日期映射
            Map<String, Integer> dateCountMap = new HashMap<>();
            for (Map<String, Object> row : dbData) {
                // 修复：使用java.sql.Date的toLocalDate()
                java.sql.Date sqlDate = (java.sql.Date) row.get("date");
                LocalDate localDate = sqlDate.toLocalDate();
                String dateKey = localDate.format(formatter);
                Integer count = ((Number) row.get("count")).intValue();
                dateCountMap.put(dateKey, count);
                total += count;
            }

            // 填充所有日期（包括没有数据的日期）
            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                String dateKey = currentDate.format(formatter);
                dates.add(dateKey);

                Integer count = dateCountMap.get(dateKey);
                if (count == null) {
                    count = 0; // 没有数据的日期设为0
                }
                counts.add(count);

                currentDate = currentDate.plusDays(1);
            }

            // 3. 构建返回结果
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", Map.of(
                    "dates", dates,
                    "counts", counts,
                    "total", total,
                    "startDate", startDate.toString(),
                    "endDate", endDate.toString(),
                    "queryDays", days
            ));

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "数据查询失败: " + e.getMessage());
            result.put("data", Collections.emptyMap());
        }

        return result;
    }

    /**
     * 获取Dashboard总览数据
     */
    public Map<String, Object> getDashboardOverview() {
        Map<String, Object> result = new HashMap<>();

        try {
            // 1. 风险级别统计
            String riskSql = """
                SELECT 
                    alert_level,
                    COUNT(*) as count
                FROM risk_alerts
                WHERE alert_time >= DATE_SUB(NOW(), INTERVAL 7 DAY)
                GROUP BY alert_level
                """;

            List<Map<String, Object>> riskData = jdbcTemplate.queryForList(riskSql);

            Map<String, Integer> riskStats = new HashMap<>();
            for (Map<String, Object> row : riskData) {
                String level = (String) row.get("alert_level");
                Integer count = ((Number) row.get("count")).intValue();
                riskStats.put(level.toLowerCase() + "Risk", count);
            }

            // 2. 今日预警数量
            String todaySql = """
                SELECT COUNT(*) as count
                FROM risk_alerts
                WHERE DATE(alert_time) = CURDATE()
                """;

            Integer todayCount = jdbcTemplate.queryForObject(todaySql, Integer.class);

            // 3. 预警类型分布
            String typeSql = """
                SELECT 
                    alert_type,
                    COUNT(*) as count
                FROM risk_alerts
                WHERE alert_time >= DATE_SUB(NOW(), INTERVAL 30 DAY)
                GROUP BY alert_type
                """;

            List<Map<String, Object>> typeData = jdbcTemplate.queryForList(typeSql);
            List<Map<String, Object>> typeStats = new ArrayList<>();
            for (Map<String, Object> row : typeData) {
                typeStats.add(Map.of(
                        "name", row.get("alert_type"),
                        "value", row.get("count")
                ));
            }

            // 4. 构建总览数据
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", Map.of(
                    "riskStats", Map.of(
                            "highRisk", riskStats.getOrDefault("high", 0),
                            "mediumRisk", riskStats.getOrDefault("medium", 0),
                            "lowRisk", riskStats.getOrDefault("low", 0)
                    ),
                    "todayAlerts", todayCount != null ? todayCount : 0,
                    "typeDistribution", typeStats,
                    "timestamp", System.currentTimeMillis()
            ));

        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "总览数据查询失败: " + e.getMessage());
            result.put("data", Collections.emptyMap());
        }

        return result;
    }
}