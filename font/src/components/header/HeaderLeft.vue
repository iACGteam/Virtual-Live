<template>
  <div class="header-left">
    <div class="logo" @click="goHome">
      <!-- <img src="@/assets/logo.png" /> -->
      <span class="site-name">VirtuaLive</span>
    </div>
  </div>
</template>

<script setup>

</script>

<style scoped>
.header-left {
  flex-shrink: 0;
}
.logo {
  display: flex;
  align-items: center;
  cursor: pointer;
}
.logo img {
  height: 32px;
  margin-right: 8px;
}
.site-name {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
}
</style>
