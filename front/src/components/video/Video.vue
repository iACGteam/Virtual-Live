<template>
  <div class="video-page-container" :class="{ 'with-padding': showBackButton }" ref="pageRef">
    <button v-if="showBackButton" class="back-btn" @click="goBack">
      <span class="back-icon">←</span> 返回
    </button>
    <!-- 左侧内容区 -->
    <div class="left-content">
      <!-- 标题置于视频上方 -->
      <div v-if="videoInfo" class="video-title-row">
        <div class="title-block">
          <h2 class="title">{{ videoInfo.title }}</h2>
          <div class="title-stats">
            <span v-if="videoInfo.views">{{ videoInfo.views }} 次观看</span>
            <span v-if="videoInfo.views" class="dot">·</span>
            <span>{{ videoInfo.commentsCount || 0 }} 条评论</span>
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

        <div v-if="videoInfo" class="action-buttons">
          <button class="act-btn" @click="toggleLike(videoInfo)">👍 {{ videoInfo.likes ? videoInfo.likes : ''
            }}</button>
        </div>

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
      <!-- <div v-if="videoInfo" class="video-meta">
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
      </div> -->

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
          <img class="avatar" :src="videoInfo?.authorAvatar || defaultAvatar" />
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
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Comment from './Comment.vue'
import { addViewHistory, toggleFollow as apiToggleFollow, checkFollow, getVideoById, increaseViewCount, getVideosByCategory, getVideos, toggleVideoLike, checkLike, getUserProfile, getDanmaku, sendDanmaku as apiSendDanmaku } from '@/utils/api'
import { getCurrentUserId } from '@/utils/auth'
import { getDemoAsset } from '@/utils/demoDataMap'
import defaultAvatar from '@/assets/avatar.jpg'

import cover1 from '@/assets/虚拟主播/视频封面/图像 - 1742412405144.封面.jpg'
import cover2 from '@/assets/虚拟主播/视频封面/图像 - “在这里见到我，很惊讶吗？”.封面.jpg'
import cover3 from '@/assets/虚拟主播/视频封面/图像 - 【live2d模型展示】又是白毛与小猫咪（远古库存版）.封面.jpg'
import cover4 from '@/assets/虚拟主播/视频封面/图像 - 【Live2d模型展示】请问您今天要来点猫猫吗.封面.jpg'
import cover5 from '@/assets/虚拟主播/视频封面/图像 - 【live2d量贩模型】jk社恐小黑猫，适合内向宝宝的可爱日常公皮，支持vb.封面.jpg'
import cover6 from '@/assets/虚拟主播/视频封面/图像 - 所有知名虚拟主播的立牌.封面.jpg'
import cover7 from '@/assets/虚拟主播/视频封面/图像 - 超级简单的虚拟形象直播教程！4分钟教会你添加虚拟人物！.封面.jpg'
import video1 from '@/assets/虚拟主播/视频/video-1.mp4'
import video2 from '@/assets/虚拟主播/视频/video-2.mp4'
import video3 from '@/assets/虚拟主播/视频/video-3.mp4'
import video4 from '@/assets/虚拟主播/视频/video-4.mp4'
import video5 from '@/assets/虚拟主播/视频/video-5.mp4'
import video6 from '@/assets/虚拟主播/视频/video-6.mp4'
import video7 from '@/assets/虚拟主播/视频/video-7.mp4'

const router = useRouter()
const route = useRoute()
const showBackButton = computed(() => route.query.from !== 'discover')
const totalComments = ref(0)

const videoCovers = [cover1, cover2, cover3, cover4, cover5, cover6, cover7]
const coverCycle = index => videoCovers[index % videoCovers.length]
const videoSources = [video1, video2, video3, video4, video5, video6, video7]
const shuffledVideoSources = [...videoSources].sort(() => Math.random() - 0.5)
const videoSourceCycle = index => shuffledVideoSources[index % shuffledVideoSources.length]

const localVideos = []

// 监听路由变化，实现同组件跳转刷新
watch(
  () => route.query.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadVideo()
    }
  }
)

// 记录观看历史
const recordHistory = async (vid) => {
  const uid = getCurrentUserId()
  if (uid && vid) {
    try {
      await addViewHistory(uid, vid)
    } catch (e) {
      console.warn('记录历史失败', e)
    }
  }
}

