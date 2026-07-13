<template>
  <el-card>
    <!-- 操作按钮 -->
    <el-button @click="$router.go(-1)">返回</el-button>
    <el-button type="primary" @click="addRow" :icon="Plus">添加分组</el-button>
    <!-- 树形分组列表 -->
    <el-table
      :data="categoryTree"
      row-key="id"
      style="width: 100%; margin-bottom: 20px"
    >
      <el-table-column prop="label" label="分组名" />
      <el-table-column fixed="right" label="操作" width="160">
        <template #default="{ row }"> 
          <el-button type="warning" @click="editRow(row)" plain>修改</el-button>
          <el-button type="danger" @click="delRow(row)" plain>删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
  <!-- 编辑分组的弹出框 -->
  <el-dialog v-model="dialogVisible" :title="id ? '修改分组' : '添加分组'" :before-close="handleBeforeClose" style="min-width: 400px;">
    <editCat ref="categoryForm" :id="id" :type="$route.params.type" :tree="categoryTree" @success="editSuccess" />
  </el-dialog>
</template>

<script setup>
import editCat from './editCat.vue'
import { delCategory, getCategoryTree } from '@/api'
import { Plus } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 分组树列表
const categoryTree = ref([])
// 弹出框显示
const dialogVisible = ref(false)
// 弹出框中的表单
const categoryForm = ref()
// 弹出框中分组的ID
const id = ref()

onMounted(() => {
  loadCategoryTree()
})

// 获取分组
const loadCategoryTree = async () => {
  const params = {
    type: route.params.type
  }
  const data = await getCategoryTree(params)
  categoryTree.value = data
}

// 添加分组
const addRow = () => {
  if (categoryForm.value) {
    categoryForm.value.resetForm(0)
  }
  id.value = 0
  dialogVisible.value = true
}

// 修改分组
const editRow = row => {
  if (categoryForm.value) {
    categoryForm.value.resetForm(row.id)
  }
  id.value = row.id
  dialogVisible.value = true
}

// 添加完成
const editSuccess = () => {
  loadCategoryTree()
  dialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeClose = () => {
  dialogVisible.value = false
  setTimeout(() => {
    categoryForm.value.resetForm()
  }, 500)
}

// 删除分组
const delRow = row => {
  ElMessageBox.confirm('确定要删除此分组吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delCategory({ id: row.id })) {
      loadCategoryTree()
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
</style>