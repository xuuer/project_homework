<template>
  <splitpanes>
    <pane :size="20">
      <el-card shadow="never" class="aside-box">
        <template #header>
          <span>资源分组</span>&nbsp;
          <el-button @click="goToCategory">管理</el-button>
        </template>
        <el-tree :data="categoryTree" :expand-on-click-node="false" node-key="id" highlight-current
          @node-click="handleNodeClick" />
      </el-card>
    </pane>
    <pane :size="80">
      <el-card shadow="never" class="main-box">
        <!-- 查询表单 -->
        <el-form :model="form" label-width="auto" ref="formRef" inline>
          <el-form-item label="资源名" prop="name">
            <el-input v-model="form.name" style="max-width:250px;" placeholder="请输入资源名" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadResourceList" :icon="Search">搜索</el-button>
            <el-button type="primary" @click="addRow" :icon="Plus" plain>上传资源</el-button>
          </el-form-item>
        </el-form>
        <!-- 资源列表 -->
        <div>
          <el-empty v-if="!resourceList.length" description="暂无数据" />
          <el-card shadow="hover" class="list-item" v-for="res in resourceList" @click="preview(res)">
            <div class="item-container">
              <div class="icon">
                <el-image src="/images/icon-res.png" fit="cover" />
              </div>
              <div class="name-box">
                <div class="name">{{ res.name }}</div>
                <div class="time">{{ res.createTime }}</div>
              </div>
              <div class="opr">
                <el-tooltip content="下载" placement="bottom">
                  <el-button type="success" :icon="Download" circle @click.stop="downloadFile(res)" />
                </el-tooltip>
                <el-tooltip content="编辑" placement="bottom">
                  <el-button type="warning" :icon="Edit" circle @click.stop="editRow(res)" />
                </el-tooltip>
                <el-tooltip content="删除" placement="bottom">
                  <el-button type="danger" :icon="Delete" circle @click.stop="delRow(res)" />
                </el-tooltip>
              </div>
            </div>
          </el-card>
        </div>
        <div>
          你好
        </div>
        <div style="margin: 10px 0">总记录: {{ total }}, 每页: {{ pageSize }}, 当前页: {{ pageNum }}</div>
        <el-pagination v-model:current-page="pageNum" background layout="total, prev, pager, next, jumper"
          :total="total" :page-size="pageSize" @current-change="handleCurrentChange" />
      </el-card>
    </pane>
  </splitpanes>
  <!-- 添加资源的弹出框 -->
  <el-dialog v-model="dialogVisible" :title="id ? '修改资源信息' : '上传资源'" :before-close="handleBeforeClose"
    style="min-width: 400px;">
    <editRes ref="resourceForm" :id="id" :categoryTree="categoryTree" @success="addSuccess" />
  </el-dialog>
</template>

<script setup>
import editRes from './editRes.vue'
import { delResource, downResource, getCategoryTree, getResourceList, previewResource } from '@/api'
import { Delete, Download, Edit, Plus, Search } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'
import { Pane, Splitpanes } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import config from '@/config'
// 路由相关
const router = useRouter()
// 分组树列表
const categoryTree = ref([])
// 当前页资源列表
const resourceList = ref([])
// 分页相关
const pageNum = ref(1)
const pageSize = ref(10)
const total = ref(0)
// 查询表单
const form = ref({
  name: '',
  categoryId: ''
})
const formRef = ref()
// 弹出框相关
const dialogVisible = ref(false)
const resourceForm = ref()
const id = ref()

onMounted(() => {
  loadCategory()
  loadResourceList()
  console.log("resList,{}", resourceList);

})

// 获取分组
const loadCategory = async () => {
  const data = await getCategoryTree({ type: 'resourcelib' })
  categoryTree.value = data
}

// 获取资源
const loadResourceList = async () => {
  const params = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    name: form.value.name,
    categoryId: form.value.categoryId
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

// 分组节点单击事件
const handleNodeClick = data => {
  form.value.categoryId = data.id
  loadResourceList()
}

// 添加资源
const addRow = () => {
  if (resourceForm.value) {
    resourceForm.value.resetForm(0)
  }
  id.value = 0
  dialogVisible.value = true
}

// 修改资源
const editRow = row => {
  if (resourceForm.value) {
    resourceForm.value.resetForm(row.id)
  }
  id.value = row.id
  dialogVisible.value = true
}

// 添加完成
const addSuccess = () => {
  loadResourceList()
  dialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeClose = () => {
  ElMessageBox.confirm('确定关闭对话框吗？', {
    showClose: false,
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(() => {
    dialogVisible.value = false
    setTimeout(() => {
      resourceForm.value.resetForm()
    }, 500)
  }).catch(() => { })
}

// 下载资源
// const download = async row => {
//   await downResource({ id: row.id })
// }
// 下载文件
const downloadFile = async (attachment) => {
  console.log('111', config.resourceURL + attachment.url)
  try {
    const link = document.createElement('a')
    link.href = config.resourceURL + attachment.url
    link.download = attachment.name
    link.click()
  } catch (error) {
    ElMessage.error('文件下载失败')
  }
}

// 删除资源
const delRow = row => {
  ElMessageBox.confirm('确定要删除此资源吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delResource({ id: row.id })) {
      loadResourceList()
    }
  }).catch(() => { })
}

// 跳转到分组管理界面
const goToCategory = () => {
  router.push({ name: 'category', params: { type: 'resourcelib' } })
}

// 预览资源
const preview = async row => {
  const url = await previewResource({ id: row.id })
  window.open(url)
}
</script>

<style lang="scss" scoped>
.aside-box {
  height: calc(100vh - 52px);
  overflow: auto;
}

.main-box {
  height: calc(100vh - 52px);
  overflow: auto;
}

.list-item:hover {
  background-color: var(--el-menu-hover-bg-color);
}

.list-item {
  cursor: pointer;
  margin-bottom: 20px;

  .item-container {
    display: flex;
    flex-direction: row;

    .icon {
      width: 70px;

      .el-image {
        width: 60px;
        height: 60px;
      }
    }

    .name-box {
      .name {
        margin-bottom: 10px;
      }

      .time {
        color: #737373;
        font-size: 13px;
      }
    }

    .opr {
      width: 120px;
      margin-left: auto;
    }
  }
}
</style>