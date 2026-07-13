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
          <template #footer>
            <div class="card-box">
              <div class="tip">
                <div class="info"></div>
              </div>
              <div>待批阅</div>
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
                <el-col>
                  <el-row>
                    <el-col :span="1" :xs="24">
                      <div class="num">{{ index + 1 }}</div>
                    </el-col>
                    <el-col :span="22" :xs="24" class="qu-content" v-html="question.content"></el-col>
                  </el-row>
                  <el-row class="qu-option" v-for="(option, index) in question.options">
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
                      <el-row>批注：<Markdown :content="question.remark == null ? '' : question.remark" /></el-row>
                    </el-col>
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
import { getExamResult } from '@/api'
import Markdown from '@/components/Markdown/index.vue'
import { CircleCheckFilled, CircleCloseFilled } from '@element-plus/icons-vue'
import { computed, onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 测验信息
const student = ref({})
const exam = ref({})
const paper = ref([{}])
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
  const data = await getExamResult({ examId: route.params.id })
  student.value = data.student
  exam.value = data.exam
  paper.value = data.paper
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