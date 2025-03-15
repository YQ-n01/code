<template>
  <div class="uploader-container">
    <el-card>
      <h3>GCC 音频文件上传与处理</h3>

      <el-form label-width="100px">
        <!-- 音频文件上传 -->
        <el-form-item label="文件 1">
          <input type="file" accept=".wav" @change="handleFile1" />
          <span v-if="file1Name" class="file-name">{{ file1Name }}</span>
        </el-form-item>

        <el-form-item label="文件 2">
          <input type="file" accept=".wav" @change="handleFile2" />
          <span v-if="file2Name" class="file-name">{{ file2Name }}</span>
        </el-form-item>

        <el-form-item label="文件 3">
          <input type="file" accept=".wav" @change="handleFile3" />
          <span v-if="file3Name" class="file-name">{{ file3Name }}</span>
        </el-form-item>

        <el-form-item label="文件 4">
          <input type="file" accept=".wav" @change="handleFile4" />
          <span v-if="file4Name" class="file-name">{{ file4Name }}</span>
        </el-form-item>

        <el-form-item label="采样率 (fs)">
          <el-input v-model="fs" placeholder="请输入采样率" />
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="success" :disabled="!file1 || !file2 || !file3 || !file4 || !fs" @click="upload">
            提交处理
          </el-button>
        </el-form-item>
      </el-form>

      <el-divider />

      <!-- **📢 结果文本框** -->
      <div v-if="result !== null" class="result-box">
        <h4>time-delay 计算结果：</h4>
        <el-input type="textarea" :rows="3" v-model="resultDisplay" readonly />
      </div>
      <div v-if="result !== null" class="result-box">
        <h4>GCC 返回值：</h4>
        <el-input type="textarea" :rows="5" v-model="gccDisplay" readonly />
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
const file3 = ref(null);
const file4 = ref(null);
const file1Name = ref("");
const file2Name = ref("");
const file3Name = ref("");
const file4Name = ref("");
const fs = ref("");
const result = ref(null);
const resultDisplay = ref(""); // 显示 time_delay 计算结果
const gccDisplay = ref(""); // 显示 GCC 返回值

// ✅ 监听 result 变化，更新返回值显示
watch(result, () => {
  if (result.value) {
    // ✅ 处理 `gccResult1`（一维数组）
    if (result.value.gccResult1) {
      resultDisplay.value = `time-delay 返回值：\n${result.value.gccResult1.join(", ")}`;
    }

    // ✅ 处理 `gccResult2`
    if (result.value.gccResult2) {
      // 🚨 检查 `gccResult2` 是否是二维数组
      if (Array.isArray(result.value.gccResult2[0])) {
        // ✅ `gccResult2` 是 **二维数组**，按行拼接
        gccDisplay.value = `GCC 返回值：\n${result.value.gccResult2
            .map(row => row.join(", "))  // ✅ 逐行拼接
            .join("\n")}`;
      } else {
        // ✅ `gccResult2` 是 **一维数组**，直接 `.join()`
        gccDisplay.value = `GCC 返回值：\n${result.value.gccResult2.join(", ")}`;
      }
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

const handleFile3 = (event) => {
  const file = event.target.files[0];
  if (!file || !file.type.includes("audio/wav")) {
    ElMessage.error("请选择有效的 WAV 文件");
    return;
  }
  file3.value = file;
  file3Name.value = file.name;
};

const handleFile4 = (event) => {
  const file = event.target.files[0];
  if (!file || !file.type.includes("audio/wav")) {
    ElMessage.error("请选择有效的 WAV 文件");
    return;
  }
  file4.value = file;
  file4Name.value = file.name;
};

// 提交文件到后端
const upload = async () => {
  if (!file1.value || !file2.value || !file3.value || !file4.value || !fs.value) {
    ElMessage.error("请确保选择了 4 个文件并填写了采样率");
    return;
  }

  const formData = new FormData();
  formData.append("file1", file1.value);
  formData.append("file2", file2.value);
  formData.append("file3", file3.value);
  formData.append("file4", file4.value);
  formData.append("fs", fs.value);

  try {
    const res = await axios.post("http://localhost:9999/api/gcccal/process", formData, {
      headers: { "Content-Type": "multipart/form-data" },
    });

    console.log("📢 API 返回的数据:", res.data); // ✅ 确保 API 返回了 GCC 计算结果
    result.value = res.data;
    ElMessage.success("音频处理成功");
  } catch (error) {
    console.error("❌ 上传失败:", error);
    ElMessage.error("音频处理失败，请检查服务器日志");
  }
};
</script>

<style scoped>
.uploader-container {
  max-width: 600px;
  margin: 20px auto;
}

.file-name {
  margin-left: 10px;
  font-size: 14px;
  color: #666;
}

.result-box {
  margin-top: 20px;
}
</style>
