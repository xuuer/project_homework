<template>
  <div class="text item">
    <div class="avatar">
      <el-avatar class="avatar" shape="square" :size="50" :src="config.resourceURL + avatarURL" />
    </div>
    <el-upload
      ref="uploadRef"
      class="upload-demo"
      :limit="1"
      :action="uploadURL"
      :headers="headers"
      :data="uploadData"
      :auto-upload="false"
      :on-success="uploadSuccess"
    >
      <template #trigger>
        <p><el-button type="primary">选择头像</el-button></p>
      </template>
      <div>
        <el-button type="success" @click="submitUpload">上传头像</el-button>
      </div>
      <template #tip>
        <div class="el-upload__tip"><p>限制上传1个文件，且旧文件会被新文件覆盖</p></div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import defaultAvatarURL from  '/images/avatar-default.png'
import { changeAdminInfo, uploadPictureURL } from '@/api'
import config from '@/config'
import useToken from '@/stores/token'
import useAdmin from '@/stores/admin'
import notification from '@/utils/notification'
import { ref } from 'vue'

const { token } = useToken()
const { admin, updateAdmin } = useAdmin()
const headers = { Authorization: token }
const uploadURL = uploadPictureURL()
const uploadData = { type: 'admin_avatar' }
const avatarURL = ref(admin.avatar || defaultAvatarURL)
const uploadRef = ref()

const submitUpload = () => {
  uploadRef.value.submit()
}

const uploadSuccess = async response => {
  const { code, msg, data } = response
  if (code !== 200) {
    notification({
      message: msg,
      type: 'error'
    })
  } else {
    notification({
      message: '上传成功',
      type: 'success'
    })
    let url = config.resourceURL + data.url;
    await changeAdminInfo({
      avatar: data.url
    })
    updateAdmin({
      avatar: data.url
    })
    avatarURL.value = url
  }
  uploadRef.value.clearFiles()
}
</script>

<style scoped lang="scss">
.avatar {
  text-align: center;
}
.upload-demo {
  text-align: center;
}
</style>
