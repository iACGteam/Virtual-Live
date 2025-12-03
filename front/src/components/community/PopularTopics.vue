<template>
  <div class="topics">
    <h3 class="title">📌 热门话题</h3>

    <div class="tag-list">
      <el-tag
        v-for="(tag, index) in tags"
        :key="index"
        size="large"
        class="topic-tag"
      >
        # {{ tag }}
      </el-tag>
    </div>
  </div>
</template>

<script setup>
const tags = [
  "虚拟直播",
  "AI 语音",
  "直播弹幕",
  "互动社区",
  "虚拟偶像"
]
</script>

<style scoped>
.title {
  font-size: 18px;
  margin-bottom: 12px;
  color: black; 
  margin-bottom: 5px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}


.topic-tag {
  cursor: pointer;
  transition: all 0.2s ease;
  background-color: rgb(244, 233, 235);
  color: black;
  border: 1px solid pink; 
  border-radius: 12px;
  padding: 4px 12px;
  font-weight: 500;
}

.topic-tag:hover {
  /* background: linear-gradient(135deg, #409eff, #66b1ff); */
  /* color: white; */
  /* border-color: transparent; */
  transform: translateY(-2px); 
}
</style>
