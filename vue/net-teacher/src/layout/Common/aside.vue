<template>
  <el-menu
      class="common-menu"
      :default-active="$route.path"
      :collapse="setting.collapseSideBar"
      router
  >
    <el-menu-item index="/index" class="navbar">
      <span>计算机网络课程学习平台</span>
    </el-menu-item>
    <el-menu-item index="/courselib">
      <el-icon><Management /></el-icon>
      <template #title>班课管理</template>
    </el-menu-item>
    <el-menu-item index="/resourcelib">
      <el-icon><Box /></el-icon>
      <template #title>资源管理</template>
    </el-menu-item>
    <el-menu-item index="/examlib">
      <el-icon><Platform /></el-icon>
      <template #title>测验管理</template>
    </el-menu-item>
    <!-- 新增作业管理菜单项 -->
    <el-menu-item index="/homeworklib">
      <el-icon><DocumentChecked /></el-icon>
      <template #title>作业管理</template>
    </el-menu-item>
    <el-menu-item index="/questionlib">
      <el-icon><List /></el-icon>
      <template #title>题目管理</template>
    </el-menu-item>
    <el-menu-item index="/taglib">
      <el-icon><Share /></el-icon>
      <template #title>知识图谱</template>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { Management, Box, Platform, List, Share, DocumentChecked } from '@element-plus/icons-vue'
import useSetting from '@/stores/setting'

const { setting } = useSetting()
</script>

<style scoped lang="scss">
.navbar {
  font-size: 16px;
  font-weight: bold;
  color: #FFF;
}
.common-menu {
  --el-menu-bg-color: #001628;
  --el-menu-hover-bg-color: #002030;
  --el-menu-text-color: #FFFFFFB3;
  border-right: 0;
  height: 100vh;
}
.icon {
  width: 25px;
  height: 25px;
}
</style>