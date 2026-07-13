<template>
  <el-container class="common-wrap">
    <el-main>
      <el-card class="intro-card">
        <el-row>
          <el-col :span="12">
            <el-image class="cover" :src="config.resourceURL + course.cover" fit="cover" />
          </el-col>
          <el-col :span="12">
            <div class="name">{{ course.name }}</div>
            <el-card shadow="never">
              <el-descriptions :column="1">
                <el-descriptions-item label="学期">{{ course.semester }}</el-descriptions-item>
                <el-descriptions-item label="班级">{{ course.clazz }}</el-descriptions-item>
                <el-descriptions-item label="教师">{{ course.teacherName }}</el-descriptions-item>
              </el-descriptions>
            </el-card>
          </el-col>
        </el-row>
      </el-card>
      <el-card>
          <el-header>
            <Header></Header>
          </el-header>
          <el-main>
            <router-view></router-view>
          </el-main>
      </el-card>
    </el-main>
  </el-container>
</template>
  
<script setup>
import Header from './header.vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourse } from '@/api'
import config from '@/config'
 
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
  
<style lang="scss" scoped>
.common-wrap {
  height: calc(100vh - 60px);
}
.intro-card {
  min-height: 368px;
  margin-bottom: 20px !important;
  .cover {
    width: 510px;
    height: 288px;
  }
  .name {
    font-size: 24px;
    font-weight: 600;
    margin-bottom: 20px;
  }
}
</style>