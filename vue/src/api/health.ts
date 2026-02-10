import request from '../utils/request';

// 健康检查接口定义
export const healthAPI = {
  ping: () => request.get('/api/health/ping'),
  check: () => request.get('/api/health/check'),
  schedulerStatus: () => request.get('/api/health/scheduler/status')
};

export default healthAPI;
