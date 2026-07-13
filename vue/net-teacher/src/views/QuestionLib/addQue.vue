<template>
  <el-card>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="分组" prop="categoryId">
        <el-tree-select 
          v-model="form.categoryId" 
          :data="categoryTree" 
          check-strictly
        />
      </el-form-item>
      <el-form-item label="知识点" prop="tag">
        <el-space wrap>
          <el-tag
            v-for="tag in form.tags"
            :key="tag"
            closable
            :disable-transitions="false"
            @close="handleClose(tag)"
          >
            {{ tag }}
          </el-tag>
          <el-input
            v-if="inputVisible"
            ref="InputRef"
            v-model="inputValue"
            size="small"
            style="width: 100px;"
            @keyup.enter="handleInputConfirm"
            @blur="handleInputConfirm"
          />
          <el-button v-else size="small" @click="showInput">+ 添加知识点</el-button>
        </el-space>
      </el-form-item>
      <el-form-item label="难度" prop="difficulty">
        <el-select v-model="form.difficulty" placeholder="难度">
          <el-option label="简单" value="简单" />
          <el-option label="中等" value="中等" />
          <el-option label="困难" value="困难" />
        </el-select>
      </el-form-item>
      <el-form-item label="题型" prop="type">
        <el-select v-model="form.type" placeholder="题型" @change="handleTypeChange">
          <el-option label="单选题" value="单选题" />
          <el-option label="多选题" value="多选题" />
          <el-option label="判断题" value="判断题" />
          <el-option label="综合题" value="综合题" />
        </el-select>
      </el-form-item>
      <el-form-item label="题干" prop="content">
        <WangEditor :content="form.content" @onChange="(val) => form.content = val" />
      </el-form-item>
      <el-form-item label="选项" prop="options" v-if="hasOptions(form.type)">
        <div class="options">
          <div v-for="(item, index) in form.options" class="option">
            <span>{{ String.fromCharCode(65 + index) }}.</span>
            <WangEditor :content="form.options[index]" @onChange="(val) => form.options[index] = val" />
            <el-button
              :type="answerArr[index] ? 'success' : 'info'"
              :icon="Check"
              circle
              @click="handleOptionChange(index)"
            />
            <el-button
              type="danger"
              :icon="Delete"
              circle
              @click="delOption(index)"
            />
          </div>
          <div>
            <el-button type="primary" :icon="Plus" @click="addOption()" v-if="form.type !== '判断题'">添加选项</el-button>
          </div>
        </div>
      </el-form-item>
      <el-form-item label="答案" prop="answer" v-else>
        <WangEditor :content="form.answer" @onChange="(val) => form.answer = val" />
      </el-form-item>
      <el-form-item label="解析" prop="analysis">
        <WangEditor :content="form.analysis" @onChange="(val) => form.analysis = val" />
      </el-form-item>
    </el-form>
    <div class="footer">
      <el-button type="primary" @click="addSubmit(formRef)">保存</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { addQuestion, getCategoryTree } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import notification from '@/utils/notification'
import { Check, Delete, Plus } from '@element-plus/icons-vue'
import { ElInput } from 'element-plus'
import { nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 分组树列表
const categoryTree = ref([])
// 表单数据
const form = reactive({
  id: '',
  analysis: '',
  answer: '',
  categoryId: '',
  content: '',
  difficulty: '',
  options: ['', '', '', ''],
  tags: [],
  type: '',
})
const formRef = ref()
const answerArr = ref([false, false, false, false])
// 知识点相关
const inputValue = ref('')
const inputVisible = ref(false)
const InputRef = ref()

// 表单校验
const rules = {
  categoryId: [
    { required: true, message: "分组不能为空", trigger: "change" }
  ],
  difficulty: [
    { required: true, message: "难度不能为空", trigger: "change" }
  ],
  type: [
    { required: true, message: "题型不能为空", trigger: "change" }
  ]
}

onMounted(() => {
  loadCategory()
})

const convertToAnswerStr = (answerArr) => {
  let answer = ''
  for(let i = 0; i < answerArr.length; i ++) {
    if(answerArr[i] === true) {
      answer += String.fromCharCode(65 + i)
    }
  }
  return answer
}

const hasOptions = (type) => {
  return (type === '单选题' || type === '多选题' || type === '判断题')
}

// 获取分组
const loadCategory = async () => {
  const params = {
    type: 'questionlib'
  }
  const data = await getCategoryTree(params)
  categoryTree.value = data
}

// 题型改变时
const handleTypeChange = (type) => {
  if(hasOptions(type)) {
    if(type === '判断题') {
      form.options = ['正确', '错误']
      answerArr.value = Array(2).fill(false)
    } else {
      form.options = Array(4).fill('')
      answerArr.value = Array(4).fill(false)
    }
  } else {
    form.options = []
  }
  form.answer = ''
}

// 添加选项
const addOption = () => {
  form.options.push('')
  answerArr.value.push(false)
}

// 正确选项改变时
const handleOptionChange = (index) => {
  if(answerArr.value[index] === false) {
    if(form.type === '单选题' || form.type === '判断题') {
      answerArr.value.fill(false)
    }
    answerArr.value[index] = true
  } else {
    answerArr.value[index] = false
  }
}

// 删除选项
const delOption = (index) => {
  form.options.splice(index, 1)
  answerArr.value.splice(index, 1)
}

// 知识点标签相关
const handleClose = tag => {
  form.tags.splice(form.tags.indexOf(tag), 1)
}

const showInput = () => {
  inputVisible.value = true
  nextTick(() => {
    InputRef.value.input.focus()
  })
}

const handleInputConfirm = () => {
  if (inputValue.value) {
    form.tags.push(inputValue.value)
  }
  inputVisible.value = false
  inputValue.value = ''
}

// 添加试题
const addSubmit = formEl => {
  if(hasOptions(form.type)) {
    form.answer = convertToAnswerStr(answerArr.value)
  }
  formEl.validate(async valid => {
    if (valid) {
      await addQuestion(form)
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}
</script>
    
<style lang="scss" scoped>
.options {
  .option {
    display: flex;
    flex-direction: row;
    margin-bottom: 20px;
  }
}
.footer {
  text-align: right;
}
</style>