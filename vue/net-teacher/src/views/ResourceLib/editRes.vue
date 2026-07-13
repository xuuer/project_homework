<template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="分组" prop="categoryId">
        <el-tree-select 
          v-model="form.categoryId" 
          :data="props.categoryTree" 
          check-strictly
        />
      </el-form-item>
      <el-form-item label="知识点" prop="tag">
        <el-space wrap>
          <el-tag
            v-for="tag in form.tags"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="handleClose(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            size="small"
            style="width: 100px;"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else size="small" @click="showInput">+ 添加知识点</el-button>
        </el-space>
      </el-form-item>
      <el-upload
        v-show="!form.id"
        ref="uploadRef"
        :limit="1"
        :headers="headers"
        :action="uploadURL"
        :auto-upload="true"
        @success="uploadSuccess"
        drag
      >
        <el-icon class="el-icon--upload"><upload-filled /></el-icon>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
    </el-form>
    <div class="footer">
      <el-button type="primary" @click="editSubmit(formRef)" v-if="form.id">确定</el-button>
      <el-button type="primary" @click="addSubmit(formRef)" v-else>确定</el-button>
    </div>
</template>

<script setup>
import { addResource, editResource, getResource, uploadPictureURL } from '@/api'
import useToken from '@/stores/token'
import notification from '@/utils/notification'
import { UploadFilled } from '@element-plus/icons-vue'
import { nextTick, onMounted, reactive, ref } from 'vue'
import { ElInput } from 'element-plus'

const { token } = useToken()

// 父传子
const props = defineProps({
  id: {
    type: Number
  },
  categoryTree: {
    type: Array
  }
})
// 子传父
const emit = defineEmits(['success'])

// 文件上传相关
const headers = { Authorization: token }
const uploadURL = uploadPictureURL()
const uploadRef = ref()

// 表单数据
const form = reactive({
  id: props.id,
  categoryId: '',
  tags: [],
  name: '',
  url: ''
})
const formRef = ref()

// 表单校验
const rules = {
  categoryId: [
    { required: true, message: "分组不能为空", trigger: "change" }
  ]
}

onMounted(() => {
  loadQuestion()
})

// 获取资源
const loadQuestion = async () => {
  if (form.id) {
    const resource = await getResource({ id: form.id })
    Object.assign(form, resource)
    if(form.tags == null) {
      form.tags = []
    }
  }
}

// 文件上传成功后
const uploadSuccess = async response => {
  const { code, msg, data } = response
  if (code !== 200) {
    notification({
      message: msg,
      type: 'error'
    })
  } else {
    form.name = data.name
    form.url = data.url
  }
}

// 添加资源
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if(await addResource(form)){
        notification({
          message: '上传成功',
          type: 'success'
        })
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

// 修改资源
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await editResource(form)) {
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
const btnCancel = () => {
  formRef.value.resetFields()
  uploadRef.value.clearFiles()
  loadQuestion()
}

const resetForm = id => {
  form.id = id
  btnCancel()
}

defineExpose({ resetForm })


const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref()

const handleClose = tag => {
  form.tags.splice(form.tags.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value.input.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>