<template>
  <div>
    <el-row>
      <el-col :span="6" :xs="0">
        <el-card shadow="never" class="aside-box">
          <template #header>
            <div>答题卡</div>
          </template>
          <div class="group-card">
            <el-row>测验名称： {{ exam.name }}</el-row>
            <el-row>学号：<span class="group-card-font">{{ student.no }}</span></el-row>
            <el-row>姓名：<span class="group-card-font">{{ student.name }}</span></el-row>
          </div>
          <el-empty v-if="paper.length === 0" description="暂无数据" />
          <div v-else class="card-box">
            <div class="item" v-for="num in paper.length" @click="current = num - 1">
              <div :class="['num', getStatusClass(num - 1)]">{{ num }}</div>
            </div>
          </div>
          <template #footer>
            <div class="card-box">
              <div class="tip">
                <div class="info"></div>
              </div>
              <div>未答</div>
              <div class="tip">
                <div class="success"></div>
              </div>
              <div>已答</div>
              <div class="tip">
                <div class="warning"></div>
              </div>
              <div>当前</div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-container>
          <el-header>
            <div>
              本试卷共{{ exam.questionNum }}题，满分{{ exam.totalScore }}分
              <span v-if="exam.duration">，考试用时{{ exam.duration }}分钟</span>
            </div>
            <div style="margin-left: auto;">
              <el-countdown title="剩余时间" :value="endTime" @finish="finishExam" />
            </div>
            <div style="margin-left: 10px;">
              <el-button type="danger" @click="finishExam">提交试卷</el-button>
            </div>
          </el-header>
          <el-main>
            <el-empty v-if="!paper.length" description="暂无数据" />
            <el-card shadow="never">
              <el-alert type="info" show-icon :closable="false">
                <template #title>
                  {{ paper[current].type }}（本题{{ paper[current].score }}分）
                </template>
              </el-alert>
              <el-row>
                <el-col>
                  <el-row>
                    <el-col :span="1" :xs="24">
                      <div class="num">{{ current + 1 }}</div>
                    </el-col>
                    <el-col :span="22" :xs="24" 
                      class="item-content" 
                      v-html="paper[current].content">
                    </el-col>
                  </el-row>
                  <el-row
                    v-if="hasOptions(paper[current].type)"
                    v-for="(option, index) in paper[current].options" 
                    @click="handleOptionChange(index)" 
                    :class="['option-item', (answerArr[index] === true) ? 'active' : '']"
                  >
                    <el-col :span="1" class="option-item-tag">
                      {{ String.fromCharCode(65 + index) }}
                    </el-col>
                    <el-col :span="23" v-html="option"></el-col>
                  </el-row>
                  <el-row v-else>
                    <WangEditor :content="paper[current].studentAnswer" @onChange="(val) => paper[current].studentAnswer = val" />
                  </el-row>
                </el-col>
              </el-row>
            </el-card>
            <el-divider border-style="dashed" />
            <div>
              <el-button type="primary" :icon="ArrowLeft" @click="preQuestion()" v-if="current > 0">上一题</el-button>
              <el-button type="danger" :icon="ArrowRight" @click="nextQuestion()" v-if="current < paper.length - 1">下一题</el-button>
            </div>
          </el-main>
        </el-container>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { endExam, startExam } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import { ArrowLeft, ArrowRight } from '@element-plus/icons-vue'
import { useDocumentVisibility } from '@vueuse/core'
import { ElMessageBox } from 'element-plus'
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
// 学生信息
const student = reactive({
  name: '',
  no: ''
})
// 测验信息
const exam = reactive({
  examName: '',
  questionNum: 0,
  totalScore: 0,
  duration: '',
  screenCutTime: '',
  screenCutNum: ''
})
const paper = ref([{}])
const endTime = ref(Date.now() + 1000 * 60)
const current = ref(0)
const answerArr = ref([])

// visibility 离开页面-hidden 进入页面-visible
const visibility = useDocumentVisibility()

// 切屏数据
const screenCutting = reactive({
  time: 0, // 实际时间
  num: 0, // 实际数量
})

