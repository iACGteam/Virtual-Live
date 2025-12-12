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
      
      <!-- 视频信息 -->
      <div class="video-info" v-if="videoInfo">
        <h2>{{ videoInfo.title }}</h2>
        <div class="video-meta">
          <span>{{ videoInfo.views }} 次播放</span>
          <span>{{ videoInfo.likes }} 点赞</span>
          <span>{{ formatDate(videoInfo.createdAt) }}</span>
        </div>
        <p class="video-desc">{{ videoInfo.content }}</p>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comments-section">
      <div class="comments-header">
        <h3>评论 ({{ comments.length }})</h3>
        <select v-model="sortOrder" @change="loadComments">
          <option value="time">按时间</option>
          <option value="hot">按热度</option>
        </select>
      </div>

      <!-- 发表评论 -->
      <div class="comment-input">
        <input 
          v-model="newComment" 
          placeholder="发表评论..." 
          @keyup.enter="submitComment"
        />
        <button @click="submitComment">发送</button>
      </div>

      <ul class="comments-list">
        <li v-for="comment in comments" :key="comment.id" class="comment-item">
          <div class="comment-avatar">
            <img :src="comment.userAvatar || '/default-avatar.png'" alt="头像" />
          </div>
          <div class="comment-body">
            <div class="comment-header">
              <strong>{{ comment.username }}</strong>
              <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
            <div class="comment-actions">
              <button @click="handleLike(comment.id)">👍 {{ comment.likes }}</button>
            </div>
          </div>
        </li>
      </ul>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-if="!loading && comments.length === 0" class="no-comments">暂无评论</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getVideoById, getComments, addComment, likeComment } from '@/utils/api'

const router = useRouter()
const route = useRoute()

// 视频数据
const videoId = ref(route.query.id || 1)
const videoSrc = ref('https://www.w3schools.com/html/mov_bbb.mp4')
const videoInfo = ref(null)

// 评论数据
const comments = ref([])
const sortOrder = ref('time')
const newComment = ref('')
const loading = ref(false)

// 加载视频信息
const loadVideo = async () => {
  try {
    const data = await getVideoById(videoId.value)
    videoInfo.value = data
    if (data.videoUrl) {
      videoSrc.value = data.videoUrl
    }
  } catch (error) {
    console.error('加载视频失败:', error)
    // 使用默认数据
    videoInfo.value = {
      title: '示例视频',
      content: '这是一个示例视频描述',
      views: 1234,
      likes: 56
    }
  }
}

// 加载评论
const loadComments = async () => {
  loading.value = true
  try {
    const data = await getComments(videoId.value, 0, 50, sortOrder.value)
    comments.value = data.content || []
  } catch (error) {
    console.error('加载评论失败:', error)
    // 使用本地数据
    comments.value = [
      { id: 1, username: 'Alice', content: '太棒了！', createdAt: '2025-11-23T17:00:00', likes: 10 },
      { id: 2, username: 'Bob', content: '非常喜欢！', createdAt: '2025-11-23T17:05:00', likes: 15 },
      { id: 3, username: 'Charlie', content: '学习了', createdAt: '2025-11-23T17:10:00', likes: 8 }
    ]
  } finally {
    loading.value = false
  }
}

// 发表评论
const submitComment = async () => {
  if (!newComment.value.trim()) return
  
  try {
    const userId = 1 // 暂时使用固定用户ID
    await addComment(videoId.value, userId, newComment.value)
    newComment.value = ''
    await loadComments()
  } catch (error) {
    console.error('发表评论失败:', error)
    // 本地添加
    comments.value.unshift({
      id: Date.now(),
      username: '我',
      content: newComment.value,
      createdAt: new Date().toISOString(),
      likes: 0
    })
    newComment.value = ''
  }
}

// 点赞评论
const handleLike = async (commentId) => {
  try {
    await likeComment(commentId)
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) comment.likes++
  } catch (error) {
    console.error('点赞失败:', error)
    // 本地点赞
    const comment = comments.value.find(c => c.id === commentId)
    if (comment) comment.likes++
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)} 分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)} 小时前`
  return date.toLocaleDateString()
}

// 返回
const goBack = () => {
  router.back()
}

onMounted(() => {
  loadVideo()
  loadComments()
})
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
  padding: 8px 16px;
  background: #222;
  color: #fff;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.back-btn:hover {
  background: #333;
}

.video-wrapper {
  width: 90%;
  max-width: 900px;
  margin-bottom: 20px;
}

video {
  width: 100%;
  border-radius: 8px;
  background-color: #000;
}

.video-info {
  margin-top: 16px;
}

.video-info h2 {
  font-size: 20px;
  margin-bottom: 8px;
}

.video-meta {
  display: flex;
  gap: 16px;
  color: #888;
  font-size: 14px;
  margin-bottom: 12px;
}

.video-desc {
  color: #aaa;
  font-size: 14px;
  line-height: 1.6;
}

.comments-section {
  width: 90%;
  max-width: 900px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.comments-header h3 {
  font-size: 18px;
}

.comments-header select {
  padding: 6px 12px;
  border-radius: 6px;
  border: 1px solid #333;
  background: #1a1a1a;
  color: #fff;
}

.comment-input {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.comment-input input {
  flex: 1;
  padding: 12px 16px;
  border-radius: 8px;
  border: 1px solid #333;
  background: #1a1a1a;
  color: #fff;
  font-size: 14px;
}

.comment-input input:focus {
  outline: none;
  border-color: #5b7fff;
}

.comment-input button {
  padding: 12px 24px;
  border-radius: 8px;
  border: none;
  background: linear-gradient(135deg, #5b7fff, #8b5cf6);
  color: #fff;
  cursor: pointer;
  transition: opacity 0.2s;
}

.comment-input button:hover {
  opacity: 0.9;
}

.comments-list {
  list-style: none;
  padding: 0;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 16px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  overflow: hidden;
  background: #333;
}

.comment-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 6px;
}

.comment-header strong {
  font-size: 14px;
}

.comment-time {
  color: #666;
  font-size: 12px;
}

.comment-content {
  color: #ddd;
  font-size: 14px;
  line-height: 1.5;
  margin-bottom: 8px;
}

.comment-actions button {
  background: none;
  border: none;
  color: #888;
  cursor: pointer;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background 0.2s;
}

.comment-actions button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.loading, .no-comments {
  text-align: center;
  color: #666;
  padding: 40px 0;
}
</style>
