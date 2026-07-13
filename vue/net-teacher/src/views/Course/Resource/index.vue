<template>
  <div>
    <!-- 操作按钮 -->
    <el-button type="primary" plain :icon="Plus" @click="addCategory">添加分组</el-button>
    <!-- 资源列表 -->
    <el-table :data="dataSource" default-expand-all>
      <el-table-column type="expand">
        <template #default="{ row }">
          <div>
            <el-card shadow="hover" class="list-item" v-for="res in row.children" @click="preview(res)">
              <div class="item-container">
                <div class="icon">
                  <el-image src="/images/icon-res.png" fit="cover" />
                </div>
                <div>{{ res.name }}</div>
                <div class="opr">
                  <el-button type="success" :icon="Download" circle @click.stop="downloadResource(res)"/>
                  <el-button type="danger" :icon="Delete" circle @click.stop="removeResource(row.id, res.id)"/>
                </div>
              </div>
            </el-card>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="" prop="name" />
      <el-table-column fixed="right" label="" width="160">
        <template #default="{ row }">
          <el-link type="primary" circle @click="addResource(row.id)">添加资源</el-link>
          <el-link type="warning" circle @click="editCategory(row.id)">编辑</el-link>
          <el-link type="danger" circle @click="removeCategory(row.id)">删除</el-link>
        </template>
      </el-table-column>
    </el-table>
  </div>
  <!-- 弹出框 -->
  <el-dialog v-model="categoryDialogVisible" :title="categoryId ? '修改目录' : '添加目录'" :before-close="handleBeforeCloseCategoryDialog" style="min-width: 400px;">
    <editCat ref="categoryForm" :id="categoryId" :courseId="$route.params.id" @success="addCategorySuccess" />
  </el-dialog>
  <el-dialog v-model="resourceDialogVisible" title="添加资源" :before-close="handleBeforeCloseResourceDialog" style="min-width: 400px;">
    <addRes ref="resourceForm" :categoryId="categoryId" @success="addResourceSuccess" />
  </el-dialog>
</template>
  
<script setup>
import addRes from './addRes.vue'
import editCat from './editCat.vue'
import { delCategory, delCategoryResource, downResource, getCourseResourceList, previewResource } from '@/api'
import { Plus, Delete, Download } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'
import config from "@/config.js";

// 路由相关
const route = useRoute()
// 数据列表
const dataSource = ref([])
// 弹出框显示
const categoryDialogVisible = ref(false)
const resourceDialogVisible = ref(false)
// 弹出框中的表单
const categoryForm = ref()
const resourceForm = ref()
// 弹出框中的分组ID
const categoryId = ref()

onMounted(() => {
  loadResourceList()
})

// 获取目录
const loadResourceList = async () => {
  const data = await getCourseResourceList({ id: route.params.id })
  dataSource.value = data
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
// 添加目录
const addCategory = () => {
  if (categoryForm.value) {
    categoryForm.value.resetForm(0)
  }
  categoryId.value = 0
  categoryDialogVisible.value = true
}

// 修改目录
const editCategory = id => {
  if (categoryForm.value) {
    categoryForm.value.resetForm(id)
  }
  categoryId.value = id
  categoryDialogVisible.value = true
}

// 添加资源
const addResource = id => {
  if (resourceForm.value) {
    resourceForm.value.resetForm()
  }
  categoryId.value = id
  resourceDialogVisible.value = true
}

// 添加完成
const addCategorySuccess = () => {
  loadResourceList()
  categoryDialogVisible.value = false
}

const addResourceSuccess = () => {
  loadResourceList()
  resourceDialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeCloseCategoryDialog  = () => {
  categoryDialogVisible.value = false
  setTimeout(() => {
    categoryForm.value.resetForm()
  }, 500)
}

const handleBeforeCloseResourceDialog  = () => {
  resourceDialogVisible.value = false
  setTimeout(() => {
    resourceForm.value.resetForm()
  }, 500)
}

// 删除目录
const removeCategory = id => {
  ElMessageBox.confirm('确定要删除此分组吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delCategory({ id: id })) {
      loadResourceList()
    }
  }).catch(() => {})
}

// 删除资源
const removeResource = (categoryId, resourceId) => {
  ElMessageBox.confirm('确定要删除此资源吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delCategoryResource({ id: categoryId, resourceId: resourceId })) {
      loadResourceList()
    }
  }).catch(() => {})
}

// 预览资源
const preview = async row => {
  const url = await previewResource({ id: row.id })
  window.open(url)
}
</script>
  
<style lang="scss" scoped>
.el-link {
  margin-right: 8px;
}
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
      width: 80px;
      margin-left: auto;
    }
  }
}
</style>