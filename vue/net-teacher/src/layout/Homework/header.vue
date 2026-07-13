<template>
  <div class="name">{{ homework.title }}</div>
  <el-menu
    mode="horizontal"
    :default-active="$route.path"
    router
  >
    <el-menu-item :index="'/homework/'+$route.params.id+'/detail'">
      <el-icon><View /></el-icon>
      <span>详情</span>
    </el-menu-item>
    <el-menu-item :index="'/homework/'+$route.params.id+'/students'">
      <el-icon><User /></el-icon>
      <span>学生作业</span>
    </el-menu-item>
    <el-menu-item :index="'/homework/'+$route.params.id+'/setting'">
      <el-icon><Setting /></el-icon>
      <span>设置</span>
    </el-menu-item>
  </el-menu>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { View, User, Setting } from '@element-plus/icons-vue'
import { getHomework } from '@/api'

// 路由
const route = useRoute()
// 作业信息
const homework = ref({})

onMounted(() => {
  loadHomework()
})

// 获取作业信息
const loadHomework = async () => {
  const data = await getHomework({ id: route.params.id })
  homework.value = data
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