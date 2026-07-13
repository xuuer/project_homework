<template>
  <splitpanes>
    <pane :size="20">
      <el-card class="aside-box">
        <template #header>
          <span>图谱分组</span>&nbsp;
          <el-button @click="goToCategory">管理</el-button>
        </template>
        <el-tree
          :data="categoryTree"
          :expand-on-click-node="false"
          node-key="id"
          highlight-current
          @node-click="handleNodeClick"
        />
      </el-card>
    </pane>
    <pane :size="80">
      <el-card shadow="never" class="main-box">
        <el-button type="primary" @click="addNode">添加节点</el-button>
        <el-button type="primary" @click="addRelationship">添加关系</el-button>
        <div id="main" style="width: auto; height:600px;"></div>
      </el-card>
    </pane>
  </splitpanes>
  <el-dialog v-model="nodeDialogVisible" :title="nodeId ? '编辑节点' : '创建节点'" :before-close="handleBeforeCloseNodeDialog" style="min-width: 400px;">
    <editTagNode ref="nodeForm" :id="nodeId" :categoryId="categoryId" @success="editNodeSuccess" />
  </el-dialog>

  <el-dialog v-model="relationshipDialogVisible" :title="relationshipId ? '编辑关系' : '创建关系'" :before-close="handleBeforeCloseRelationshipDialog" style="min-width: 400px;">
    <editTagRel ref="relationshipForm" :id="relationshipId" :categoryId="categoryId" :nodes="nodes" @success="editRelationshipSuccess" />
  </el-dialog>

  <el-drawer v-model="nodeDrawer" direction="rtl" size="400">
    <template #header>
      <div>
        <span>知识点详情</span>
      </div>
    </template>
    <div class="group-card">
      <el-row>知识点： {{ tag.name }}</el-row>
    </div>
    <el-button type="warning" @click="editNode(tag)">编辑</el-button>
    <el-button type="danger" @click="delTagNode(tag)">删除</el-button>
  </el-drawer>

  <el-drawer v-model="relationshipDrawer" direction="rtl" size="400">
    <template #header>
      <div>
        <span>知识点关系详情</span>
      </div>
    </template>
    <div class="group-card">
      <el-row>起始： {{ map.get(relationship.source) }}</el-row>
      <el-row>目标： {{ map.get(relationship.target) }}</el-row>
      <el-row>关系： {{ relationship.name }}</el-row>
    </div>
    <el-button type="warning" @click="editRelationship(relationship)">编辑</el-button>
    <el-button type="danger" @click="delTagRelationship(relationship)">删除</el-button>
  </el-drawer>
</template>

<script setup>
import editTagNode from './editTagNode.vue'
import editTagRel from './editTagRel.vue'
import { delNode, delRelationship, getCategoryTree, getTag } from '@/api'
import * as echarts from 'echarts'
import { ElMessageBox } from 'element-plus'
import { Pane, Splitpanes } from 'splitpanes'
import 'splitpanes/dist/splitpanes.css'
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

// 路由相关
const router = useRouter()
// 分组树列表
const categoryTree = ref([])
// 知识图谱相关
const nodes = ref([])
const links = ref([])
const tag = ref({})
const relationship = ref({})

const nodeDrawer = ref(false)
const relationshipDrawer = ref(false)

const map = ref(new Map())

// 弹出框显示
const nodeDialogVisible = ref(false)
const relationshipDialogVisible = ref(false)
// 弹出框中的表单
const nodeForm = ref()
const relationshipForm = ref()
// 弹出框中的ID
const nodeId = ref()
const relationshipId = ref()

const categoryId = ref()

onMounted(() => {
  loadCategory()
})

// 获取分组
const loadCategory = async () => {
  const data = await getCategoryTree({ type: 'taglib' })
  categoryTree.value = data
}

const loadTag = async () => {
  const data = await getTag({ categoryId: categoryId.value })
  nodes.value = data.nodes
  links.value = data.links
  let mp = new Map()
  for(let i=0;i<data.nodes.length;i++) {
    mp.set(String(data.nodes[i].id), data.nodes[i].name)
  }
  map.value = mp
  for(let i=0;i<nodes.value.length;i++) {
    if(nodes.value[i].category == "课程") {
      nodes.value[i].symbolSize = 50
    } else if(nodes.value[i].category == "章") {
      nodes.value[i].symbolSize = 40
    } else if(nodes.value[i].category == "节") {
      nodes.value[i].symbolSize = 20
    } else if(nodes.value[i].category == "知识点") {
      nodes.value[i].symbolSize = 10
    }
  }
  initChart()
}

