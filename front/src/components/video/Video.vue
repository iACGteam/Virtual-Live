<template>
  <div class="video-page-container">
    <!-- 左侧内容区 -->
    <div class="left-content">
      <!-- 视频播放区域（保留你的逻辑） -->
      <div class="video-wrapper">
        <video ref="videoRef" :src="videoSrc" controls autoplay></video>
      </div>
      <!-- 视频信息 -->
      <div v-if="videoInfo" class="video-meta">
        <h2 class="title">{{ videoInfo.title }}</h2>
        <div class="meta-row">
          <!-- UP 主信息 -->
          <div class="creator-box">
            <div class="avatar" @click="goToChannel"></div>

            <div class="channel-info" @click="goToChannel">
              <p class="creator-name">
                {{ videoInfo.creator }}
                <span class="verified">✔</span>
              </p>
              <p class="sub-count">{{ videoInfo.subs }} 订阅</p>
            </div>

            <div class="button-area">
              <button class="follow-btn" @click="toggleFollow">
                {{ isFollowing ? '已关注 ✓' : '关注' }}
              </button>

              <button class="join-btn" @click="toggleJoin">
                {{ isJoined ? '已加入 ✔' : '加入圈子' }}
              </button>
            </div>
          </div>

          <!-- 互动按钮 -->
          <div class="action-buttons">
            <button class="act-btn" @click="toggleLike(videoInfo)">
              👍 {{ videoInfo.likes ?? 0 }}
            </button>
            <button class="act-btn">🔗 分享</button>
          </div>
        </div>

        <div class="stats-row">
          <span>{{ videoInfo.views }}</span> ·
          <span>{{ videoInfo.duration }}</span>
        </div>
      </div>

      <!-- 评论区 -->
      <div class="comments-section">

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
            <div class="comment-item">
              <strong>{{ comment.user }}:</strong> {{ comment.content }}
              <div class="comment-actions">
                <span class="like-btn" :class="{ liked: comment.liked }" @click="toggleLike(comment)">
                  ❤️ {{ comment.likes }}
                </span>
                <span @click="toggleReplyBox(comment)">💬 回复</span>
              </div>
              <div v-if="replyingTo === comment.id" class="reply-box">
                <textarea v-model="replyText" placeholder="回复内容…"></textarea>
                <button @click="submitReply(comment)">发送</button>
              </div>
              <ul class="reply-list" v-if="comment.replies.length > 0">
                <li v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                  <strong>{{ reply.user }}:</strong> {{ reply.content }}
                </li>
              </ul>
            </div>
          </li>
        </ul>

      </div>
    </div>

    <!-- 右侧推荐视频区 -->
    <aside class="right-sidebar">
      <h3>推荐视频</h3>

      <div class="recommend-card" v-for="item in recommendedVideos" :key="item.id" @click="openRecommend(item)">
        <img class="thumb" :src="item.thumbnail" />
        <div class="info">
          <p class="title">{{ item.title }}</p>
          <small>@{{ item.author }} · {{ item.views }} 次观看</small>
        </div>
      </div>
    </aside>

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
// 释放 blob 记录
let blobUrl = null

const isFollowing = ref(false)

const toggleFollow = () => {
  isFollowing.value = !isFollowing.value
}

// ======== 加入圈子状态 ========
const isJoined = ref(false)

const toggleJoin = () => {
  isJoined.value = !isJoined.value
}

const goToChannel = () => {
  router.push({ path: '/profile'})
  // query: { id: videoInfo.value.creatorId }

  console.log("跳转到作者主页逻辑这里写")
}

// 评论数据示例
const comments = ref([
  { id: 1, user: 'Alice', content: '太棒了！', time: '2025-11-23T17:00:00', hot: 10 },
  { id: 2, user: 'Bob', content: '非常喜欢！', time: '2025-11-23T17:05:00', hot: 15 },
  { id: 3, user: 'Charlie', content: '学习了', time: '2025-11-23T17:10:00', hot: 8 }
])

// 评论增强
comments.value = comments.value.map(c => ({
  ...c,
  likes: c.hot || 0,
  liked: false,
  replies: []
}))

