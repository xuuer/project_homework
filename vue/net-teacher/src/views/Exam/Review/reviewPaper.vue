<!-- <template>
  <div>
    <el-row>
      <el-col :span="6" :xs="0">
        <el-card shadow="never" class="aside-box">
          <template #header>
            <div>
              <span>答题卡</span>
            </div>
          </template>
          <div class="group-card">
            <el-row>测验名称： {{ exam.name }}</el-row>
            <el-row>学生学号： {{ student.no }}</el-row>
            <el-row>学生姓名： {{ student.name }}</el-row>
            <el-row>学生成绩：<span class="group-card-font"> {{ student.score }} 分</span></el-row>
            <el-row>试卷总分：<span class="group-card-font"> {{ exam.totalScore }} 分</span></el-row>
          </div>
          <el-empty v-if="!paper.length" description="暂无数据" />
          <div class="card-box">
            <div v-for="num in paper.length" @click="current = num - 1" class="item">
              <div :class="['num', getStatusClass(num - 1)]">
                  {{ num }}
              </div>
            </div>
          </div>
          <div>
            <el-col>
              <el-row>
                <el-button type="danger" style="width: 100%; height: 30px;" @click="save">保存批阅结果</el-button>
              </el-row>
              <el-row style="margin-top: 10px;">
                <el-button type="success" style="width: 100%; height: 30px;" @click="download">导出学生答卷</el-button>
              </el-row>
            </el-col>
          </div>
          <template #footer>
            <div class="card-box">
              <div class="tip">
                <div class="success"></div>
              </div>
              <div>答对</div>
              <div class="tip">
                <div class="error"></div>
              </div>
              <div>答错</div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-container>
          <el-main>
            <el-empty v-if="!paper.length" description="暂无数据" />
            <el-card shadow="never" v-for="(question, index) in paper">
              <el-alert type="info" show-icon :closable="false">
                <template #title>
                  {{ question.type }}（本题{{ question.score }}分）
                </template>
              </el-alert>
              <el-row>
                <el-col :span="18" :xs="24">
                  <el-row>
                    <el-col :span="1" :xs="24">
                      <div class="num">{{ index + 1 }}</div>
                    </el-col>
                    <el-col :span="22" :xs="24" class="qu-content" v-html="question.content"></el-col>
                  </el-row>
                  <el-row class="qu-option" v-for="(option, index) in question.options" >
                    <el-col :span="1" class="option-tag">{{ String.fromCharCode(65 + index) }}</el-col>
                    <el-col :span="23" v-html="option"></el-col>
                  </el-row>
                  <el-row class="as-box">
                    <el-col>
                      <el-row>
                        <div class="right-line">
                          <el-tag type="primary" v-if="question.studentScore === null">
                            <span>待批阅</span>
                          </el-tag>
                          <el-tag type="success" v-else-if="question.studentScore === question.score">
                            <el-icon><CircleCheckFilled /></el-icon>
                            <span>答对了</span>
                          </el-tag>
                          <el-tag type="danger" v-else>
                            <el-icon><CircleCloseFilled /></el-icon>
                            <span>答错了</span>
                          </el-tag>
                        </div>
                      </el-row>
                      <el-row>正确答案：<div v-html="question.answer"></div></el-row>
                      <el-row>考生答案：<div v-html="question.studentAnswer" class="student-answer"></div></el-row>
                      <el-row>考生得分：{{ question.studentScore }}</el-row>
                      <el-row v-if="question.type === '综合题'">备注：
                        <WangEditor :content="question.remark" @onChange="(val) => question.remark = val" />
                      </el-row>
                    </el-col>
                  </el-row>
                </el-col>
                <el-col :span="6" :xs="24">
                  <el-row>
                    <div style="margin: 0 auto;" v-if="question.type === '综合题'">
                      本题 <el-input-number v-model="question.studentScore" :min="0" :max="question.score" /> 分
                    </div>
                  </el-row>
                </el-col>
              </el-row>
            </el-card>
          </el-main>
        </el-container>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { editExamResult, exportStudentPaper, getExamResult } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 学生信息
const student = ref({})
// 测验信息
const exam = ref({})
// 试卷信息
const paper = ref([{}])
// 当前题目下标
const current = ref(0)

onMounted(() => {
  loadExam()
})

