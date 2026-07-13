<template>
  <div>
    <!-- 查询表单 -->
    <el-form :model="examForm" label-width="auto" ref="examFormRef" inline>
      <el-form-item label="测验名" prop="name">
        <el-input v-model="examForm.name" style="max-width:250px;" placeholder="请输入测验名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadExamList" :icon="Search">查询</el-button>
        <el-button type="primary" @click="addExamHandler" :icon="Plus" plain>创建测验</el-button>
      </el-form-item>
    </el-form>

    <!-- 测验列表 -->
    <div>
      <el-empty v-if="!examList.length" description="暂无数据" />
      <el-card shadow="hover" class="list-item" v-for="exam in examList" @click="goToExam(exam)" :key="exam.id">
        <div class="item-container">
          <div class="icon">
            <el-image src="/images/icon-exam.png" fit="cover" />
          </div>
          <div>
            <div class="name">{{ exam.name }}</div>
            <div class="desc">{{ exam.openTime }} - {{ exam.closeTime }}</div>
          </div>
          <div class="tag">
            <el-tag size="large" type="warning" v-if="new Date() < new Date(exam.openTime)">未开放</el-tag>
            <el-tag size="large" type="primary" v-else-if="new Date() < new Date(exam.closeTime)">已开放</el-tag>
            <el-tag size="large" type="info" v-else>已关闭</el-tag>
          </div>
        </div>
      </el-card>
    </div>

    <el-pagination
        v-model:current-page="examPageNum"
        background layout="total, prev, pager, next, jumper"
        :total="examTotal"
        :page-size="pageSize"
        @current-change="handleExamPageChange"
    />

    <!-- 创建测验的弹出框 -->
    <el-dialog v-model="examDialogVisible" title="创建测验" :before-close="handleBeforeCloseExam" style="min-width: 400px;">
      <addExam ref="examForm" :courseId="$route.params.id" @success="editExamSuccess" />
    </el-dialog>
  </div>
</template>

<script setup>
import addExam from './addExam.vue'
import { getCourseExamList } from '@/api'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()

// 测验相关
const examList = ref([])
const examPageNum = ref(1)
const examTotal = ref(0)
const examForm = ref({
  name: ''
})
const examFormRef = ref()
const examDialogVisible = ref(false)
const examFormComponent = ref()

// 分页相关
const pageSize = ref(10)

onMounted(() => {
  loadExamList()
})

// 获取测验列表
const loadExamList = async () => {
  const params = {
    id: route.params.id,
    pageNum: examPageNum.value,
    pageSize: pageSize.value,
    name: examForm.value.name
  }
  const data = await getCourseExamList(params)
  examList.value = data.list
  examTotal.value = data.total
}

// 测验换页
const handleExamPageChange = value => {
  examPageNum.value = value
  loadExamList()
}

// 添加测验
const addExamHandler = () => {
  if (examFormComponent.value) {
    examFormComponent.value.resetForm()
  }
  examDialogVisible.value = true
}

// 测验编辑完成
const editExamSuccess = () => {
  loadExamList()
  examDialogVisible.value = false
}

// 关闭测验弹出框前
const handleBeforeCloseExam = () => {
  ElMessageBox.confirm('确定关闭对话框吗？', {
    showClose: false,
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    examDialogVisible.value = false
    setTimeout(() => {
      examFormComponent.value.resetForm()
    }, 500)
  }).catch(() => {})
}

// 进入测验详情界面
const goToExam = row => {
  router.push({ name: 'exam_preview', params: { id: row.id } })
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