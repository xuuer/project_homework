<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="节点名" prop="name">
      <el-input v-model="form.name" placeholder="请输入节点名" />
    </el-form-item>
    <el-form-item label="层次" prop="category">
      <el-select v-model="form.category">
        <el-option
          v-for="item in categories"
          :key="item"
          :label="item"
          :value="item"
        />
      </el-select>
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="editSubmit(formRef)" v-if="form.id">确定</el-button>
    <el-button type="primary" @click="addSubmit(formRef)" v-else>确定</el-button>
  </div>
</template>

<script setup>
import { addNode, editNode, getNode } from '@/api'
import notification from '@/utils/notification'
import { onMounted, reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  id: {
    type: Number
  },
  categoryId: {
    type: Number
  }
})
// 子传父
const emit = defineEmits(['success'])

// 表单数据
const form = reactive({
  id: props.id,
  name: '',
  category: '',
  categoryId: props.categoryId
})
const formRef = ref()

const categories = ref([
  '课程', '章', '节', '知识点'
])

// 表单校验
const rules = {
  name: [
    { required: true, message: '节点名不能为空', trigger: 'blur' },
    { min: 1, max: 10, message: '节点名长度必须介于 1 和 10 之间', trigger: 'blur' }
  ],
  category: [
    { required: true, message: '请选择知识点层次', trigger: 'change' }
  ]
}

onMounted(() => {
  loadNode()
})

// 获取节点信息
const loadNode = async () => {
  if (form.id) {
    const node = await getNode({ id: form.id })
    Object.assign(form, node)
  }
}

// 添加节点
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await addNode(form)) {
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

// 修改节点
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await editNode(form)) {
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
  loadNode()
}

const resetForm = id => {
  form.id = id
  btnCancel()
}
    
defineExpose({ resetForm })
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>