<template>
  <div class="video-page-container" ref="pageRef">
    <!-- 左侧内容区 -->
    <div class="left-content">
      <!-- 标题置于视频上方 -->
      <div v-if="videoInfo" class="video-title-row">
        <div class="title-block">
          <h2 class="title">{{ videoInfo.title }}</h2>
          <div class="title-stats">
            <span v-if="videoInfo.views">{{ videoInfo.views }} 次观看</span>
            <span v-if="videoInfo.views" class="dot">·</span>
            <span>{{ totalComments }} 条评论</span>
          </div>
        </div>
      </div>

      <!-- 视频播放区域（保留你的逻辑） -->
      <div class="video-wrapper">
        <video ref="videoRef" :src="videoSrc" controls autoplay></video>
        <!-- 弹幕显示层 -->
        <div class="danmu-overlay">
          <div v-for="dm in activeDanmus" :key="dm.id" class="danmu-item"
            :style="{ top: dm.top + 'px', left: dm.left + 'px' }">
            {{ dm.text }}
          </div>
        </div>
      </div>

      <div class="danmu-send-bar">
        <!-- 管理按钮 -->
        <div class="danmu-settings-btn" @click="toggleDanmuSettings">
          ⚙️
        </div>
        <!-- 开关 -->
        <div class="danmu-switch" @click="toggleDanmu">
          <div class="switch-icon" :class="{ on: danmuEnabled }"></div>
          <span>{{ danmuEnabled ? "弹幕：开" : "弹幕：关" }}</span>
        </div>
        <!-- 输入 -->
        <input class="danmu-input" v-model="danmuInput" :placeholder="danmuEnabled ? '发个友善的弹幕见证当下' : '弹幕已关闭'"
          :disabled="!danmuEnabled" @keydown.enter="sendDanmu" />
        <!-- 发送按钮 -->
        <button class="danmu-send-btn" :disabled="!danmuEnabled || !danmuInput.trim()" @click="sendDanmu">
          发送
        </button>
      </div>

      <!-- 弹幕设置面板 -->
      <div class="danmu-settings-panel" v-show="showDanmuSettings">
        <h4>弹幕设置</h4>

        <!-- 透明度 -->
        <div class="setting-row">
          <label>透明度：{{ danmuOpacity }}</label>
          <input type="range" min="0" max="1" step="0.1" v-model="danmuOpacity" />
        </div>

        <!-- 字号 -->
        <div class="setting-row">
          <label>字号：</label>
          <select v-model="danmuFontSize">
            <option value="14">小</option>
            <option value="16">中</option>
            <option value="20">大</option>
            <option value="24">特大</option>
          </select>
        </div>

        <!-- 显示区域 -->
        <div class="setting-row">
          <label>显示区域：</label>
          <select v-model="danmuArea">
            <option value="full">全屏</option>
            <option value="top">顶部</option>
            <option value="bottom">底部</option>
          </select>
        </div>
      </div>


      <!-- 视频信息 -->
      <div v-if="videoInfo" class="video-meta">
        <div class="meta-row">
          <div class="action-buttons">
            <button class="act-btn" @click="toggleLike(videoInfo)">
              👍 {{ videoInfo.likes ? videoInfo.likes : '' }}
            </button>
          </div>
        </div>
        <div class="stats-row">
          <span v-if="videoInfo.views">{{ videoInfo.views }}</span>
          <span v-if="videoInfo.views && videoInfo.duration"> · </span>
          <span v-if="videoInfo.duration">{{ videoInfo.duration }}</span>
        </div>
      </div>

      <!-- 视频简介 -->
      <div v-if="videoInfo" class="video-description">
        <p>{{ videoInfo.description || '暂无简介' }}</p>
      </div>

      <!-- 评论区 -->
       <Comment></Comment>
    </div>

    <!-- 右侧推荐视频区 -->
    <aside class="right-sidebar" :style="{ minHeight: sidebarMinHeight }">
      <div class="creator-panel">
        <div class="creator-info" @click="goToChannel">
          <div class="avatar"></div>
          <div class="channel-info">
            <p class="creator-name">
              {{ videoInfo?.creator || '作者' }}
              <span class="verified">✔</span>
            </p>
            <p class="sub-count">{{ videoInfo?.subs || '订阅' }}</p>
          </div>
        </div>
        <div class="button-area">
          <button class="follow-btn" @click="toggleFollow">
            {{ isFollowing ? '已关注 ✓' : '关注' }}
          </button>

          <button class="join-btn" @click="handleJoinClick">
            {{ isJoined && isFollowing ? '参与讨论' : '加入圈子' }}
          </button>
          <div class="join-note">需粉丝等级≥3 才可加入圈子</div>
        </div>
      </div>

      <div class="danmu-list">
        <div class="list-header" @click="toggleDanmuList">
          弹幕列表
          <span>{{ showDanmuList ? '▼' : '▲' }}</span>
        </div>
        <div v-show="showDanmuList" class="list-body">
          <table class="danmu-table">
            <thead>
              <tr>
                <th>时间</th>
                <th>弹幕内容</th>
                <th>发送时间</th>
                <!-- <th>操作</th> -->
              </tr>
            </thead>
            <tbody>
              <tr v-for="dm in danmuList" :key="dm.id">
                <td>{{ dm.videoTime }}</td>
                <td>{{ dm.text }}</td>
                <td>{{ dm.sendTime }}</td>
                <!-- <td>
                  <button @click="reportDanmu(dm)" class="button">举报</button>
                  <button @click="blockUser(dm.user)" class="button">屏蔽用户</button>
                </td> -->
              </tr>
            </tbody>
          </table>
        </div>
      </div>

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
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Comment from './Comment.vue'

