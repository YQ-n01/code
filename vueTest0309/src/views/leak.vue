<template>
  <div class="leak-predict" style="padding: 30px; max-width: 600px; margin: auto;">
    <h2>🔍 Leak Prediction Tool</h2>

    <el-form label-width="120px">
      <el-form-item label="音频文件">
        <input type="file" @change="handleFileChange" accept=".wav" />
      </el-form-item>

      <el-form-item label="采样率">
        <el-input v-model="fs" type="number" placeholder="采样率" />
      </el-form-item>

      <el-form-item label="输出路径">
        <el-input v-model="outputFolder" placeholder="D:/leak_output/" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="submit">🚀 开始计算</el-button>
      </el-form-item>
    </el-form>

    <div v-if="result" style="margin-top: 30px;">
      <h3>✅ 计算结果：</h3>
      <p><strong>Label:</strong> {{ result.label }}</p>
      <p><strong>Score:</strong> {{ result.score.toFixed(4) }}</p>

      <!-- ✅ 新增语谱图显示 -->
      <div v-if="result.imageUrl">
        <h4>📊 语谱图：</h4>
        <img :src="result.imageUrl" alt="语谱图" style="max-width: 100%; border: 1px solid #ccc;" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const file = ref(null)
const fs = ref(5000)
const outputFolder = ref('D:/leak_output/')
const result = ref(null)

const handleFileChange = (e) => {
  file.value = e.target.files[0]
}

const submit = async () => {
  if (!file.value) {
    ElMessage.error('请先选择音频文件')
    return
  }

  const formData = new FormData()
  formData.append('file', file.value)
  formData.append('fs', fs.value)
  formData.append('outputFolder', outputFolder.value)

  try {
    const res = await axios.post('http://localhost:9999/api/leak/predict', formData)
    result.value = res.data
    ElMessage.success('✅ 计算完成')
  } catch (err) {
    console.error(err)
    ElMessage.error('❌ 计算出错')
  }
}
</script>
