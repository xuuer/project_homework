<template>
  <div>
    <!-- 查询表单 -->
    <el-form :model="homeworkForm" label-width="auto" ref="homeworkFormRef" inline>
      <el-form-item label="作业名" prop="title">
        <el-input v-model="homeworkForm.title" style="max-width:250px;" placeholder="请输入作业名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadHomeworkList" :icon="Search">查询</el-button>
        <el-button type="primary" @click="addHomeworkHandler" :icon="Plus" plain>创建作业</el-button>
      </el-form-item>
    </el-form>

    <!-- 作业列表 -->
    <div>
      <el-empty v-if="!homeworkList.length" description="暂无数据" />
      <el-card shadow="hover" class="list-item" v-for="homework in homeworkList" @click="goToHomework(homework)" :key="homework.id">
        <div class="item-container">
          <div class="icon">
            <el-image src="/images/icon-exam.png" fit="cover" />
          </div>
          <div>
            <div class="name">{{ homework.title }}</div>
            <div class="desc">截止时间：{{ homework.deadline }}</div>
          </div>
          <div class="tag">
            <el-tag size="large" type="primary" v-if="new Date() < new Date(homework.deadline)">进行中</el-tag>
            <el-tag size="large" type="info" v-else>已截止</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <el-pagination
      v-model:current-page="homeworkPageNum"
      background layout="total, prev, pager, next, jumper"
      :total="homeworkTotal"
      :page-size="pageSize"
      @current-change="handleHomeworkPageChange"
    />

    <!-- 创建作业的弹出框 -->
    <el-dialog v-model="homeworkDialogVisible" title="创建作业" :before-close="handleBeforeCloseHomework" style="min-width: 400px;">
      <addHomework ref="homeworkFormComponent" :courseId="$route.params.id" @success="editHomeworkSuccess" />
    </el-dialog>
  </div>
</template>

<script setup>
import addHomework from '../Exam/addHomework.vue'
import { getCourseHomeworkList } from '@/api'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()

// 作业相关
const homeworkList = ref([])
const homeworkPageNum = ref(1)
const homeworkTotal = ref(0)
const homeworkForm = ref({
  title: ''
})
const homeworkFormRef = ref()
const homeworkDialogVisible = ref(false)
const homeworkFormComponent = ref()

// 分页相关
const pageSize = ref(10)

onMounted(() => {
  loadHomeworkList()
})

// 获取作业列表
const loadHomeworkList = async () => {
  const params = {
    id: route.params.id,
    pageNum: homeworkPageNum.value,
    pageSize: pageSize.value,
    title: homeworkForm.value.title
  }
  const data = await getCourseHomeworkList(params)
  homeworkList.value = data.list
  homeworkTotal.value = data.total
}

// 作业换页
const handleHomeworkPageChange = value => {
  homeworkPageNum.value = value
  loadHomeworkList()
}

// 添加作业
const addHomeworkHandler = () => {
  if (homeworkFormComponent.value) {
    homeworkFormComponent.value.resetForm()
  }
  homeworkDialogVisible.value = true
}

// 作业编辑完成
const editHomeworkSuccess = () => {
  loadHomeworkList()
  homeworkDialogVisible.value = false
}

// 关闭作业弹出框前
const handleBeforeCloseHomework = () => {
  ElMessageBox.confirm('确定关闭对话框吗？', {
    showClose: false,
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    homeworkDialogVisible.value = false
    setTimeout(() => {
      homeworkFormComponent.value.resetForm()
    }, 500)
  }).catch(() => {})
}

// 进入作业详情界面
const goToHomework = row => {
  router.push({ name: 'homework_detail', params: { id: row.id } })
}
</script>

<style lang="scss" scoped>
.list-item:hover {
  background-color: var(--el-menu-hover-bg-color);
}
.list-item {
  cursor: pointer;
  margin-bottom: 20px;
  .item-container {
    display: flex;
    flex-direction: row;
    .icon {
      width: 70px;
      .el-image {
        width: 60px;
        height: 60px;
      }
    }
    .name {
      margin-bottom: 10px;
    }
    .desc {
      color: #737373;
      font-size: 13px;
    }
    .tag {
      width: 60px;
      line-height: 64px;
      margin-left: auto;
    }
  }
}
</style>