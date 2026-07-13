<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
    <el-form-item label="源节点" prop="source">
      <el-select v-model="form.source">
        <el-option
          v-for="item in props.nodes"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="目标节点" prop="target">
      <el-select v-model="form.target">
        <el-option
          v-for="item in props.nodes"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        />
      </el-select>
    </el-form-item>
    <el-form-item label="关系名" prop="name">
      <el-input v-model="form.name" placeholder="请输入关系名" />
    </el-form-item>
  </el-form>
  <div class="footer">
    <el-button type="primary" @click="editSubmit(formRef)" v-if="form.id">确定</el-button>
    <el-button type="primary" @click="addSubmit(formRef)" v-else>确定</el-button>
  </div>
</template>

<script setup>
import { addRelationship, editRelationship, getRelationship } from '@/api'
import notification from '@/utils/notification'
import { onMounted, reactive, ref } from 'vue'

// 父传子
const props = defineProps({
  id: {
    type: Number
  },
  categoryId: {
    type: Number
  },
  nodes: {
    type: Array
  }
})
// 子传父
const emit = defineEmits(['success'])

// 表单数据
const form = reactive({
  id: props.id,
  source: '',
  target: '',
  name: '',
  categoryId: props.categoryId
})
const formRef = ref()

// 表单校验
const rules = {
  name: [
    { required: true, message: "关系名不能为空", trigger: "blur" },
    { min: 1, max: 10, message: '关系名长度必须介于 1 和 10 之间', trigger: 'blur' }
  ]
}

onMounted(() => {
  loadRelationship()
})

// 获取关系信息
const loadRelationship = async () => {
  if (form.id) {
    const relationship = await getRelationship({ id: form.id })
    Object.assign(form, relationship)
  }
}

// 添加关系
const addSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await addRelationship(form)) {
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

// 修改关系
const editSubmit = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      if (await editRelationship(form)) {
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
  loadRelationship()
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