const router = useRouter()
const route = useRoute()
const totalComments = ref(0)


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



// =======================弹幕=============================



const activeDanmus = ref([]);     // 当前显示中的弹幕
const danmuList = ref([]);        // 弹幕列表（用于表格展示）
const showDanmuList = ref(false);

// 打开/关闭弹幕列表
const toggleDanmuList = () => {
  showDanmuList.value = !showDanmuList.value;
};

// 监听视频播放进度，用时间触发弹幕
const videoRef = ref(null);

onMounted(() => {
  videoRef.value.addEventListener("timeupdate", () => {
    if (!danmuEnabled.value) return;   // ❗关闭时不显示弹幕

    const current = Math.floor(videoRef.value.currentTime);

    danmuList.value
      .filter(dm => dm.videoTimeSec === current)
      .forEach(showDanmu);
  });

});

// 展示弹幕
function showDanmu(dm) {
  if (!danmuEnabled.value) return;
  const topMax =
    danmuArea.value === "full"
      ? 200
      : danmuArea.value === "top"
        ? 80
        : 80;

  const topBase = danmuArea.value === "bottom" ? 150 : 20;
  const topPos = topBase + Math.random() * topMax;

  activeDanmus.value.push({
    id: dm.id,
    text: dm.text,
    top: topPos,
    left: 0,
    opacity: danmuOpacity.value,
    size: danmuFontSize.value,
  });

  setTimeout(() => {
    activeDanmus.value = activeDanmus.value.filter((d) => d.id !== dm.id);
  }, 6000);
}

// 举报
function reportDanmu(dm) {
  alert("已举报: " + dm.text);
}

// 屏蔽用户
function blockUser(user) {
  alert("已屏蔽用户：" + user);
}

// 示例：你从后端加载到的弹幕
danmuList.value = [
  {
    id: 1,
    text: "热乎的",
    user: "用户A",
    videoTime: "00:06",
    videoTimeSec: 6,
    sendTime: "12-11 11:37"
  },
  {
    id: 2,
    text: "我是第一",
    user: "用户B",
    videoTime: "00:00",
    videoTimeSec: 0,
    sendTime: "12-11 11:38"
  }
];



// 开关状态
const danmuEnabled = ref(true);

// 输入内容
const danmuInput = ref("");

// 切换弹幕开关
function toggleDanmu() {
  danmuEnabled.value = !danmuEnabled.value;
  if (!danmuEnabled.value) {
    activeDanmus.value = [];   // 清空现有弹幕
  }
}

