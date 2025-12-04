<template>
  <div class="video-detail-page">
    <!-- 返回按钮 -->
    <button class="back-btn" @click="goBack">← 返回</button>

    <!-- 视频信息 -->
    <div v-if="videoInfo" class="video-info">
      <h2 class="video-title">{{ videoInfo.title }}</h2>
      <p class="video-creator">@{{ videoInfo.creator }}</p>
      <p class="video-stats">{{ videoInfo.views }} · {{ videoInfo.duration }}</p>
    </div>

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
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 视频源地址
const videoSrc = ref('https://www.w3schools.com/html/mov_bbb.mp4')
// 视频信息
const videoInfo = ref(null)
// 用于存储创建的blob URL，以便清理
let blobUrl = null

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

// 加载视频
const loadVideo = () => {
  // 从路由参数获取视频ID
  const videoId = route.query.id
  
  if (videoId) {
    try {
      // 从localStorage加载用户作品
      const userWorks = localStorage.getItem('userWorks')
      if (userWorks) {
        const works = JSON.parse(userWorks)
        const video = works.find(w => w.id === Number(videoId))
        
        if (video && video.videoData) {
          // 如果视频有base64数据，创建blob URL
          videoInfo.value = video
          
          // 将base64数据转换为blob URL
          // videoData是data:video/mp4;base64,xxxxx格式
          if (video.videoData.startsWith('data:')) {
            videoSrc.value = video.videoData
          } else {
            // 如果不是data URL格式，尝试创建blob
            const byteCharacters = atob(video.videoData)
            const byteNumbers = new Array(byteCharacters.length)
            for (let i = 0; i < byteCharacters.length; i++) {
              byteNumbers[i] = byteCharacters.charCodeAt(i)
            }
            const byteArray = new Uint8Array(byteNumbers)
            const blob = new Blob([byteArray], { type: video.fileType || 'video/mp4' })
            blobUrl = URL.createObjectURL(blob)
            videoSrc.value = blobUrl
          }
          return
        }
      }
    } catch (err) {
      console.error('加载视频失败:', err)
    }
  }
  
  // 如果没有找到视频或没有ID，使用默认视频
  videoSrc.value = 'https://www.w3schools.com/html/mov_bbb.mp4'
}

// 返回按钮逻辑
const goBack = () => {
  router.back()
}

// 组件挂载时加载视频
onMounted(() => {
  loadVideo()
})

// 组件卸载前清理blob URL
onBeforeUnmount(() => {
  if (blobUrl) {
    URL.revokeObjectURL(blobUrl)
    blobUrl = null
  }
})
</script>

<style scoped>
.video-detail-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  min-height: 100vh;
  position: relative;
}

.back-btn {
  align-self: flex-start;
  margin-bottom: 20px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  color: #2d2d2d;
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  color: #ff69b4;
  border-color: rgba(255, 105, 180, 0.5);
}

.video-info {
  width: 90%;
  max-width: 800px;
  margin-bottom: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
}

.video-title {
  margin: 0 0 8px;
  font-size: 1.5rem;
  color: #2d2d2d;
  font-weight: 600;
}

.video-creator {
  margin: 0 0 6px;
  color: rgba(45, 45, 45, 0.7);
  font-size: 1rem;
}

.video-stats {
  margin: 0;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.9rem;
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
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comments-header h3 {
  color: #2d2d2d;
  margin: 0;
}

.comments-header select {
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  cursor: pointer;
}

.comments-list li {
  margin-bottom: 10px;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  color: rgba(45, 45, 45, 0.8);
}

.comments-list li strong {
  color: #ff69b4;
}
</style>
