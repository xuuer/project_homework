<template>
  <el-card shadow="never">
    <el-row>
      <el-col :span="8">
        <el-statistic title="最高分" :value="stat.maxScore">
          <template #suffix>
            <el-icon style="vertical-align: -0.125em">
              <Top />
            </el-icon>
          </template>
        </el-statistic>
      </el-col>
      <el-col :span="8">
        <el-statistic title="平均分" :value="stat.avgScore">
          <template #suffix>
            <el-icon style="vertical-align: -0.125em">
              <Switch />
            </el-icon>
          </template>
        </el-statistic>
      </el-col>
      <el-col :span="8">
        <el-statistic title="最低分" :value="stat.minScore">
          <template #suffix>
            <el-icon style="vertical-align: -0.125em">
              <Bottom />
            </el-icon>
          </template>
        </el-statistic>
      </el-col>
    </el-row>
  </el-card>
</template>

<script setup>
import { getExamStatistics } from '@/api'
import { Bottom, Switch, Top } from '@element-plus/icons-vue'
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

// 路由相关
const route = useRoute()
// 统计数据
const stat = ref({
  maxScore: '',
  minScore: '',
  avgScore: ''
})

onMounted(() => {
  loadStatistics()
})

// 获取统计数据
const loadStatistics = async () => {
  const data = await getExamStatistics({ id: route.params.id })
  stat.value = data
}
</script>
  
<style lang="scss" scoped>
.el-col {
  text-align: center;
}
</style>