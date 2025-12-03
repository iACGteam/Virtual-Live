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
  background: #1c1c1c;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  z-index: 1000;
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
