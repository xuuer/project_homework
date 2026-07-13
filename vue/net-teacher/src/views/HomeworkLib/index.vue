<template>
  <el-card shadow="never">
    <!-- 查询表单 -->
    <el-form :model="form" label-width="auto" ref="formRef" inline>
      <el-form-item label="作业名" prop="title">
        <el-input v-model="form.title" style="max-width:250px;" placeholder="请输入作业名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadHomeworkList" :icon="Search">搜索</el-button>
      </el-form-item>
    </el-form>
    <!-- 作业列表 -->
    <div>
      <el-empty v-if="!homeworkList.length" description="暂无数据" />
      <el-card shadow="hover" class="list-item" v-for="homework in homeworkList" @click="goToHomework(homework)">
        <div class="item-container">
          <div class="icon">
            <el-image src="/images/icon-exam.png" fit="cover" />
          </div>
          <div>
            <div class="name">{{ homework.title }}</div>
            <div class="desc">截止时间：{{ homework.deadline }}</div>
            <div class="desc">所属班课：{{ homework.courseName || '未知' }}</div>
          </div>
          <div class="tag">
            <el-tag size="large" type="primary" v-if="new Date() < new Date(homework.deadline)">进行中</el-tag>
            <el-tag size="large" type="info" v-else>已截止</el-tag>
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
import { getHomeworkList } from '@/api'
import { Search } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 作业列表
const homeworkList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单
const form = ref({
  title: ''
})
const formRef = ref()

onMounted(() => {
  loadHomeworkList()
})

// 获取作业列表
const loadHomeworkList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    title: form.value.title
  }
  const data = await getHomeworkList(params)
  homeworkList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadHomeworkList()
}

// 跳转到作业详情界面
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
      font-weight: bold;
    }
    .desc {
      color: #737373;
      font-size: 13px;
      margin-bottom: 5px;
    }
    .tag {
      width: 80px;
      line-height: 64px;
      margin-left: auto;
    }
  }
}
</style>