// 状态样式
const getStatusClass = computed(() => {
  return (index) => {
    if (paper.value[index].studentScore == null) {
      return 'info'
    } else if (paper.value[index].studentScore >= paper.value[index].score) {
      return 'success'
    } else {
      return 'error'
    }
  }
})

// 获取测验信息
const loadExam = async () => {
  const data = await getExamResult({ examId: route.params.id, studentId: route.params.studentId })
  student.value = data.student
  paper.value = data.paper
  exam.value = data.exam
}

const save = async () => {
  if(await editExamResult({ examId: route.params.id, studentId: route.params.studentId, paper: paper.value })) {
    loadExam()
  }
}

const download = async () => {
  await exportStudentPaper({ examId: route.params.id, studentId: route.params.studentId, paper: paper.value })
}
</script>

<style lang="scss" scoped>
.aside-box {
  height: 100vh;
  overflow: auto;
}
.el-header {
  align-content: center;
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
.qu-content {
  font-size: 16px;
}
.qu-option {
  border: #eee 1px solid;
  border-radius: 5px;
  margin: 15px 0;
  padding: 10px;
  cursor: pointer;
  font-size: 14px;
  line-height: 28px;
  .option-tag {
    width: 10px;
    font-weight: 700;
    color: #0A84FF;
  }
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
    .error {
      border-radius: 2px;
      background: #F00;
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
  .error {
    border-radius: 2px;
    background: #F00;
    color: #FFF !important;
    text-align: center;
    height: 20px;
    line-height: 14px !important;
  }
}
.el-alert {
  margin-bottom: 15px;
}
.as-box {
  border: 1px solid rgb(238, 238, 238);
  border-radius: 5px;
  line-height: 24px;
  margin: 5px 0 10px;
  padding: 10px;
  font-size: 14px;
  background-color: var(--el-fill-color-light);
}
.right-line {
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 10px;
}
:deep(.student-answer img) {
  width: 100% !important;
}
</style> -->

<template>
  <div>
    <el-row>
      <el-col :span="6" :xs="0">
        <el-card shadow="never" class="aside-box">
          <template #header>
            <div>
              <span>答题卡</span>
            </div>
          </template>
          <div class="group-card">
            <el-row>测验名称： {{ exam.name || '--' }}</el-row>
            <el-row>学生学号： {{ student.no || '--' }}</el-row>
            <el-row>学生姓名： {{ student.name || '--' }}</el-row>
            <el-row>学生成绩：<span class="group-card-font"> {{ student.score || 0 }} 分</span></el-row>
            <el-row>试卷总分：<span class="group-card-font"> {{ exam.totalScore || 0 }} 分</span></el-row>
          </div>
          <el-empty v-if="!paper || paper.length === 0" description="暂无数据" />
          <div v-else class="card-box">
            <div v-for="num in paper.length" :key="num" @click="current = num - 1" class="item">
              <div :class="['num', getStatusClass(num - 1)]">
                  {{ num }}
              </div>
            </div>
          </div>
          <div>
            <el-col>
              <el-row>
                <el-button type="danger" style="width: 100%; height: 30px;" @click="save">保存批阅结果</el-button>
              </el-row>
              <el-row style="margin-top: 10px;">
                <el-button type="success" style="width: 100%; height: 30px;" @click="download">导出学生答卷</el-button>
              </el-row>
            </el-col>
          </div>
          <template #footer>
            <div class="card-box">
              <div class="tip">
                <div class="success"></div>
              </div>
              <div>答对</div>
              <div class="tip">
                <div class="error"></div>
              </div>
              <div>答错</div>
            </div>
          </template>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-container>
          <el-main>
            <el-empty v-if="!paper || paper.length === 0" description="暂无数据" />
            <template v-else>
              <el-card shadow="never" v-for="(question, index) in paper" :key="index">
                <el-alert type="info" show-icon :closable="false">
                  <template #title>
                    {{ question.type || '未知题型' }}（本题{{ question.score || 0 }}分）
                  </template>
                </el-alert>
                <el-row>
                  <el-col :span="18" :xs="24">
                    <el-row>
                      <el-col :span="1" :xs="24">
                        <div class="num">{{ index + 1 }}</div>
                      </el-col>
                      <el-col :span="22" :xs="24" class="qu-content" v-html="question.content || ''"></el-col>
                    </el-row>
                    <el-row class="qu-option" v-for="(option, optIndex) in question.options || []" :key="optIndex">
                      <el-col :span="1" class="option-tag">{{ String.fromCharCode(65 + optIndex) }}</el-col>
                      <el-col :span="23" v-html="option"></el-col>
                    </el-row>
                    <el-row class="as-box">
                      <el-col>
                        <el-row>
                          <div class="right-line">
                            <el-tag type="primary" v-if="question.studentScore === null">
                              <span>待批阅</span>
                            </el-tag>
                            <el-tag type="success" v-else-if="question.studentScore === question.score">
                              <el-icon><CircleCheckFilled /></el-icon>
                              <span>答对了</span>
                            </el-tag>
                            <el-tag type="danger" v-else>
                              <el-icon><CircleCloseFilled /></el-icon>
                              <span>答错了</span>
                            </el-tag>
                          </div>
                        </el-row>
                        <el-row>正确答案：<div v-html="question.answer || ''"></div></el-row>
                        <el-row>考生答案：<div v-html="question.studentAnswer || ''" class="student-answer"></div></el-row>
                        <el-row>考生得分：{{ question.studentScore || 0 }}</el-row>
                        <el-row v-if="question.type === '综合题'">备注：
                          <WangEditor :content="question.remark || ''" @onChange="(val) => question.remark = val" />
                        </el-row>
                      </el-col>
                    </el-row>
                  </el-col>
                  <el-col :span="6" :xs="24">
                    <el-row>
                      <div style="margin: 0 auto;" v-if="question.type === '综合题'">
                        本题 <el-input-number v-model="question.studentScore" :min="0" :max="question.score || 0" />
                      </div>
                    </el-row>
                  </el-col>
                </el-row>
              </el-card>
            </template>
          </el-main>
        </el-container>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { editExamResult, exportStudentPaper, getExamResult } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 学生信息
const student = ref({
  no: '',
  name: '',
  score: 0
})
// 测验信息
const exam = ref({
  name: '',
  totalScore: 0
})
// 试卷信息
const paper = ref([]) // 初始化为空数组
// 当前题目下标
const current = ref(0)

onMounted(() => {
  loadExam()
})

// 状态样式
const getStatusClass = computed(() => {
  return (index) => {
    const question = paper.value[index]
    if (!question) return 'info'
    
    if (question.studentScore == null) {
      return 'info'
    } else if (question.studentScore >= (question.score || 0)) {
      return 'success'
    } else {
      return 'error'
    }
  }
})

// 获取测验信息
const loadExam = async () => {
  try {
    const data = await getExamResult({ 
      examId: route.params.id, 
      studentId: route.params.studentId 
    })
    
    student.value = data.student || {}
    paper.value = data.paper || []
    exam.value = data.exam || {}
  } catch (error) {
    console.error('加载考试数据失败:', error)
    paper.value = []
  }
}

const save = async () => {
  try {
    const success = await editExamResult({ 
      examId: route.params.id, 
      studentId: route.params.studentId, 
      paper: paper.value 
    })
    
    if (success) {
      await loadExam()
      ElMessage.success('批阅结果保存成功')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存批阅结果失败')
  }
}

const download = async () => {
  try {
    await exportStudentPaper({ 
      examId: route.params.id, 
      studentId: route.params.studentId, 
      paper: paper.value 
    })
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出学生答卷失败')
  }
}
</script>

<style lang="scss" scoped>
.aside-box {
  height: 100vh;
  overflow: auto;
}
.el-header {
  align-content: center;
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
.qu-content {
  font-size: 16px;
}
.qu-option {
  border: #eee 1px solid;
  border-radius: 5px;
  margin: 15px 0;
  padding: 10px;
  cursor: pointer;
  font-size: 14px;
  line-height: 28px;
  .option-tag {
    width: 10px;
    font-weight: 700;
    color: #0A84FF;
  }
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
    .error {
      border-radius: 2px;
      background: #F00;
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
  .error {
    border-radius: 2px;
    background: #F00;
    color: #FFF !important;
    text-align: center;
    height: 20px;
    line-height: 14px !important;
  }
}
.el-alert {
  margin-bottom: 15px;
}
.as-box {
  border: 1px solid rgb(238, 238, 238);
  border-radius: 5px;
  line-height: 24px;
  margin: 5px 0 10px;
  padding: 10px;
  font-size: 14px;
  background-color: var(--el-fill-color-light);
}
.right-line {
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 10px;
}
:deep(.student-answer img) {
  width: 100% !important;
}
</style>