<template>
  <div class="uploader-container">
    <el-card>
      <h3>音频文件上传与处理</h3>

      <el-form label-width="100px">
        <el-form-item label="文件1">
          <input type="file" accept=".wav" @change="handleFile1" />
          <span v-if="file1Name" class="file-name">{{ file1Name }}</span>
        </el-form-item>

        <el-form-item label="文件2">
          <input type="file" accept=".wav" @change="handleFile2" />
          <span v-if="file2Name" class="file-name">{{ file2Name }}</span>
        </el-form-item>

        <el-form-item label="采样率 (fs)">
          <el-input v-model="fs" placeholder="请输入采样率" />
        </el-form-item>

        <el-form-item>
          <el-button type="success" :disabled="!file1 || !file2 || !fs" @click="upload">
            提交处理
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <!-- **📢 分别显示 MATLAB 计算的所有结果** -->
      <div v-if="result !== null" class="result-box">
        <h4>F 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="fDisplay" readonly />
      </div>

      <div v-if="result !== null" class="result-box">
        <h4>phase_spectrum_frame1 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="phaseSpectrum1Display" readonly />
      </div>

      <div v-if="result !== null" class="result-box">
        <h4>phase_spectrum_frame2 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="phaseSpectrum2Display" readonly />
      </div>

      <div v-if="result !== null" class="result-box">
        <h4>Phase_delta 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="phaseDifferenceDisplay" readonly />
      </div>

      <div v-if="result !== null" class="result-box">
        <h4>segments_start_end 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="filteredSegmentsDisplay" readonly />
      </div>
    </el-card>
  </div>
</template>
<script setup>
import { ref, watch } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";

const file1 = ref(null);
const file2 = ref(null);
const file1Name = ref("");
const file2Name = ref("");
const fs = ref("");
const result = ref(null);

// ✅ 新增变量，分别存储 MATLAB 计算结果
const fDisplay = ref("");
const phaseSpectrum1Display = ref("");
const phaseSpectrum2Display = ref("");
const phaseDifferenceDisplay = ref("");
const filteredSegmentsDisplay = ref("");

// ✅ 监听 result 变化，更新所有计算结果
watch(result, () => {
  if (result.value) {
    // ✅ F 计算结果
    if (result.value.f) {
      fDisplay.value = `F 返回值：\n${result.value.f.join(", ")}`;
    }
    // ✅ Phase Spectrum 1 计算结果
    if (result.value.phaseSpectrum1) {
      phaseSpectrum1Display.value = `phase_spectrum_frame1：\n${result.value.phaseSpectrum1.join(", ")}`;
    }
    // ✅ Phase Spectrum 2 计算结果
    if (result.value.phaseSpectrum2) {
      phaseSpectrum2Display.value = `phase_spectrum_frame2：\n${result.value.phaseSpectrum2.join(", ")}`;
    }
    // ✅ Phase Difference 计算结果
    if (result.value.phaseDifference) {
      phaseDifferenceDisplay.value = `Phase_delta：\n${result.value.phaseDifference.join(", ")}`;
    }
    // ✅ Filtered Segments 计算结果（二维数组）
    if (result.value.filteredSegments) {
      filteredSegmentsDisplay.value = `segments_start_end：\n${result.value.filteredSegments.map(row => row.join(", ")).join("\n")}`;
    }
  }
});

// 选择文件
const handleFile1 = (event) => {
  const file = event.target.files[0];
  if (!file || !file.type.includes("audio/wav")) {
    ElMessage.error("请选择有效的 WAV 文件");
    return;
  }
  file1.value = file;
  file1Name.value = file.name;
};

const handleFile2 = (event) => {
  const file = event.target.files[0];
  if (!file || !file.type.includes("audio/wav")) {
    ElMessage.error("请选择有效的 WAV 文件");
    return;
  }
  file2.value = file;
  file2Name.value = file.name;
};

// 提交文件到后端
const upload = async () => {
  if (!file1.value || !file2.value || !fs.value) {
    ElMessage.error("请确保选择了两个文件并填写了采样率");
    return;
  }

  const formData = new FormData();
  formData.append("file1", file1.value);
  formData.append("file2", file2.value);
  formData.append("fs", fs.value);

  try {
    const res = await axios.post("http://localhost:9999/api/audio/process", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    console.log("📢 API 返回的数据:", res.data); // ✅ 确保 API 返回了完整的 MATLAB 计算结果
    result.value = res.data;

    ElMessage.success("音频处理成功");
  } catch (error) {
    console.error("❌ 上传失败:", error);
    ElMessage.error("音频处理失败，请检查服务器日志");
  }
};
</script>