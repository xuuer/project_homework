<template>
  <el-card shadow="never">
    <!-- 查询表单 -->
    <div class="header">
      <el-form :model="form" label-width="auto" ref="formRef" inline>
        <el-form-item label="班课名" prop="name">
          <el-input v-model="form.name" placeholder="请输入班课名"/>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-input v-model="form.semester" placeholder="请输入学期"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCourseList" :icon="Search">搜索</el-button>
          <el-button type="primary" @click="addRow" :icon="Plus" plain>创建班课</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 班课列表 -->
    <el-empty v-if="!courseList.length" description="暂无数据" />
    <el-space wrap class="list">
      <el-card shadow="hover" class="list-item"
        :body-style="{ padding: '0px' }" 
        v-for="course in courseList"
        @click="goToCourse(course)"
      >
        <el-image class="cover" fit="cover" :src="config.resourceURL + course.cover" />
        <div class="text">
          <div>{{ course.name }}</div>
          <div class="desc">{{ course.semester }}</div>
        </div>
      </el-card>
    </el-space>
    <el-pagination
      v-model:current-page="pageNum"
      background layout="total, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
    />
  </el-card>
  <!-- 添加班课的弹出框 -->
  <el-dialog v-model="dialogVisible" title="创建班课" :before-close="handleBeforeClose" style="min-width: 400px;">
    <addCoz ref="courseForm" @success="addSuccess" />
  </el-dialog>
</template>

<script setup>
import addCoz from './addCoz.vue'
import { getCourseList } from '@/api'
import config from '@/config'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 班课列表
const courseList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单
const form = ref({
  name: '',
  semester: ''
})
const formRef = ref()

// 弹出框显示
const dialogVisible = ref(false)
// 弹出框中的表单
const courseForm = ref()

onMounted(() => {
  loadCourseList()
})

// 获取班课列表
const loadCourseList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: form.value.name,
    semester: form.value.semester
  }
  console.log('查询参数:', loadCourseList)
  const data = await getCourseList(params)
  courseList.value = data.list
  total.value = data.total
}

// 进入班课详情界面
const goToCourse = row => {
  router.push({ name: 'course_exam', params: { id: row.id } })
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadCourseList()
}

// 添加班课
const addRow = () => {
  if (courseForm.value) {
    courseForm.value.resetForm()
  }
  dialogVisible.value = true
}

// 添加完成
const addSuccess = () => {
  loadCourseList()
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
      courseForm.value.resetForm()
    }, 500)
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.header {
  .el-input {
    max-width: 250px;
  }
}
.list {
  .list-item {
    width: 220px;
    cursor: pointer;
    .cover {
      height: 220px;
      width: 220px;
    }
    .text {
      padding: 10px;
      .desc {
        color: #999;
        font-size: 14px;
      }
    }
  }
}
</style>