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
      <!-- 顶部评论输入 -->
      <div class="comment-input-box">
        <textarea v-model="newComment" placeholder="发表你的看法…"></textarea>
        <button @click="postComment">发表评论</button>
      </div>

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
          
          <!-- 评论操作按钮 -->
          <div class="comment-actions">
            <span class="like-btn" :class="{ liked: comment.liked }" @click="toggleLike(comment)">
              ❤️ {{ comment.likes }}
            </span>
            <span @click="toggleReplyBox(comment)">💬 回复</span>
          </div>

          <!-- 回复输入框 -->
          <div v-if="replyingTo === comment.id" class="reply-box">
            <textarea v-model="replyText" placeholder="回复内容…"></textarea>
            <button @click="submitReply(comment)">发送</button>
          </div>

          <!-- 回复列表 -->
          <ul class="reply-list" v-if="comment.replies && comment.replies.length > 0">
            <li v-for="reply in comment.replies" :key="reply.id" class="reply-item">
              <strong>{{ reply.user }}:</strong> {{ reply.content }}
            </li>
          </ul>
        </li>
      </ul>
    </div>

    <!-- 回到顶部按钮 -->
    <button class="back-top-btn" @click="handleBackTop">
      ⬆
    </button>
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
  { id: 1, user: 'Alice', content: '太棒了！', time: '2025-11-23T17:00:00', hot: 10, likes: 10, liked: false, replies: [] },
  { id: 2, user: 'Bob', content: '非常喜欢！', time: '2025-11-23T17:05:00', hot: 15, likes: 15, liked: false, replies: [] },
  { id: 3, user: 'Charlie', content: '学习了', time: '2025-11-23T17:10:00', hot: 8, likes: 8, liked: false, replies: [] }
])

// 评论排序方式
const sortOrder = ref('time')
// 新评论内容
const newComment = ref("")
// 正在回复的评论ID
const replyingTo = ref(null)
// 回复内容
const replyText = ref("")
// 记录上次滚动位置
const lastScrollY = ref(0)

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
  // 先检查是否从主页传来了视频源
  const videoSrc_query = route.query.src
  if (videoSrc_query) {
    videoSrc.value = videoSrc_query
    // 从query获取视频信息
    const videoId = route.query.id
    if (videoId) {
      // 如果需要，可以从sessionStorage或其他方式获取视频详细信息
      videoInfo.value = {
        id: videoId,
        title: `视频 ${videoId}`,
        creator: 'VirtuaLive',
        views: '点击播放',
        duration: '动态加载'
      }
    }
    return
  }
  
  // 从路由参数获取视频ID
  const videoId = route.query.id
  
  if (videoId) {
    try {
      // 从localStorage加载用户作品
      const userWorks = localStorage.getItem('userWorks')
      if (userWorks) {
        const works = JSON.parse(userWorks)
        const video = works.find(w => w.id === Number(videoId))
        
        if (video) {
          videoInfo.value = video
          
          // 优先从sessionStorage读取blob URL
          const blobUrl = sessionStorage.getItem(`videoBlob_${videoId}`)
          if (blobUrl) {
            videoSrc.value = blobUrl
            return
          }
          
          // 如果视频有base64数据，创建blob URL
          if (video.videoData) {
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

// 发布评论
const postComment = () => {
  if (!newComment.value.trim()) return

  comments.value.unshift({
    id: Date.now(),
    user: "You",
    content: newComment.value,
    time: new Date().toISOString(),
    hot: 0,
    likes: 0,
    liked: false,
    replies: []
  })

  newComment.value = ""
}

// 点赞评论
const toggleLike = (comment) => {
  if (!comment.liked) {
    comment.likes++
    comment.liked = true
  } else {
    comment.likes--
    comment.liked = false
  }
}

// 展开/关闭回复框
const toggleReplyBox = (comment) => {
  if (replyingTo.value === comment.id) {
    replyingTo.value = null
  } else {
    replyingTo.value = comment.id
    replyText.value = ""
  }
}

// 发送回复
const submitReply = (comment) => {
  if (!replyText.value.trim()) return

  if (!comment.replies) {
    comment.replies = []
  }

  comment.replies.push({
    id: Date.now(),
    user: "You",
    content: replyText.value,
    time: new Date().toISOString()
  })

  replyText.value = ""
  replyingTo.value = null
}

// 回到顶部按钮逻辑
const handleBackTop = () => {
  const now = window.scrollY

  // 点击后回到顶部
  if (now > 50) {
    lastScrollY.value = now
    window.scrollTo({ top: 0, behavior: "smooth" })
  } else {
    // 再次点击 → 回到之前的位置
    window.scrollTo({ top: lastScrollY.value, behavior: "smooth" })
  }
}
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
  max-width: 100%;
  margin-bottom: 20px;
  aspect-ratio: 16 / 9;
}

video {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  background-color: #000;
  object-fit: contain;
}

.comments-section {
  width: 85%;
  max-width: 800px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
}

/* 顶部评论输入框 */
.comment-input-box {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.comment-input-box textarea {
  flex: 1;
  height: 80px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  resize: vertical;
}

.comment-input-box button {
  align-self: flex-end;
  margin-top: 8px;
  padding: 6px 12px;
  border-radius: 8px;
  background: #ff69b4;
  color: white;
  cursor: pointer;
  border: none;
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
  color: black;
}

/* 评论操作按钮 */
.comment-actions {
  display: flex;
  gap: 12px;
  margin: 4px 0 8px;
  color: gray;
}

.comment-actions span {
  cursor: pointer;
}

/* 回复输入框 */
.reply-box {
  margin: 8px 0 10px 20px;
  display: flex;
  flex-direction: column;
}

.reply-box textarea {
  width: 90%;
  height: 60px;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  resize: vertical;
}

.reply-box button {
  align-self: flex-start;
  margin-top: 6px;
  padding: 4px 10px;
  border-radius: 6px;
  border: none;
  background: #ff69b4;
  color: white;
}

/* 回复列表 */
.reply-list {
  margin-left: 20px;
  margin-top: 8px;
  padding-left: 15px;
  border-left: 2px solid rgba(255, 105, 180, 0.3);
}

.reply-item {
  margin-bottom: 6px;
  color: rgba(45, 45, 45, 0.8);
}

/* 回到顶部按钮 */
.back-top-btn {
  position: fixed;
  right: 20px;
  bottom: 30px;
  width: 45px;
  height: 45px;
  font-size: 20px;
  border: none;
  border-radius: 50%;
  background: #ff69b4;
  color: white;
  cursor: pointer;
  box-shadow: 0 6px 12px rgba(255, 105, 180, 0.3);
}

textarea:hover,
textarea:focus {
  border-color: rgba(255, 105, 180, 0.6) !important;
  outline: none !important;
  box-shadow: 0 0 6px rgba(255, 105, 180, 0.2);
}

/* 点赞按钮 */
.like-btn {
  cursor: pointer;
  transition: all 0.2s;
  color: rgba(45, 45, 45, 0.6);
}

.like-btn.liked {
  color: #ff4d88; /* 爱心红色 */
  transform: scale(1.2);
}

.like-btn:hover {
  color: #ff69b4;
}
</style>
