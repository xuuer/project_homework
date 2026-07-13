<template>
  <div class="menu">
    <div class="menu-content-box">
      <div class="menu-content">
        <el-menu
          mode="horizontal"
          :default-active="$route.path"
          router
        >
          <el-menu-item :index="'/course/'+$route.params.id+'/resource'">
            <el-icon><Folder /></el-icon>
            <span>资源</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/exam'">
            <el-icon><Timer /></el-icon>
            <span>测验/作业</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/student'">
            <el-icon><User /></el-icon>
            <span>成员</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/analysis'">
            <el-icon><DataAnalysis /></el-icon>
            <span>学情</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/assistant'">
            <el-icon><Star /></el-icon>
            <span>AI助教</span>
          </el-menu-item>
        </el-menu>
      </div>
    </div>
  </div>
</template>
  
<script setup>
import { User, Folder, Timer, DataAnalysis, Star } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourse } from '@/api'
 
const route = useRoute()
const course = ref({})

onMounted(() => {
  loadCourse()
})

const loadCourse = async () => {
  const data = await getCourse({ id: route.params.id })
  course.value = data
}
</script>
  
<style scoped lang="scss">
.menu {
  .menu-content-box {
    .menu-content {
      margin: 0px auto;
      .el-menu {
        justify-content: center;
      }
    }
  }
}
</style>