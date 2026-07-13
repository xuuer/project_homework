<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="旧密码" prop="oldPassword">
      <el-input v-model="form.oldPassword" placeholder="请输入旧密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="新密码" prop="newPassword">
      <el-input v-model="form.newPassword" placeholder="请输入新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item label="确认密码" prop="confirmPassword">
      <el-input v-model="form.confirmPassword" placeholder="请确认新密码" type="password" show-password/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submitForm(formRef)">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { changeTeacherPassword } from '@/api'
import notification from '@/utils/notification'
import { reactive, ref } from 'vue'

// 表单数据
const form = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const formRef = ref()

// 校验密码
const validatePass = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入密码不一致！'))
  } else {
    callback()
  }
}

// 表单校验
const rules = reactive({
  oldPassword: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 24, message: '密码长度为6~24个字符', trigger: 'blur'},
  ],
  newPassword: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 24, message: '密码长度为6~24个字符', trigger: 'blur'},
  ],
  confirmPassword: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {validator: validatePass, trigger: 'blur'}
  ]
})

// 提交表单
const submitForm = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      await changeTeacherPassword({  // 调用教师API
        oldPassword: form.oldPassword,
        newPassword: form.newPassword
      })
      resetForm()
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
  formRef.value.resetFields();
}
</script>