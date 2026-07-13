<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="作业描述" prop="description">
      <WangEditor :content="form.description" @onChange="(val) => form.description = val" />
    </el-form-item>

    <el-form-item label="作业附件">
      <el-upload
        ref="uploadRef"
        :headers="headers"
        :action="uploadURL"
        :auto-upload="true"
        @success="uploadSuccess"
        multiple
      >
        <el-button type="primary">上传附件</el-button>
        <template #tip>
          <div class="el-upload__tip">支持上传多个文件</div>
        </template>
      </el-upload>

      <div v-if="form.attachments && form.attachments.length" style="margin-top: 10px;">
        <el-tag v-for="(attachment, index) in form.attachments" :key="index" closable @close="removeAttachment(index)">
          {{ attachment.fileName }}
        </el-tag>
      </div>
    </el-form-item>

    <el-form-item label="参考答案" prop="answer">
      <WangEditor :content="form.answer" @onChange="(val) => form.answer = val" />
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="editSubmit(formRef)">保存</el-button>
  </div>
</template>

<script setup>
import { addHomeworkFile, editHomeworkFile, getHomeworkDetail, uploadPictureURL } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import useToken from '@/stores/token'
import notification from '@/utils/notification'
import { onMounted, reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  homeworkId: {
    type: [Number, String]
  }
})
// 子传父
const emit = defineEmits(['success'])

const { token } = useToken()
const headers = { Authorization: token }
const uploadURL = uploadPictureURL()
const uploadRef = ref()

// 表单数据
const form = reactive({
  description: '',
  answer: '',
  attachments: []
})
const formRef = ref()

// 表单校验
const rules = {}

onMounted(() => {
  loadHomework()
})

// 获取作业详情
const loadHomework = async () => {
  if (props.homeworkId) {
    const homework = await getHomeworkDetail({ id: props.homeworkId })
    form.description = homework.description || ''
    form.answer = homework.answer || ''
    form.attachments = homework.attachments || []
  }
}

// 文件上传成功
const uploadSuccess = (response) => {
  const { code, msg, data } = response
  if (code !== 200) {
    notification({
      message: msg,
      type: 'error'
    })
  } else {
    form.attachments.push({
      fileName: data.name,
      fileUrl: data.url
    })
  }
}

// 移除附件
const removeAttachment = (index) => {
  form.attachments.splice(index, 1)
}

// 保存作业内容
const editSubmit = formEl => {
  console.log('答案',form)
  formEl.validate(async valid => {
    if (valid) {
      const homeworkDto = {
        description: form.description,
        answer: form.answer,
        attachments: form.attachments
      }

      if (await editHomeworkFile({ id: props.homeworkId, homeworkDto })) {
        emit('success')
      }
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
  form.attachments = []
  uploadRef.value.clearFiles()
  loadHomework()
}

defineExpose({ resetForm })
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
.el-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}
</style>