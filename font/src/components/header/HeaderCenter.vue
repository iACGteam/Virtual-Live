<template>
  <div class="header-center">
    <el-menu
      :default-active="activeMenu"
      mode="horizontal"
      class="center-menu"
      @select="handleMenuSelect"
    >
      <el-menu-item index="home">
        <el-icon><House /></el-icon> <span>首页</span>
      </el-menu-item>

      <el-menu-item index="live">
        <el-icon><VideoPlay /></el-icon> <span>直播</span>
      </el-menu-item>

      <el-menu-item index="community">
        <el-icon><ChatDotRound /></el-icon> <span>社区</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { House, VideoPlay, ChatDotRound } from '@element-plus/icons-vue'

const router = useRouter()
const activeMenu = ref('home')

// 打开 SearchDialog
// const openSearch = () => {
//   bus.emit('open-search')
// }

const handleMenuSelect = (index) => {
  if (index !== 'search') {
    activeMenu.value = index
    router.push(`/${index}`)
  }
}
</script>

<style scoped>
.header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}
.center-menu {
  border-bottom: none;
}
.search-menu-item {
  cursor: pointer !important;
}
</style>
