<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="课程名" prop="name">
      <el-input v-model="form.name" placeholder="请输入班课名" />
    </el-form-item>
    <el-form-item label="学期" prop="semester">
      <el-input v-model="form.semester" placeholder="请输入学期" />
    </el-form-item>
    <el-form-item label="班级" prop="clazz">
      <el-input v-model="form.clazz" placeholder="请输入班级" />
    </el-form-item>
    <el-form-item label="封面" prop="cover">
      <!-- 上传图片 -->
      <el-upload
        ref="uploadRef"
        :limit="1"
        :action="uploadURL"  
        :headers="headers"
        :auto-upload="true"
        :on-success="handleSuccess"
        :on-preview="handlePreview"
        list-type="picture-card"
      >
        <el-icon><Plus /></el-icon>
      </el-upload>
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="addSubmit(formRef)">确定</el-button>
  </div>
  <!-- 图片预览弹出框 -->
  <el-dialog v-model="dialogVisible">
    <img :src="dialogImageUrl" style="width: 100%;" />
  </el-dialog>
</template>

<script setup>
import { addCourse, uploadPictureURL } from '@/api'
import notification from '@/utils/notification'
import useToken from '@/stores/token'
import { Plus } from '@element-plus/icons-vue'
import { reactive, ref } from 'vue'

// 子传父
const emit = defineEmits(['success'])

// 文件上传相关
const { token } = useToken()
const headers = { Authorization: token }
const uploadURL = uploadPictureURL()
const uploadRef = ref()

// 图片预览相关
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

// 表单数据
const form = reactive({
  id: '',
  cover: '',
  name: '',
  semester: '',
  clazz: ''
})
const formRef = ref()

// 表单校验
const rules = {
  name: [
    { required: true, message: "班课名不能为空", trigger: "blur" },
    { min: 2, max: 20, message: '班课名长度必须介于 2 和 20 之间', trigger: 'blur' }
  ],
  semester: [
    { required: true, message: "学期不能为空", trigger: "blur" }
  ],
  clazz: [
    { required: true, message: "班级不能为空", trigger: "blur" }
  ]
}

// 图片预览
const handlePreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
}

// 上传图片成功后
const handleSuccess = async response => {
  const { code, msg, data } = response
  if (code !== 200) {
    notification({
      message: msg,
      type: 'error'
    })
  } else {
    form.cover = data.url
  }
}

// 添加班课
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await addCourse(form)) {
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
  uploadRef.value.clearFiles()
}
    
defineExpose({ resetForm })
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>