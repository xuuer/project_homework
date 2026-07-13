<template>
  <el-menu 
    class="common-menu" 
    mode="horizontal" 
    :default-active="$route.path"
    router
  >
    <el-menu-item index="/index" class="navbar">
      <span>计算机网络课程学习平台</span>
    </el-menu-item>
  </el-menu>
  <el-menu
    class="user-menu"
    mode="horizontal" 
    :ellipsis="false"
    :default-active="$route.path"
  >
    <el-sub-menu index="1">
      <template #title>
        <el-avatar class="avatar" :src="config.resourceURL + admin.avatar"> {{ admin.username }} </el-avatar>
      </template>
      <router-link :to="{ name: 'setting' }">
        <el-menu-item index="5">个人中心</el-menu-item>
      </router-link>
      <el-menu-item index="6" @click="onLogout">退出登录</el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script setup>
import { onMounted } from 'vue'
import { getAdmin } from '@/api'
import useAdmin from '@/stores/admin'
import router from '@/router'
import useToken from '@/stores/token'
import notification from '@/utils/notification'
import config from '@/config'

const { removeToken } = useToken()

const { admin, updateAdmin, removeAdmin } = useAdmin()

onMounted(() => {
  loadAdmin()
})

const loadAdmin = async () => {
  let data = await getAdmin()
  updateAdmin({
    name: data.name,
    avatar: data.avatar
  })
}

const onLogout = async () => {
  removeToken()
  removeAdmin()
  router.push({ name: 'login' })
  notification({
    message: '退出成功',
    type: 'success'
  })
}
</script>

<style scoped lang="scss">
.navbar {
  font-size: 20px;
  font-weight: bold;
}
.common-menu {
  width: calc(100vw - 100px);
  --el-menu-bg-color: #083364;
  --el-menu-hover-bg-color: #083364;
  --el-menu-text-color: #FFF;
  --el-menu-active-color: #FE9D38;
  --el-menu-hover-text-color: #FE9D38;
  border-bottom: 0 !important;
}
.user-menu {
  --el-menu-bg-color: #083364;
  --el-menu-text-color: #FFF;
  border-bottom: 0 !important;
}
</style>