// 发送弹幕
function sendDanmu() {
  if (!danmuEnabled.value) return;
  if (!danmuInput.value.trim()) return;

  const text = danmuInput.value.trim();

  // 创建一个新的弹幕对象
  const currentTimeSec = Math.floor(videoRef.value.currentTime);

  const newDanmu = {
    id: Date.now(),
    text,
    videoTimeSec: currentTimeSec,
    videoTime: formatTime(currentTimeSec),
    user: "你自己",
    sendTime: new Date().toLocaleString(),
  };

  // 加入弹幕列表（供列表页显示）
  danmuList.value.push(newDanmu);

  // 立即显示弹幕
  showDanmu(newDanmu);

  // 清空输入
  danmuInput.value = "";
}

function formatTime(sec) {
  const m = String(Math.floor(sec / 60)).padStart(2, "0");
  const s = String(sec % 60).padStart(2, "0");
  return `${m}:${s}`;
}

// =======================弹幕=============================



// ==================== 弹幕管理设置 =====================
const showDanmuSettings = ref(false);

// 打开 / 关闭 设置面板
function toggleDanmuSettings() {
  showDanmuSettings.value = !showDanmuSettings.value;
  console.log("面板状态：", showDanmuSettings.value);
}

// 透明度（影响全部弹幕）
const danmuOpacity = ref(1.0);

// 字号
const danmuFontSize = ref(16);

// 弹幕区域
// full / top / bottom
const danmuArea = ref("full");


// ==================== 弹幕管理设置 =====================

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


// ======== 加入圈子状态 ========
const isJoined = ref(false)

const toggleJoin = () => {
  isJoined.value = !isJoined.value
}

const handleJoinClick = () => {
  if (!isFollowing.value) {
    alert('请先关注主播，再加入圈子');
    return;
  }

  if (isJoined.value) {
    const id = videoInfo.value?.id || 'creator-circle'
    const name = videoInfo.value?.creator || '作者圈子'
    router.push({ path: '/com-detail', query: { id, name } })
    return
  }
  toggleJoin()
}

const goToChannel = () => {
  router.push({ path: '/profile' })
  // query: { id: videoInfo.value.creatorId }

  console.log("跳转到作者主页逻辑这里写")
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
  },
  {
    id: 106,
    title: "标题",
    author: "作者",
    views: "6.1万",
    thumbnail: "https://picsum.photos/200/120?6"
  },
  {
    id: 107,
    title: "标题",
    author: "作者",
    views: "4.7万",
    thumbnail: "https://picsum.photos/200/120?7"
  },
  {
    id: 108,
    title: "标题",
    author: "作者",
    views: "2.9万",
    thumbnail: "https://picsum.photos/200/120?8"
  },
  {
    id: 109,
    title: "标题",
    author: "作者",
    views: "9.6万",
    thumbnail: "https://picsum.photos/200/120?9"
  },
  {
    id: 110,
    title: "标题",
    author: "作者",
    views: "1.1万",
    thumbnail: "https://picsum.photos/200/120?10"
  },
  {
    id: 111,
    title: "标题",
    author: "作者",
    views: "7.5万",
    thumbnail: "https://picsum.photos/200/120?11"
  },
  {
    id: 112,
    title: "标题",
    author: "作者",
    views: "5.2万",
    thumbnail: "https://picsum.photos/200/120?12"
  }
])

const getViewsById = (id) => {
  if (!id) return null
  const found = recommendedVideos.value.find(v => String(v.id) === String(id))
  return found ? found.views : null
}

const pageRef = ref(null)
const commentsRef = ref(null)
const sidebarMinHeight = ref('auto')

const updateSidebarHeight = () => {
  if (!pageRef.value || !commentsRef.value) return
  const pageRect = pageRef.value.getBoundingClientRect()
  const commentsRect = commentsRef.value.getBoundingClientRect()
  const height = Math.max(commentsRect.bottom - pageRect.top, 0)
  sidebarMinHeight.value = `${height}px`
}


