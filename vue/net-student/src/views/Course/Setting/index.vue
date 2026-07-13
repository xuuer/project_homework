<template>
  <div>
    <h1>基本信息</h1>
    <el-row>
      <el-col :span="12" :xs="24">
        <el-image class="cover" :src="config.resourceURL + course.cover" fit="cover" />
      </el-col>
      <el-col :span="12" :xs="24">
        <el-descriptions :column="1">
          <el-descriptions-item label="课程">{{ course.name }}</el-descriptions-item>
          <el-descriptions-item label="学期">{{ course.semester }}</el-descriptions-item>
          <el-descriptions-item label="班级">{{ course.clazz }}</el-descriptions-item>
          <el-descriptions-item label="教师">{{ course.teacherName }}</el-descriptions-item>
        </el-descriptions>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getCourse } from '@/api'
import config from '@/config'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 班课信息
const course = ref({})

onMounted(() => {
  loadCourse()
})

// 获取班课
const loadCourse = async () => {
  const data = await getCourse({ id: route.params.id })
  course.value = data
}
</script>

<style lang="scss" scoped>
.cover {
  width: 132px;
  height: 132px;
  border-radius: 4px;
  margin: 3px;
}
</style>