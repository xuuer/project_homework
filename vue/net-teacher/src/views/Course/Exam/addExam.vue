<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="测验名称" prop="name">
      <el-input v-model="form.name" />
    </el-form-item>
    <el-form-item label="开放时间" prop="openTime">
      <el-date-picker
        v-model="form.openTime"
        type="datetime"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
      />
    </el-form-item>
    <el-form-item label="关闭时间" prop="closeTime">
      <el-date-picker
        v-model="form.closeTime"
        type="datetime"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
      />
    </el-form-item>
    <el-form-item label="答题时长限制" prop="durationVisible">
      <el-switch v-model="durationVisible" />
    </el-form-item>
    <el-form-item label="答题时长" prop="duration" v-if="durationVisible">
      <el-input v-model="form.duration">
        <template #append>分钟</template>
      </el-input>
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="addSubmit(formRef)">创建</el-button>
  </div>
</template>
    
<script setup>
import { addExam } from '@/api'
import notification from '@/utils/notification'
import { reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  courseId: {
    type: Number
  }
})
// 子传父
const emit = defineEmits(['success'])

// 表单数据
const form = reactive({
  courseId: props.courseId,
  name: '',
  openTime: '',
  closeTime: '',
  duration: ''
})
const formRef = ref()
const durationVisible = ref(false)

const checkEndTime = (rule, value, callback) => {
  if(value == '' || value == null) {
    callback(new Error('请选择关闭时间'))
  } else if(form.openTime != '' && form.openTime >= value) {
    callback(new Error('关闭时间不应早于开放时间'))
  } else {
    callback()
  }
}

const checkDuration = (rule, value, callback) => {
  if(!durationVisible.value) {
    callback()
  } else if(value == '' || value == null) {
    callback(new Error('答题时长不能为空'))
  } else if(value > Math.round((new Date(form.closeTime) - new Date(form.openTime))/60000)) {
    callback(new Error('答题时长不能超过测验开放时间段'))
  } else {
    callback()
  }
}

// 表单校验
const rules = {
  name: [
    { required: true, message: "测验名不能为空", trigger: "blur" }
  ],
  openTime: [
    { required: true, message: "请选择开放时间", trigger: "change", type: "date" }
  ],
  closeTime: [
    { validator: checkEndTime, trigger: "change", type: "date" }
  ],
  duration: [
    { validator: checkDuration, trigger: "blur" }
  ]
}

// 添加测验
const addSubmit = formEl => {
  if(!durationVisible.value) {
    form.duration = ''
  }
  formEl.validate(async valid => {
    if (valid) {
      if (await addExam(form)) {
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