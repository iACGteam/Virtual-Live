<template>
  <div class="complex-header">
    <div class="header-container">
      <HeaderLeft />
      <HeaderCenter />
      <HeaderRight />
    </div>

    <!-- 搜索对话框（独立组件） -->
    <SearchDialog />
  </div>
</template>

<script setup>
import HeaderLeft from './HeaderLeft.vue'
import HeaderCenter from './HeaderCenter.vue'
import HeaderRight from './HeaderRight.vue'
</script>

<style scoped>
.complex-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 60px;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 240, 248, 0.95) 100%);
  backdrop-filter: blur(20px);
  box-shadow: 0 2px 20px rgba(255, 105, 180, 0.15), 0 1px 8px rgba(147, 112, 219, 0.1);
  z-index: 1000;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
}

.header-container {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
</style>
