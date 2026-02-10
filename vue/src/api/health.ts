// D:\demo\code2026\vue\src\api\health.ts
import axios from 'axios';

// 重要：后端运行在9090端口，不是8080！
export const request = axios.create({
  baseURL: 'http://localhost:9090', // 修改这里！
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 健康检查接口定义
export const healthAPI = {
  ping: () => request.get('/api/health/ping').then(res => res.data),
  check: () => request.get('/api/health/check').then(res => res.data),
  schedulerStatus: () => request.get('/api/health/scheduler/status').then(res => res.data)
};

export default healthAPI;