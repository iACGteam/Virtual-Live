const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  lintOnSave: false,
  devServer: {
    port: 8080,
    proxy: {
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        // 将 /api 前缀转发给后端，保持路径不变
        // 若后端不需要 /api 前缀，可开启 pathRewrite
        // pathRewrite: { '^/api': '' },
      },
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true,
      }
    }
  }
})
