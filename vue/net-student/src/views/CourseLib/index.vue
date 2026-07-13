<template>
  <el-card class="box-wrap">
    <!-- 查询表单 -->
    <div class="header">
      <el-form :model="form" label-width="auto" ref="formRef" inline>
        <el-form-item label="班课名" prop="name">
          <el-input v-model="form.name" placeholder="请输入班课名"/>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCourseList" :icon="Search">查询</el-button>
        </el-form-item>
      </el-form>
    </div>
    <!-- 班课列表 -->
    <el-empty v-if="!courseList.length" description="暂无数据" />
    <div class="list">
      <el-card shadow="hover" class="list-item"
        :body-style="{ padding: '0px' }"
        v-for="course in courseList"
        @click="goToCourse(course.id)"
      >
        <el-image class="cover" fit="cover" :src="config.resourceURL + course.cover" />
        <div class="text">
          <div>{{ course.name }}</div>
          <div class="desc">{{ course.semester }}</div>
        </div>
      </el-card>
    </div>
    <div>
      <el-pagination
        v-model:current-page="pageNum"
        background layout="prev, pager, next"
        :total="total"
        :page-size="pageSize"
        @current-change="handleCurrentChange"
      />
    </div>
  </el-card>
</template>

<script setup>
import { getCourseList } from '@/api'
import config from '@/config'
import { Search } from '@element-plus/icons-vue'
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
  name: ''
})
// 查询表单引用
const formRef = ref()

onMounted(() => {
  loadCourseList()
})

// 获取班课列表
const loadCourseList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: form.value.name
  }
  const data = await getCourseList(params)
  courseList.value = data.list
  total.value = data.total
}

// 跳转到班课详情界面
const goToCourse = (id) => {
  router.push({ name: 'course_resource', params: { id: id } })
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadCourseList()
}
</script>

<style lang="scss" scoped>
.box-wrap {
  margin: 20px auto;
  max-width: 1200px;
  .header {
    .el-input {
      max-width: 250px;
    }
  }
  .list {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: space-around;
    .list-item {
      margin: 10px;
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
}
</style>