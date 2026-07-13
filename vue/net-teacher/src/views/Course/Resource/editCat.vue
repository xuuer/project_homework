<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="分组名" prop="name">
      <el-input v-model="form.name" placeholder="分组名"/>
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="editSubmit(formRef)" v-if="form.id">修改</el-button>
    <el-button type="primary" @click="addSubmit(formRef)" v-else>添加</el-button>
  </div>
</template>
    
<script setup>
import { addCategory, editCategory, getCategory } from '@/api'
import notification from '@/utils/notification'
import { onMounted, reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  id: {
    type: Number
  },
  courseId: {
    type: Number
  }
})
// 子传父
const emit = defineEmits(['success'])

// 数据表单
const form = reactive({
  id: props.id,
  courseId: props.courseId,
  name: ''
})
const formRef = ref()

// 表单校验
const rules = {
  name: [
    { required: true, message: "分组名不能为空", trigger: "blur" }
  ]
}

onMounted(() => {
  loadCategory()
})

// 获取分组
const loadCategory = async () => {
  if(form.id) {
    const category = await getCategory({ id: form.id })
    Object.assign(form, category)
  }
}
    
// 添加分组
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      const data = {
        name: form.name,
        courseId: form.courseId
      }
      if (await addCategory(data)) {
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

// 获取分组
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await editCategory(form)) {
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
  loadCategory()
}

const resetForm = id => {
  form.id = id
  btnCancel()
}

defineExpose({ resetForm })
</script>
    
<style scoped>
.footer {
  text-align: right;
}
</style>