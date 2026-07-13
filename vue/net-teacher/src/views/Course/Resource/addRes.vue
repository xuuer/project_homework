<template>
  <div>
    <!-- 查询表单 -->
    <el-form :model="form" label-width="auto" ref="formRef" inline>
      <el-form-item prop="name">
        <el-input v-model="form.name" style="max-width:250px;" placeholder="资源名"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadResourceList" :icon="Search">查询</el-button>
        <el-button type="primary" @click="addSubmit" :icon="Plus">添加</el-button>
      </el-form-item>
    </el-form>
    <el-alert type="info" show-icon :closable="false">
      <template #title>
        <span v-if="multipleSelection.length === 0">未选中任何数据</span>
        <span v-else>已选中 {{ multipleSelection.length }} 条记录(可跨页)</span>
      </template>
    </el-alert>
    <!-- 资源列表 -->
    <el-table
      :data="resourceList"
      @selection-change="handleSelectionChange"
      style="margin-bottom: 20px;"
      row-key="id"
    >
      <el-table-column type="selection" width="55" :reserve-selection="true" />
      <el-table-column prop="name" label="资源名" />
      <el-table-column prop="tags" label="知识点">
        <template #default="{ row }">
          <el-space wrap>
            <el-tag v-for="tag in row.tags" :key="tag">{{ tag }}</el-tag>
          </el-space>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="pageNum"
      background layout="total, prev, pager, next, jumper"
      :total="total"
      :page-size="pageSize"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { addCategoryResource, getResourceList } from '@/api'
import { Plus, Search } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'

// 父传子
const props = defineProps({
  categoryId: {
    type: Number
  }
})
// 子传父
const emit = defineEmits(['success'])

// 当前页资源列表
const resourceList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单
const form = ref({
  categoryId: props.categoryId,
  name: ''
})
const formRef = ref()

onMounted(() => {
  loadResourceList()
})

// 获取资源
const loadResourceList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: form.value.name
  }
  const data = await getResourceList(params)
  resourceList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadResourceList()
}

// 多选
const multipleSelection = ref([])
const handleSelectionChange = val => {
  multipleSelection.value = val
}

// 添加资源
const addSubmit = async () => {
  const resourceIds = multipleSelection.value.map(item=>item.id)
  if (await addCategoryResource({ id: form.value.categoryId, resourceIds: resourceIds })) {
    emit('success')
  }
}

// 重置表单
const resetForm = () => {
  formRef.value.resetFields()
}
    
defineExpose({ resetForm })
</script>

<style lang="scss" scoped>
</style>