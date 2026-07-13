<template>
  <div>
    <el-row>
      <el-col :span="6" :xs="0">
        <el-card shadow="never" class="aside-box">
          <template #header>
            <div>
              <span>作业信息</span>
            </div>
          </template>
          <div class="group-card">
            <el-row>作业标题： {{ homework.title || '--' }}</el-row>
            <el-row>学生学号： {{ studentDetail.studentHomeworkVo?.no || '--' }}</el-row>
            <el-row>学生姓名： {{ studentDetail.studentHomeworkVo?.name || '--' }}</el-row>
            <el-row>学生成绩：<span class="group-card-font"> {{ studentDetail.studentHomeworkVo?.score || 0 }} 分</span></el-row>
            <el-row>截止时间：<span class="group-card-font"> {{ homework.deadline || '--' }}</span></el-row>
          </div>
          <div>
            <el-col>
              <el-row>
                <el-button type="danger" style="width: 100%; height: 30px;" @click="save">保存批阅结果</el-button>
              </el-row>
            </el-col>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-container>
          <el-header>
            <el-button @click="$router.go(-1)">返回</el-button>
          </el-header>
          <el-main>
            <el-card shadow="never" style="margin-bottom: 20px;">
              <template #header>
                <span>作业要求</span>
              </template>
              <div v-html="homework.description || '暂无作业要求'"></div>

              <el-divider>作业附件</el-divider>
              <div v-if="homework.attachments && homework.attachments.length">
                <el-card shadow="hover" class="attachment-item" v-for="attachment in homework.attachments" :key="attachment.id">
                  <div class="attachment-container">
                    <div class="icon">
                      <el-image src="/images/icon-res.png" fit="cover" />
                    </div>
                    <div>
                      <div class="name">{{ attachment.fileName }}</div>
                      <div class="time">{{ attachment.uploadTime }}</div>
                    </div>
                    <div class="opr">
                      <el-button type="success" :icon="Download" circle @click="downloadFile(attachment.fileUrl)"/>
                    </div>
                  </div>
                </el-card>
              </div>
              <el-empty v-else description="暂无附件" />
            </el-card>

            <el-card shadow="never" style="margin-bottom: 20px;">
              <template #header>
                <span>学生提交</span>
              </template>
              <el-descriptions>
                <el-descriptions-item label="提交时间">
                  {{ studentDetail.studentHomeworkVo?.startTime || '--' }}
                </el-descriptions-item>
                <el-descriptions-item label="提交状态">
                  {{ studentDetail.studentHomeworkVo?.status || '--' }}
                </el-descriptions-item>
              </el-descriptions>

              <el-divider>文字内容</el-divider>
              <div v-html="studentDetail.textContent || '暂无文字内容'"></div>

              <el-divider>提交文件</el-divider>
              <div v-if="studentDetail.files && studentDetail.files.length">
                <el-card shadow="hover" class="attachment-item" v-for="file in studentDetail.files" :key="file.id">
                  <div class="attachment-container">
                    <div class="icon">
                      <el-image src="/images/icon-res.png" fit="cover" />
                    </div>
                    <div>
                      <div class="name">{{ file.fileName }}</div>
                      <div class="time">{{ file.uploadTime }}</div>
                    </div>
                    <div class="opr">
                      <el-button type="success" :icon="Download" circle @click="downloadFile(file.fileUrl)"/>
                    </div>
                  </div>
                </el-card>
              </div>
              <el-empty v-else description="暂无提交文件" />
            </el-card>

            <el-card shadow="never">
              <template #header>
                <span>批阅</span>
              </template>
              <el-form ref="formRef" :model="markForm" label-width="auto">
                <el-form-item label="分数">
                  <el-input-number v-model="markForm.score" :min="0" :max="100" />
                </el-form-item>
                <el-form-item label="评语">
                  <el-input v-model="markForm.comment" type="textarea" :rows="4" placeholder="请输入评语" />
                </el-form-item>
              </el-form>
            </el-card>
          </el-main>
        </el-container>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { getHomework, getStudentHomeworkDetail, markStudentHomework } from '@/api'
import { Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 作业信息
const homework = ref({})
// 学生详情
const studentDetail = ref({})
// 批阅表单
const markForm = reactive({
  score: 0,
  comment: ''
})
const formRef = ref()

onMounted(() => {
  loadHomework()
  loadStudentDetail()
})

// 获取作业信息
const loadHomework = async () => {
  const data = await getHomework({ id: route.params.homeworkId })
  console.log('data',data)
  homework.value = data
}

// 获取学生详情
const loadStudentDetail = async () => {
  const data = await getStudentHomeworkDetail({
    homeworkId: route.params.homeworkId,
    studentId: route.params.studentId
  })
  studentDetail.value = data
  markForm.score = data.studentHomeworkVo?.score || 0
  markForm.comment = data.studentHomeworkVo.comment || ''
}

// 保存批阅结果
const save = async () => {
  const success = await markStudentHomework({
    homeworkId: route.params.homeworkId,
    studentId: route.params.studentId,
    score: markForm.score,
    comment: markForm.comment
  })

  if (success) {
    await loadStudentDetail()
    ElMessage.success('批阅结果保存成功')
  }
}

// 下载文件
const downloadFile = (fileUrl) => {
  window.open(fileUrl, '_blank')
}
</script>

<style lang="scss" scoped>
.aside-box {
  height: 100vh;
  overflow: auto;
}
.el-header {
  align-content: center;
}
.el-container {
  height: 100vh;
  overflow: auto;
}
.group-card {
  line-height: 28px;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 5px;
  .group-card-font {
    color: #1890FF;
    font-style: italic;
  }
}

.attachment-item {
  margin-bottom: 10px;
  .attachment-container {
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
    .time {
      color: #737373;
      font-size: 13px;
    }
    .opr {
      width: 80px;
      margin-left: auto;
    }
  }
}
</style>