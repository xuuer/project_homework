<template>
  <el-card shadow="never">
    <el-button type="primary" @click="goToEdit" :icon="Edit">管理</el-button>
    <el-button type="success" @click="download" :icon="Download">导出</el-button>
    <!-- 数据列表 -->
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
            <el-col :span="22" :xs="24" class="item-content" v-html="question.content"></el-col>
          </el-row>
          <el-row v-for="(option, index) in question.options" class="option-item">
            <el-col :span="1" class="option-item-tag">{{ String.fromCharCode(65 + index) }}</el-col>
            <el-col :span="23" v-html="option"></el-col>
          </el-row>
          <el-row class="as-box">
            <div>答案：</div>
            <div v-html="question.answer"></div>
          </el-row>
        </el-col>
      </el-row>
    </el-card>
  </el-card>
</template>
  
<script setup>
import { exportExamQuestion, getExamQuestionList } from '@/api'
import { Download, Edit } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
// 试卷信息
const paper = ref([])

onMounted(() => {
  loadPaper()
})

// 获取试卷
const loadPaper = async () => {
  const data = await getExamQuestionList({ id: route.params.id })
  paper.value = data
}

// 导出试卷
const download = async () => {
  await exportExamQuestion({ id: route.params.id })
}

// 管理试卷
const goToEdit = () => {
  router.push({ name: 'paper_edit' , params:{ id: route.params.id }})
}
</script>
  
<style lang="scss" scoped>
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
.el-alert {
  margin-bottom: 15px;
}
</style>