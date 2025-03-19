<template>
  <div class="history-page">
    <!-- 顶部上传按钮 -->
    <el-card style="width: 100%; height: 200px; margin: 0 auto;">
      <!-- 第一行：上传按钮 -->
      <div style="display: flex; gap: 60px; justify-content: center; align-items: center; margin-top: 40px;">
        <el-upload :before-upload="handleParamUpload" show-file-list="false" accept=".txt">
          <el-button type="primary">📁 上传参数文件</el-button>
        </el-upload>
        <el-upload :before-upload="handleWaveformUpload" show-file-list="false" accept=".txt">
          <el-button type="success">📁 上传波形文件</el-button>
        </el-upload>
      </div>

      <!-- 第二行：采样率输入框 -->
      <div style="display: flex; justify-content: center; align-items: center; margin-top: 20px;">
        <span style="margin-right: 10px; font-weight: bold;">采样率 (kHz)：</span>
        <el-input v-model="sampleRateKHz" placeholder="例如：1000" style="width: 200px;" />
      </div>
    </el-card>


    <!-- 下方内容：左右布局 -->
    <div class="bottom-area">
      <!-- 左侧：参数表格 -->
      <div class="left-table">
        <div style="font-size: 20px; margin-bottom: 10px;">
          <h4>📋 历史参数</h4>
        </div>
        <div class="table-wrapper">
          <el-table :data="paramList" style="width: 100%;" height="100%">
            <el-table-column prop="time" label="到达时间" width="180" />
            <el-table-column prop="amp" label="幅值" />
            <el-table-column prop="power" label="功率" />
            <el-table-column prop="rms" label="均方根" />
            <el-table-column prop="asl" label="平均声压" />
            <el-table-column prop="tr" label="上升时间" />
            <el-table-column prop="rise_count" label="上升计数" />
            <el-table-column prop="duration" label="持续时间" />
            <el-table-column prop="ring_count" label="振铃计数" />
          </el-table>
        </div>
      </div>

      <!-- 右侧：波形图 -->
      <div class="right-chart">
        <div style="font-size: 20px; margin-bottom: 10px;">
          <h4>📈 历史波形</h4>
        </div>
        <div id="historyChart" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import * as echarts from 'echarts'

// 参数列表
const paramList = ref([])

// 波形数组
const waveform = ref([])
let chart = null
const sampleRateKHz = ref(1000) // 默认采样率 1000 kHz

// 参数解析
const handleParamUpload = (file) => {
  const reader = new FileReader()
  reader.onload = () => parseParams(reader.result)
  reader.readAsText(file)
  return false
}

const parseParams = (text) => {
  const lines = text.split('\n').filter(l => l.includes('到达时间'))
  const parsed = lines.map(line => {
    const m = line.match(/到达时间: (.+?), AMP: ([\d.]+), Power: ([\d.]+), RMS: ([\d.]+), ASL: ([\d.]+), tr: (\d+), 上升计数: (\d+), 持续时间: (\d+), 振铃计数: (\d+)/)
    if (!m) return null
    return {
      time: m[1],
      amp: parseFloat(m[2]),
      power: parseFloat(m[3]),
      rms: parseFloat(m[4]),
      asl: parseFloat(m[5]),
      tr: Number(m[6]),
      rise_count: Number(m[7]),
      duration: Number(m[8]),
      ring_count: Number(m[9]),
    }
  }).filter(Boolean)
  paramList.value = parsed
}

// 波形解析
const handleWaveformUpload = (file) => {
  const reader = new FileReader()
  reader.onload = () => {
    const text = reader.result

    // 每行一个波形包，拼接成大数组
    const allPoints = text
        .split('\n')
        .flatMap(line =>
            line.trim().split(/\s+/).filter(s => s && !isNaN(s)).map(s => parseFloat(Number(s).toFixed(6)))
        )

    waveform.value = allPoints
    renderChart()
  }
  reader.readAsText(file)
  return false
}


// 绘图
const renderChart = () => {
  nextTick(() => {
    const dom = document.getElementById('historyChart')
    if (!dom) return

    if (!chart) chart = echarts.init(dom)

    const rate = sampleRateKHz.value * 1000 // kHz → Hz
    const interval = 1000 / rate            // 每个点多少 ms
    const xData = waveform.value.map((_, i) => (i * interval).toFixed(3))

    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: {
        type: 'category',
        data: xData,
        name: '时间 (ms)',
        axisLabel: { fontSize: 10 }
      },
      yAxis: {
        type: 'value',
        name: '幅值 (V)',
        minInterval: 0.000001,
        axisLabel: { fontSize: 10 }
      },
      series: [{
        type: 'line',
        data: waveform.value,
        showSymbol: false,
        lineStyle: { width: 1 }
      }],
      animation: false
    })

    chart.resize()
  })
}

</script>

<style scoped>
.history-page {
  padding: 20px;
}

.bottom-area {
  display: flex;
  margin-top: 20px;
  height: 500px;
}

.left-table {
  flex: 1;
  margin-right: 20px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.table-wrapper {
  flex: 1;
  overflow-y: auto;
}

.right-chart {
  flex: 1;
  display: flex;
  flex-direction: column;
  border-left: 1px solid #ccc;
  padding-left: 20px;
}

.chart-container {
  flex: 1;
  width: 100%;
  height: 100%;
  background: #f8f8f8;
}
</style>
