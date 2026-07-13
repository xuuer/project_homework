<template>
  <el-card>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="分组" prop="categoryId">
        <el-tree-select 
          v-model="form.categoryId" 
          :data="categoryTree" 
          check-strictly
          disabled
        />
      </el-form-item>
      <el-form-item label="知识点" prop="tag">
        <el-space wrap>
          <el-tag v-for="tag in form.tags" :key="tag">{{ tag }}</el-tag>
        </el-space>
      </el-form-item>
      <el-form-item label="难度" prop="difficulty">
        <el-select v-model="form.difficulty" placeholder="难度" disabled>
          <el-option label="简单" value="简单" />
          <el-option label="中等" value="中等" />
          <el-option label="困难" value="困难" />
        </el-select>
      </el-form-item>
      <el-form-item label="题型" prop="type">
        <el-select v-model="form.type" placeholder="题型" disabled>
          <el-option label="单选题" value="单选题" />
          <el-option label="多选题" value="多选题" />
          <el-option label="判断题" value="判断题" />
          <el-option label="综合题" value="综合题" />
        </el-select>
      </el-form-item>
      <el-form-item label="题干" prop="content">
        <WangEditor :content="form.content" @onChange="(val) => form.content = val" :disabled="true" />
      </el-form-item>
      <el-form-item label="选项" prop="options" v-if="hasOptions(form.type)">
        <div class="options">
          <div v-for="(item, index) in form.options" class="option">
            <span>{{ String.fromCharCode(65 + index) }}.</span>
            <WangEditor :content="form.options[index]" @onChange="(val) => form.options[index] = val" :disabled="true" />
            <el-button
              :type="answerArr[index] ? 'success' : 'info'"
              :icon="Check"
              circle
            />
          </div>
        </div>
      </el-form-item>
      <el-form-item label="答案" prop="answer" v-else>
        <WangEditor :content="form.answer" @onChange="(val) => form.answer = val" :disabled="true" />
      </el-form-item>
      <el-form-item label="解析" prop="analysis">
        <WangEditor :content="form.analysis" @onChange="(val) => form.analysis = val" :disabled="true" />
      </el-form-item>
    </el-form>
  </el-card>
</template>

<script setup>
import { getCategoryTree, getQuestion } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import { Check } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'
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
  loadQuestion()
})

const convertToAnswerArr = (len, answer) => {
  let answerArr = Array(len).fill(false)
  for(let i = 0; i < answer.length; i ++) {
    answerArr[answer[i].charCodeAt(0) - 65] = true
  }
  return answerArr
}

const hasOptions = (type) => {
  return (type === '单选题' || type === '多选题' || type === '判断题')
}

// 获取分组
const loadCategory = async () => {
  const data = await getCategoryTree({ type: 'questionlib' })
  categoryTree.value = data
}

// 获取试题
const loadQuestion = async () => {
  const question = await getQuestion({ id: route.params.id })
  Object.assign(form, question)
  if(hasOptions(form.type)) {
    answerArr.value = convertToAnswerArr(form.options.length, form.answer)
  }
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