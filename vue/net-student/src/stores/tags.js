import { defineStore } from 'pinia'
import { ref } from 'vue'

const useTags = defineStore('tags', () => {
  const tags = ref([])
  const updateTags = val => tags.value = val
  const removeTags = () => tags.value = null
  return { tags, updateTags, removeTags }
}, {
  persist: {
    enabled: true,
    strategies: [
      {
        key: 'tags',
        storage: localStorage
      }
    ]
  }
})
export default useTags