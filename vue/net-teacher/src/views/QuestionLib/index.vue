<template>
  <splitpanes>
    <pane :size="20">
      <el-card shadow="never" class="aside-box">
        <template #header>
          <span>题目分组</span>&nbsp;
          <el-button @click="goToCategory">管理</el-button>
        </template>
        <el-tree
          :data="treeData"
          :expand-on-click-node="false"
          node-key="id"
          highlight-current
          @node-click="handleNodeClick"
        />
      </el-card>
    </pane>
    <pane :size="80">
      <el-card shadow="never" class="main-box">
        <!-- 查询表单 -->
        <el-form :model="form" label-width="auto" ref="formRef" inline>
          <el-form-item label="题干" prop="content">
            <el-input v-model="form.content" style="max-width:250px;" placeholder="请输入题干"/>
          </el-form-item>
          <el-form-item label="难度" prop="difficulty">
            <el-select v-model="form.difficulty" placeholder="难度" style="width:150px;">
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
          </el-form-item>
          <el-form-item label="题型" prop="type">
            <el-select v-model="form.type" placeholder="题型" style="width:150px;">
              <el-option label="单选题" value="单选题" />
              <el-option label="多选题" value="多选题" />
              <el-option label="判断题" value="判断题" />
              <el-option label="综合题" value="综合题" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadQuestionList" :icon="Search">搜索</el-button>
            <el-button type="primary" @click="addRow" :icon="Plus" plain>创建题目</el-button>
            <el-button type="danger" @click="delMultipleRow" :icon="Delete" :disabled="multipleSelection.length === 0">批量删除</el-button>
          </el-form-item>
        </el-form>
        <el-alert type="info" show-icon :closable="false">
          <template #title>
            <span v-if="multipleSelection.length === 0">未选中任何数据</span>
            <span v-else>已选中 {{ multipleSelection.length }} 条记录(可跨页)</span>
          </template>
        </el-alert>
        <!-- 试题列表 -->
        <el-table
          :data="questionList"
          row-key="id"
          @selection-change="handleSelectionChange"
        >
          <el-table-column type="selection" width="55" :reserve-selection="true" />
          <el-table-column prop="content" label="题干" min-width="200">
            <template #default="scope">
              <div class="ellipsis" v-html="scope.row.content"></div>
            </template>
          </el-table-column>
          <el-table-column prop="type" label="类型" min-width="50" />
          <el-table-column prop="difficulty" label="难度" min-width="50" />
          <el-table-column fixed="right" label="操作" width="135">
            <template #default="scope">
              <el-link type="primary" @click="detail(scope.row)">详情</el-link>
              <el-link type="warning" @click="editRow(scope.row)">编辑</el-link>
              <el-link type="danger" @click="delRow(scope.row)">删除</el-link>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-model:current-page="pageNum"
          background layout="total, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          @current-change="handleCurrentChange"
          style="margin-top: 20px;"
        />
      </el-card>
    </pane>
  </splitpanes>
</template>

<script setup>
import { delQuestion, getCategoryTree, getQuestionList } from '@/api'
import { Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { Pane, Splitpanes } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 分组树列表
const treeData = ref([])
// 题目列表
const questionList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单相关
const form = ref({
  categoryId: '',
  content: '',
  difficulty: '',
  type: ''
})
const formRef = ref()
// 多选列表
const multipleSelection = ref([])

onMounted(() => {
  loadCategory()
  loadQuestionList()
})

// 获取分组
const loadCategory = async () => {
  const data = await getCategoryTree({ type: 'questionlib' })
  treeData.value = data
}

// 获取试题
const loadQuestionList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    content: form.value.content,
    categoryId: form.value.categoryId,
    difficulty: form.value.difficulty,
    type: form.value.type
  }
  const data = await getQuestionList(params)
  questionList.value = data.list
  total.value = data.total
}

// 换页
const handleCurrentChange = value => {
  pageNum.value = value
  loadQuestionList()
}

// 分组节点单击事件
const handleNodeClick = data => {
  form.value.categoryId = data.id
  loadQuestionList()
}

// 管理分组
const goToCategory = () => {
  router.push({ name: 'category', params: { type: 'questionlib' } })
}

// 添加题目
const addRow = () => {
  router.push({ name: 'question_add' })
}

// 修改题目
const editRow = row => {
  router.push({ name: 'question_edit', params: { id: row.id } })
}

// 题目详情
const detail = row => {
  router.push({ name: 'question_detail', params: { id: row.id } })
}

// 多选
const handleSelectionChange = val => {
  multipleSelection.value = val
}

// 删除题目
const delRow = row => {
  ElMessageBox.confirm('确定要删除此题目吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delQuestion({ ids: [ row.id ] })) {
      loadQuestionList()
    }
  }).catch(() => {})
}

// 批量删除
const delMultipleRow = () => {
  ElMessageBox.confirm('确定要删除选中的题目吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delQuestion({ ids: multipleSelection.value.map(item=>item.id) })) {
      multipleSelection.value = []
      loadQuestionList()
    }
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.el-link {
  margin-right: 8px;
}
.aside-box {
  height: calc(100vh - 52px);
  overflow: auto;
}
.main-box {
  height: calc(100vh - 52px);
  overflow: auto;
  .ellipsis {
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
}
</style>