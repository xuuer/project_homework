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
.el-header {
  padding: 0;
  height: 120px;
}
</style>