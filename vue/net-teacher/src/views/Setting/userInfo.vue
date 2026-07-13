<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
    <el-form-item label="工号" prop="no">
      <el-input v-model="form.no" />
    </el-form-item>
    <el-form-item label="姓名" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" size="mini" @click="submit(formRef)">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import {changeTeacherInfo} from '@/api'
import useAdmin from '@/stores/admin'
import notification from '@/utils/notification'
import {onMounted, reactive, ref} from 'vue'

const form = reactive({
  no: '',
  name: ''
})
const formRef = ref()

const {admin, updateAdmin} = useAdmin()

onMounted(() => {
  loadAdmin()
})

const loadAdmin = () => {
  form.no = admin.no
  form.name = admin.name
}

// 表单校验
const rules = {
  no: [
    {required: true, message: "工号不能为空", trigger: "blur"}
  ],
  name: [
    {required: true, message: "姓名不能为空", trigger: "blur"}
  ]
}

// 修改用户信息
const submit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await changeTeacherInfo(form)) {  // 调用教师API
        updateAdmin({
          no: form.no,
          name: form.name
        })
      }
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}
</script>