<template>
  <el-card class="chat-wrap">
    <div class="chat-content">
      <div class="chat-main">
        <!-- 当消息列表为空时显示暂无对话提示 -->
        <el-empty v-if="msgList.length == 0" description="暂无对话" />
        <!-- 遍历消息列表，根据消息类型显示不同的样式 -->
        <div :class="[(item.type == 'ai') ? 'ai-msg' : 'user-msg']" v-for="item in msgList">
          <!-- 使用 Markdown 组件渲染消息内容 -->
          <span><Markdown :content="item.content" /></span>
        </div>
      </div>
      <div class="chat-footer">
        <!-- 删除聊天记录的按钮 -->
        <el-tooltip effect="dark" content="清空历史对话" placement="top-start">
          <el-button type="danger" :icon="Delete" circle @click="delHistory"/>
        </el-tooltip>
        <!-- 切换是否携带聊天记录的按钮 -->
        <el-tooltip effect="dark" content="对话记忆" placement="top-start">
          <el-button :type="hasMemory ? 'success' : 'danger'" :icon="Clock" circle @click="changeMemory"/>
        </el-tooltip>
        <!-- 输入框表单 -->
        <el-form ref="formRef" @submit.native.prevent>
          <el-input
            placeholder="来说点什么吧..."
            v-model="userMsg"
            @keyup.enter.native="chat"
          >
            <!-- 发送按钮 -->
            <template #append>
              <el-button :icon="Position" @click="chat" />
            </template>
          </el-input>
        </el-form>
      </div>
    </div>
  </el-card>
</template>

<script setup>
import { delChatHistory, getChatHistory } from '@/api'
import Markdown from '@/components/Markdown/index.vue'
import config from '@/config'
import useToken from '@/stores/token'
import { Clock, Delete, Position } from '@element-plus/icons-vue'
import { fetchEventSource } from '@microsoft/fetch-event-source'
import { ElMessage, ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 获取token
const { token } = useToken()
// 获取当前路由参数
const route = useRoute()
// 消息列表
const msgList = ref([])
// 用户输入的消息
const userMsg = ref('')
// 用户输入框引用
const formRef = ref()
// 是否正在聊天
const isTalk = ref(false)
// 是否携带聊天记录
const hasMemory = ref(true)
// 等待AI回答时的提示文本
const loading = ref('思考中...')

onMounted(() => {
  loadChatHistory()
})

// 获取聊天记录
const loadChatHistory = async () => {
  const data = await getChatHistory({ id: route.params.id })
  msgList.value = data
}

// 发送消息
const chat = () => {
  if(!isTalk.value) {
    isTalk.value = true
    // 添加用户消息到消息列表
    msgList.value.unshift({ type: 'user', content: userMsg.value })
    let msg = userMsg.value
    userMsg.value = ''

    // 添加AI加载中的消息到消息列表
    msgList.value.unshift({ type: 'ai', content: loading.value })
    let index = 0
    // 请求服务器，并实时接收AI回复
    fetchEventSource(config.baseURL + '/chat/coze/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json;charset=utf-8',
        'Authorization': token
      },
      body: JSON.stringify({
        courseId: route.params.id,
        memory: hasMemory.value,
        msg: msg
      }),
      onmessage(event) {
        // 更新AI消息内容
        if(msgList.value[index].content == loading.value) {
          msgList.value[index].content = ''
        }
        msgList.value[index].content += event.data
      },
      onclose() {
        isTalk.value = false
      }
    })
  }
}

// 切换是否携带聊天记录
const changeMemory = () => {
  hasMemory.value = !hasMemory.value
  if(hasMemory.value) {
    ElMessage({
      message: '当前模式下，发送消息会携带之前的聊天记录',
      type: 'success',
    })
  } else {
    ElMessage({
      message: '当前模式下，发送消息不会携带之前的聊天记录',
      type: 'warning',
    })
  }
}

// 删除聊天记录
const delHistory = () => {
  ElMessageBox.confirm('是否清空对话？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delChatHistory({ id: route.params.id })) {
      loadChatHistory()
    }
  }).catch(() => {})
}
</script>
  
<style lang="scss" scoped>
.chat-wrap {
  margin: 20px auto;
  .chat-content {
    margin: 0px auto;
    font-size: 14px;
    .chat-main {
      height: 600px;
      margin: 10px auto 0;
      overflow: auto;
      display: flex;
      flex-direction: column-reverse;
      .ai-msg {
        margin: 10px;
        span {
          display: inline-block;
          background: #f4f6f8;
          border-radius: 10px;
          padding: 5px 10px;
          border-top-left-radius: 0;
          word-break: break-all;
          text-align: left;
        }
      }
      .user-msg {
        margin: 10px;
        text-align: right;
        span {
          display: inline-block;
          border-radius: 10px;
          border-top-right-radius: 0;
          background: #409eff;
          color: #fff;
          padding: 5px 10px;
          word-break: break-all;
          text-align: left;
        }
      }
    }
    .chat-footer {
      margin: 10px auto 0;
      display: flex;
      .el-form {
        width: 100%;
      }
    }
  }
}
</style>