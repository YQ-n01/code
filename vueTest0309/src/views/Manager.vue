<template>
  <div>
    <!-- 头部区域 -->
    <div style="height:100px; display:flex; margin-left: 5px;">
      <!-- 左侧区域，包含 logo 和标题 -->
      <div style="width:445px; display:flex; align-items: center; padding-left: 10px; background-color:#132A56;">
        <!-- Logo 图片，设置圆角 -->
        <img style="width: 80px; height: 80px; border-radius: 10%" src="@/assets/imgs/logo.png" alt=""/>
        <!-- 标题文字，使用大字号，字体加粗，并设置颜色为 mintcream -->
        <span style="font-size: 36px; font-weight: bold; color: #ffffff; margin-left: 20px">蒸汽管路泄漏监测</span>
      </div>
      <!-- 右侧区域，使用 flex 进行占位 -->
      <div style="flex: 1; display: flex; align-items: center; padding-left: 20px;border-bottom: 2px solid #ddd">
        <span style="font-size: 23px; font-weight: bold; color: #010101; margin-left: 15px">首页 / 登录</span>
      </div>
      <div style="width:fit-content;padding-right:20px;display:flex;align-items:center;border-bottom:2px solid #ddd">
        <el-dropdown>
          <div style="display: flex;align-items:center">
            <img style="width:40px;height:40px;border-radius:50%" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
            <span style="margin-left:5px">管理员</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item>个人信息</el-dropdown-item>
              <el-dropdown-item>修改密码</el-dropdown-item>
              <el-dropdown-item>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <!-- 下方区域，包含菜单和数据展示 -->
    <div style="display:flex">
      <!-- 菜单部分 -->
      <div style="width: 450px; height: 200px; box-shadow: 0 0 8px rgba(0, 0, 0, 0.12);">
        <!-- el-menu 组件，控制菜单的展开、选中状态 -->
        <el-menu :router="true" :default-openeds="['1']" :default-active="router.currentRoute.value.path" style="min-height: calc(100vh - 80px);">
          <!-- 一级菜单项：首页 -->
          <el-menu-item index="/manager/home" style="font-size: 26px; align-items: center; justify-content: center;">
            <el-icon size="25"><House /></el-icon>
            <span>首页</span>
          </el-menu-item>

          <!-- 二级菜单：信号采集 -->
          <el-sub-menu index="1">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>信号采集</span>
            </template>
            <el-menu-item index="/manager/admin">二级菜单</el-menu-item>
          </el-sub-menu>

          <!-- 二级菜单：数据库 -->
          <el-sub-menu index="2">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>数据库</span>
            </template>
            <el-menu-item index="/manager/pgSQL">数据查询</el-menu-item>
          </el-sub-menu>

          <!-- 二级菜单：神经网络训练 -->
          <el-sub-menu index="3">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>神经网络训练</span>
            </template>
            <el-menu-item index="/manager/neural-network">二级菜单</el-menu-item>
          </el-sub-menu>

          <!-- 二级菜单：泄漏判断 -->
          <el-sub-menu index="4">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>泄漏判断</span>
            </template>
            <el-menu-item index="/manager/leak-detection">二级菜单</el-menu-item>
          </el-sub-menu>

          <!-- 二级菜单：泄漏定位 -->
          <el-sub-menu index="5">
            <template #title>
              <el-icon><Location /></el-icon>
              <span>泄漏定位</span>
            </template>
            <el-menu-item index="/manager/leak-location">二级菜单</el-menu-item>
          </el-sub-menu>
        </el-menu>
      </div>

      <!-- 数据展示区域（目前为空） -->
      <div style="flex:1; width: 0; background-color: #edf3ff;min-height: calc(100vh - 80px);">
        <RouterView />
      </div>
    </div>
  </div>
</template>

<script setup>
// 导入 Vue Router 的功能
import { useRoute } from 'vue-router';
import { ElDropdown, ElDropdownMenu, ElDropdownItem, ElMenu, ElMenuItem, ElSubMenu, ElIcon } from 'element-plus';
import router from "@/router/index.js";
import {House, Location} from "@element-plus/icons-vue";

const route = useRoute(); // 获取当前路由
</script>

<style>
/* 设置菜单背景颜色和去除边框 */
.el-menu {
  background-color: #132A56; /* 设置菜单的背景颜色为深蓝色 */
  border: none; /* 去掉菜单的边框 */
  margin-left: 5px; /* 为菜单左侧添加外边距 */
}

/* 设置子菜单标题的文字颜色 */
.el-sub-menu__title {
  color: #fff; /* 设置子菜单标题文字为白色 */
}

/* 设置菜单项的文字颜色 */
.el-menu-item {
  color: #fff; /* 设置菜单项文字为白色 */
}

/* 设置菜单项在未选中状态时，鼠标悬停时的背景色和文字颜色 */
.el-menu-item:not(.is-active):hover {
  background-color: #8fa0cc; /* 鼠标悬停时背景色为浅蓝色 */
  color: #333; /* 鼠标悬停时文字颜色为深灰色 */
}

/* 设置选中状态的菜单项背景色和文字颜色 */
.el-menu .is-active {
  background-color: #0B2B64; /* 选中状态时背景色为亮蓝色 */
  color: #d1dff3; /* 选中状态时文字颜色为白色 */
}

/* 设置子菜单标题在鼠标悬停时的背景色 */
.el-sub-menu__title:hover {
  background-color: #132A56; /* 鼠标悬停时背景色为深蓝色 */
}
.el-menu .el-sub-menu.is-active > .el-sub-menu__title {
  background-color: #132A56; /* 设置菜单的背景颜色为深蓝色 */
  color: #fff; /* 选中状态时文字颜色为白色 */
}
.el-dropdown {
  cursor: pointer;
}
</style>
