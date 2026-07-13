<template>
  <el-card shadow="never" style="margin-bottom: 20px;">
    <template #header>
      <span>基本信息</span>
    </template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="作业标题" prop="title">
        <el-input v-model="form.title" />
      </el-form-item>
      <el-form-item label="截止时间" prop="deadline">
        <el-date-picker
          v-model="form.deadline"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="作业分值" prop="homeworkScore">
        <el-input-number v-model="form.homeworkScore" :min="0" />
      </el-form-item>
    </el-form>
    <div class="footer">
      <el-button type="warning" @click="editSubmit(formRef)" :icon="Edit">修改作业</el-button>
    </div>
  </el-card>
  <el-card shadow="never">
    <template #header>
      <span>危险操作</span>
    </template>
    <div>删除此作业，此操作将无法撤回，请谨慎考虑</div>
    <div class="footer">
      <el-button type="danger" @click="delRow(form)" :icon="Delete">删除作业</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { delHomework, editHomework, getHomework } from '@/api'
import notification from '@/utils/notification'
import { Delete, Edit } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
// 表单数据
const form = reactive({
  id: '',
  title: '',
  deadline: '',
  homeworkScore: 100
})
const formRef = ref()

// 表单校验
const rules = {
  title: [
    { required: true, message: "作业标题不能为空", trigger: "blur" }
  ],
  deadline: [
    { required: true, message: "请选择截止时间", trigger: "change", type: "date" }
  ]
}

onMounted(() => {
  loadHomework()
})

// 获取作业信息
const loadHomework = async () => {
  const homework = await getHomework({ id: route.params.id })
  Object.assign(form, homework)
}

// 修改作业
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      await editHomework(form)
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}

// 删除作业
const delRow = row => {
  ElMessageBox.confirm('确定要删除此作业吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delHomework({ id: row.id })) {
      router.replace({ name: 'course_exam', params: { id: row.courseId } })
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>