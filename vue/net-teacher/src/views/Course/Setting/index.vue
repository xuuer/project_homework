<template>
  <el-card shadow="never" style="margin-bottom: 20px;">
    <template #header>
      <span>基本信息</span>
    </template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="课程" prop="name">
        <el-input v-model="form.name" placeholder="请输入班课名" />
      </el-form-item>
      <el-form-item label="学期" prop="semester">
        <el-input v-model="form.semester" placeholder="请输入学期" />
      </el-form-item>
      <el-form-item label="班级" prop="clazz">
        <el-input v-model="form.clazz" placeholder="请输入班级" />
      </el-form-item>
      <el-form-item label="封面" prop="cover">
        <el-upload
          ref="uploadRef"
          :limit="1"
          :action="uploadURL"
          :headers="headers"
          :auto-upload="true"
          :on-success="handleSuccess"
          :on-preview="handlePreview"
          :before-upload="beforeUpload"
          :show-file-list="false"
          class="avatar-uploader"
        >
          <img v-if="form.cover" :src="config.resourceURL + form.cover" class="avatar" />
          <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
        </el-upload>
      </el-form-item>
      <el-form-item label="知识图谱" prop="tagCategoryId">
        <el-tree-select v-model="form.tagCategoryId" :data="tagTree" />
      </el-form-item>
      <el-form-item label="题库" prop="questionCategoryId">
        <el-tree-select v-model="form.questionCategoryId" :data="questionTree" />
      </el-form-item>
    </el-form>
    <div class="footer">
      <el-button type="warning" @click="editSubmit(formRef)" :icon="Edit">修改班课</el-button>
    </div>
  </el-card>
  <el-card shadow="never">
    <template #header>
      <span>危险操作</span>
    </template>
    <div>删除班课，此操作将无法撤回，请谨慎考虑</div>
    <div class="footer">
      <el-button type="danger" @click="delRow(form)" :icon="Delete">删除班课</el-button>
    </div>
  </el-card>
  <!-- 图片预览弹出框 -->
  <el-dialog v-model="dialogVisible">
    <img :src="dialogImageUrl" style="width: 100%;" />
  </el-dialog> 
</template>

<script setup>
import { delCourse, editCourse, getCategoryTree, getCourse, uploadPictureURL } from '@/api'
import config from '@/config'
import useToken from '@/stores/token'
import notification from '@/utils/notification'
import { Delete, Edit, Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()

// 文件上传相关
const { token } = useToken()
const headers = { Authorization: token }
const uploadURL = uploadPictureURL()
console.log('uploadURL', uploadURL) // 查看上传的网址
const uploadRef = ref()
const coverURL = ref()

// 图片预览相关
const dialogImageUrl = ref('')
const dialogVisible = ref(false)

const tagTree = ref()
const questionTree = ref()

// 表单数据相关
const form = reactive({
  id: route.params.id,
  cover: '',
  name: '',
  semester: '',
  clazz: '',
  tagCategoryId: '',
  questionCategoryId: ''
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

onMounted(() => {
  loadCourse()
  loadCategory()
})

// 获取分组
const loadCategory = async () => {
  tagTree.value = await getCategoryTree({ type: 'taglib' })
  questionTree.value = await getCategoryTree({ type: 'questionlib' })
}

// 获取班课信息
const loadCourse = async () => {
  if (form.id) {
    const course = await getCourse({ id: form.id })
    Object.assign(form, course)
    coverURL.value = config.resourceURL + course.cover
  }
}

// 图片上传前
const beforeUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg' && rawFile.type !== 'image/jpg' && rawFile.type !== 'image/png') {
    ElMessage.error('图片格式必须是 JPG/JPEG/PNG !')
    return false
  } else if (rawFile.size / 1024 / 1024 > 2) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

// 图片预览
const handlePreview = (uploadFile) => {
  dialogImageUrl.value = uploadFile.url
  dialogVisible.value = true
}

// 上传封面成功后
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
  uploadRef.value.clearFiles() // 注意清除上传记录，否则无法第二次上传
}

// 修改班课
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      await editCourse(form)
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}

// 删除班课
const delRow = row => {
  ElMessageBox.confirm('确定要删除此班课吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delCourse({ id: row.id })) {
      router.replace({ name: 'courselib' })
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}

.footer {
  text-align: right;
}
</style>