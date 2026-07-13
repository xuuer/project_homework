<template>
  <div class="menu">
    <div class="menu-head-box">
      <div class="menu-head-content">
        <div class="cover-box">
          <el-image class="cover" :src="config.resourceURL + course.cover" fit="cover" />
        </div>
        <div class="name-box">
          <div class="name">{{ course.name }}</div>
          <div class="clazz">{{ course.clazz }}</div>
        </div>
      </div>
    </div>
    <div class="menu-content-box">
      <div class="menu-content">
        <el-menu
            mode="horizontal"
            :default-active="$route.path"
            router
        >
          <el-menu-item :index="'/course/'+$route.params.id+'/student'">
            <el-icon><User /></el-icon>
            <span>成员</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/resource'">
            <el-icon><Folder /></el-icon>
            <span>资源</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/exam'">
            <el-icon><Timer /></el-icon>
            <span>测验</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/homework'">
            <el-icon><DocumentChecked /></el-icon>
            <span>作业</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/tag'">
            <el-icon><Share /></el-icon>
            <span>知识</span>
          </el-menu-item>
          <el-menu-item :index="'/course/'+$route.params.id+'/setting'">
            <el-icon><Setting /></el-icon>
            <span>设置</span>
          </el-menu-item>
        </el-menu>
      </div>
    </div>
  </div>
</template>

<script setup>
import { User, Folder, Timer, Setting, Share, DocumentChecked } from '@element-plus/icons-vue'
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getCourse } from '@/api'
import config from '@/config'

const route = useRoute()
const course = ref({})

onMounted(() => {
  loadCourse()
})

const loadCourse = async () => {
  const data = await getCourse({ id: route.params.id })
  course.value = data
}
</script>

<style scoped lang="scss">
.menu {
  .menu-head-box {
    background: linear-gradient(to bottom, #0E4C92, #005CA2);
    .menu-head-content {
      margin: 0px auto;
      height: 120px;
      background: linear-gradient(to bottom, #0E4C92, #005CA2);
      position: relative;
      .cover-box {
        position: absolute;
        top: 20px;
        left: 20px;
        width: 138px;
        height: 138px;
        border: 1px solid #CCC;
        border-radius: 4px;
        background-color: #FFF;
        z-index: 2;
        .cover {
          width: 132px;
          height: 132px;
          border-radius: 4px;
          margin: 3px;
        }
      }
      .name-box {
        position: absolute;
        left: 180px;
        bottom: 13px;
        .name {
          color: #FFF;
          font-size: 20px;
          margin-bottom: 5px;
        }
        .clazz {
          color: #EEE;
          font-size: 12px;
        }
      }
    }
  }
  .menu-content-box {
    .menu-content {
      padding-left: 165px;
      .el-menu {
        border: 0
      }
    }
  }
}
</style>