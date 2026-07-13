<template>
  <el-card shadow="never">
    <!-- 查询表单 -->
    <el-form :model="form" label-width="auto" ref="formRef" inline>
      <el-form-item label="状态" prop="difficulty">
        <el-select v-model="form.status" placeholder="批阅状态" style="width:150px;">
          <el-option label="全部" value="" />
          <el-option label="待批阅" value="待批阅" />
          <el-option label="已批阅" value="已批阅" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadStudentResultList" :icon="Search">搜索</el-button>
        <el-button type="primary" @click="chatResult" :icon="Star" :disabled="multipleSelection.length === 0">AI批阅</el-button>
      </el-form-item>
    </el-form>
    <el-alert type="info" show-icon :closable="false">
      <template #title>
        <span v-if="multipleSelection.length === 0">未选中任何数据</span>
        <span v-else>已选中 {{ multipleSelection.length }} 条记录(可跨页)</span>
      </template>
    </el-alert>
    <!-- 学生列表 -->
    <el-table
      :data="studentList"
      style="width: 100%; margin-bottom: 20px"
      row-key="id"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="55" :reserve-selection="true" />
      <el-table-column prop="no" label="学号" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="score" label="成绩" />
      <el-table-column prop="startTime" label="开始时间" />
      <el-table-column prop="endTime" label="结束时间" />
      <el-table-column prop="status" label="批阅状态" />
      <el-table-column fixed="right" label="操作">
        <template #default="{ row }">
          <el-button type="warning" @click="goToResult(row)" plain>批阅</el-button>
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
  </el-card>
  <!-- AI批阅弹出框 -->
  <el-dialog v-model="dialogVisible" title="AI批阅试卷中" :before-close="handleBeforeClose">
    <el-progress
      :percentage="percentage"
      :stroke-width="15"
      striped
      striped-flow
    />
    <div class="footer">
      <el-button type="primary" v-if="percentage === 100" @click="complete()">完成</el-button>
    </div>
  </el-dialog>
</template>

<script setup>
import { getStudentResultList, markPaper } from '@/api'
import { Search, Star } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
// 当前页学生列表
const studentList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单相关
const form = ref({
  status: ''
})
const formRef = ref()
// 多选列表
const multipleSelection = ref([])
// 弹出框相关
const dialogVisible = ref(false)
const percentage = ref(0)

onMounted(() => {
  loadStudentResultList()
})

// 获取学生
const loadStudentResultList = async () => {
  const params = {
    id: route.params.id,
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    status: form.value.status
  }
  const data = await getStudentResultList(params)
  studentList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadStudentResultList()
}

// 进入批阅界面
const goToResult = row => {
  router.push({ name: 'exam_result', params: { id: route.params.id, studentId: row.id } })
}

// 多选
const handleSelectionChange = val => {
  multipleSelection.value = val
}

const chatResult = async () => {
  percentage.value = 0
  dialogVisible.value = true
  for(let i=0;i<multipleSelection.value.length;i++){
    if(await markPaper({ examId: route.params.id, studentId: multipleSelection.value[i].id })){
      percentage.value = (i+1)*100/multipleSelection.value.length
    }
  }
}

const complete = () => {
  dialogVisible.value = false
  multipleSelection.value = []
  percentage.value = 0
}

// 关闭弹出框前
const handleBeforeClose = () => {
  ElMessageBox.confirm('确定关闭对话框吗？', {
    showClose: false,
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    dialogVisible.value = false
    setTimeout(() => {
      multipleSelection.value = []
      percentage.value = 0
    }, 500)
  }).catch(() => {})
}

</script>
  
<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>