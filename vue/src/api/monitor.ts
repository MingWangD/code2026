// src/api/monitor.ts - 系统监控API模块
import { request } from './health'  // 复用同一个axios实例

export interface PerformanceMetrics {
    memory: {
        heap_used: string
        non_heap_used: string
        heap_max: string
    }
    os: {
        available_processors: number
        name: string
        arch: string
        version: string
    }
    threads: {
        daemon_threads: number
        total_threads: number
        peak_threads: number
    }
    jvm: {
        name: string
        vendor: string
        version: string
    }
}

export const monitorAPI = {
    // 获取性能指标
    getPerformance: (): Promise<PerformanceMetrics> =>
        request.get('/api/monitor/performance')
}

export default monitorAPI