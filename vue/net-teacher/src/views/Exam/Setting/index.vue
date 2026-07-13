<template>
  <el-card shadow="never" style="margin-bottom: 20px;">
    <template #header>
      <span>基本信息</span>
    </template>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="auto">
      <el-form-item label="测验名称" prop="name">
        <el-input v-model="form.name" />
      </el-form-item>
      <el-form-item label="开放时间" prop="openTime">
        <el-date-picker
          v-model="form.openTime"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="关闭时间" prop="closeTime">
        <el-date-picker
          v-model="form.closeTime"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
        />
      </el-form-item>
      <el-form-item label="答题时长限制" prop="durationVisible">
        <el-switch v-model="durationVisible" />
      </el-form-item>
      <el-form-item label="答题时长" prop="duration" v-if="durationVisible">
        <el-input v-model="form.duration">
          <template #append>分钟</template>
        </el-input>
      </el-form-item>
      <el-form-item label="切屏后强制交卷" prop="screenCutVisible">
        <el-switch v-model="screenCutVisible" />
      </el-form-item>
      <el-form-item label="切屏时长" prop="screenCutTime" v-if="screenCutVisible">
        <el-input v-model="form.screenCutTime">
          <template #prepend>切换其他页面</template>
          <template #append>秒后即判定为切屏</template>
        </el-input>
      </el-form-item>
      <el-form-item label="切屏次数" prop="screenCutNum" v-if="screenCutVisible">
        <el-input v-model="form.screenCutNum">
          <template #prepend>切换页面超过</template>
          <template #append>次后将被强制交卷</template>
        </el-input>
      </el-form-item>
      <el-form-item label="考生须知" prop="description">
        <WangEditor :content="form.description" @onChange="(val) => form.description = val" />
      </el-form-item>
    </el-form>
    <div class="footer">
      <el-button type="warning" @click="editSubmit(formRef)" :icon="Edit">修改测验</el-button>
    </div>
  </el-card>
  <el-card shadow="never">
    <template #header>
      <span>危险操作</span>
    </template>
    <div>删除此测验，此操作将无法撤回，请谨慎考虑</div>
    <div class="footer">
      <el-button type="danger" @click="delRow(form)" :icon="Delete">删除测验</el-button>
    </div>
  </el-card>
</template>

<script setup>
import { delExam, editExam, getExam } from '@/api'
import WangEditor from '@/components/WangEditor/index.vue'
import notification from '@/utils/notification'
import { Delete, Edit } from '@element-plus/icons-vue'
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'

// 路由相关
const route = useRoute()
const router = useRouter()
// 表单数据
const form = reactive({
  id: '',
  courseId: '',
  name: '',
  openTime: '',
  closeTime: '',
  duration: '',
  description: '',
  screenCutTime: '',
  screenCutNum: ''
})
const formRef = ref()
const durationVisible = ref(false)
const screenCutVisible = ref(false)

// 校验关闭时间
const checkCloseTime = (rule, value, callback) => {
  if(value === '' || value === null) {
    callback(new Error('请选择关闭时间'))
  } else if(form.openTime != '' && form.openTime >= value) {
    callback(new Error('关闭时间不应早于开放时间'))
  } else {
    callback()
  }
}

// 校验测验时长
const checkDuration = (rule, value, callback) => {
  if(!durationVisible.value) {
    callback()
  } else if(value === '' || value === null) {
    callback(new Error('答题时长不能为空'))
  } else if(value > Math.round((new Date(form.closeTime) - new Date(form.openTime))/60000)) {
    callback(new Error('答题时长不能超过测验开放时间段'))
  } else {
    callback()
  }
}

// 校验切屏次数
const checkScreenCutNum = (rule, value, callback) => {
  if(!screenCutVisible.value) {
    callback()
  } else if(value === '' || value === null) {
    callback(new Error('切屏次数不能为空'))
  } else {
    callback()
  }
}

// 校验切屏时长
const checkScreenCutTime = (rule, value, callback) => {
  if(!screenCutVisible.value) {
    callback()
  } else if(value === '' || value === null) {
    callback(new Error('切屏时长不能为空'))
  } else {
    callback()
  }
}

// 表单校验
const rules = {
  name: [
    { required: true, message: "测验名不能为空", trigger: "blur" }
  ],
  openTime: [
    { required: true, message: "请选择开放时间", trigger: "change", type: "date" }
  ],
  closeTime: [
    { validator: checkCloseTime, trigger: "change", type: "date" }
  ],
  duration: [
    { validator: checkDuration, trigger: "blur" }
  ],
  screenCutNum: [
    { validator: checkScreenCutNum, trigger: "blur" }
  ],
  screenCutTime: [
    { validator: checkScreenCutTime, trigger: "blur" }
  ]
}

onMounted(() => {
  loadExam()
})

// 获取测验信息
const loadExam = async () => {
  const exam = await getExam({ id: route.params.id })
  Object.assign(form, exam)
  if(exam.duration != null) {
    durationVisible.value = true
  }
  if(exam.screenCutNum != null) {
    screenCutVisible.value = true
  }
}

// 修改测验
const editSubmit = formEl => {
  if(!durationVisible.value) {
    form.duration = ''
  }
  if(!screenCutVisible.value) {
    form.screenCutTime = ''
    form.screenCutNum = ''
  }
  formEl.validate(async valid => {
    if (valid) {
      await editExam(form)
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}

// 删除班课
const delRow = row => {
  ElMessageBox.confirm('确定要删除此测验吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delExam({ id: row.id })) {
      router.replace({ name: 'examlib' })
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.footer {
  text-align: right;
}
</style>