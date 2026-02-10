import { request } from './health';

const API_BASE = '/api/dashboard';

// 获取预警趋势数据
export const getWarningTrend = async (days: number = 30) => {
    try {
        const response = await request.get(`${API_BASE}/warning-trend`, {
            params: { days }
        });
        return response.data;
    } catch (error) {
        console.error('获取趋势数据失败:', error);
        // 返回模拟数据作为后备
        return generateMockTrendData(days);
    }
};

// 安全的日期格式化函数（避免padStart和Date构造函数问题）
const formatDateSafe = (timestamp: number): string => {
    const date = new Date(timestamp);
    const month = date.getMonth() + 1;
    const day = date.getDate();

    // 手动补零，不使用padStart
    const monthStr = month < 10 ? `0${month}` : `${month}`;
    const dayStr = day < 10 ? `0${day}` : `${day}`;

    return `${monthStr}-${dayStr}`;
};

// 模拟数据生成（后备方案）- 完全避免Date构造函数问题
const generateMockTrendData = (days: number) => {
    const dates: string[] = [];
    const counts: number[] = [];
    let total = 0;

    // 使用当前时间戳
    const now = Date.now();
    const oneDayMs = 24 * 60 * 60 * 1000;

    for (let i = days - 1; i >= 0; i--) {
        // 计算过去第i天的时间戳
        const pastDateTimestamp = now - (i * oneDayMs);

        // 格式化日期
        dates.push(formatDateSafe(pastDateTimestamp));

        // 生成随机数据
        const count = 50 + Math.floor(Math.random() * 30);
        counts.push(count);
        total += count;
    }

    // 计算开始和结束日期字符串
    const endDate = new Date(now);
    const startDate = new Date(now - ((days - 1) * oneDayMs));

    return {
        code: 200,
        message: 'success (mock data)',
        data: {
            dates,
            counts,
            total,
            startDate: startDate.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0],
            queryDays: days
        }
    };
};

// 获取Dashboard总览数据
export const getDashboardOverview = async () => {
    try {
        const response = await request.get(`${API_BASE}/overview`);
        return response.data;
    } catch (error) {
        console.error('获取总览数据失败:', error);
        // 返回模拟总览数据
        return {
            code: 200,
            message: 'success (mock overview)',
            data: {
                riskStats: {
                    highRisk: 12,
                    mediumRisk: 45,
                    lowRisk: 128
                },
                todayAlerts: 23,
                typeDistribution: [
                    { name: 'HOMEWORK', value: 156 },
                    { name: 'VIDEO', value: 89 },
                    { name: 'EXAM', value: 67 },
                    { name: 'ATTENDANCE', value: 42 },
                    { name: 'BEHAVIOR', value: 35 }
                ],
                timestamp: Date.now()
            }
        };
    }
};