let timeUpdateHandler = null



// 视频源地址
const videoSrc = ref('https://www.w3schools.com/html/mov_bbb.mp4')
// 视频信息
const videoInfo = ref(null)
// 释放 blob 记录
let blobUrl = null



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
  // 先加载视频
  loadVideo()
  updateSidebarHeight()
  window.addEventListener('resize', updateSidebarHeight)

  nextTick(() => {
    const el = videoRef.value
    if (!el) {
      console.warn('videoRef 仍然为 null')
      return
    }

    // 单独定义 handler，方便卸载时 removeEventListener
    timeUpdateHandler = (e) => {
      if (!danmuEnabled.value) return

      const video = e.target
      if (!video || !video.currentTime) return

      const current = Math.floor(video.currentTime)

      danmuList.value
        .filter(dm => dm.videoTimeSec === current)
        .forEach(showDanmu)
    }

    el.addEventListener('timeupdate', timeUpdateHandler)
  })
})



// 展示弹幕
function showDanmu(dm) {
  if (!danmuEnabled.value) return;
  
  // 防止同一条弹幕在短时间内重复显示（解决 timeupdate 多次触发问题）
  if (activeDanmus.value.some(d => d.id === dm.id)) return;

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
// function reportDanmu(dm) {
//   alert("已举报: " + dm.text);
// }
// 屏蔽用户
// function blockUser(user) {
//   alert("已屏蔽用户：" + user);
// }

// 加载弹幕
const loadDanmaku = async () => {
  const vid = route.query.id
  if (!vid) return
  try {
    const res = await getDanmaku(vid)
    // api.js 已经解包了 data.data，所以 res 直接就是弹幕列表
    if (Array.isArray(res)) {
      danmuList.value = res.map(d => ({
        id: d.id,
        text: d.text,
        color: d.color,
        videoTimeSec: Math.floor(d.time),
        videoTime: formatTime(Math.floor(d.time)),
        user: d.username,
        avatar: d.avatarUrl
      }))
    }
  } catch (e) {
    console.error('加载弹幕失败', e)
  }
}

// 监听视频ID变化重新加载弹幕
watch(() => route.query.id, (newId) => {
  if (newId) loadDanmaku()
})

onMounted(() => {
  loadDanmaku()
})

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
async function sendDanmu() {
  if (!danmuEnabled.value) return
  if (!danmuInput.value.trim()) return

  const video = videoRef.value
  if (!video) {
    console.warn('videoRef is null, video not ready yet.')
    return
  }
  
  const uid = getCurrentUserId()
  if (!uid) {
    alert('请先登录')
    return
  }

  const text = danmuInput.value.trim()
  const currentTimeSec = video.currentTime

  const danmuData = {
    text,
    color: '#ffffff', 
    time: currentTimeSec,
    userId: uid
  }

  try {
    const vid = route.query.id
    const res = await apiSendDanmaku(vid, danmuData)
    
    // api.js 已经处理了 code !== 0 的情况并抛出错误，且返回的是 data.data
    const newDanmu = {
      id: res.id,
      text: res.text,
      videoTimeSec: Math.floor(res.time),
      videoTime: formatTime(Math.floor(res.time)),
      user: res.username,
      color: res.color
    }
    danmuList.value.push(newDanmu)
    showDanmu(newDanmu)
    danmuInput.value = ''
  } catch (e) {
    console.error('发送弹幕失败', e)
    alert('发送失败: ' + (e.message || '网络错误'))
  }
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
const toggleLike = async (item) => {
  const uid = getCurrentUserId()
  if (!uid) {
    alert('请先登录')
    return
  }

  try {
    await toggleVideoLike(item.id, uid)
    
    if (!item.liked) {
      item.likes++
      item.liked = true
    } else {
      item.likes--
      item.liked = false
    }
  } catch (e) {
    console.error('点赞操作失败', e)
  }
}


// ======== 关注状态 ========
const isFollowing = ref(false)

const toggleFollow = async () => {
  const uid = getCurrentUserId()
  if (!uid) {
    alert('请先登录')
    return
  }
  
  // 这里假设关注的是视频作者，实际应该用 videoInfo.value.creatorId
  // 暂时用一个模拟ID或从视频信息获取
  const targetId = videoInfo.value?.creatorId || 1 
  
  try {
    // toggleFollow(followingId, userId) -> 关注目标ID，当前用户ID
    await apiToggleFollow(targetId, uid)
    isFollowing.value = !isFollowing.value
  } catch (e) {
    console.warn('关注失败', e)
  }
}

const checkFollowStatus = async () => {
  const uid = getCurrentUserId()
  const targetId = videoInfo.value?.creatorId || 1
  if (uid && targetId) {
    try {
      const status = await checkFollow(uid, targetId)
      isFollowing.value = status
    } catch (e) {
      console.warn('获取关注状态失败', e)
    }
  }
}

const goToChannel = () => {
  if (videoInfo.value && videoInfo.value.creatorId) {
    const currentUserId = getCurrentUserId()
    // Check if the creator is the current user
    if (currentUserId && String(currentUserId) === String(videoInfo.value.creatorId)) {
      router.push({ path: '/profile' })
    } else {
      // If not the owner, go to the visitor profile page
      router.push({ path: '/user-profile', query: { id: videoInfo.value.creatorId } })
    }
  } else {
    router.push({ path: '/profile' })
  }
}

const goBack = () => {
  router.back()
}

// ========= 推荐视频 =========
const recommendedVideos = ref([])

// 点击推荐视频 -> 跳转播放
const openRecommend = (item) => {
  // 如果是同一个视频，不跳转
  if (String(item.id) === String(route.query.id)) return

  // 跳转到新视频
  router.push({
    path: "/video",
    query: { 
      id: item.id,
      src: item.videoSrc // 传递视频源
    }
  }).then(() => {
    // 滚动到顶部
    window.scrollTo(0, 0)
  })
}

const resolveUrl = (url) => {
  if (!url) return 'https://picsum.photos/200/120'
  // Check demo asset map first
  const demoAsset = getDemoAsset(url) 
  if (demoAsset) return demoAsset

  // 如果是 http、blob、data 协议，直接返回
  if (url.startsWith('http') || url.startsWith('blob:') || url.startsWith('data:')) return url
  
  // Handle assets path
  if (url.includes('assets')) {
      let assetPath = url.replace('@/', '/');
      if (!assetPath.startsWith('/')) {
          assetPath = '/' + assetPath;
      }
      return assetPath;
  }

  const cleanUrl = url.startsWith('/') ? url.slice(1) : url
  return `http://127.0.0.1:8081/${cleanUrl}`
}

// 自动加载视频
const loadVideo = async () => {
  const videoId = route.query.id
  if (!videoId) return

  const uid = getCurrentUserId()

  try {
    // 增加播放次数
    try {
      await increaseViewCount(videoId)
    } catch (e) {
      console.warn('增加播放次数失败', e)
    }

    // 1. 获取视频详情
    const data = await getVideoById(videoId)
    if (data) {
      let authorIntro = '暂无简介'
      // 获取作者简介
      if (data.authorId) {
        try {
          const authorProfile = await getUserProfile(data.authorId)
          if (authorProfile && authorProfile.introduction) {
            authorIntro = authorProfile.introduction
          }
        } catch (e) {
          console.warn('获取作者简介失败', e)
        }
      }

      videoInfo.value = {
        id: data.id,
        title: data.title,
        creator: data.authorName,
        creatorId: data.authorId,
        authorAvatar: data.authorAvatar, // 添加作者头像
        subs: authorIntro, // 显示作者简介
        likes: data.likes || 0,
        views: data.views || 0,
        commentsCount: data.commentsCount || 0, // 绑定评论数
        duration: data.duration,
        description: data.content,
        category: data.category,
        liked: false // 默认未点赞
      }

      // 记录观看历史 & 检查点赞状态 & 检查关注状态
      if (uid) {
        addViewHistory(uid, videoId).catch(e => console.warn('记录历史失败', e))
        checkLike(uid, videoId, 'video').then(res => {
          videoInfo.value.liked = res
        }).catch(e => console.warn('检查点赞失败', e))
        
        // 检查关注状态
        if (videoInfo.value.creatorId) {
           checkFollow(uid, videoInfo.value.creatorId).then(res => {
             isFollowing.value = res
           }).catch(e => console.warn('检查关注失败', e))
        }
      }
      
      // 设置视频源：优先使用路由参数中的 src（来自 HomeView 的本地资源）
      if (route.query.src) {
        videoSrc.value = route.query.src
      } else if (data.videoUrl) {
        videoSrc.value = resolveUrl(data.videoUrl)
      } else {
        // 兼容旧的本地存储逻辑（如果有的话）
        // ...
        videoSrc.value = 'https://www.w3schools.com/html/mov_bbb.mp4' // 默认兜底
      }

      // 2. 获取推荐视频（使用本地数据）
      loadRecommendations(data.category)
    }
  } catch (err) {
    console.error('加载视频失败:', err)
    // 即使后端失败，如果前端有 src，也尝试播放
    if (route.query.src) {
       videoSrc.value = route.query.src
       // 尝试从本地数据中恢复 info
       const localInfo = localVideos.find(v => String(v.id) === String(videoId))
       if (localInfo) {
          videoInfo.value = {
             id: localInfo.id,
             title: localInfo.title,
             creator: localInfo.creator,
             views: localInfo.views,
             description: '本地演示视频',
             likes: 0
          }
          loadRecommendations()
       }
    }
  }
}

// 加载推荐视频
const loadRecommendations = async (category) => {
  try {
    let list = []
    if (category) {
      // 如果有分类，获取同分类视频
      const res = await getVideosByCategory(category)
      list = Array.isArray(res) ? res : []
    } else {
      // 否则获取最新视频作为推荐
      const res = await getVideos(0, 10, 'newest')
      list = res?.content || []
    }

    const resolveUrl = (url) => {
      if (!url) return 'https://picsum.photos/200/120'
      if (url.startsWith('http') || url.startsWith('blob:') || url.startsWith('data:') || url.includes('assets')) return url
      const cleanUrl = url.startsWith('/') ? url.slice(1) : url
      return `http://127.0.0.1:8081/${cleanUrl}`
    }
  
    if (Array.isArray(list)) {
      recommendedVideos.value = list
        .filter(v => String(v.id) !== String(route.query.id)) // 排除当前视频
        .filter(v => !v.tags || !v.tags.includes('__PRIVATE__')) // 过滤掉私密视频
        .map(v => ({
          id: v.id,
          title: v.title,
          author: v.authorName || '未知用户',
          views: v.views || 0,
          thumbnail: resolveUrl(v.coverImageUrl),
          videoSrc: resolveUrl(v.videoUrl)
        }))
    }
  } catch (err) {
    console.warn('加载推荐视频失败', err)
    recommendedVideos.value = []
  }
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

// onMounted(() => {
//   loadVideo()
//   updateSidebarHeight()
//   window.addEventListener('resize', updateSidebarHeight)
// })



onBeforeUnmount(() => {
  if (blobUrl) {
    URL.revokeObjectURL(blobUrl)
    blobUrl = null
  }

  window.removeEventListener('resize', updateSidebarHeight)

  const el = videoRef.value
  if (el && timeUpdateHandler) {
    el.removeEventListener('timeupdate', timeUpdateHandler)
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
  overflow-x: hidden;
  position: relative;
}

.video-page-container.with-padding {
  padding-top: 70px;
}

.back-btn {
  position: absolute;
  top: 20px;
  left: 20px;
  z-index: 100;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 20px;
  color: #ff69b4;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
}

.back-btn:hover {
  background: #fff;
  transform: translateX(-2px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.25);
}

.back-icon {
  font-size: 1.2rem;
  line-height: 1;
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
  /* justify-content: flex-end; */
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
  border-radius: 50%;
  object-fit: cover;
  background-color: #f0f0f0;
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
  border: none;
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
  pointer-events: none;
  /* 不挡住 UI */
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
  bottom: 100px;
  /* 自行调整位置 */
  left: 120px;
  background: #fff;
  border: 1px solid #ddd;
  color: black;
  padding: 12px;
  padding-top: 0px;
  z-index: 9999;
  /* 覆盖所有内容 */
  border-radius: 8px;
}


.danmu-settings-pane .h4 {
  margin: 0px;
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
  background: #ff69b4;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: 0.2s;
}

.danmu-send-btn:disabled {
  background: #f7c5de;
  cursor: not-allowed;
}

.danmu-send-btn:not(:disabled):hover {
  background: #f7429d;
}
</style>
