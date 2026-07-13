<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item,index) in levelList" :key="item.path">
        <span v-if="item.redirect === 'noRedirect' || index == levelList.length - 1" class="no-redirect">{{ item.meta.title }}</span>
        <a v-else @click.prevent="handleLink(item)">{{ item.meta.title }}</a>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const levelList = ref(null)

// 组件创建时获取面包屑信息
onMounted(() => {
  getBreadcrumb()
})
 
// 获取面包屑信息的方法
const getBreadcrumb = () => {
  // 只显示带有 meta.title 的路由
  let matched = router.currentRoute.value.matched.filter(item => item.meta && item.meta.title)
  let first = matched[0]
 
  // if (!isDashboard(first)) {
  //   matched = [{ path: '/index', meta: { title: '首页' }}].concat(matched)
  // }
 
  levelList.value = matched.filter(item => item.meta && item.meta.title && item.meta.breadcrumb !== false)
}
 
// 判断是否为首页（仪表盘）的方法
const isDashboard = (route) => {
  const name = route && route.name
  if (!name) {
    return false
  }
  return name.trim() === 'Index'
}
 
// 处理链接点击的方法
const handleLink = (item) => {
  const { redirect, path } = item
  if (redirect) {
    router.push(redirect)
    return
  }
  router.push(path)
}
 
// 监听路由变化
watch(
  () => router.currentRoute.value,
  (route) => {
    // 如果跳转到重定向页面，不更新面包屑
    if (route.path.startsWith('/redirect/')) {
      return
    }
    getBreadcrumb()
  },
  { immediate: true } // 立即执行一次
)
</script>

<style lang="scss" scoped>
.app-breadcrumb.el-breadcrumb {
  display: inline-block;
  font-size: 14px;
  line-height: 50px;
  margin-left: 8px;

  .no-redirect {
    color: #97a8be;
    cursor: text;
  }
}
</style>
