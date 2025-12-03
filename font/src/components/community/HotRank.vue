<template>
  <div class="hot-rank">
    <h3 class="title">🔥 热度排行榜</h3>

    <div
      v-for="(item, index) in hotList"
      :key="index"
      class="rank-item"
    >
      <span class="index">{{ index + 1 }}</span>
      <span class="text">{{ item }}</span>
    </div>
  </div>
</template>

<script setup>
const hotList = [
  "纸片人捷丰",
  "贤者",
  "布鲁斯",
  "曼波"
]
</script>

<style scoped>
.hot-rank {
  width: 88%;
}

.title {
  font-size: 18px;
  margin-bottom: 12px;
}

.rank-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 0;
  cursor: pointer;
  transition: color 0.2s;
}

.rank-item:hover {
  color: #409eff;
}

.index {
  width: 20px;
  text-align: center;
  font-weight: bold;
  color: #409eff;
}

.text {
  flex: 1;
  font-size: 14px;
}
</style>
