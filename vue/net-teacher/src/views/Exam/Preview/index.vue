<template>
  <div>
    <el-card shadow="never">
      <template #header>
        <div>
          <span>测验概述</span>
        </div>
      </template>
      <el-descriptions>
        <el-descriptions-item label="开放时间">
          <span class="card-font">{{ exam.openTime ? exam.openTime : "--" }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="关闭时间">
          <span class="card-font">{{ exam.closeTime ? exam.closeTime : "--" }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="测验时长">
          <span class="card-font">{{ exam.duration ? exam.duration : "--" }} 分钟</span>
        </el-descriptions-item>
        <el-descriptions-item label="测验总分">
          <span class="card-font">{{ exam.totalScore ? exam.totalScore : "--" }} 分</span>
        </el-descriptions-item>
        <el-descriptions-item label="题目总数">
          <span class="card-font">{{ exam.questionNum ? exam.questionNum : "--" }} 题</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
    <el-card shadow="never">
      <template #header>
        <div>
          <span>考生须知</span>
        </div>
      </template>
      <div v-html="exam.description"></div>
    </el-card>
  </div>
</template>

<script setup>
import { getExam } from '@/api'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
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

<style lang="scss" scoped>
.el-card {
  margin-bottom: 10px;
  .card-title {
    font-size: 22px;
    font-weight: 700;
  }
  .card-font {
    color: #1890ff;
    font-style: italic;
  }
}
.footer {
  text-align: center;
}
</style>