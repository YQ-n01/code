<template>
  <div>HHH</div>
</template>

<script setup>
import request from "@/utils/request.js";
import {ElMessage} from "element-plus";

request.get("/admin/selectAll").then(response => {
    console.log(response);
})
</script>
