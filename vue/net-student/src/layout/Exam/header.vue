<template>
  <el-menu
    mode="horizontal"
    :default-active="$route.path"
    router
  >
    <el-menu-item class="navbar">
      <span>{{ exam.name }}</span>
    </el-menu-item>
    <el-menu-item :index="'/exam/'+$route.params.id+'/preview'">
      <el-icon><Setting /></el-icon>
      <span>预览</span>
    </el-menu-item>
    <el-menu-item :index="'/exam/'+$route.params.id+'/ranking'">
      <el-icon><Medal /></el-icon>
      <span>排名</span>
    </el-menu-item>
    <el-menu-item :index="'/exam/'+$route.params.id+'/statistics'">
      <el-icon><PieChart /></el-icon>
      <span>统计</span>
    </el-menu-item>
  </el-menu>
</template>
  
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Medal, PieChart, Setting } from '@element-plus/icons-vue'
import { getExam } from '@/api'

// 路由
const route = useRoute()
// 测验信息
const exam = ref({})

onMounted(() => {
  loadExam()
})

// 获取测验信息
const loadExam = async () => {
  const data = await getExam({ id: route.params.id })
  exam.value = data
}
</script>
  
<style scoped lang="scss">
.card {
  height: 30px;
  background-color: #FFF;
  padding: 20px;
  .card-title {
    font-size: 22px;
    font-weight: 700;
  }
}
.navbar {
  font-size: 20px;
  font-weight: bold;
}
</style>