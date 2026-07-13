<template>
  <el-card class="box-card">
    <h1 class="login-title">计算机网络课程学习平台-教师端</h1>
    <el-card class="box-form">
      <template #header>
        <div class="card-header">
          <h1>登录</h1>
        </div>
      </template>
      <el-form ref="ruleFormRef" status-icon :model="form" :rules="rules" label-width="120px">
        <el-form-item prop="username" label-width="0">
          <el-input v-model="form.username" placeholder="工号" size="large" />
        </el-form-item>
        <el-form-item prop="password" label-width="0">
          <el-input v-model="form.password" type="password" show-password placeholder="密码" size="large" />
        </el-form-item>
        <el-form-item label-width="0">
          <el-button class="button" @click="submitForm(ruleFormRef)" type="primary" size="large" style="width:100%;">登录</el-button>
        </el-form-item>
      </el-form>
      <div class="footer">
        <el-link @click="goToRegister">没有账号？去注册</el-link>
      </div>
    </el-card>
  </el-card>
</template>

<script setup>
import {teacherLogin} from '@/api'
import useToken from '@/stores/token'
import useAdmin from '@/stores/admin'
import notification from '@/utils/notification'
import {reactive, ref} from 'vue'
import {useRouter} from 'vue-router'

// 路由相关
const router = useRouter()
// 表单数据
const form = reactive({
  username: '',
  password: ''
})
const ruleFormRef = ref()

const {updateToken} = useToken()
const {updateAdmin} = useAdmin()

// 表单提交
const submitForm = formEl => {
  formEl.validate(async valid => {
    if (valid) {
      const token = await teacherLogin(form)  // 直接调用教师登录
      if (token) {
        updateToken(token)
        updateAdmin({
          no: form.username
        })
        router.push({name: 'index'})
      }
    } else {
      notification({
        message: '表单填写有误',
        type: 'error'
      })
    }
  })
}

const rules = reactive({
  username: [
    {required: true, message: '请输入用户名', trigger: 'blur'},
    {min: 3, max: 12, message: '用户名长度为3~12个字符', trigger: 'blur'},
  ],
  password: [
    {required: true, message: '请输入密码', trigger: 'blur'},
    {min: 6, max: 24, message: '密码长度为6~24个字符', trigger: 'blur'},
  ]
})

const goToRegister = () => {
  router.push({name: 'register'})
}
</script>

<style lang="scss" scoped>
.box-card {
  height: 100%;
  background: linear-gradient(to bottom right, #20D6AA, #21B4C9);

  .login-title {
    font-size: 2.5em;
    color: #fff;
    text-shadow: 1px 1px 6px #333;
    text-align: center;
  }

  .box-form {
    position: absolute;
    top: 55%;
    left: 50%;
    width: 400px;
    height: 500px;
    transform: translate(-50%, -50%);
    border-radius: 10px;

    .card-header {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .el-form {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;

      .el-form-item {
        width: 90%;
        display: flex;
        align-items: center;
        justify-content: center;
        --el-form-label-font-size: 16px;
        margin-top: 15px;;
        margin-bottom: 15px;

        .button {
          width: 90px;
        }

        &.center {
          display: flex;
          justify-content: center;
        }
      }
    }
  }

  .footer {
    text-align: center;
  }
}
</style>