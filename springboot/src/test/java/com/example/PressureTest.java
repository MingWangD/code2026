package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 关键接口压力测试（简化版）
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PressureTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试健康检查接口的响应时间
     */
    @Test
    public void testHealthCheckPerformance() throws Exception {
        System.out.println("开始压力测试...");
        long startTime = System.currentTimeMillis();
        int requestCount = 20;

        for (int i = 0; i < requestCount; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/health/ping"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("OK"));

            // 每5次请求打印一次进度
            if ((i + 1) % 5 == 0) {
                System.out.println("已完成 " + (i + 1) + " 次请求");
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = totalTime / (double) requestCount;

        System.out.println("✅ 压力测试结果:");
        System.out.println("   总请求数: " + requestCount);
        System.out.println("   总耗时: " + totalTime + "ms");
        System.out.println("   平均响应时间: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("   QPS: " + String.format("%.2f", requestCount / (totalTime / 1000.0)));
    }

    /**
     * 测试数据库连接性能
     */
    @Test
    public void testDatabasePerformance() throws Exception {
        System.out.println("开始数据库性能测试...");
        long startTime = System.currentTimeMillis();
        int requestCount = 10;

        for (int i = 0; i < requestCount; i++) {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/health/check"))
                    .andExpect(status().isOk());

            System.out.println("数据库检查 " + (i + 1) + "/" + requestCount);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("✅ 数据库性能测试完成");
        System.out.println("   总耗时: " + (endTime - startTime) + "ms");
    }
}