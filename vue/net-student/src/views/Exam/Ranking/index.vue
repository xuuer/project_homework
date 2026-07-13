<template>
  <el-card shadow="never">
    <!-- 学生列表 -->
    <el-table :data="studentList" style="width: 100%; margin-bottom: 20px">
      <el-table-column prop="ranking" label="排名" />
      <el-table-column prop="no" label="学号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="score" label="成绩" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      background layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
      style="margin-bottom: 50px;"
    />
  </el-card>
</template>

<script setup>
import { getExamRanking } from '@/api'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 学生列表
const studentList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)

onMounted(() => {
  loadStudentList()
})

// 获取学生
const loadStudentList = async () => {
  const params = {
    id: route.params.id,
    pageNum: pageNum.value,
    pageSize: pageSize.value
  }
  const data = await getExamRanking(params)
  studentList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadStudentList()
}
</script>
  
<style lang="scss" scoped>
</style>