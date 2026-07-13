<template>
  <div class="name">{{ exam.name }}</div>
  <el-menu
    mode="horizontal"
    :default-active="$route.path"
    router
  >
    <el-menu-item :index="'/exam/'+$route.params.id+'/preview'">
      <el-icon><View /></el-icon>
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
    <el-menu-item :index="'/exam/'+$route.params.id+'/review'">
      <el-icon><DocumentChecked /></el-icon>
      <span>批阅</span>
    </el-menu-item>
    <el-menu-item :index="'/exam/'+$route.params.id+'/paper'">
      <el-icon><Tickets /></el-icon>
      <span>试卷</span>
    </el-menu-item>
    <el-menu-item :index="'/exam/'+$route.params.id+'/setting'">
      <el-icon><Setting /></el-icon>
      <span>设置</span>
    </el-menu-item>
  </el-menu>
</template>
  
<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { View, Tickets, Medal, PieChart, Setting, DocumentChecked } from '@element-plus/icons-vue'
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
.name {
  height: 60px;
  text-align: center;
  line-height: 60px;
  font-weight: 700;
  font-size: 25px;
}
.el-menu {
  justify-content: center;
}
</style>