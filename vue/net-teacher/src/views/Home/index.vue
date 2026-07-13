<template>
  <div style="padding: 20px;">
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <router-link :to="{ name: 'courselib' }">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>班课</span>
                <el-icon><Management /></el-icon>
              </div>
            </template>
            <div class="num">{{ courseTotal }}</div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link :to="{ name: 'resourcelib' }">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>资源</span>
                <el-icon><Box /></el-icon>
              </div>
            </template>
            <div class="num">{{ resourceTotal }}</div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link :to="{ name: 'examlib' }">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>测验</span>
                <el-icon><Platform /></el-icon>
              </div>
            </template>
            <div class="num">{{ examTotal }}</div>
          </el-card>
        </router-link>
      </el-col>
      <el-col :span="6">
        <router-link :to="{ name: 'homeworklib' }">
          <el-card>
            <template #header>
              <div class="card-header">
                <span>作业</span>
                <el-icon><DocumentChecked /></el-icon>
              </div>
            </template>
            <div class="num">{{ homeworkTotal }}</div>
          </el-card>
        </router-link>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="8">
        <el-card style="min-height: 480px;">
          <template #header>
            <div class="card-header">
              <span>最近的班课</span>
              <router-link :to="{ name: 'courselib' }">
                <el-link>查看更多</el-link>
              </router-link>
            </div>
          </template>
          <el-empty v-if="!courseList || !courseList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="course in (courseList || [])" @click="goToCourse(course)" :key="course.id">
            <div class="item-container">
              <div class="icon">
                <el-image :src="config.resourceURL + course.cover" fit="cover" />
              </div>
              <div>
                <div class="name">{{ course.name }}</div>
                <div class="desc">{{ course.clazz }}</div>
              </div>
            </div>
          </el-card>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card style="min-height: 480px;">
          <template #header>
            <div class="card-header">
              <span>最近的测验</span>
              <router-link :to="{ name: 'examlib' }">
                <el-link>查看更多</el-link>
              </router-link>
            </div>
          </template>
          <el-empty v-if="!examList || !examList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="exam in (examList || [])" @click="goToExam(exam)" :key="exam.id">
            <div class="item-container">
              <div class="icon">
                <el-image src="/images/icon-exam.png" fit="cover" />
              </div>
              <div>
                <div class="name">{{ exam.name }}</div>
                <div class="desc">{{ exam.openTime }} - {{ exam.closeTime }}</div>
              </div>
            </div>
          </el-card>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card style="min-height: 480px;">
          <template #header>
            <div class="card-header">
              <span>最近的作业</span>
              <router-link :to="{ name: 'homeworklib' }">
                <el-link>查看更多</el-link>
              </router-link>
            </div>
          </template>
          <el-empty v-if="!homeworkList || !homeworkList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="homework in (homeworkList || [])" @click="goToHomework(homework)" :key="homework.id">
            <div class="item-container">
              <div class="icon">
                <el-image src="/images/icon-exam.png" fit="cover" />
              </div>
              <div>
                <div class="name">{{ homework.title }}</div>
                <div class="desc">截止时间：{{ homework.deadline }}</div>
              </div>
            </div>
          </el-card>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getCourseList, getExamList, getResourceList, getHomeworkList, getTeacherHomeworkCount } from '@/api'
import config from '@/config'
import { Management, Box, Platform, DocumentChecked } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 分页相关
const pageNum = ref(1)
const pageSize = ref(3)
const courseTotal = ref(0)
const resourceTotal = ref(0)
const examTotal = ref(0)
const homeworkTotal = ref(0)

const courseList = ref([])
const examList = ref([])
const homeworkList = ref([])

onMounted(async () => {
  console.log('首页组件挂载，开始加载数据...')

  // 使用Promise.allSettled确保即使某个请求失败，其他请求仍能继续
  await Promise.allSettled([
    loadCourseList(),
    loadResourceList(),
    loadExamList(),
    loadHomeworkList(),
    loadHomeworkCount()
  ])

  console.log('数据加载完成')
})

// 获取班课列表
const loadCourseList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const data = await getCourseList(params)
    console.log('班课列表响应:', data)

    // 确保data存在且是对象
    if (data && typeof data === 'object') {
      courseList.value = Array.isArray(data.list) ? data.list : []
      courseTotal.value = typeof data.total === 'number' ? data.total : 0
    } else {
      courseList.value = []
      courseTotal.value = 0
    }
  } catch (error) {
    console.error('获取班课列表失败:', error)
    courseList.value = []
    courseTotal.value = 0
  }
}

// 获取资源列表
const loadResourceList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const data = await getResourceList(params)
    console.log('资源列表响应:', data)

    if (data && typeof data === 'object') {
      resourceTotal.value = typeof data.total === 'number' ? data.total : 0
    } else {
      resourceTotal.value = 0
    }
  } catch (error) {
    console.error('获取资源列表失败:', error)
    resourceTotal.value = 0
  }
}

// 获取测验列表
const loadExamList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const data = await getExamList(params)
    console.log('测验列表响应:', data)

    if (data && typeof data === 'object') {
      examList.value = Array.isArray(data.list) ? data.list : []
      examTotal.value = typeof data.total === 'number' ? data.total : 0
    } else {
      examList.value = []
      examTotal.value = 0
    }
  } catch (error) {
    console.error('获取测验列表失败:', error)
    examList.value = []
    examTotal.value = 0
  }
}

// 获取作业列表
const loadHomeworkList = async () => {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value
    }
    const data = await getHomeworkList(params)
    console.log('作业列表响应:', data)

    if (data && typeof data === 'object') {
      homeworkList.value = Array.isArray(data.list) ? data.list : []
    } else {
      homeworkList.value = []
    }
  } catch (error) {
    console.error('获取作业列表失败:', error)
    homeworkList.value = []
  }
}

// 获取作业数量统计
const loadHomeworkCount = async () => {
  try {
    const count = await getTeacherHomeworkCount()
    console.log('作业统计响应:', count)

    if (typeof count === 'number') {
      homeworkTotal.value = count
    } else if (count && typeof count === 'object' && typeof count.total === 'number') {
      homeworkTotal.value = count.total
    } else {
      homeworkTotal.value = 0
    }
  } catch (error) {
    console.error('获取作业统计失败:', error)
    homeworkTotal.value = 0
  }
}

// 进入班课详情界面
const goToCourse = row => {
  router.push({ name: 'course_exam', params: { id: row.id } })
}

// 进入测验详情界面
const goToExam = row => {
  router.push({ name: 'exam_preview', params: { id: row.id } })
}

// 进入作业详情界面
const goToHomework = row => {
  router.push({ name: 'homework_detail', params: { id: row.id } })
}
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}
.num {
  font-weight: 700;
  font-size: 1.5rem;
  line-height: 2rem;
}
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
    }
  }
}
</style>