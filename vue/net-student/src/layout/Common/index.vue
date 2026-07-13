<template>
  <div class="common-layout">
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import Header from './header.vue'
</script>

<style lang="scss" scoped>
.el-container {
  height: 100vh;
  .el-header {
    text-align: center;
    line-height: 30px;
    color: #333;
    display: flex;
    flex-direction: row;
    background-color: #083364;
  }
  .el-main {
    background-color: var(--el-bg-color);
    color: #333;
    overflow: auto;
    padding: 0px !important;
  }
}
</style>
