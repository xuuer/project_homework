<template>
  <el-button @click="$router.go(-1)">返回</el-button>

  <el-tabs type="border-card">
    <el-tab-pane label="知识卡片">
      <el-card shadow="never">
        <template #header>
          <div>
            <h1>{{ tag.name }}</h1>
          </div>
        </template>
        <div v-if="description == ''">
          <div>AI整理资料中，大概需要1分钟，请耐心等待...</div>
          <div v-loading="true" style="height: 100px;"></div>
        </div>
        <div v-else>
          <Markdown :content="description" />
          <div v-if="resourceList.length > 0">
            <h1>课内相关资源：</h1>
            <el-card shadow="hover" class="list-item" v-for="res in resourceList" @click="preview(res)">
              <div class="item-container">
                <div class="icon">
                  <el-image src="/images/icon-res.png" fit="cover" />
                </div>
                <div>{{ res.name }}</div>
                <div class="opr">
                  <el-button type="success" :icon="Download" circle @click.stop="downloadResource(res)"/>
                </div>
              </div>
            </el-card>
          </div>
          <div class="footer">
            <el-button @click="loadTagInfo(tag)">不满意，请再次整理</el-button>
          </div>
        </div>
      </el-card>
    </el-tab-pane>

    <el-tab-pane label="练习">
      <div v-if="!question.content">
        <div>AI整理练习中，请稍等...</div>
        <div>（AI会先从班课题库中取题，若没有合适的题目，则会根据课内知识出题）</div>
        <div v-loading="true" style="height: 100px;"></div>
      </div>
      <el-card v-else shadow="never">
        <el-alert type="info" show-icon :closable="false" style="margin-bottom: 10px;">
          <template #title>
            {{ question.type }}
          </template>
        </el-alert>
        <el-row>
          <el-col>
            <el-row>
              <el-col :span="1" :xs="24">
                <div class="num">1</div>
              </el-col>
              <el-col :span="22" :xs="24" class="item-content" v-html="question.content"></el-col>
            </el-row>
            <el-row
              v-for="(option, index) in question.options" 
              @click="handleOptionChange(index)" 
              :class="['option-item', (answerCard[index]=='')?'':'active']"
            >
              <el-col :span="1" class="option-item-tag">{{ String.fromCharCode(65 + index) }}</el-col>
              <el-col :span="23" v-html="option"></el-col>
            </el-row>
          </el-col>
        </el-row>
        <el-divider border-style="dashed" />
        <el-button type="primary" @click="submitPractice" v-if="!isSubmit">提交</el-button>
        <div v-if="isSubmit">
          <el-row class="as-box">
            <el-col>
              <el-row>
                <div class="right-line">
                  <el-tag type="success" v-if="question.answer==answer">
                    <el-icon><CircleCheckFilled /></el-icon>
                    <span>答对了</span>
                  </el-tag>
                  <el-tag type="danger" v-else>
                    <el-icon><CircleCloseFilled /></el-icon>
                    <span>答错了</span>
                  </el-tag>
                </div>
              </el-row>
              <el-row>正确答案：{{ question.answer }}</el-row>
            </el-col>
          </el-row>
          <el-button type="danger" :icon="ArrowRight" @click="loadPractice">下一题</el-button>
        </div>
      </el-card>
    </el-tab-pane>
  </el-tabs>
</template>

<script setup>
import { downResource, getBrain, getTag, getTagPractice, getTagResource, previewResource, submitTagPractice } from '@/api'
import Markdown from '@/components/Markdown/index.vue'
import useTags from '@/stores/tags'
import { ArrowRight, CircleCheckFilled, CircleCloseFilled, Download } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 获取tag介绍
const { tags, updateTags } = useTags()
// 知识点信息
const tag = ref({})
// 知识点描述
const description = ref('')
// 资源信息
const resourceList = ref([])
// 练习信息
const question = ref({})
const answerCard = ref([])
const answer = ref('')
// 是否已经提交答案
const isSubmit = ref(false)

onMounted(() => {
  loadTag()
  loadTagResource()
  loadPractice()
})

// 获取知识点信息
const loadTag = async () => {
  const data = await getTag({ id: route.params.tagId })
  tag.value = data

  for(let i=0; i<tags.length; i++){
    const item = tags[i]
    if(item.tagId === data.id){
      description.value = item.description
      return
    }
  }

  loadTagInfo(tag.value)
}

const loadTagInfo = async node => {
  description.value = ""

  let msg = "介绍一下：" + node.name
  const answer = await getBrain({
    msg: msg,
    id: route.params.id,
    memory: false
  })
  description.value = answer

  let array = tags
  let flag = false
  for(let i=0; i<array.length; i++){
    const item = array[i]
    if(item.tagId === node.id){
      flag = true
      array[i].description = answer
      break
    }
  }
  if(!flag) {
    array.push({
      tagId: node.id,
      description: answer
    })
  }
  updateTags(array)
}

const loadTagResource = async () => {
  const data = await getTagResource({ id: route.params.id, tagId: route.params.tagId })
  resourceList.value = data
}

// 下载资源
const downloadResource = async row => {
  await downResource({ id: row.id })
}

// 预览资源
const preview = async row => {
  const url = await previewResource({ id: row.id })
  window.open(url)
}

// 获取练习信息
const loadPractice = async () => {
  isSubmit.value = false
  question.value = {}
  const data = await getTagPractice({ id: route.params.id, tagId: route.params.tagId })
  question.value = data
  answerCard.value = Array(5).fill('')
  answer.value = ''
}

// 提交练习
const submitPractice = async () => {
  question.value.studentAnswer = answer.value
  await submitTagPractice(question.value)
  isSubmit.value = true
}

const handleOptionChange = (index) => {
  if(answerCard.value[index] == '') {
    if(question.value.type === '单选题' || question.value.type === '判断题') {
      answerCard.value.fill('')
    }
    answerCard.value[index] = String.fromCharCode(65 + index)
  } else {
    answerCard.value[index] = ''
  }
  answer.value = answerCard.value.join('')
}
</script>

<style lang="scss" scoped>
.header {
  .el-steps {
    max-width: 600px;
    margin: 0 auto;
  }
}
.footer {
  text-align: center;
}
.el-card {
  margin-bottom: 10px;
  .card-title {
    font-size: 22px;
    font-weight: 700;
  }
  .card-font {
    color: #1890ff;
    font-style: italic;
  }
}
.footer {
  text-align: right;
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
.as-box {
  border: 1px solid rgb(238, 238, 238);
  border-radius: 5px;
  line-height: 24px;
  margin: 5px 0 10px;
  padding: 10px;
  font-size: 14px;
  color: rgb(85, 85, 85);
  background: var(--el-bg-color-page);
}
.right-line {
  display: flex;
  -webkit-box-align: center;
  align-items: center;
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 10px;
}
.list-item {
  cursor: pointer;
  margin-bottom: 10px;
  .item-container {
    display: flex;
    flex-direction: row;
    width: 100%;
    .icon {
      width: 70px;
      .el-image {
        width: 60px;
        height: 60px;
      }
    }
    .opr {
      width: 40px;
      margin-left: auto;
    }
  }
}
</style>