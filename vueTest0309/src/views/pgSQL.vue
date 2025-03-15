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
    <div class="card" style="margin-right: 20px;margin-left: 20px;margin-top: 10px;">
      <el-table :data="data.tableData" style="width: 100%" :header-cell-style="{ color: '#333' , background: '#eaf4ff' }">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="username" label="账号" />
        <el-table-column prop="name" label="用户名" />
        <el-table-column prop="phone" label="电话" />
      </el-table>
    </div>
    <div class="card" style="margin-right: 20px;margin-left: 20px;margin-top: 5px;">
      <el-pagination
        v-model:current-page="data.pageNum"
        :page-size="data.pageSize"
        layout="total, prev, pager, next"
        :total="data.total"
        @current-change="load"
      />
    </div>
  </div>
</template>

<script setup>
import { reactive} from "vue";
import {Search} from "@element-plus/icons-vue";
import request from "@/utils/request.js";

const data = reactive({
  name: null,
  pageNum:1,
  pageSize:5,
  total:0,
  tableData:[]
})

const load = () => {
  request.get("/admin/selectPage",{
    params:{
      pageNum:data.pageNum,
      pageSize:data.pageSize,
    }
  }).then(res => {
    console.log("API 返回的数据：", res.data); // 打印数据结构

    // 直接赋值，因为 res.data 就是一个数组
    data.tableData = res.data.list;
    data.total = res.data.total; // 计算数据总条数
  }).catch(error => {
    console.error("API 请求失败：", error);
  });
}

load();

</script>