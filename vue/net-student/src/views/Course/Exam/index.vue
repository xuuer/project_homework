<template>
  <div>
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <!-- 测验标签页 -->
      <el-tab-pane label="测验" name="exam">
        <!-- 查询表单 -->
        <el-form :model="examForm" label-width="auto" ref="examFormRef" inline>
          <el-form-item label="名称" prop="name">
            <el-input v-model="examForm.name" style="max-width:250px;" placeholder="请输入名称"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadExamList" :icon="Search">查询</el-button>
          </el-form-item>
        </el-form>
        <!-- 测验列表 -->
        <div>
          <el-empty v-if="!examList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="exam in examList" @click="goToExam(exam)">
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
            background layout="prev, pager, next"
            :total="examTotal"
            :page-size="examPageSize"
            @current-change="handleExamCurrentChange"
        />
      </el-tab-pane>

      <!-- 作业标签页 -->
      <el-tab-pane label="作业" name="homework">
        <!-- 查询表单 -->
        <el-form :model="homeworkForm" label-width="auto" ref="homeworkFormRef" inline>
          <el-form-item label="标题" prop="title">
            <el-input v-model="homeworkForm.title" style="max-width:250px;" placeholder="请输入作业标题"/>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadHomeworkList" :icon="Search">查询</el-button>
          </el-form-item>
        </el-form>
        <!-- 作业列表 -->
        <div>
          <el-empty v-if="!homeworkList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="homework in homeworkList" @click="goToHomework(homework)">
            <div class="item-container">
              <div class="icon">
                <el-image src="/images/icon-homework.png" fit="cover" />
              </div>
              <div>
                <div class="name">{{ homework.title }}</div>
                <div class="desc">截止时间：{{ homework.deadline }}</div>
              </div>
              <div class="tag">
                <el-tag size="large" type="warning" v-if="new Date() > new Date(homework.deadline)">已截止</el-tag>
                <el-tag size="large" type="primary" v-else>进行中</el-tag>
              </div>
            </div>
          </el-card>
        </div>
        <el-pagination
            v-model:current-page="homeworkPageNum"
            background layout="prev, pager, next"
            :total="homeworkTotal"
            :page-size="homeworkPageSize"
            @current-change="handleHomeworkCurrentChange"
        />
      </el-tab-pane>
    </el-tabs>

    <!-- 作业详情弹窗 -->
    <el-dialog v-model="homeworkDialogVisible" :title="currentHomework.title" width="80%" :before-close="handleCloseDialog">
      <HomeworkDetail
          v-if="homeworkDialogVisible"
          :homework-id="currentHomework.id"
          @close="homeworkDialogVisible = false"
          @refresh="loadHomeworkList"
      />
    </el-dialog>
  </div>
  <div>
<!--    <iframe :src="officeViewerUrl" width="100%" height="600px" frameborder="0"></iframe>-->

<!--    <iframe src="http://8.148.218.255:9000/kkfileview/2d92969a-aa0e-4f6c-8cf1-8e80b5a4d8c6_要求文档.docx" width="100%" height="600px"></iframe>-->
  </div>
</template>

<script setup>
import { getCourseExamList, getCourseHomeworkList } from '@/api'
import { Search } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import HomeworkDetail from './components/HomeworkDetail.vue'
import { computed } from 'vue'
// const fileUrl = 'http://8.148.218.255:9000/kkfileview/2d92969a-aa0e-4f6c-8cf1-8e80b5a4d8c6_要求文档.docx' // 公开或签名URL
// const fileUrl = 'http://8.148.218.255:9000/kkfileview/%E9%A2%98%E7%9B%AE.pdf' // 公开或签名URL
// const officeViewerUrl = computed(() =>
//         `https://view.officeapps.live.com/op/view.aspx?src=${encodeURIComponent(fileUrl)}`
// )
// 路由相关
const route = useRoute()
const router = useRouter()

// 标签页相关
const activeTab = ref('exam')

// 测验相关数据
const examList = ref([])
const examPageNum = ref(1)
const examPageSize = ref(10)
const examTotal = ref(0)
const examForm = ref({ name: '' })
const examFormRef = ref()

// 作业相关数据
const homeworkList = ref([])
const homeworkPageNum = ref(1)
const homeworkPageSize = ref(10)
const homeworkTotal = ref(0)
const homeworkForm = ref({ title: '' })
const homeworkFormRef = ref()

// 作业详情弹窗
const homeworkDialogVisible = ref(false)
const currentHomework = ref({})

onMounted(() => {
  loadExamList()
  loadHomeworkList()
})

// 标签页切换
const handleTabClick = (tab) => {
  if (tab.props.name === 'exam' && examList.value.length === 0) {
    loadExamList()
  } else if (tab.props.name === 'homework' && homeworkList.value.length === 0) {
    loadHomeworkList()
  }
}

// 获取测验列表
const loadExamList = async () => {
  const params = {
    id: route.params.id,
    pageNum: examPageNum.value,
    pageSize: examPageSize.value,
    name: examForm.value.name
  }
  const data = await getCourseExamList(params)
  examList.value = data.list
  examTotal.value = data.total
}

// 获取作业列表
const loadHomeworkList = async () => {
  const params = {
    id: route.params.id,
    pageNum: homeworkPageNum.value,
    pageSize: homeworkPageSize.value,
    title: homeworkForm.value.title
  }
  const data = await getCourseHomeworkList(params)
  homeworkList.value = data.list
  homeworkTotal.value = data.total
}

// 测验换页
const handleExamCurrentChange = value => {
  examPageNum.value = value
  loadExamList()
}

// 作业换页
const handleHomeworkCurrentChange = value => {
  homeworkPageNum.value = value
  loadHomeworkList()
}

// 跳转到测验详情界面
const goToExam = row => {
  router.push({ name: 'exam_preview', params: { id: row.id } })
}

// 查看作业详情
const goToHomework = homework => {
  currentHomework.value = homework
  homeworkDialogVisible.value = true
}

// 关闭作业详情弹窗
const handleCloseDialog = () => {
  homeworkDialogVisible.value = false
  currentHomework.value = {}
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
      font-weight: 600;
    }
    .desc {
      color: #737373;
      font-size: 13px;
    }
    .tag {
      width: 80px;
      line-height: 64px;
      margin-left: auto;
    }
  }
}
</style>