<template>
  <div class="video-detail-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">← 返回</button>

    <!-- 视频播放区域 -->
    <div class="video-wrapper">
      <video
        ref="videoRef"
        :src="videoSrc"
        controls
        autoplay
      ></video>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <div class="comments-header">
        <h3>评论</h3>
        <select v-model="sortOrder">
          <option value="time">按时间</option>
          <option value="hot">按热度</option>
        </select>
      </div>

      <ul class="comments-list">
        <li v-for="comment in sortedComments" :key="comment.id">
          <strong>{{ comment.user }}:</strong> {{ comment.content }}
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

// 假设通过路由传参或 props 获取视频地址
const videoSrc = ref('https://www.w3schools.com/html/mov_bbb.mp4') 

// 评论数据示例
const comments = ref([
  { id: 1, user: 'Alice', content: '太棒了！', time: '2025-11-23T17:00:00', hot: 10 },
  { id: 2, user: 'Bob', content: '非常喜欢！', time: '2025-11-23T17:05:00', hot: 15 },
  { id: 3, user: 'Charlie', content: '学习了', time: '2025-11-23T17:10:00', hot: 8 }
])

// 评论排序方式
const sortOrder = ref('time')

// 根据选择排序评论
const sortedComments = computed(() => {
  return [...comments.value].sort((a, b) => {
    if (sortOrder.value === 'time') {
      return new Date(b.time) - new Date(a.time)
    } else if (sortOrder.value === 'hot') {
      return b.hot - a.hot
    }
    return 0
  })
})

// 返回按钮逻辑
const goBack = () => {
  router.back()
}
</script>

<style scoped>
.video-detail-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background-color: #0f1016;
  color: #fff;
  min-height: 100vh;
}

.back-btn {
  align-self: flex-start;
  margin-bottom: 20px;
  padding: 6px 12px;
  background: #222;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.video-wrapper {
  width: 90%;
  max-width: 800px;
  margin-bottom: 20px;
}

video {
  width: 100%;
  border-radius: 8px;
  background-color: #000;
}

.comments-section {
  width: 80%;
  max-width: 800px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comments-header select {
  padding: 4px 8px;
  border-radius: 6px;
  border: none;
}

.comments-list li {
    margin-bottom: 10px;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
</style>
