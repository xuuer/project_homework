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
        <el-button type="primary" @click="loadStudentList" :icon="Search">搜索</el-button>
        <el-button type="primary" @click="addRow" :icon="Plus" plain>添加学生</el-button>
        <el-button type="success" @click="download" :icon="Download" plain>导出学生</el-button>
      </el-form-item>
    </el-form>
    <!-- 学生列表 -->
    <el-table
      :data="studentList"
      style="width: 100%; margin-bottom: 20px"
    >
      <el-table-column prop="no" label="学号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column fixed="right" label="操作" width="160">
        <template #default="{ row }">
          <el-link type="primary" @click="goToInfo(row)" plain >学情</el-link>
          <el-link type="danger" @click="delRow(row)" plain >删除</el-link>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      background layout="total, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
    />
    <!-- 添加学生的弹出框 -->
    <el-dialog v-model="dialogVisible" title="添加学生" :before-close="handleBeforeClose" style="min-width: 400px;">
      <addStu ref="studentForm" :id="$route.params.id" @success="addSuccess" />
    </el-dialog>
  </div>
</template>

<script setup>
import addStu from './AddStu.vue'
import { delCourseStudent, exportCourseStudent, getCourseStudentList } from '@/api'
import { Download, Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
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
const formRef = ref()
// 弹出框相关
const dialogVisible = ref(false)
const studentForm = ref()

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

// 导出学生 
const download = async () => {
  await exportCourseStudent({ id: route.params.id })
}

// 添加学生
const addRow = () => {
  if (studentForm.value) {
    studentForm.value.resetForm(0)
  }
  dialogVisible.value = true
}

// 添加完成
const addSuccess = () => {
  loadStudentList()
  dialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeClose  = () => {
  ElMessageBox.confirm('确定关闭对话框吗？', {
    showClose: false,
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    dialogVisible.value = false
    setTimeout(() => {
      studentForm.value.resetForm()
    }, 500)
  }).catch(() => {})
}

// 删除学生
const delRow = row => {
  ElMessageBox.confirm('确定要删除此学生吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delCourseStudent({ id: route.params.id, studentId: row.id })) {
      loadStudentList()
    }
  }).catch(() => {})
}

// 查看学生学情
const goToInfo = row => {
  router.push({ name: 'course_student_analysis', params: { id: route.params.id, studentId: row.id } })
}
</script>

<style lang="scss" scoped>
.el-link {
  margin-right: 8px;
}
</style>