// 分组节点单击事件
const handleNodeClick = data => {
  categoryId.value = data.id
  loadTag()
}

const initChart = () => {
  var myChart = echarts.init(document.getElementById('main'));
  var webkitDep = {
    categories: [
        {
            "name": "课程"
        },
        {
            "name": "章"
        },
        {
            "name": "节"
        },
        {
            "name": "知识点"
        }
    ],
    nodes: nodes.value,
    links: links.value
  }
  var option = {
    tooltip: {
      formatter: function(x) {
        return x.data.name
      }
    },
    legend: {
      data: ['课程', '章', '节', '知识点']
    },
    series: [
      {
        type: 'graph',
        layout: 'force', // 力引导图
        force: {
          edgeLength: 100, // 边的两个节点的距离，值越大，长度越长
          repulsion: 400, // 斥力因子，值越大，斥力越大 
          gravity: 0.1 // 向中心引力因子，值越大，越往中心靠拢
        },
        edgeSymbol: ['circle', 'arrow'],
        edgeSymbolSize: [4, 10],
        edgeLabel: { // 链接边标签
          show: true,
          fontSize: 12,
          formatter: "{b}", // 连接线文字展示
          formatter: function(x) { // 通过回调函数设置连线上的标签
            return x.data.name
          }
        },
        label: {
          show: true,
          position: 'right',
          fontSize: 15
        },
        symbolSize: 60,
        draggable: true,
        roam: true,
        categories: webkitDep.categories,
        data: webkitDep.nodes,
        links: webkitDep.links.map(function (link, idx) {
          link.source = String(link.source)
          link.target = String(link.target)
          return link
        })
      }
    ]
  };

  myChart.on("click", function(param) {
    if(param.dataType == "node") {
      tag.value = param.data
      nodeDrawer.value = true
    } else if(param.dataType == "edge") {
      relationship.value = param.data
      relationshipDrawer.value = true
    }
  })

  myChart.setOption(option);
}

// 添加知识点
const addNode = () => {
  if (nodeForm.value) {
    nodeForm.value.resetForm(0)
  }
  nodeId.value = 0
  nodeDialogVisible.value = true
}

// 修改知识点
const editNode = row => {
  if (nodeForm.value) {
    nodeForm.value.resetForm(row.id)
  }
  nodeId.value = row.id
  nodeDialogVisible.value = true
}

// 编辑知识点完成
const editNodeSuccess = () => {
  loadTag()
  nodeDialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeCloseNodeDialog = () => {
  nodeDialogVisible.value = false
  setTimeout(() => {
    nodeForm.value.resetForm()
  }, 500)
}

// 删除知识点
const delTagNode = row => {
  ElMessageBox.confirm('确定要删除此知识点吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delNode({ id: row.id })) {
      loadTag()
    }
  }).catch(() => {})
}

// 添加关系
const addRelationship = () => {
  if (relationshipForm.value) {
    relationshipForm.value.resetForm(0)
  }
  relationshipId.value = 0
  relationshipDialogVisible.value = true
}

// 修改关系
const editRelationship = row => {
  if (relationshipForm.value) {
    relationshipForm.value.resetForm(row.id)
  }
  relationshipId.value = row.id
  relationshipDialogVisible.value = true
}

// 编辑关系完成
const editRelationshipSuccess = () => {
  loadTag()
  relationshipDialogVisible.value = false
}

// 关闭弹出框前
const handleBeforeCloseRelationshipDialog = () => {
  relationshipDialogVisible.value = false
  setTimeout(() => {
    relationshipForm.value.resetForm()
  }, 500)
}

// 删除关系
const delTagRelationship = row => {
  ElMessageBox.confirm('确定要删除此关系吗？', {
    closeOnClickModal: false,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  }).then(async () => {
    if (await delRelationship({ id: row.id })) {
      loadTag()
    }
  }).catch(() => {})
}

// 跳转到分组管理界面
const goToCategory = () => {
  router.push({ name: 'category', params: { type: 'taglib' } })
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
.list-item {
  cursor: pointer;
  margin-bottom: 10px;
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
    .opr {
      width: 80px;
      margin-left: auto;
    }
  }
}
.list-item:hover {
  border: 1px solid var(--el-color-warning);
}
.group-card {
  line-height: 28px;
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 5px;
  .group-card-font {
    color: #1890FF;
    font-style: italic;
  }
}
</style>