onMounted(() => {
  loadExam()
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

// 获取测验信息
const loadExam = async () => {
  const data = await startExam({ examId: route.params.id })
  Object.assign(student, data.student)
  Object.assign(exam, data.exam) 
  endTime.value = new Date(data.endTime).valueOf()
  paper.value = data.paper
  handleQuestionChange()
}

// 点击选项时
const handleOptionChange = (index) => {
  let question = paper.value[current.value]
  if(answerArr.value[index] === false) {
    if(question.type === '单选题' || question.type === '判断题') {
      answerArr.value.fill(false)
    }
    answerArr.value[index] = true
  } else {
    answerArr.value[index] = false
  }
  paper.value[current.value].studentAnswer = convertToAnswerStr(answerArr.value)
}

// 状态样式
const getStatusClass = computed(() => {
  return (index) => {
    if (current.value === index) {
      return 'warning'
    } else if (paper.value[index].studentAnswer === '' || paper.value[index].studentAnswer === null) {
      return 'info'
    } else {
      return 'success'
    }
  }
})

// 切换题目时
const handleQuestionChange = () => {
  let question = paper.value[current.value]
  if(hasOptions(question.type)) {
    if(question.studentAnswer === null) {
      question.studentAnswer = ''
    }
    answerArr.value = convertToAnswerArr(question.options.length, question.studentAnswer)
  }
}

const nextQuestion = () => {
  current.value ++
  handleQuestionChange()
}

const preQuestion = () => {
  current.value --
  handleQuestionChange()
}

// 交卷
const finishExam = async () => {
  const data = {
    examId: route.params.id,
    questions: paper.value
  }
  if(await endExam(data)) {
    router.replace({
      name: 'exam_ranking',
      params: {
        id: route.params.id
      }
    })
  }
}

// 监听visibility
const unwatch = watch(visibility, (current, previous) => {
  if(current === 'hidden') {
    // 记住离开当前时间戳
    screenCutting.time = new Date().getTime()
  }
  if(current === 'visible' && previous === 'hidden') {
    // 进来时间戳
    const currentTime = new Date().getTime()
    // 时间单位s
    const diff = (currentTime - screenCutting.time) / 1000
    if(diff >= exam.screenCutTime) {
      screenCutting.num += 1
      if(exam.screenCutNum !== '') {
        if(screenCutting.num <= exam.screenCutNum) {
          ElMessageBox.alert(
            `您已切屏${screenCutting.num}次，超过${exam.screenCutNum}次将强行交卷`,
            '提示',
            {
              confirmButtonText: '我知道了',
              type: 'warning',
            }
          )
        } else {
          finishExam()
        }
      }
    }
  }
})

onUnmounted(() => {
  unwatch()
})
</script>

<style lang="scss" scoped>
.aside-box {
  height: 100vh;
  overflow: auto;
}
.el-header {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  flex-direction: row;
}
.el-container {
  height: 100vh;
  overflow: auto;
}
.group-card {
  line-height: 28px;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 5px;
  .group-card-font {
    color: #1890FF;
    font-style: italic;
  }
}
.num {
  background: url(/images/num-background.png) no-repeat 100% 100%;
  background-size: cover;
  height: 30px;
  width: 30px;
  line-height: 26px;
  color: #FFF;
  font-size: 14px;
  text-align: center;
}
.item-content {
  font-size: 16px;
}
.option-item {
  border: #eee 1px solid;
  border-radius: 5px;
  margin: 15px 0;
  padding: 10px;
  cursor: pointer;
  font-size: 14px;
  line-height: 28px;
  .option-item-tag {
    width: 10px;
    font-weight: 700;
    color: #0A84FF;
  }
}
.active {
    background-color: #00CC5580;
}
.card-box {
  display: flex;
  flex-wrap: wrap;
  align-content: flex-start;
  margin-left: -5px;
  margin-top: 10px;
  .item {
    width: 30px;
    height: 35px;
    background: #EEE;
    border-radius: 2px;
    font-size: 12px;
    -webkit-box-align: center;
    align-items: center;
    cursor: pointer;
    margin: 5px 5px 10px;
    .num {
      text-align: center;
      height: 20px;
      line-height: 20px !important;
    }
    .info {
      border-radius: 2px;
      background: #1890FF;
      color: #FFF !important;
    }
    .success {
      border-radius: 2px;
      background: #0B0;
      color: #FFF !important;
    }
    .warning {
      border-radius: 2px;
      background: #F1C40F;
      color: #FFF !important;
    }
  }
}
.tip {
  width: 30px;
  height: 20px;
  border-radius: 2px;
  font-size: 12px;
  align-items: center;
  cursor: pointer;
  margin: 5px 5px 10px;
  .info {
    border-radius: 2px;
    background: #1890FF;
    color: #FFF !important;
    text-align: center;
    height: 20px;
    line-height: 14px !important;
  }
  .success {
    border-radius: 2px;
    background: #0B0;
    color: #FFF !important;
    text-align: center;
    height: 20px;
    line-height: 14px !important;
  }
  .warning {
    border-radius: 2px;
    background: #F1C40F;
    color: #FFF !important;
    text-align: center;
    height: 20px;
    line-height: 14px !important;
  }
}
.el-alert {
  margin-bottom: 15px;
}
</style>