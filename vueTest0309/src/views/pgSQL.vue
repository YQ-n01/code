<template>
  <div>
    <div class="card">
    <el-input style="height: 50px; width: 660px; margin-left: 20px;margin-top: 10px; font-size: 20px;" v-model="data.name" placeholder="输入文件" :prefix-icon="Search"></el-input>
    <el-button style="height: 45px; width: 100px; margin-left: 20px;margin-top: 10px; font-size: 20px; font-weight: bold;" type="primary">查 询</el-button>
    </div>
    <div class="card">
      <el-button style="height: 45px; width: 100px; margin-left: 20px;margin-top: 10px; font-size: 17px;" type="success">新增数据</el-button>
      <el-button style="height: 45px; width: 100px; margin-left: 20px;margin-top: 10px; font-size: 17px;" type="danger">删除数据</el-button>
      <el-button style="height: 45px; width: 100px; margin-left: 20px;margin-top: 10px; font-size: 17px;" type="info">批量导入</el-button>
      <el-button style="height: 45px; width: 100px; margin-left: 20px;margin-top: 10px; font-size: 17px;" type="warning">批量导出</el-button>
    </div>
    <div class="card" style="width: 800px; margin-left: 20px;margin-top: 10px; margin-bottom: 5px;">
      <el-table :data="data.tableData" stripe style="width: 100%">
        <el-table-column prop="date" label="名称" width="180" />
        <el-table-column prop="name" label="类型" width="180" />
        <el-table-column prop="address" label="大小" width="180" />
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { reactive} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";

const data = reactive({
  name: null,
  tableData:[]
})

const load = () =>{
  request.get("/admin/selectAll").then(res => {
    console.log(res.data);
  })
}

load()
</script>