// 选择排序方式
const sortOrder = ref('time')

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

// 点赞功能（视频或评论通用）
const toggleLike = (item) => {
  if (!item.liked) {
    item.likes++
    item.liked = true
  } else {
    item.likes--
    item.liked = false
  }
}

// 发布评论
const newComment = ref("")
const replyingTo = ref(null)
const replyText = ref("")

const postComment = () => {
  if (!newComment.value.trim()) return

  comments.value.unshift({
    id: Date.now(),
    user: "You",
    content: newComment.value,
    time: new Date().toISOString(),
    likes: 0,
    replies: []
  })

  newComment.value = ""
}

// 回复框开关
const toggleReplyBox = (comment) => {
  if (replyingTo.value === comment.id) {
    replyingTo.value = null
  } else {
    replyingTo.value = comment.id
    replyText.value = ""
  }
}

// 回复提交
const submitReply = (comment) => {
  if (!replyText.value.trim()) return

  comment.replies.push({
    id: Date.now(),
    user: "You",
    content: replyText.value,
    time: new Date().toISOString()
  })

  replyText.value = ""
  replyingTo.value = null
}


// ========= 推荐视频假数据 (可替换真实 API) =========

const recommendedVideos = ref([
  {
    id: 101,
    title: "标题",
    author: "作者",
    views: "12万",
    thumbnail: "https://picsum.photos/200/120?1"
  },
  {
    id: 102,
    title: "标题",
    author: "作者",
    views: "8.4万",
    thumbnail: "https://picsum.photos/200/120?2"
  },
  {
    id: 103,
    title: "标题",
    author: "作者",
    views: "34万",
    thumbnail: "https://picsum.photos/200/120?3"
  },
  {
    id: 104,
    title: "标题",
    author: "作者",
    views: "19万",
    thumbnail: "https://picsum.photos/200/120?4"
  },
  {
    id: 105,
    title: "标题",
    author: "作者",
    views: "3.3万",
    thumbnail: "https://picsum.photos/200/120?5"
  }
])

// 点击推荐视频 -> 跳转播放
const openRecommend = (item) => {
  router.push({
    path: "/video",
    query: { id: item.id, src: videoSrc.value }
  })
}


