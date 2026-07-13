<template>
  <el-card shadow="never">
    <!-- 查询表单 -->
    <el-form :model="form" label-width="auto" ref="formRef" inline>
      <el-form-item label="测验名" prop="name">
        <el-input v-model="form.name" style="max-width:250px;" placeholder="请输入测验名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadResourceList" :icon="Search">搜索</el-button>
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
      v-model:current-page="pageNum"
      background layout="total, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
    />
  </el-card>
</template>

<script setup>
import { getExamList } from '@/api'
import { Search } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 测验列表
const examList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单
const form = ref({
  name: ''
})
const formRef = ref()

onMounted(() => {
  loadExamList()
})

// 获取测验列表
const loadExamList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: form.value.name
  }
  const data = await getExamList(params)
  examList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadExamList()
}

// 跳转到测验详情界面
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