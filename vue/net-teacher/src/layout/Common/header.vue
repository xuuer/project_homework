<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="setting.collapseSideBar" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />

    <div class="right-menu">
      <el-tooltip content="主题" effect="dark" placement="bottom">
        <div class="right-menu-item hover-effect">
          <el-switch v-model="isDark" :active-icon="Moon" :inactive-icon="Sunny" inline-prompt @change="customToggleDark" />
        </div>
      </el-tooltip>
      <el-dropdown class="avatar-container right-menu-item hover-effect">
        <div class="avatar-wrapper">
          <img :src="config.resourceURL + admin.avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <router-link :to="{ name: 'setting' }">
              <el-dropdown-item>个人中心</el-dropdown-item>
            </router-link>
            <el-dropdown-item divided @click.native="logout">
              <span>退出登录</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import router from '@/router'
import { useDark } from '@vueuse/core'
import { Moon, Sunny } from '@element-plus/icons-vue'
import notification from '@/utils/notification'
import { getTeacher } from '@/api'
import Breadcrumb from '@/components/Breadcrumb/index.vue'
import Hamburger from '@/components/Hamburger/index.vue'
import useAdmin from '@/stores/admin'
import useToken from '@/stores/token'
import useSetting from '@/stores/setting'
import config from '@/config'

const { removeToken } = useToken()

const { admin, updateAdmin, removeAdmin } = useAdmin()
const { setting, updateSetting } = useSetting()

const isDark = useDark()

onMounted(() => {
  loadAdmin()
})

const loadAdmin = async () => {
  console.log('loadAdmin')
  let data = await getTeacher()  // ✅ 直接调用教师API
  updateAdmin({
    name: data.name,
    avatar: data.avatar
  })
}

const logout = async () => {
  removeToken()
  removeAdmin()
  router.push({name: 'login'})
  notification({
    message: '退出成功',
    type: 'success'
  })
}

const customToggleDark = e => {
  if (e) {
    const theme = 'dark'
    document.documentElement.setAttribute('class', theme)
  } else {
    const theme = 'light'
    document.documentElement.setAttribute('class', theme)
  }
}

const toggleSideBar = () => {
  updateSetting({
    collapseSideBar: !setting.collapseSideBar
  })
}
</script>

<style scoped lang="scss">
.navbar {
  height: 50px;
  overflow: hidden;
  position: relative;
  background: var(--el-bg-color);
  box-shadow: 0 1px 4px rgba(0, 21, 41, .08);

  .hamburger-container {
    line-height: 46px;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: background .3s;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: rgba(0, 0, 0, .025)
    }
  }

  .breadcrumb-container {
    float: left;
  }

  .right-menu {
    float: right;
    height: 100%;
    line-height: 50px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: inline-block;
      padding: 0 8px;
      height: 100%;
      font-size: 18px;
      color: #5a5e66;
      vertical-align: text-bottom;

      &.hover-effect {
        cursor: pointer;
        transition: background .3s;

        &:hover {
          background: rgba(0, 0, 0, .025)
        }
      }
    }

    .avatar-container {
      margin-right: 30px;

      .avatar-wrapper {
        margin-top: 5px;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 40px;
          height: 40px;
          border-radius: 10px;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: absolute;
          right: -20px;
          top: 25px;
          font-size: 12px;
        }
      }
    }
  }
}
</style>