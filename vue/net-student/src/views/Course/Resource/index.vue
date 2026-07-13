<template>
  <div>
    <!-- 分组列表 -->
    <el-table :data="category" default-expand-all>
      <el-table-column type="expand">
        <template #default="{ row }">
          <!-- 资源列表 -->
          <el-card shadow="hover" class="list-item" v-for="res in row.children" @click="preview(res)">
            <div class="item-container">
              <div class="icon">
                <el-image src="/images/icon-res.png" fit="cover" />
              </div>
              <div>{{ res.name }}</div>
              <div class="opr">
                <el-button type="success" :icon="Download" circle @click.stop="downloadResource(res)"/>
              </div>
            </div>
          </el-card>
        </template>
      </el-table-column>
      <el-table-column label="" prop="name" />
    </el-table>
  </div>
</template>
  
<script setup>
import { downResource, getCourseResourceList, previewResource } from '@/api'
import { Download } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import config from "@/config.js";

// 路由相关
const route = useRoute()
// 数据列表
const category = ref([])

onMounted(() => {
  loadResourceList()
})

// 获取资源列表
const loadResourceList = async () => {
  const data = await getCourseResourceList({ id: route.params.id })
  category.value = data
}

// 下载资源
// const downloadResource = async row => {
//   await downResource({ id: row.id })
// }
const downloadResource = async (attachment) => {
  console.log('111',config.resourceURL + attachment.url)
  try {
    const link = document.createElement('a')
    link.href = config.resourceURL + attachment.url
    link.download = attachment.name
    link.click()
  } catch (error) {
    ElMessage.error('文件下载失败')
  }
}
// 预览资源
const preview = async row => {
  const url = await previewResource({ id: row.id })
  window.open(url)
}
</script>
  
<style lang="scss" scoped>
.list-item {
  cursor: pointer;
  margin-bottom: 10px;
  .item-container {
    display: flex;
    flex-direction: row;
    width: 100%;
    .icon {
      width: 70px;
      .el-image {
        width: 60px;
        height: 60px;
      }
    }
    .opr {
      width: 40px;
      margin-left: auto;
    }
  }
}
</style>