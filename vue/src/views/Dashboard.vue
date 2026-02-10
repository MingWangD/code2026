<template>
  <div class="dashboard-container">
    <!-- 顶部标题栏 -->
    <header class="dashboard-header">
      <h1>学情智能预警系统</h1>
      <div class="system-status">
        <span class="status-dot active"></span>
        <span>系统运行正常</span>
        <span class="update-time">最后更新: {{ updateTime }}</span>
      </div>
    </header>

    <!-- 学情预警总览卡片 -->
    <div class="overview-cards">
      <div class="card risk-high">
        <div class="card-icon">⚠️</div>
        <div class="card-content">
          <h3>高风险学生</h3>
          <div class="card-value">{{ overview.highRisk }}</div>
          <div class="card-trend">较昨日 +{{ overview.highRiskChange }}</div>
        </div>
      </div>

      <div class="card risk-medium">
        <div class="card-icon">⚠️</div>
        <div class="card-content">
          <h3>中风险学生</h3>
          <div class="card-value">{{ overview.mediumRisk }}</div>
          <div class="card-trend">较昨日 +{{ overview.mediumRiskChange }}</div>
        </div>
      </div>

      <div style="margin: 20px 0; padding: 10px; background: #f5f5f5; border-radius: 8px;">
        <button @click="testBackendAPI" style="padding: 8px 16px; background: #1890ff; color: white; border: none; border-radius: 4px; cursor: pointer;">
          测试趋势数据接口
        </button>
      </div>

      <div class="card risk-low">
        <div class="card-icon">📊</div>
        <div class="card-content">
          <h3>低风险学生</h3>
          <div class="card-value">{{ overview.lowRisk }}</div>
          <div class="card-trend">较昨日 +{{ overview.lowRiskChange }}</div>
        </div>
      </div>

      <div class="card total-alerts">
        <div class="card-icon">🔔</div>
        <div class="card-content">
          <h3>今日新增预警</h3>
          <div class="card-value">{{ overview.todayAlerts }}</div>
          <div class="card-trend">24小时内</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 - 两列布局 -->
    <div class="charts-grid">
      <!-- 左侧：预警类型分布 -->
      <div class="chart-section">
        <div class="section-header">
          <h2>预警类型分布</h2>
          <div class="time-filter">
            <button :class="{ active: timeFilter === 'day' }" @click="timeFilter = 'day'">今日</button>
            <button :class="{ active: timeFilter === 'week' }" @click="timeFilter = 'week'">本周</button>
            <button :class="{ active: timeFilter === 'month' }" @click="timeFilter = 'month'">本月</button>
          </div>
        </div>
        <div class="chart-container" style="height: 300px">
          <EchartsDemo :option="warningTypeChartOption" />
        </div>
      </div>

      <!-- 右侧：预警趋势分析 -->
      <div class="chart-section">
        <div class="section-header">
          <h2>预警趋势分析</h2>
          <div style="display: flex; align-items: center; gap: 10px;">
            <div class="time-filter">
              <button :class="{ active: trendPeriod === '7' }" @click="changeTrendPeriod('7')">近7天</button>
              <button :class="{ active: trendPeriod === '30' }" @click="changeTrendPeriod('30')">近30天</button>
              <button :class="{ active: trendPeriod === '90' }" @click="changeTrendPeriod('90')">近90天</button>
            </div>
            <button @click="refreshData" class="refresh-btn" title="刷新数据" style="padding: 6px 12px; background: #f0f5ff; border: 1px solid #1890ff; border-radius: 4px; cursor: pointer;">
              <span>🔄</span>
            </button>
            <span v-if="isLoading" class="loading-text" style="color: #666; font-size: 14px;">加载中...</span>
          </div>
        </div>
        <div class="chart-container" style="height: 300px">
          <div ref="trendChartRef" style="width: 100%; height: 100%;"></div>
        </div>
      </div>
    </div>

    <!-- 近期预警列表 -->
    <div class="alert-list-section">
      <div class="section-header">
        <h2>近期预警事件</h2>
        <button class="view-all">查看全部 →</button>
      </div>
      <div class="alert-table">
        <div class="table-header">
          <div class="col-student">学生姓名</div>
          <div class="col-risk">风险级别</div>
          <div class="col-type">预警类型</div>
          <div class="col-time">预警时间</div>
          <div class="col-status">处理状态</div>
        </div>
        <div class="table-body">
          <!-- 预警列表项 -->
          <div class="table-row" v-for="alert in recentAlerts" :key="alert.id">
            <div class="col-student">
              <span class="student-name">{{ alert.studentName }}</span>
              <span class="student-id">{{ alert.studentId }}</span>
            </div>
            <div class="col-risk">
              <span :class="'risk-level ' + alert.riskLevel">{{ alert.riskLevelLabel }}</span>
            </div>
            <div class="col-type">{{ alert.type }}</div>
            <div class="col-time">{{ formatTime(alert.time) }}</div>
            <div class="col-status">
              <span :class="'status ' + alert.status">{{ alert.statusLabel }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 系统信息 -->
    <div class="system-info">
      <div class="info-card">
        <h3>📊 系统性能</h3>
        <div class="info-item">
          <span>API响应时间</span>
          <span class="info-value">{{ systemInfo.apiResponseTime }}ms</span>
        </div>
        <div class="info-item">
          <span>数据库连接</span>
          <span class="info-value success">正常</span>
        </div>
        <div class="info-item">
          <span>定时任务</span>
          <span class="info-value success">运行中</span>
        </div>
      </div>
      <div class="info-card">
        <h3>📈 统计信息</h3>
        <div class="info-item">
          <span>监测学生总数</span>
          <span class="info-value">{{ systemInfo.totalStudents }}</span>
        </div>
        <div class="info-item">
          <span>活跃预警</span>
          <span class="info-value">{{ systemInfo.activeAlerts }}</span>
        </div>
        <div class="info-item">
          <span>已处理预警</span>
          <span class="info-value">{{ systemInfo.resolvedAlerts }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import EchartsDemo from '../components/EchartsDemo.vue'
import { ref, onMounted, onBeforeUnmount, watch } from 'vue'
// 注意：需要先创建dashboard.ts文件，然后使用相对路径导入
import { getWarningTrend } from '../api/dashboard'

export default {
  components: {
    EchartsDemo
  },
  name: 'Dashboard',
  setup() {
    // 数据定义
    const updateTime = ref('刚刚')
    const timeFilter = ref('day')
    const trendPeriod = ref('30') // 趋势图默认显示30天
    const trendChartRef = ref(null)
    let trendChartInstance = null
    const isLoading = ref(false)

    // 预警类型分布图配置
    const warningTypeChartOption = ref({
      // 删除整个title配置
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: {c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'middle',
        data: ['学业预警', '出勤预警', '行为预警', '心理预警']
      },
      series: [
        {
          name: '预警类型',
          type: 'pie',
          radius: ['40%', '70%'],
          center: ['50%', '40%'],
          avoidLabelOverlap: false,
          itemStyle: {
            borderRadius: 10,
            borderColor: '#fff',
            borderWidth: 2
          },
          label: {
            show: false,
            position: 'center'
          },
          emphasis: {
            label: {
              show: true,
              fontSize: '16',
              fontWeight: 'bold'
            }
          },
          labelLine: {
            show: false
          },
          data: [
            { value: 156, name: '学业预警', itemStyle: { color: '#5470c6' } },
            { value: 89, name: '出勤预警', itemStyle: { color: '#91cc75' } },
            { value: 67, name: '行为预警', itemStyle: { color: '#fac858' } },
            { value: 42, name: '心理预警', itemStyle: { color: '#ee6666' } }
          ]
        }
      ]
    })

    // 总览数据
    const overview = ref({
      highRisk: 12,
      highRiskChange: 2,
      mediumRisk: 45,
      mediumRiskChange: 5,
      lowRisk: 128,
      lowRiskChange: 8,
      todayAlerts: 23
    })

    // 近期预警列表
    const recentAlerts = ref([
      {
        id: 1,
        studentName: '张三',
        studentId: '20230001',
        riskLevel: 'high',
        riskLevelLabel: '高风险',
        type: '学业预警',
        time: '2024-01-15 14:30:00',
        status: 'pending',
        statusLabel: '待处理'
      },
      {
        id: 2,
        studentName: '李四',
        studentId: '20230002',
        riskLevel: 'medium',
        riskLevelLabel: '中风险',
        type: '出勤预警',
        time: '2024-01-15 10:15:00',
        status: 'processing',
        statusLabel: '处理中'
      },
      {
        id: 3,
        studentName: '王五',
        studentId: '20230003',
        riskLevel: 'low',
        riskLevelLabel: '低风险',
        type: '行为预警',
        time: '2024-01-15 09:45:00',
        status: 'resolved',
        statusLabel: '已处理'
      },
      {
        id: 4,
        studentName: '赵六',
        studentId: '20230004',
        riskLevel: 'high',
        riskLevelLabel: '高风险',
        type: '心理预警',
        time: '2024-01-14 16:20:00',
        status: 'pending',
        statusLabel: '待处理'
      }
    ])

    // 系统信息
    const systemInfo = ref({
      apiResponseTime: 156,
      totalStudents: 185,
      activeAlerts: 57,
      resolvedAlerts: 128
    })

    // 格式化时间
    const formatTime = (time) => {
      return time.split(' ')[1].substring(0, 5)
    }

    // 生成模拟趋势图数据（后备方案）
    const generateMockTrendData = (days) => {
      const dates = []
      const data = []
      const today = new Date()

      for (let i = days - 1; i >= 0; i--) {
        const date = new Date(today)
        date.setDate(date.getDate() - i)
        const month = (date.getMonth() + 1).toString().padStart(2, '0')
        const day = date.getDate().toString().padStart(2, '0')
        dates.push(`${month}/${day}`)

        // 模拟数据：基础值加上随机波动
        const baseValue = 30 + Math.floor(Math.random() * 40)
        const dailyValue = baseValue + Math.floor(Math.random() * 15) - 7
        data.push(Math.max(20, dailyValue))
      }

      return { dates, data }
    }

    // 更新趋势图（使用模拟数据）
    const updateTrendChart = (echarts) => {
      const days = parseInt(trendPeriod.value)
      const { dates, data } = generateMockTrendData(days)

      const option = {
        backgroundColor: 'transparent',
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#1890ff',
          borderWidth: 1,
          textStyle: {
            color: '#333'
          },
          formatter: function(params) {
            const date = params[0].axisValue
            const value = params[0].value
            return `${date}<br/>预警数量: <b style="color: #1890ff">${value}</b> 次`
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '15%',
          top: '10%',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: false,
          data: dates,
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 12,
            rotate: days > 30 ? 45 : 0
          }
        },
        yAxis: {
          type: 'value',
          name: '预警数量',
          nameTextStyle: {
            color: '#666',
            fontSize: 12
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            color: '#666',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0',
              type: 'dashed'
            }
          }
        },
        series: [
          {
            name: '预警数量',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: {
              width: 3,
              color: '#1890ff'
            },
            itemStyle: {
              color: '#1890ff',
              borderColor: '#fff',
              borderWidth: 2
            },
            areaStyle: {
              // 修复：使用简单的颜色，避免echarts.graphic.LinearGradient
              color: 'rgba(24, 144, 255, 0.15)'
            },
            data: data
          }
        ],
        dataZoom: [
          {
            type: 'inside',
            xAxisIndex: 0,
            start: days > 30 ? 70 : 0,
            end: 100
          },
          {
            show: days > 30,
            xAxisIndex: 0,
            type: 'slider',
            bottom: 10,
            start: days > 30 ? 70 : 0,
            end: 100,
            height: 20,
            borderColor: 'transparent',
            fillerColor: 'rgba(24, 144, 255, 0.2)',
            handleStyle: {
              color: '#1890ff'
            }
          }
        ]
      }

      if (trendChartInstance) {
        trendChartInstance.setOption(option, true)
      }
    }

    // 更新趋势图使用真实数据
    const updateTrendChartWithRealData = (dates, counts) => {
      if (!trendChartInstance) return

      const option = trendChartInstance.getOption()

      // 更新数据
      option.xAxis[0].data = dates
      option.series[0].data = counts

      // 更新提示框显示总数
      const total = counts.reduce((sum, count) => sum + count, 0)
      // 如果有标题，更新标题
      if (option.title && option.title.length > 0) {
        option.title[0].text = `近${dates.length}天预警趋势 (总计: ${total}次)`
      }

      trendChartInstance.setOption(option, true)
      isLoading.value = false
    }

    // 获取趋势数据
    const fetchTrendData = async (days) => {
      console.log(`📡 获取${days}天趋势数据...`)
      isLoading.value = true

      try {
        const result = await getWarningTrend(days)

        if (result.code === 200) {
          console.log('✅ 趋势数据获取成功:', result.data.dates.length, '天')
          console.log('数据示例:', result.data.dates.slice(0, 3), result.data.counts.slice(0, 3))

          // 使用真实数据更新图表
          updateTrendChartWithRealData(result.data.dates, result.data.counts)
        } else {
          console.warn('⚠️ 接口返回非200状态:', result.message)
          // 使用模拟数据
          if (window._echarts) {
            updateTrendChart(window._echarts)
          }
          isLoading.value = false
        }
      } catch (error) {
        console.error('❌ 获取趋势数据失败:', error)
        // 失败时使用模拟数据
        if (window._echarts) {
          updateTrendChart(window._echarts)
        }
        isLoading.value = false
      }
    }

    // 初始化趋势图
    const initTrendChart = () => {
      if (!trendChartRef.value) return

      // 方法1：尝试使用CDN全局echarts
      if (window.echarts) {
        console.log('✅ 使用全局echarts对象')
        window._echarts = window.echarts
        trendChartInstance = window.echarts.init(trendChartRef.value)
        updateTrendChart(window.echarts)
        fetchTrendData(parseInt(trendPeriod.value))
      } else {
        // 方法2：尝试从Vite打包路径导入
        console.log('🔄 尝试从Vite路径导入echarts...')
        import('/node_modules/.vite/deps/echarts.js?v=d8515f20').then(module => {
          const echarts = module.default || module
          console.log('✅ 从Vite路径导入成功:', echarts.version)
          window._echarts = echarts
          trendChartInstance = echarts.init(trendChartRef.value)
          updateTrendChart(echarts)
          fetchTrendData(parseInt(trendPeriod.value))
        }).catch(error => {
          console.error('❌ 所有导入方法都失败:', error)
          console.log('ℹ️ 图表将无法渲染，请检查ECharts模块加载问题')
        })
      }

      // 响应窗口大小变化
      window.addEventListener('resize', handleChartResize)
    }

    // 刷新数据
    const refreshData = () => {
      console.log('🔄 手动刷新数据...')
      fetchTrendData(parseInt(trendPeriod.value))
    }

    // 切换趋势图周期
    const changeTrendPeriod = (period) => {
      trendPeriod.value = period
    }

    // 处理图表大小变化
    const handleChartResize = () => {
      if (trendChartInstance) {
        trendChartInstance.resize()
      }
    }

    // 模拟数据更新
    const updateData = () => {
      const now = new Date()
      updateTime.value = now.toLocaleTimeString('zh-CN', {
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    // test
    const testBackendAPI = async () => {
      console.log('🔍 开始测试后端接口...')

      const baseURL = (import.meta.env.VITE_BASE_URL || "").replace(/\/$/, "")
      const endpoints = [
        '/api/dashboard/warning-trend?days=7',
        '/api/dashboard/overview',
        '/api/dashboard/health',
        '/api/health/ping'
      ]

      for (const endpoint of endpoints) {
        try {
          const response = await fetch(`${baseURL}${endpoint}`)
          console.log(`📡 ${endpoint}: ${response.status} ${response.statusText}`)

          if (response.ok) {
            const data = await response.json()
            console.log('✅ 数据格式:', data)
          } else if (response.status === 404) {
            console.log('❌ 接口不存在')
          }
        } catch (error) {
          console.log(`❌ ${endpoint}: 请求失败`, error.message)
        }

        // 延迟一下，避免请求过快
        await new Promise(resolve => setTimeout(resolve, 100))
      }
    }

    // 生命周期
    onMounted(() => {
      updateData()
      initTrendChart()

      // 每30秒更新一次时间
      setInterval(updateData, 30000)
    })

    onBeforeUnmount(() => {
      if (trendChartInstance) {
        trendChartInstance.dispose()
        trendChartInstance = null
      }
      window.removeEventListener('resize', handleChartResize)
      delete window._echarts
    })

    // 监听趋势图周期变化
    watch(trendPeriod, () => {
      fetchTrendData(parseInt(trendPeriod.value))
    })

    return {
      updateTime,
      timeFilter,
      trendPeriod,
      trendChartRef,
      warningTypeChartOption,
      overview,
      recentAlerts,
      systemInfo,
      isLoading,
      formatTime,
      refreshData,
      fetchTrendData,
      changeTrendPeriod,
      testBackendAPI
    }
  }
}
</script>

<style scoped>
/* 样式部分保持不变 */
.dashboard-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eaeaea;
}

.dashboard-header h1 {
  font-size: 24px;
  color: #333;
  margin: 0;
}

.system-status {
  display: flex;
  align-items: center;
  gap: 10px;
  color: #666;
  font-size: 14px;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ccc;
}

.status-dot.active {
  background-color: #52c41a;
}

.update-time {
  color: #999;
  font-size: 12px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s, box-shadow 0.2s;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
}

.card-icon {
  font-size: 32px;
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.risk-high .card-icon { background: #fff1f0; }
.risk-medium .card-icon { background: #fff7e6; }
.risk-low .card-icon { background: #f6ffed; }
.total-alerts .card-icon { background: #f0f5ff; }

.card-content h3 {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.card-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 4px;
}

.risk-high .card-value { color: #cf1322; }
.risk-medium .card-value { color: #fa8c16; }
.risk-low .card-value { color: #52c41a; }
.total-alerts .card-value { color: #1890ff; }

.card-trend {
  font-size: 12px;
  color: #999;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(600px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  font-size: 18px;
  color: #333;
  margin: 0;
}

.time-filter button {
  padding: 6px 16px;
  border: 1px solid #d9d9d9;
  background: white;
  color: #666;
  border-radius: 6px;
  cursor: pointer;
  margin-left: 8px;
  font-size: 14px;
}

.time-filter button.active {
  background: #1890ff;
  color: white;
  border-color: #1890ff;
}

.view-all {
  background: none;
  border: none;
  color: #1890ff;
  cursor: pointer;
  font-size: 14px;
  padding: 8px 12px;
  border-radius: 6px;
}

.view-all:hover {
  background: #f0f5ff;
}

.chart-container {
  position: relative;
  width: 100%;
}

.alert-list-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.alert-table {
  border: 1px solid #f0f0f0;
  border-radius: 8px;
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  background: #fafafa;
  padding: 16px 20px;
  font-weight: 500;
  color: #666;
  font-size: 14px;
  border-bottom: 1px solid #f0f0f0;
}

.table-body {
  max-height: 400px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  align-items: center;
}

.table-row:hover {
  background: #fafafa;
}

.student-name {
  display: block;
  font-weight: 500;
  margin-bottom: 4px;
}

.student-id {
  font-size: 12px;
  color: #999;
}

.risk-level {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.risk-level.high {
  background: #fff1f0;
  color: #cf1322;
}

.risk-level.medium {
  background: #fff7e6;
  color: #fa8c16;
}

.risk-level.low {
  background: #f6ffed;
  color: #52c41a;
}

.status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.status.pending {
  background: #fff1f0;
  color: #cf1322;
}

.status.processing {
  background: #e6f7ff;
  color: #1890ff;
}

.status.resolved {
  background: #f6ffed;
  color: #52c41a;
}

.system-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.info-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.info-card h3 {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #333;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-value {
  font-weight: 500;
  color: #333;
}

.info-value.success {
  color: #52c41a;
}

@media (max-width: 1200px) {
  .charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .overview-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .chart-section {
    padding: 16px;
  }

  .table-header,
  .table-row {
    grid-template-columns: repeat(5, 1fr);
    font-size: 12px;
    padding: 12px;
  }
}
</style>