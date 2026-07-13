<template>
  <div>知识点分析</div>
  <div id="main" style="width: auto; height:600px;"></div>

  <el-drawer v-model="drawer" direction="rtl" size="400">
    <template #header>
      <div>
        <span>知识点详情</span>
      </div>
    </template>
    <div class="group-card">
      <el-row>
        <el-progress type="dashboard" :percentage="tag.value" :color="colors" style="margin: 0 auto;">
          <template #default="{ percentage }">
            <span class="percentage-value">{{ percentage }}%</span>
            <span class="percentage-label">掌握度</span>
          </template>
        </el-progress>
      </el-row>
      <el-row>
        <div style="margin: 0 auto;">
          {{ tag.name }}
        </div>
      </el-row>
    </div>
    <template #footer>
      <div class="footer">
        <el-button type="primary" style="width: 100%;" @click="goToPractice(tag)">去练习</el-button>
      </div>
    </template>
  </el-drawer>
</template>
  
<script setup>
import { getTagGraph } from '@/api'
import * as echarts from 'echarts'
import { onMounted, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'

// 路由相关
const router = useRouter()
const route = useRoute()

// 知识图谱相关
const nodes = ref([]) // 节点集合
const links = ref([]) // 边集合
const tag = ref({}) // 点击选中的知识点

// 抽屉组件显示
const drawer = ref(false)

const colors = [
  { color: '#f56c6c', percentage: 0 },
  { color: '#f56c6c', percentage: 60 },
  { color: '#e6a23c', percentage: 85 },
  { color: '#5cb87a', percentage: 100 },
]

onMounted(() => {
  loadTagGraph()
})

// 获取知识图谱
const loadTagGraph = async () => {
  const data = await getTagGraph({ id: route.params.id })
  nodes.value = data.nodes
  links.value = data.links
  initChart()
}

// 初始化图表
const initChart = () => {
  var myChart = echarts.init(document.getElementById('main'))
  var option = {
    // 鼠标悬浮时的提示框
    tooltip: {
      formatter: function(x) {
        return x.data.name
      }
    },
    // 图例
    legend: {
      data: ['未学', '优秀', '良好', '薄弱']
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
        edgeSymbol: ['circle', 'arrow'], // 边两端的标记类型
        edgeSymbolSize: [4, 10], // 边两端标记的大小
        edgeLabel: { // 边标签
          show: true,
          fontSize: 12,
          formatter: function(x) { // 通过回调函数设置连线上的标签
            return x.data.name
          }
        },
        label: {
          show: true,
          position: 'right',
          fontSize: 15
        },
        roam: true, // 是否允许缩放和平移
        draggable: true, // 是否允许拖拽节点
        symbolSize: 30, // 节点图形的大小
        categories: [
          { "name": "未学" },
          { "name": "优秀" },
          { "name": "良好" },
          { "name": "薄弱" }
        ],
        data: nodes.value,
        links: links.value.map(function (link, idx) {
          link.source = String(link.source)
          link.target = String(link.target)
          return link
        }),
      }
    ]
  }

  // 点击节点时，在抽屉组件中显示当前点击的节点详情
  myChart.on("click", function(param) {
    if(param.dataType == "node") {
      tag.value = param.data
      drawer.value = true
    }
  })

  myChart.setOption(option)
}

// 跳转到练习页面
const goToPractice = (tag) => {
  router.push({ name: 'practice', params: { id: route.params.id, tagId: tag.id } })
}
</script>
  
<style lang="scss" scoped>
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
.percentage-value {
  display: block;
  margin-top: 10px;
  font-size: 28px;
}
.percentage-label {
  display: block;
  margin-top: 10px;
  font-size: 12px;
}
.footer {
  text-align: center;
}
</style>