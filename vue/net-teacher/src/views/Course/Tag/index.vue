<template>
  <div id="main" style="width: auto; height:600px;"></div>

  <el-drawer v-model="nodeDrawer" direction="rtl" size="400">
    <template #header>
      <div>
        <span>知识点详情</span>
      </div>
    </template>
    <div class="group-card">
      <el-row>知识点： {{ tag.name }}</el-row>
    </div>
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
  </el-drawer>
</template>
  
<script setup>
import { getCourseTag } from '@/api'
import * as echarts from 'echarts'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()

const nodes = ref([])
const links = ref([])
const tag = ref({})
const relationship = ref({})

const nodeDrawer = ref(false)
const relationshipDrawer = ref(false)

const map = ref(new Map())

onMounted(() => {
  loadTag()
})

const loadTag = async () => {
  const data = await getCourseTag({ id: route.params.id })
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
    title: {
      text: '课程知识图谱',
    },
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
        }),
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
</style>