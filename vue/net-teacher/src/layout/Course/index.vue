<template>
  <el-container>
    <el-header>
      <Header></Header>
    </el-header>
    <el-main>
      <router-view></router-view>
    </el-main>
  </el-container>
</template>
  
<script setup>
import Header from './header.vue'
</script>
  
<style lang="scss" scoped>
.el-container {
  .el-header {
    height: 180px;
    color: #333;
    padding: 0px !important;
  }
  .el-main {
    color: #333;
    background-color: var(--el-bg-color);
    .el-card {
      margin: 0px auto;
    }
  }
}
</style>