// 自动加载视频
const loadVideo = () => {
  const videoSrc_query = route.query.src
  if (videoSrc_query) {
    videoSrc.value = videoSrc_query
    const videoId = route.query.id
    if (videoId) {
      videoInfo.value = {
        id: videoId,
        title: `视频 ${videoId}`,
        creator: 'VirtuaLive',
        subs: '6666',
        likes: '888',
        views: '点击播放',
        duration: '动态加载'
      }
    }
    return
  }

  const videoId = route.query.id
  if (videoId) {
    try {
      const userWorks = localStorage.getItem('userWorks')
      if (userWorks) {
        const works = JSON.parse(userWorks)
        const video = works.find(w => w.id === Number(videoId))

        if (video) {
          videoInfo.value = video

          const blobUrl = sessionStorage.getItem(`videoBlob_${videoId}`)
          if (blobUrl) {
            videoSrc.value = blobUrl
            return
          }

          if (video.videoData) {
            if (video.videoData.startsWith('data:')) {
              videoSrc.value = video.videoData
            } else {
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

  videoSrc.value = 'https://www.w3schools.com/html/mov_bbb.mp4'
}

onMounted(() => loadVideo())

onBeforeUnmount(() => {
  if (blobUrl) {
    URL.revokeObjectURL(blobUrl)
    blobUrl = null
  }
})
</script>


<style scoped>
/* 页面整体布局 */
.video-page-container {
  display: flex;
  gap: 20px;
  padding: 20px;
  min-height: 100vh;
  background: #fefbff;
}

/* 左侧内容布局 */
.left-content {
  flex: 2.2;
  display: flex;
  flex-direction: column;
}

/* 视频区域 */
.video-wrapper {
  width: 100%;
  aspect-ratio: 16 / 9;
  border-radius: 10px;
  overflow: hidden;
  margin-bottom: 10px;
}

video {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  background-color: #000;
  object-fit: contain;
}

/* 视频信息模块 */
.video-meta {
  background: #fefbff;
  padding: 10px 15px;
  border-bottom: 1px solid #ddd;
  border-radius: 12px;
  border: 1px solid #fedef0;
  box-shadow: 0 4px 10px rgba(255, 105, 180, 0.15);
}

.video-meta .title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2d2d2d;
}

.meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.creator-box {
  display: flex;
  align-items: center;
  /* justify-content:left; */
  width: 100%;
  gap: 12px;
  margin-top: 8px;
  cursor: pointer;
}

/* 头像 */
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ddd, #bbb);
  flex-shrink: 0;
  cursor: pointer;
}

/* 频道信息 */
.channel-info {
  /* flex: 1; */
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 频道名 */
.creator-name {
  font-size: 16px;
  font-weight: 600;
  color: #222;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 认证勾 ✔ */
.verified {
  display: inline-block;
  font-size: 13px;
  color: #555;
}

/* 订阅数 */
.sub-count {
  margin: 0;
  font-size: 13px;
  color: #777;
}

/* 右侧按钮区域 （YouTube 风格） */
.button-area {
  display: flex;
  gap: 10px;
  align-items: center;
}

/* 关注按钮 —— 粉色风格 */
.follow-btn {
  padding: 6px 12px;
  background: #ff69b4;
  color: white;
  border: none;
  border-radius: 18px;
  cursor: pointer;
  font-size: 13px;
  transition: 0.2s;
}

.follow-btn:hover {
  background: #ff4d9b;
}

/* Join / 加入圈子按钮 —— YouTube 黑色样式 */
.join-btn {
  padding: 6px 16px;
  background: #000;
  color: white;
  border: none;
  border-radius: 18px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: 0.25s;
}

.join-btn:hover {
  opacity: 0.85;
}


/* 操作按钮区 */
.action-buttons .act-btn {
  padding: 6px 10px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  margin-left: 8px;
  cursor: pointer;
}

.stats-row {
  margin-top: 6px;
  color: gray;
  font-size: 14px;
}

/* ================== 评论区样式 ================== */

.comments-section {
  /* width: 95%; */
  /* max-width: 800px; */
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
  margin-top: 20px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comments-header h3 {
  margin: 0;
  color: #2d2d2d;
}

.comments-header select {
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  cursor: pointer;
}

/* 评论输入框 */
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

/* 评论显示 */
.comments-list li {
  margin-bottom: 10px;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  color: rgba(45, 45, 45, 0.8);
}

.comments-list li strong {
  color: black;
}

/* 评论动作 */
.comment-actions {
  display: flex;
  gap: 12px;
  margin: 4px 0 8px;
  color: gray;
}

.comment-actions span {
  cursor: pointer;
}

/* 回复框 */
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

/* 评论点赞按钮 */
.like-btn {
  cursor: pointer;
  transition: all 0.2s;
  color: rgba(45, 45, 45, 0.6);
}

.like-btn.liked {
  color: #ff4d88;
  transform: scale(1.2);
}

.like-btn:hover {
  color: #ff69b4;
}

/* ================== 推荐视频区域 ================== */

.right-sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.right-sidebar h3 {
  margin-bottom: 5px;
}

.recommend-card {
  color: black;
  display: flex;
  gap: 10px;
  cursor: pointer;
  padding: 6px;
  border-radius: 8px;
  transition: 0.2s;
  border: 1px solid transparent;
}

.recommend-card:hover {
  background: #fcf5ff;
  border-color: #fcf5ff;
}

.thumb {
  width: 120px;
  height: 70px;
  border-radius: 6px;
  object-fit: cover;
}

.info .title {
  margin: 0;
  font-size: 14px;
  font-weight: 500;
}

/* 文本框 hover 统一控制 */
textarea:hover,
textarea:focus {
  border-color: rgba(255, 105, 180, 0.6) !important;
  outline: none !important;
  box-shadow: 0 0 6px rgba(255, 105, 180, 0.2);
}

small {
  color: gray;
}

h3 {
  color: black;
}
</style>
