<template>
  <el-card shadow="never">
    <el-button type="primary" @click="editHomework" :icon="Edit">编辑作业内容</el-button>

    <el-descriptions style="margin-top: 20px;">
      <el-descriptions-item label="作业标题">
        <span class="card-font">{{ homework.title || "--" }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="截止时间">
        <span class="card-font">{{ homework.deadline || "--" }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="创建时间">
        <span class="card-font">{{ homework.createTime || "--" }}</span>
      </el-descriptions-item>
      <el-descriptions-item label="更新时间">
        <span class="card-font">{{ homework.updateTime || "--" }}</span>
      </el-descriptions-item>
    </el-descriptions>

    <el-divider>作业描述</el-divider>
    <div v-html="homework.description || '暂无描述'"></div>

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
            <el-button type="success" :icon="Download" circle @click="downloadAttachment(attachment)"/>
            <el-button type="danger" :icon="Delete" circle @click="deleteAttachment(attachment)"/>
          </div>
        </div>
      </el-card>
    </div>
    <el-empty v-else description="暂无附件" />

    <el-divider>参考答案</el-divider>
    <div v-html="homework.answer || '暂无参考答案'"></div>
  </el-card>

  <!-- 编辑作业内容弹出框 -->
  <el-dialog v-model="dialogVisible" title="编辑作业内容" :before-close="handleBeforeClose" style="min-width: 600px;">
    <editHomeworkContent ref="homeworkForm" :homeworkId="$route.params.id" @success="editSuccess" />
  </el-dialog>
</template>

<script setup>
import { getHomeworkDetail, delHomeworkFile,download } from '@/api'
import editHomeworkContent from './editContent.vue'
import { Edit, Download, Delete } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 作业信息
const homework = ref({})
// 弹出框相关
const dialogVisible = ref(false)
const homeworkForm = ref()

onMounted(() => {
  loadHomework()
})

// 获取作业详情
const loadHomework = async () => {
  const data = await getHomeworkDetail({ id: route.params.id })
  homework.value = data
}

// 编辑作业
const editHomework = () => {
  if (homeworkForm.value) {
    homeworkForm.value.resetForm()
  }
  dialogVisible.value = true
}

// 编辑完成
const editSuccess = () => {
  loadHomework()
  dialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeClose = () => {
  dialogVisible.value = false
  setTimeout(() => {
    homeworkForm.value.resetForm()
  }, 500)
}

// 下载附件
const downloadAttachment = async(attachment) => {
  //console.log('attachment',attachment.id)
  //const data = download({ id: attachment.id })
  // 这里可以实现下载逻辑
  //window.open(attachment.fileUrl, '_blank')
  try {
    const response = await download({ id: attachment.id })
    // 解析文件名
    const disposition = response.headers['content-disposition']
    let filename = 'downloaded_file'
    if (disposition) {
      const match = disposition.match(/filename="?(.+)"?/)
      if (match && match[1]) {
        filename = decodeURIComponent(match[1])
      }
    }

    // 处理blob数据
    const blob = new Blob([response.data], { type: response.data.type })

    // 创建临时a标签触发下载
    const link = document.createElement('a')
    link.href = window.URL.createObjectURL(blob)
    link.download = filename
    link.style.display = 'none'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(link.href)
  } catch (error) {
    console.error('下载失败', error)
  }
}

// 删除附件
const deleteAttachment = (attachment) => {
  ElMessageBox.confirm('确定要删除此附件吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delHomeworkFile({ id: route.params.id, fileId: attachment.id })) {
      loadHomework()
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.card-font {
  color: #1890ff;
  font-style: italic;
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