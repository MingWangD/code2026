package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 完整的压力测试套件
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CompletePressureTest {

    @Autowired
    private MockMvc mockMvc;

    private int successCount = 0;
    private int failureCount = 0;

    @BeforeEach
    public void setup() {
        successCount = 0;
        failureCount = 0;
        System.out.println("🔧 测试初始化完成");
    }

    /**
     * 测试1: 简单ping接口压力测试
     */
    @Test
    public void testPingEndpoint() throws Exception {
        System.out.println("🚀 开始ping接口压力测试...");
        long startTime = System.currentTimeMillis();
        int totalRequests = 50;

        for (int i = 0; i < totalRequests; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/health/ping"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.status").value("OK"));
                successCount++;
            } catch (Exception e) {
                failureCount++;
                System.err.println("请求失败: " + e.getMessage());
            }

            // 进度显示
            if ((i + 1) % 10 == 0) {
                System.out.println("  进度: " + (i + 1) + "/" + totalRequests);
            }
        }

        printTestResult("Ping接口测试", totalRequests, startTime);
    }

    /**
     * 测试2: 健康检查接口测试
     */
    @Test
    public void testHealthCheckEndpoint() throws Exception {
        System.out.println("🚀 开始健康检查接口测试...");
        long startTime = System.currentTimeMillis();
        int totalRequests = 20;

        for (int i = 0; i < totalRequests; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/health/check"))
                        .andExpect(status().isOk());
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }
        }

        printTestResult("健康检查接口测试", totalRequests, startTime);
    }

    /**
     * 测试3: 定时任务状态接口测试
     */
    @Test
    public void testSchedulerStatusEndpoint() throws Exception {
        System.out.println("🚀 开始定时任务状态接口测试...");
        long startTime = System.currentTimeMillis();
        int totalRequests = 15;

        for (int i = 0; i < totalRequests; i++) {
            try {
                mockMvc.perform(MockMvcRequestBuilders.get("/api/health/scheduler/status"))
                        .andExpect(status().isOk());
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }
        }

        printTestResult("定时任务状态接口测试", totalRequests, startTime);
    }

    /**
     * 测试4: 混合请求测试（模拟真实场景）
     */
    @Test
    public void testMixedEndpoints() throws Exception {
        System.out.println("🚀 开始混合接口测试（模拟真实场景）...");
        long startTime = System.currentTimeMillis();
        int totalRequests = 100;

        String[] endpoints = {
                "/api/health/ping",
                "/api/health/check",
                "/api/health/scheduler/status"
        };

        for (int i = 0; i < totalRequests; i++) {
            String endpoint = endpoints[i % endpoints.length];
            try {
                mockMvc.perform(MockMvcRequestBuilders.get(endpoint))
                        .andExpect(status().isOk());
                successCount++;
            } catch (Exception e) {
                failureCount++;
            }

            // 每10次请求后短暂暂停，模拟真实用户行为
            if ((i + 1) % 10 == 0) {
                Thread.sleep(10);
            }
        }

        printTestResult("混合接口测试", totalRequests, startTime);
    }

    /**
     * 测试5: 并发测试（使用线程模拟并发）
     */
    @Test
    public void testConcurrentRequests() throws Exception {
        System.out.println("🚀 开始并发请求测试（模拟10个并发用户）...");
        long startTime = System.currentTimeMillis();
        int threadCount = 10;
        int requestsPerThread = 10;

        Thread[] threads = new Thread[threadCount];

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < requestsPerThread; i++) {
                    try {
                        mockMvc.perform(MockMvcRequestBuilders.get("/api/health/ping"))
                                .andExpect(status().isOk());
                        synchronized (this) {
                            successCount++;
                        }
                    } catch (Exception e) {
                        synchronized (this) {
                            failureCount++;
                        }
                    }
                }
                System.out.println("   线程 " + threadId + " 完成");
            });
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        int totalRequests = threadCount * requestsPerThread;
        printTestResult("并发请求测试", totalRequests, startTime);
    }

    /**
     * 打印测试结果
     */
    private void printTestResult(String testName, int totalRequests, long startTime) {
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double avgTime = totalTime / (double) totalRequests;
        double successRate = (double) successCount / totalRequests * 100;
        double qps = totalRequests / (totalTime / 1000.0);

        System.out.println("📊 " + testName + " 结果:");
        System.out.println("   └─ 总请求数: " + totalRequests);
        System.out.println("   └─ 成功数: " + successCount);
        System.out.println("   └─ 失败数: " + failureCount);
        System.out.println("   └─ 成功率: " + String.format("%.2f", successRate) + "%");
        System.out.println("   └─ 总耗时: " + totalTime + "ms");
        System.out.println("   └─ 平均响应时间: " + String.format("%.2f", avgTime) + "ms");
        System.out.println("   └─ QPS: " + String.format("%.2f", qps));
        System.out.println("   └─ 性能评级: " + getPerformanceRating(avgTime, qps));
        System.out.println();
    }

    /**
     * 性能评级
     */
    private String getPerformanceRating(double avgTime, double qps) {
        if (avgTime < 5 && qps > 500) {
            return "⭐⭐⭐⭐⭐ 优秀";
        } else if (avgTime < 10 && qps > 300) {
            return "⭐⭐⭐⭐ 良好";
        } else if (avgTime < 20 && qps > 200) {
            return "⭐⭐⭐ 中等";
        } else if (avgTime < 50 && qps > 100) {
            return "⭐⭐ 一般";
        } else {
            return "⭐ 需要优化";
        }
    }
}