<template>
  <div class="box-wrap">
    <el-row :gutter="20">
      <el-col :span="6" :xs="24">
        <el-card>
          <template #header>
            <span>个人信息</span>
          </template>
          <div>
            <div class="avatar">
              <el-image :src="config.resourceURL + admin.avatar" fit="cover" />
            </div>
            <el-descriptions class="desc" :column="1">
              <el-descriptions-item label="工号">{{ admin.no }}</el-descriptions-item>
              <el-descriptions-item label="姓名">{{ admin.name }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-card>
      </el-col>
      <el-col :span="18" :xs="24">
        <el-card>
          <template #header>
            <span>基本资料</span>
          </template>
          <el-tabs v-model="activeName">
            <el-tab-pane label="基本资料" name="info">
              <userInfo />
            </el-tab-pane>
            <el-tab-pane label="密码" name="password">
              <resetPwd />
            </el-tab-pane>
            <el-tab-pane label="头像" name="avatar">
              <userAvatar />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import resetPwd from './resetPwd.vue'
import userAvatar from './userAvatar.vue'
import userInfo from './userInfo.vue'
import config from '@/config'
import useAdmin from '@/stores/admin'
import { ref } from 'vue'

const { admin } = useAdmin()
console.log(admin)
const activeName = ref('info')
</script>

<style lang="scss" scoped>
.box-wrap {
  padding: 15px;
  .avatar {
    text-align: center;
    .el-image {
      width: 130px;
      height: 130px;
    }
  }
  .desc {
    margin-top: 10px;
  }
}
</style>