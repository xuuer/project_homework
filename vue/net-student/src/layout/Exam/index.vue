<template>
  <div class="common-layout">
    <el-container>
      <el-main>
        <div>
          <router-view></router-view>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import Header from './header.vue'
</script>

<style lang="scss" scoped>
.common-layout {
  .el-container {
    margin: 0 auto;
    .el-header {
      padding: 0px !important;
    }
  }
}
</style>