<template>
  <div class="homework-detail">
    <!-- 作业信息 -->
    <el-card class="homework-info" shadow="never">
      <template #header>
        <div class="card-header">
          <span>作业信息</span>
        </div>
      </template>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="作业标题">{{ homeworkDetail.title }}</el-descriptions-item>
        <el-descriptions-item label="截止时间">
          <el-tag :type="isOverdue ? 'danger' : 'primary'">
            {{ homeworkDetail.deadline }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="作业描述" :span="2">
          <div v-html="homeworkDetail.description"></div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 作业附件 -->
      <div v-if="homeworkDetail.attachments && homeworkDetail.attachments.length > 0" class="attachments">
        <h4>作业附件：</h4>
        <el-card shadow="hover" class="attachment-item" v-for="attachment in homeworkDetail.attachments" :key="attachment.id">
          <div class="attachment-container">
            <div class="attachment-icon">
              <el-image src="/images/icon-file.png" fit="cover" />
            </div>
            <div class="attachment-info">
              <div class="attachment-name">{{ attachment.fileName }}</div>
            </div>
            <div class="attachment-actions">
             <!-- <el-button type="primary" size="small" @click="previewFile(attachment)">预览</el-button>-->
              <el-button type="success" size="small" @click="downloadFile(attachment)">下载</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </el-card>

    <!-- 我的提交 -->
    <el-card class="my-submission" shadow="never">
      <template #header>
        <div class="card-header">
          <span>我的提交</span>
          <div class="header-actions">
            <el-tag v-if="submissionResult.studentHomeworkVo"
                    :type="getSubmissionStatusType(submissionResult.studentHomeworkVo.status)">
              {{ getSubmissionStatusText(submissionResult.studentHomeworkVo.status) }}
            </el-tag>
          </div>
        </div>
      </template>

      <!-- 未提交状态 -->
      <div v-if="!hasSubmitted" class="no-submission">
        <el-alert title="还未提交作业" type="info" show-icon :closable="false" />
        <div class="submission-form" v-if="!isOverdue">
          <h4>提交作业：</h4>
          <el-form :model="submissionForm" label-width="100px">
            <el-form-item label="作业内容">
              <WangEditor :content="submissionForm.textContent" @onChange="handleContentChange" />
            </el-form-item>
            <el-form-item label="上传附件">
              <el-upload
                ref="uploadRef"
                :action="uploadURL"
                :headers="uploadHeaders"
                :on-success="handleUploadSuccess"
                :on-remove="handleRemoveFile"
                :file-list="fileList"
                multiple
              >
                <el-button type="primary" size="small">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">支持多个文件上传</div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleSubmitHomework" :loading="submitting">提交作业</el-button>
            </el-form-item>
          </el-form>
        </div>
        <div v-else>
          <el-alert title="作业已截止，无法提交" type="error" show-icon :closable="false" />
        </div>
      </div>

      <!-- 已提交状态 -->
      <div v-else class="has-submission">
        <!-- 提交信息 -->
        <el-descriptions :column="2" border v-if="submissionResult.studentHomeworkVo">
          <el-descriptions-item label="提交时间">
            {{ submissionResult.studentHomeworkVo.startTime }}
          </el-descriptions-item>
          <el-descriptions-item label="当前状态">
            <el-tag :type="getSubmissionStatusType(submissionResult.studentHomeworkVo.status)">
              {{ getSubmissionStatusText(submissionResult.studentHomeworkVo.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="评分" v-if="submissionResult.studentHomeworkVo.score !== null">
            <el-tag type="success">{{ submissionResult.studentHomeworkVo.score }} 分</el-tag>
          </el-descriptions-item>
        </el-descriptions>

        <!-- 提交内容 -->
        <div class="submitted-content">
          <h4>提交内容：</h4>
          <div class="text-content" v-html="submissionResult.textContent"></div>
        </div>

        <!-- 提交的附件 -->
        <div v-if="submissionResult.files && submissionResult.files.length > 0" class="submitted-files">
          <h4>提交的附件：</h4>
          <el-card shadow="hover" class="file-item" v-for="file in submissionResult.files" :key="file.id">
            <div class="file-container">
              <div class="file-icon">
                <el-image src="/images/icon-file.png" fit="cover" />
              </div>
              <div class="file-info">
                <div class="file-name">{{ file.fileName }}</div>
                <div class="file-time">上传时间：{{ file.uploadTime }}</div>
              </div>
              <div class="file-actions">
                <!--<el-button type="primary" size="small" @click="previewSubmittedFile(file)">预览</el-button>-->
                <el-button type="danger" size="small" @click="deleteSubmittedFile(file)"
                           v-if="canModify" :loading="deletingFile === file.id">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 修改提交（如果允许的话） -->
        <div v-if="canModify" class="modify-submission">
          <el-divider />
          <h4>修改提交：</h4>
          <el-form :model="modifyForm" label-width="100px">
            <el-form-item label="作业内容">
              <WangEditor :content="modifyForm.textContent" @onChange="handleModifyContentChange" />
            </el-form-item>
            <el-form-item label="追加附件">
              <el-upload
                ref="modifyUploadRef"
                :action="uploadURL"
                :headers="uploadHeaders"
                :on-success="handleModifyUploadSuccess"
                :file-list="modifyFileList"
                multiple
              >
                <el-button type="primary" size="small">选择文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">可以追加新的文件</div>
                </template>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleModifyHomework" :loading="modifying">更新提交</el-button>
            </el-form-item>
          </el-form>
        </div>

        <!-- 批改结果（如果有的话） -->
        <div v-if="submissionResult.studentHomeworkVo && submissionResult.studentHomeworkVo.score !== null" class="grading-result">
          <el-divider />
          <h4>批改结果：</h4>
          <el-alert title="作业已批改" type="success" show-icon :closable="false" />
          <el-descriptions :column="1" border class="grading-info">
            <el-descriptions-item label="得分">
              <el-tag type="success" size="large">
                {{ submissionResult.studentHomeworkVo.score }} 分
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="教师评语" v-if="teacherComment">
              <div v-html="teacherComment"></div>
            </el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {
  getHomeworkDetail,
  getMyHomeworkResult,
  submitHomework,
  updateHomework,
  deleteHomeworkFile,
  uploadPictureURL,
  downResource,
  previewResource
} from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import useToken from '@/stores/token'
import config from '@/config'
import { ElMessage, ElMessageBox } from 'element-plus'
import { computed, onMounted, ref, watch } from 'vue'

// Props
const props = defineProps({
  homeworkId: {
    type: [String, Number],
    required: true
  }
})

// Emits
const emit = defineEmits(['close', 'refresh'])

// Token相关
const { token } = useToken()
const uploadHeaders = { Authorization: token }
const uploadURL = uploadPictureURL()

// 数据
const homeworkDetail = ref({})
const submissionResult = ref({})
const hasSubmitted = ref(false)
const teacherComment = ref('')

// 表单数据
const submissionForm = ref({
  textContent: '',
  files: []
})

const modifyForm = ref({
  textContent: '',
  files: []
})

// 文件列表
const fileList = ref([])
const modifyFileList = ref([])

// 状态
const submitting = ref(false)
const modifying = ref(false)
const deletingFile = ref(null)

// 计算属性
const isOverdue = computed(() => {
  return new Date() > new Date(homeworkDetail.value.deadline)
})

const canModify = computed(() => {
  return hasSubmitted.value && !isOverdue.value &&
         submissionResult.value.studentHomeworkVo &&
         submissionResult.value.studentHomeworkVo.score === null
})

onMounted(() => {
  loadHomeworkDetail()
  loadSubmissionResult()
})

// 监听作业ID变化
watch(() => props.homeworkId, () => {
  if (props.homeworkId) {
    loadHomeworkDetail()
    loadSubmissionResult()
  }
})

// 加载作业详情
const loadHomeworkDetail = async () => {
  try {
    const data = await getHomeworkDetail({ id: props.homeworkId })
    homeworkDetail.value = data
  } catch (error) {
    ElMessage.error('加载作业详情失败')
  }
}

// 加载提交结果
const loadSubmissionResult = async () => {
  try {
    const data = await getMyHomeworkResult({ id: props.homeworkId })
    if (data && data.studentHomeworkVo) {
      submissionResult.value = data
      console.log('submissionResult',submissionResult)
      hasSubmitted.value = true
      modifyForm.value.textContent = data.textContent || ''
      teacherComment.value = data.studentHomeworkVo.comment || ''
    } else {
      hasSubmitted.value = false
    }
  } catch (error) {
    // 如果没有提交记录，会返回错误，这是正常的
    hasSubmitted.value = false
  }
}

// 处理内容变化
const handleContentChange = (content) => {
  submissionForm.value.textContent = content
}

const handleModifyContentChange = (content) => {
  modifyForm.value.textContent = content
}

// 处理文件上传成功
const handleUploadSuccess = (response, file) => {
  if (response.code === 200) {
    submissionForm.value.files.push({
      fileName: file.name,
      fileUrl: response.data.url
    })
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '文件上传失败')
  }
}

const handleModifyUploadSuccess = (response, file) => {
  if (response.code === 200) {
    modifyForm.value.files.push({
      fileName: file.name,
      fileUrl: response.data.url
    })
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error(response.msg || '文件上传失败')
  }
}

// 移除文件
const handleRemoveFile = (file, fileList) => {
  const index = submissionForm.value.files.findIndex(f => f.fileName === file.name)
  if (index > -1) {
    submissionForm.value.files.splice(index, 1)
  }
}

// 提交作业
const handleSubmitHomework = async () => {
  if (!submissionForm.value.textContent && submissionForm.value.files.length === 0) {
    ElMessage.warning('请输入作业内容或上传附件')
    return
  }

  submitting.value = true
  try {
    await submitHomework(props.homeworkId, submissionForm.value)
    ElMessage.success('作业提交成功')
    emit('refresh')
    loadSubmissionResult()
  } catch (error) {
    ElMessage.error('作业提交失败')
  } finally {
    submitting.value = false
  }
}

// 修改作业
const handleModifyHomework = async () => {
  modifying.value = true
  try {
    await updateHomework(props.homeworkId, modifyForm.value)
    ElMessage.success('作业修改成功')
    emit('refresh')
    loadSubmissionResult()
  } catch (error) {
    ElMessage.error('作业修改失败')
  } finally {
    modifying.value = false
  }
}

// 删除已提交的文件
const deleteSubmittedFile = async (file) => {
  try {
    await ElMessageBox.confirm('确认删除此文件吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })

    deletingFile.value = file.id
    await deleteHomeworkFile(props.homeworkId, file.id)
    ElMessage.success('文件删除成功')
    loadSubmissionResult()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('文件删除失败')
    }
  } finally {
    deletingFile.value = null
  }
}

// 预览文件
const previewFile = async (attachment) => {
  try {
    const url = config.resourceURL + attachment.fileUrl
    window.open(url)
  } catch (error) {
    ElMessage.error('文件预览失败')
  }
}

const previewSubmittedFile = async (file) => {
  try {
    const url = config.resourceURL + file.fileUrl
    window.open(url)
  } catch (error) {
    ElMessage.error('文件预览失败')
  }
}

// 下载文件
const downloadFile = async (attachment) => {
    try {
    const link = document.createElement('a')
    link.href = config.resourceURL + attachment.fileUrl
    link.download = attachment.fileName
    link.click()
  } catch (error) {
    ElMessage.error('文件下载失败')
  }
}

// 获取提交状态类型
const getSubmissionStatusType = (status) => {
  switch (status) {
    case 'true': return 'success'
    case 'false': return 'info'
    default: return 'info'
  }
}

// 获取提交状态文本
const getSubmissionStatusText = (status) => {
  switch (status) {
    case 'true': return '已提交'
    case 'false': return '未提交'
    default: return '未知状态'
  }
}
</script>

<style lang="scss" scoped>
.homework-detail {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .homework-info, .my-submission {
    margin-bottom: 20px;
  }

  .attachments, .submitted-files {
    margin-top: 15px;

    h4 {
      margin-bottom: 10px;
      color: #409eff;
    }
  }

  .attachment-item, .file-item {
    margin-bottom: 10px;
    cursor: pointer;

    .attachment-container, .file-container {
      display: flex;
      align-items: center;

      .attachment-icon, .file-icon {
        width: 50px;
        margin-right: 15px;

        .el-image {
          width: 40px;
          height: 40px;
        }
      }

      .attachment-info, .file-info {
        flex: 1;

        .attachment-name, .file-name {
          font-weight: 500;
          margin-bottom: 5px;
        }

        .file-time {
          font-size: 12px;
          color: #999;
        }
      }

      .attachment-actions, .file-actions {
        .el-button {
          margin-left: 8px;
        }
      }
    }
  }

  .no-submission {
    .submission-form {
      margin-top: 20px;

      h4 {
        color: #409eff;
        margin-bottom: 15px;
      }
    }
  }

  .has-submission {
    .submitted-content {
      margin: 20px 0;

      h4 {
        color: #409eff;
        margin-bottom: 10px;
      }

      .text-content {
        background-color: var(--el-fill-color-light);
        padding: 15px;
        border-radius: 6px;
        min-height: 100px;
      }
    }

    .modify-submission {
      margin-top: 20px;

      h4 {
        color: #409eff;
        margin-bottom: 15px;
      }
    }

    .grading-result {
      margin-top: 20px;

      h4 {
        color: #67c23a;
        margin-bottom: 15px;
      }

      .grading-info {
        margin-top: 15px;
      }
    }
  }
}
</style>