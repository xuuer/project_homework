<template>
  <div style="border: 1px solid #ccc">
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <Editor
      style="height: 150px; overflow-y: hidden; resize: vertical;"
      v-model="valueHtml"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
      @onChange="handleChange"
    />
  </div>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css' // 引入 css
import { onBeforeUnmount, ref, shallowRef, onMounted, watch, nextTick } from "vue"
import { Editor, Toolbar } from "@wangeditor/editor-for-vue"
import notification from '@/utils/notification'
import { uploadPictureURL } from '@/api'
import useToken from '@/stores/token'
import config from '@/config'

const { token } = useToken()

// 父传子
const props = defineProps({
  content: {
    type: String,
    default: ''
  },
  disabled: {
    type: Boolean,
    default: false
  }
})
// 子传父
const emit = defineEmits(['onChange'])

// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef()

// 内容 HTML
const valueHtml = ref("<p></p>")

const mode = ref("simple")

onMounted(async () => {
  await nextTick()
  if(props.content) {
    valueHtml.value = props.content
  }
})

watch(() => props.content, newVal => {
  nextTick(() => {
    if(editorRef.value) {
      editorRef.value.setHtml(newVal)
      valueHtml.value = newVal
    }
  })
})

const toolbarConfig = {
  toolbarKeys: [
    'undo', // 撤销
    'redo', // 重做
    'bold', // 加粗
    'italic', // 斜体
    'underline', // 下划线
    'color', // 文字颜色
    'justifyCenter', //居中对齐
    'justifyJustify', // 两端对齐
    'justifyLeft', // 左对齐
    'justifyRight', // 右对齐
    'bulletedList', // 无序列表
    'numberedList', // 有序列表
    'blockquote', // 引用
    'insertTable', // 插入表格
    'uploadImage', // 上传图片
  ]
}

const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {}
}

editorConfig.MENU_CONF['uploadImage'] = {
  server: uploadPictureURL(),
  fieldName: 'file',
  headers: { Authorization: token },
  customInsert(res, insertFn) {
    const { code, msg, data } = res
    if (code !== 200) {
      notification({
        message: msg,
        type: 'error'
      })
    } else {
      let url = config.resourceURL + data.url;
      insertFn(url)
    }
  }
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value
  if (editor == null) return
  editor.destroy()
})

const handleCreated = (editor) => {
  editorRef.value = editor // 记录 editor 实例，重要！
  if(props.disabled) {
    editor.disable()
  }
}

const handleChange = (editor) => {
  emit('onChange', valueHtml.value)
}
</script>