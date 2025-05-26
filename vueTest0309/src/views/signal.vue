<template>
  <div class="signal-page">
    <!-- 顶部控制区域 -->
    <el-card>
      <div style="text-align: center; font-size: 26px; margin-bottom: 20px;">
        <h3 style="margin: 0;">信号采集参数设置</h3>
      </div>

      <!-- 传感器选择按钮 -->
      <div style="margin-bottom: 20px; text-align: center;">
        <el-button
            :type="currentSensorIp === '192_168_0_104' ? 'primary' : 'default'"
            @click="selectSensor('192_168_0_104')">
          传感器 1
        </el-button>
        <el-button
            :type="currentSensorIp === '192_168_0_106' ? 'primary' : 'default'"
            @click="selectSensor('192_168_0_106')">
          传感器 2
        </el-button>
      </div>

      <el-form label-width="80px" label-position="left">
        <div class="form-row">
          <el-form-item label="EET (us)">
            <el-input v-model="params.eet_v2" />
          </el-form-item>
          <el-form-item label="HDT (us)">
            <el-input v-model="params.hdt_v2" />
          </el-form-item>
          <el-form-item label="HLT (us)">
            <el-input v-model="params.hlt_v2" />
          </el-form-item>
        </div>

        <div class="form-row">
          <el-form-item label="采样速度 (kHz)">
            <el-input v-model="params.speed" />
          </el-form-item>
          <el-form-item label="门限 (dB)">
            <el-input v-model="params.trigger_db" />
          </el-form-item>
          <el-form-item label="前采点数">
            <el-input v-model="params.pre_sample_length" />
          </el-form-item>
        </div>

        <!-- 按钮 -->
        <el-form-item class="btn-row">
          <el-button type="primary" @click="startAllCollect">▶️ 开始全部采集</el-button>
          <el-button type="danger" @click="stopCollect">⛔ 停止采集</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 下方区域 -->
    <div class="bottom-area">
      <div class="left-table">
        <h4 class="table-title">📋 实时参数</h4>
        <div class="table-wrapper">
          <el-table :data="paramList" height="100%" style="width: 100%;" empty-text="暂无数据">
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

      <div class="right-chart">
        <h4 class="chart-title">📈 实时波形</h4>
        <div id="waveformChart" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import * as echarts from 'echarts'
import SockJS from 'sockjs-client'
import Stomp from 'stompjs'

const ip = ref('192.168.0.104')  // 用于参数设置
const currentSensorIp = ref('192_168_0_104')  // 当前显示的传感器（订阅用）

const params = ref({
  eet_v2: '2000',
  hdt_v2: '2000',
  hlt_v2: '500',
  speed: '1000',
  trigger_db: '35',
  pre_sample_length: '0',
})

const paramList = ref([])
let waveformChart = null
let stompClient = null

const startAllCollect = async () => {
  try {
    const paramStr = Object.entries(params.value)
        .map(([k, v]) => `${k}=${v}`)
        .join('&')

    await axios.post('http://localhost:9999/api/signal/setParams', {
      ip: ip.value,  // 通常只需要发一次参数
      params: paramStr,
    })

    await axios.post('http://localhost:9999/api/signal/startAll')
    console.log('✅ 已触发全部采集任务')
  } catch (err) {
    console.error('❌ 开始采集失败', err)
  }
}

const stopCollect = async () => {
  try {
    await axios.post('http://localhost:9999/api/signal/stop')
    console.log('✅ 停止采集成功')
  } catch (err) {
    console.error('❌ 停止采集失败', err)
  }
}

const initChart = () => {
  const dom = document.getElementById('waveformChart')
  if (!dom) return
  waveformChart = echarts.init(dom)
  waveformChart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: [], axisLine: { show: false } },
    yAxis: { type: 'value', min: 'dataMin', max: 'dataMax', minInterval: 1, name: '幅值 (V)', axisLabel: { fontSize: 10 } },
    series: [{ name: 'Voltage', type: 'line', data: [], showSymbol: false, lineStyle: { width: 1 } }],
    animation: false
  })
  waveformChart.resize()
  window.addEventListener('resize', () => waveformChart?.resize())
}

const updateChart = (points) => {
  if (!waveformChart) return
  const sampleRate = Number(params.value.speed) * 1000
  const intervalMs = 1000 / sampleRate
  const xData = points.map((_, i) => (i * intervalMs).toFixed(2))
  waveformChart.setOption({
    xAxis: { data: xData, name: '时间 (ms)', nameTextStyle: { fontSize: 12 }, axisLabel: { fontSize: 10 } },
    series: [{ data: points }]
  })
}

const selectSensor = (sensorIp) => {
  currentSensorIp.value = sensorIp
  paramList.value = []
  if (stompClient) subscribeTopics()
}

const initWebSocket = () => {
  const socket = new SockJS('http://localhost:9999/ws')
  stompClient = Stomp.over(socket)
  stompClient.connect({}, () => {
    subscribeTopics()
  })
}

const subscribeTopics = () => {
  stompClient.unsubscribe('param-sub')
  stompClient.unsubscribe('waveform-sub')

  stompClient.subscribe(`/topic/params/${currentSensorIp.value}`, (msg) => {
    const raw = JSON.parse(msg.body)
    const mapped = {
      time: raw.timestamp,
      amp: raw.amp,
      power: raw.power,
      rms: raw.rms,
      asl: raw.asl,
      tr: raw.tr,
      rise_count: raw.riseCount,
      duration: raw.duration,
      ring_count: raw.ringCount,
    }
    paramList.value.unshift(mapped)
    if (paramList.value.length > 100) paramList.value.length = 100
  }, { id: 'param-sub' })

  stompClient.subscribe(`/topic/waveform/${currentSensorIp.value}`, (msg) => {
    const points = JSON.parse(msg.body)
    updateChart(points)
  }, { id: 'waveform-sub' })

  console.log(`✅ 已切换订阅到 ${currentSensorIp.value}`)
}

onMounted(() => {
  initChart()
  initWebSocket()
})
</script>

<style scoped>
.signal-page { padding: 20px; }
.form-row { display: grid; grid-template-columns: repeat(3, 1fr); gap: 50px; }
.btn-row { margin-left: 0; margin-top: 20px; }
.bottom-area { display: flex; margin-top: 20px; height: 500px; }
.left-table, .right-chart { flex: 1; display: flex; flex-direction: column; height: 100%; }
.table-wrapper { flex: 1; overflow-y: auto; }
.left-table { margin-right: 40px; }
.right-chart { padding-left: 40px; border-left: 1px solid #531b1b; }
.table-title, .chart-title { font-size: 22px; margin: 0 0 16px 0; padding-left: 10px; }
.chart-container { flex: 1; height: 100%; width: 100%; background: #f8f8f8; overflow: hidden; }
</style>
