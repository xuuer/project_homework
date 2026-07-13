import { defineStore } from 'pinia'
import { reactive } from 'vue'

const useSetting = defineStore('setting', () => {
    const defaultSetting = {
      collapseSideBar: false
    }
    const setting = reactive(Object.assign({}, defaultSetting))
    const updateSetting = options => {
      Object.assign(setting, options)
      return setting
    }
    const removeSetting = () => {
      Object.assign(setting, defaultSetting)
      return setting
    }
    return { setting, updateSetting, removeSetting }
  }, {
    persist: {
      enabled: true,
      strategies: [
        {
          key: 'setting',
          storage: localStorage,
        }
      ]
    }
  }
)
export default useSetting