// 点击推荐视频 -> 跳转播放
const openRecommend = (item) => {
  router.push({
    path: "/video",
    query: { id: item.id, src: videoSrc.value, views: item.views }
  })
}


// 自动加载视频
const loadVideo = () => {
  const videoSrc_query = route.query.src
  if (videoSrc_query) {
    videoSrc.value = videoSrc_query
    const videoId = route.query.id
    const viewFromList = getViewsById(videoId)
    if (videoId) {
      videoInfo.value = {
        id: videoId,
        title: `视频 ${videoId}`,
        creator: 'VirtuaLive',
        subs: '6666',
        likes: '888',
        views: route.query.views || viewFromList || '',
        duration: '',
        description: route.query.desc || ''
      }
    }
    return
  }

  const videoId = route.query.id
  const viewFromList = getViewsById(videoId)
  if (videoId) {
    try {
      const userWorks = localStorage.getItem('userWorks')
      if (userWorks) {
        const works = JSON.parse(userWorks)
        const video = works.find(w => w.id === Number(videoId))

        if (video) {
          videoInfo.value = {
            ...video,
            views: route.query.views || viewFromList || video.views || videoInfo.value?.views || '',
            duration: video.duration || '',
            description: video.description || route.query.desc || ''
          }

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

onMounted(() => {
  loadVideo()
  updateSidebarHeight()
  window.addEventListener('resize', updateSidebarHeight)
})

onBeforeUnmount(() => {
  if (blobUrl) {
    URL.revokeObjectURL(blobUrl)
    blobUrl = null
  }
  window.removeEventListener('resize', updateSidebarHeight)
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
  overflow-x: hidden;
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

.video-title-row {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 10px;
}

.title-block {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  min-width: 0;
}

.video-title-row .title {
  margin: 0;
  font-size: 20px;
  font-weight: 700;
  color: #2d2d2d;
}

.title-stats {
  margin-top: 6px;
  font-size: 13px;
  color: #666;
  display: flex;
  gap: 8px;
  align-items: center;
}

.title-stats .dot {
  color: #aaa;
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
  justify-content: flex-end;
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

.join-note {
  font-size: 12px;
  color: #666;
}

.creator-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 12px;
  border: 1px solid #f0e6ff;
  border-radius: 12px;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
  margin-bottom: 8px;
}

.creator-panel .creator-info {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.creator-panel .avatar {
  width: 48px;
  height: 48px;
}

.creator-panel .channel-info {
  display: flex;
  flex-direction: column;
}

.creator-panel .button-area {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.creator-panel .join-note {
  width: 100%;
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
  padding: 8px 14px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 10px;
  margin-left: 8px;
  cursor: pointer;
  font-size: 14px;
  min-width: 88px;
}

.stats-row {
  margin-top: 6px;
  color: gray;
  font-size: 14px;
}

.video-description {
  margin: 12px 0;
  padding: 10px 15px;
  background: #fefbff;
  border: 1px solid #fedef0;
  border-radius: 12px;
  box-shadow: 0 4px 10px rgba(255, 105, 180, 0.15);
}

.video-description h4 {
  margin: 0 0 8px;
  font-size: 16px;
  color: #222;
}

.video-description p {
  margin: 0;
  color: #555;
  line-height: 1.5;
  white-space: pre-wrap;
}

/* ================== 推荐视频区域 ================== */

.right-sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: sticky;
  top: 20px;
  padding-right: 4px;
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
  width: 100%;
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

.video-wrapper {
  position: relative;
}

/* .danmu-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
} */

.danmu-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none; /* 不挡住 UI */
  z-index: 5;
}

.danmu-item {
  position: absolute;
  white-space: nowrap;
  text-shadow: 1px 1px 2px black;
  animation: danmu-move 6s linear forwards;
  opacity: v-bind(danmuOpacity);
  font-size: v-bind(danmuFontSize + 'px');
}


@keyframes danmu-move {
  from {
    left: 100%;
  }

  to {
    left: -100%;
  }
}

/* ⚙️ 齿轮按钮 */
.danmu-settings-btn {
  font-size: 20px;
  cursor: pointer;
  padding: 6px;
  border-radius: 6px;
  transition: 0.2s;
}

.danmu-settings-btn:hover {
  background: #eef6ff;
}

/* 设置面板 */
.danmu-settings-panel {
  position: absolute;
  bottom: 100px;        /* 自行调整位置 */
  left: 00px;
  background: #fff;
  border: 1px solid #ddd;
  color: black;
  padding: 12px;
  z-index: 9999;      /* 覆盖所有内容 */
  border-radius: 8px;
}

/* 浮层淡入动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(5px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}


.setting-row {
  margin: 10px 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.setting-row label {
  font-size: 14px;
  width: 70px;
}








.danmu-list {
  color: black;
  background: #fefbff;
  border: 1px solid #fedef0;
  width: 100%;
  margin-top: 10px;
  border-radius: 8px;
  overflow: hidden;
}

.list-header {
  padding: 10px;
  font-size: 18px;
  cursor: pointer;
  background: #fefbff;
}

.danmu-table {
  width: 100%;
  border-collapse: collapse;
  table-layout: fixed;
  /* 让列宽按 th 分配 */
}

.danmu-table th,
.danmu-table td {
  padding: 10px 8px;
  border-bottom: 1px solid #eee;
  text-align: left;
  font-size: 14px;
  color: gray;
}

/* 三个列宽自动分配 */
.danmu-table th:nth-child(1),
.danmu-table td:nth-child(1) {
  width: 80px;
  /* 视频时间列较短 */
}

.danmu-table th:nth-child(2),
.danmu-table td:nth-child(2) {
  width: auto;
  /* 内容列自动占满 */
}

.danmu-table th:nth-child(3),
.danmu-table td:nth-child(3) {
  width: 140px;
  /* 发送时间固定长度 */
}

/* 防止内容过长撑坏布局，自动换行 */
.danmu-text {
  white-space: normal;
  word-break: break-all;
}

.history-btn {
  width: 100%;
  padding: 10px;
  background: #fefbff;
  border: none;
  cursor: pointer;
}


.button {
  padding: 4px 10px;
  margin: 0 4px;
  font-size: 12px;
  color: #333;
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  outline: none;
  transition: all 0.2s ease;
}

/* 悬浮效果 */
.button:hover {
  color: #409eff;
  /* 轻微蓝色高亮 */
  border-color: #c6e2ff;
  background-color: #ecf5ff;
}

/* 点击时 */
.button:active {
  background-color: #d9ecff;
  border-color: #a0cfff;
}

/* .button:disabled {
  cursor: not-allowed;
  color: #bcbec2;
  background-color: #f5f5f5;
  border-color: #e4e7ed;
} */



/* ==================弹幕发送========================== */
.danmu-send-bar {
  position: relative;
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
  padding: 12px 0;
  border-top: 1px solid #eee;
  background: #fff;
}

/* 开关 */
.danmu-switch {
  color: #000;
  display: flex;
  align-items: center;
  cursor: pointer;
  gap: 6px;
  font-size: 14px;
  user-select: none;
}

.switch-icon {
  width: 36px;
  height: 18px;
  background: #ccc;
  border-radius: 18px;
  position: relative;
  transition: 0.25s;
}

.switch-icon::after {
  content: "";
  position: absolute;
  width: 16px;
  height: 16px;
  background: white;
  border-radius: 50%;
  left: 1px;
  top: 1px;
  transition: 0.25s;
}

.switch-icon.on {
  background: #00a1d6;
  /* B站蓝 */
}

.switch-icon.on::after {
  left: 19px;
}

/* 输入框 */
.danmu-input {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  outline: none;
  transition: border 0.2s;
}

.danmu-input:focus {
  border-color: #00a1d6;
}

/* 发送按钮 */
.danmu-send-btn {
  background: #00a1d6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
}

.danmu-send-btn:disabled {
  background: #9fd8ee;
  cursor: not-allowed;
}

.danmu-send-btn:not(:disabled):hover {
  background: #0092c8;
}
</style>
