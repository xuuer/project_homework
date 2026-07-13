<template>
  <div>
    <!-- 查询表单 -->
    <el-form :model="form" label-width="auto" ref="formRef" inline>
      <el-form-item label="学号" prop="no">
        <el-input v-model="form.no" style="max-width:250px;" placeholder="请输入学号"/>
      </el-form-item>
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" style="max-width:250px;" placeholder="请输入姓名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadStudentList" :icon="Search">查询</el-button>
      </el-form-item>
    </el-form>
    <!-- 学生列表 -->
    <el-table
      :data="studentList"
      style="width: 100%; margin-bottom: 20px"
    >
      <el-table-column prop="no" label="学号" />
      <el-table-column prop="name" label="姓名" />
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      background layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
    />
    <!-- 添加学生的弹出框 -->
    <el-dialog v-model="dialogVisible" title="添加学生" :before-close="handleBeforeClose" style="min-width: 400px;">
      <AddStudent ref="studentForm" :id="$route.params.id" @success="addSuccess" />
    </el-dialog>
  </div>
</template>

<script setup>
import { getCourseStudentList } from '@/api'
import { Search } from '@element-plus/icons-vue'
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
// 查询表单
const form = ref({
  no: '',
  name: ''
})
// 查询表单引用
const formRef = ref()

onMounted(() => {
  loadStudentList()
})

// 获取学生
const loadStudentList = async () => {
  const params = {
    id: route.params.id,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    no: form.value.no,
    name: form.value.name
  }
  const data = await getCourseStudentList(params)
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