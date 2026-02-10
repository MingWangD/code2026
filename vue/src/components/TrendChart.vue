<template>
  <div class="trend-chart">
    <h3>预警趋势分析</h3>
    <div ref="chartRef" style="width: 600px; height: 400px;"></div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'

const chartRef = ref(null)

// 1. 模拟数据（30天）
const generateMockData = () => {
  const dates = []
  const data = []

  // 生成最近30天的日期
  for (let i = 29; i >= 0; i--) {
    const date = new Date()
    date.setDate(date.getDate() - i)
    dates.push(`${date.getMonth() + 1}/${date.getDate()}`)

    // 模拟预警数量：40-60之间随机
    data.push(40 + Math.floor(Math.random() * 20))
  }

  return { dates, data }
}

// 2. 图表初始化
const initChart = () => {
  if (!chartRef.value) return

  const { dates, data } = generateMockData()

  const chart = echarts.init(chartRef.value)

  const option = {
    title: {
      text: '近30天预警趋势',
      left: 'center'
    },
    tooltip: {
      trigger: 'axis'
    },
    xAxis: {
      type: 'category',
      data: dates
    },
    yAxis: {
      type: 'value',
      name: '预警数量'
    },
    series: [{
      name: '预警数量',
      type: 'line',
      data: data,
      smooth: true
    }]
  }

  chart.setOption(option)

  // 响应式调整大小
  window.addEventListener('resize', () => {
    chart.resize()
  })
}

onMounted(() => {
  initChart()
})
</script>

<style scoped>
.trend-chart {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}

.trend-chart h3 {
  margin: 0 0 20px 0;
  color: #333;
}
</style>