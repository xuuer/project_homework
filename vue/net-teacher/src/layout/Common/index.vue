<template>
  <el-container class="container">
    <el-aside>
      <Aside></Aside>
    </el-aside>
    <el-container>
      <el-header>
        <Header></Header>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import Aside from './aside.vue'
import Header from './header.vue'
</script>

<style lang="scss" scoped>
.container {
  height: 100vh;
  .el-aside {
    width: auto;
    height: 100vh;
  }
  .el-header {
    height: 50px;
    padding: 0;
  }
  .el-main {
    background-color: var(--el-bg-color);
    color: #333;
    overflow: auto;
    padding: 0px !important;
  }
}
</style>
