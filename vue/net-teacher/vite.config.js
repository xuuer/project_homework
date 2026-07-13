import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  // plugins: [vue()],
  // define: {
  //   // 启用 hydration 不匹配的详细警告
  //   __VUE_PROD_HYDRATION_MISMATCH_DETAILS__: 'true',
  //   // 其他 Vue 特性标志（可选）
  //   __VUE_OPTIONS_API__: 'true',      // 是否支持 Options API（默认 true）
  //   __VUE_PROD_DEVTOOLS__: 'false',   // 是否在生产环境支持 DevTools（默认 false）
  // },

  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 后端API服务器地址
        changeOrigin: true, // 是否改变源地址
        rewrite: (path) => path.replace(/^\/api/, '') // 重写路径
      }
    }
  },
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      directoryAsNamespace: true, // 允许子目录作为组件命名空间的前缀
    })
  ],
  compilerOptions: {
    // ...
    types: ['element-plus/global'],
  },
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)) // 路径转换
    }
  }
});
