<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="学号" prop="studentText">
      <el-input 
        v-model="form.studentText" 
        placeholder="学号" 
        type="textarea" 
        :rows="4"
      />
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="addSubmit(formRef)">添加</el-button>
  </div>
</template>
  
<script setup>
import { addCourseStudent } from '@/api'
import notification from '@/utils/notification'
import { reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  id: {
    type: Number
  }
})
// 子传父
const emit = defineEmits(['success'])

// 表单数据
const form = reactive({
  id: props.id,
  studentText: ''
})
const formRef = ref()

// 表单校验
const rules = {
  studentText: [
    { required: true, message: "学号不能为空", trigger: "blur" }
  ]
}

// 添加学生
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      const data = {
        id: form.id,
        studentIds: form.studentText.split(/\n/)
      }
      if (await addCourseStudent(data)) {
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
  
<style scoped>
.footer {
  text-align: right;
}
</style>