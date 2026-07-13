<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="作业名称" prop="title">
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
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="addSubmit(formRef)">创建</el-button>
  </div>
</template>

<script setup>
import { addHomework } from '@/api'
import notification from '@/utils/notification'
import { reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  courseId: {
    type: [Number, String]
  }
})
// 子传父
const emit = defineEmits(['success'])

// 表单数据
const form = reactive({
  courseId: props.courseId,
  title: '',
  deadline: ''
})
const formRef = ref()

// 表单校验
const rules = {
  title: [
    { required: true, message: "作业名不能为空", trigger: "blur" }
  ],
  deadline: [
    { required: true, message: "请选择截止时间", trigger: "change", type: "date" }
  ]
}

// 添加作业
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await addHomework(form)) {
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
}

defineExpose({ resetForm })
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>