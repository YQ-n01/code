<template>
  <div style="padding: 20px;">
    <h2>FPGA 信号采集控制</h2>

    <label>FPGA IP 最后三位：</label>
    <input
        v-model="ipSuffix"
        placeholder="192.168.0."
        :disabled="collecting"
        style="margin-right: 10px; width: 80px;"
    />

    <button @click="start" :disabled="collecting || !ipSuffix">开始采集</button>
    <button @click="stop" :disabled="!collecting">停止采集</button>

    <p style="margin-top: 12px;">
      当前状态：
      <span :style="{ color: collecting ? 'green' : 'gray' }">
        {{ collecting ? '采集中' : '空闲中' }}
      </span>
    </p>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'SignalControl',
  data() {
    return {
      ipSuffix: '',
      collecting: false,
      statusTimer: null
    }
  },
  methods: {
    async start() {
      const ip = this.ipSuffix.trim()
      if (!ip) {
        this.$message.warning('请输入 IP 后三位')
        return
      }

      try {
        await axios.post("http://localhost:9999/api/signal/start", { ip: this.ipSuffix })
        this.$message.success(res.data || '采集已启动')
        this.checkStatus()
      } catch (err) {
        console.error('采集启动失败', err)
        this.$message.error('采集启动失败')
      }
    },
    async stop() {
      try {
        const res = await axios.post('http://localhost:9999/api/signal/stop')
        this.$message.success(res.data || '采集已停止')
        this.checkStatus()
      } catch (err) {
        console.error('采集停止失败', err)
        this.$message.error('采集停止失败')
      }
    },
    async checkStatus() {
      try {
        const res = await axios.get('http://localhost:9999/api/signal/status')
        this.collecting = res.data === true
      } catch (err) {
        console.warn('状态检查失败', err)
        this.collecting = false // 请求失败时，回退为未采集状态
      }
    }
  },
  mounted() {
    this.checkStatus()
    this.statusTimer = setInterval(this.checkStatus, 3000) // 每 3 秒轮询状态
  },
  beforeUnmount() {
    clearInterval(this.statusTimer)
  }
}
</script>

<style scoped>
button {
  margin-right: 10px;
  padding: 6px 14px;
  font-size: 14px;
}
</style>
