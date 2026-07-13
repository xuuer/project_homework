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
      <el-row>知识点： {{ tag.name }}</el-row>
      <el-row>
        <el-progress type="dashboard" :percentage="tag.value" :color="colors" style="margin: 0 auto;">
          <template #default="{ percentage }">
            <span class="percentage-value">{{ percentage }}%</span>
            <span class="percentage-label">掌握度</span>
          </template>
        </el-progress>
      </el-row>
    </div>
  </el-drawer>
</template>
  
<script setup>
import { getCourseStudentTag } from '@/api'
import * as echarts from 'echarts'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()

const nodes = ref([])
const links = ref([])
const tag = ref({})

const drawer = ref(false)

const colors = [
  { color: '#f56c6c', percentage: 0 },
  { color: '#f56c6c', percentage: 60 },
  { color: '#e6a23c', percentage: 85 },
  { color: '#5cb87a', percentage: 100 },
]

onMounted(() => {
  loadTag()
})

const loadTag = async () => {
  const data = await getCourseStudentTag({ id: route.params.id, studentId: route.params.studentId })
  nodes.value = data.nodes
  links.value = data.links
  initChart()
}

const initChart = () => {
  var myChart = echarts.init(document.getElementById('main'));
  var webkitDep = {
    categories: [
        {
            "name": "未学"
        },
        {
            "name": "优秀"
        },
        {
            "name": "良好"
        },
        {
            "name": "薄弱"
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
        draggable: true,
        roam: true,
        symbolSize: 30,
        categories: webkitDep.categories,
        data: webkitDep.nodes,
        links: webkitDep.links.map(function (link, idx) {
          link.source = String(link.source)
          link.target = String(link.target)
          return link
        }),
      }
    ]
  };

  myChart.on("click", function(param) {
    if(param.dataType == "node") {
      console.log(param)
      tag.value = param.data
      drawer.value = true
    }
  })

  myChart.setOption(option);
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
</style>