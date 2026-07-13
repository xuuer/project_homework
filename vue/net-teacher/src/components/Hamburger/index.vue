<template>
  <div style="padding: 0 15px;" @click="toggleClick">
    <el-icon size="20px" v-if="props.isActive"><Expand /></el-icon>
    <el-icon size="20px" v-else><Fold /></el-icon>
  </div>
</template>

<script setup>
import { Fold, Expand } from '@element-plus/icons-vue'

// 父传子
const props = defineProps({
  isActive: {
    type: Boolean,
    default: false
  }
})
// 子传父
const emit = defineEmits(['toggleClick'])

const toggleClick = () => {
  emit('toggleClick')
}
</script>

<style scoped>
.el-icon {
  display: inline-block;
  vertical-align: middle;
}
</style>
