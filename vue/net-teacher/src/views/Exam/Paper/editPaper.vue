<template>
  <div>
    <el-row>
      <el-col :span="6" :xs="0">
        <el-card shadow="never" class="aside-box">
          <template #header>
            <div>试卷概览</div>
          </template>
          <div class="group-card">
            <el-row>测验名称： {{ exam.name }}</el-row>
            <el-row>题目总数：<span class="group-card-font"> {{ paper.length }} 题</span></el-row>
            <el-row>试卷总分：<span class="group-card-font"> {{ scoreSum }} 分</span></el-row>
          </div>
          <div>
            <el-button type="danger" style="width: 100%; height: 30px;" @click="save">保存试卷</el-button>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-container>
          <el-header>
            <div>
              <el-button @click="$router.go(-1)">返回</el-button>
              <el-button type="primary" :icon="Plus" @click="addQuestion">添加题目</el-button>
            </div>
          </el-header>
          <el-main>
            <!-- 数据列表 -->
            <el-empty v-if="!paper.length" description="暂无数据" />
            <div
              v-for="(question, index) in paper"
              draggable="true"
              @dragstart="dragStart(index, $event)"
              @dragover.prevent
              @drop="drop(index, $event)"
            >
              <el-card shadow="never">
                <el-row>
                  <el-col :span="18" :xs="24">
                    <el-row>
                      <el-col :span="1" :xs="24">
                        <div class="num">{{ index + 1 }}</div>
                      </el-col>
                      <el-col :span="22" :xs="24" class="item-content">
                        <div v-html="question.content"></div>
                      </el-col>
                    </el-row>
                    <el-row v-for="(option, index) in question.options" class="option-item">
                      <el-col :span="1" class="option-item-tag">
                        {{ String.fromCharCode(65 + index) }}
                      </el-col>
                      <el-col :span="23" v-html="option"></el-col>
                    </el-row>
                    <el-row class="as-box">
                      <div>答案：</div>
                      <div v-html="question.answer"></div>
                    </el-row>
                  </el-col>
                  <el-col :span="6" :xs="24">
                    <el-row style="margin-bottom: 10px;">
                      <div style="margin: 0 auto;">
                        本题分值：<el-input-number size="small" v-model="paper[index].score" :min="0" />
                      </div>
                    </el-row>
                    <el-row style="margin-bottom: 10px;">
                      <div style="margin: 0 auto;">
                        评分误差：<el-input-number size="small" v-model="paper[index].markError" :min="0" :max="paper[index].score" />
                      </div>
                    </el-row>
                    <el-row style="margin-bottom: 10px;">
                      <div style="margin: 0 auto;">
                        <el-button type="danger" :icon="Delete" @click="delQuestion(question)">移除本题</el-button>
                      </div>
                    </el-row>
                  </el-col>
                </el-row>
              </el-card>
            </div>
          </el-main>
        </el-container>
      </el-col>
    </el-row>
  </div>
  <!-- 添加试题的弹出框 -->
  <el-dialog
    v-model="dialogVisible"
    title="添加试题"
    :before-close="handleBeforeCloseDialog"
    style="width: 90%; min-width: 400px;"
  >
    <addQue
      ref="questionForm"
      :examId="$route.params.id"
      @success="addSuccess"
    />
  </el-dialog>
</template>
  
<script setup>
import addQue from './addQue.vue'
import { delExamQuestion, getExam, getExamQuestionList, saveExamQuestion } from '@/api'
import { Delete, Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { computed,  onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 数据列表
const exam = reactive({
  name: ''
})
const paper = ref([])
// 弹出框显示
const dialogVisible = ref(false)
// 弹出框中的表单
const questionForm = ref()

// 试卷总分
const scoreSum = computed(() => {
  let sum = 0
  for(let i = 0; i < paper.value.length; i ++) {
    sum += paper.value[i].score
  }
  return sum
})

onMounted(() => {
  loadExam()
  loadPaper()
})

// 获取测验
const loadExam = async () => {
  const data = await getExam({ id: route.params.id })
  Object.assign(exam, data)
}

// 获取试卷
const loadPaper = async () => {
  const data = await getExamQuestionList({ id: route.params.id })
  paper.value = data
}

// 添加试题
const addQuestion = () => {
  if (questionForm.value) {
    questionForm.value.resetForm(0)
  }
  dialogVisible.value = true
}

// 添加完成
const addSuccess = () => {
  loadPaper()
  dialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeCloseDialog  = () => {
  dialogVisible.value = false
  setTimeout(() => {
    questionForm.value.resetForm()
  }, 500)
}

// 删除试题
const delQuestion = row => {
  ElMessageBox.confirm('确定要从试卷中移除此题目吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delExamQuestion({ id: route.params.id, questionId: row.id })) {
      loadPaper()
    }
  }).catch(() => {})
}

// 保存试卷
const save = async () => {
  await saveExamQuestion({ id: route.params.id, questions: paper.value})
}

// 拖拽调序
let draggedIndex = null;
 
const dragStart = (index, event) => {
  draggedIndex = index;
  event.dataTransfer.effectAllowed = 'move';
  event.dataTransfer.setData('text/plain', null); // Firefox需要这一行，否则会拖不动。
};
 
const drop = (index, event) => {
  const itemToMove = paper.value[draggedIndex];
  paper.value.splice(draggedIndex, 1); // 从原位置移除元素。
  paper.value.splice(index, 0, itemToMove); // 插入到新位置。
  draggedIndex = null; // 重置拖动索引。
};
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
.paper-title {
  text-align: center;
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
.as-box {
  border: 1px solid rgb(238, 238, 238);
  border-radius: 5px;
  line-height: 24px;
  margin: 5px 0 10px;
  padding: 10px;
  font-size: 14px;
  background-color: var(--el-fill-color-light);
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
</style>