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
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import Comment from './Comment.vue'
import { addViewHistory, toggleFollow as apiToggleFollow, checkFollow, getVideoById, getVideosByCategory, getVideos, toggleVideoLike, checkLike } from '@/utils/api'
import { getCurrentUserId } from '@/utils/auth'

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
const totalComments = ref(0)

const videoCovers = [cover1, cover2, cover3, cover4, cover5, cover6, cover7]
const coverCycle = index => videoCovers[index % videoCovers.length]
const videoSources = [video1, video2, video3, video4, video5, video6, video7]
const shuffledVideoSources = [...videoSources].sort(() => Math.random() - 0.5)
const videoSourceCycle = index => shuffledVideoSources[index % shuffledVideoSources.length]

const localVideos = [
  {
    id: 1,
    title: '星海航线直播幕后花絮',
    creator: 'NebulaNova',
    duration: '02:18',
    views: '5.8万次观看',
    tags: ['虚拟singer'],
    thumbnail: coverCycle(0),
    videoSrc: videoSourceCycle(0)
  },
  {
    id: 2,
    title: '虚拟偶像舞台 · 夜幕版本',
    creator: 'LumiRay',
    duration: '01:05',
    views: '3.1万次观看',
    tags: ['虚拟男V'],
    thumbnail: coverCycle(1),
    videoSrc: videoSourceCycle(1)
  },
  {
    id: 3,
    title: '粉丝互动问答高能合集',
    creator: 'KiraEcho',
    duration: '03:44',
    views: '2.4万次观看',
    tags: ['虚拟gamer'],
    thumbnail: coverCycle(2),
    videoSrc: videoSourceCycle(2)
  },
  {
    id: 4,
    title: '全息角色建模 timelapse',
    creator: 'MoriTech',
    duration: '02:57',
    views: '1.9万次观看',
    tags: ['虚拟声优'],
    thumbnail: coverCycle(3),
    videoSrc: videoSourceCycle(3)
  },
  {
    id: 5,
    title: '赛博朋克主题竖屏 MV',
    creator: 'Vexa',
    duration: '01:42',
    views: '4.6万次观看',
    tags: ['虚拟singer'],
    thumbnail: coverCycle(4),
    videoSrc: videoSourceCycle(4)
  },
  {
    id: 6,
    title: '直播事故剪辑：趣味合集',
    creator: 'Patchy',
    duration: '02:10',
    views: '6.2万次观看',
    tags: ['虚拟gamer'],
    thumbnail: coverCycle(5),
    videoSrc: videoSourceCycle(5)
  },
  {
    id: 7,
    title: 'AI 虚拟形象调教日常',
    creator: 'SigmaBot',
    duration: '01:33',
    views: '3.7万次观看',
    tags: ['虚拟声优'],
    thumbnail: coverCycle(6),
    videoSrc: videoSourceCycle(6)
  },
  {
    id: 8,
    title: '赛博城市观光 Vlog',
    creator: 'MetroMuse',
    duration: '02:05',
    views: '2.9万次观看',
    tags: ['虚拟男V'],
    thumbnail: coverCycle(7),
    videoSrc: videoSourceCycle(7)
  },
  {
    id: 9,
    title: '虚拟美食节目 · 宇宙餐桌',
    creator: 'ChefNova',
    duration: '03:12',
    views: '4.2万次观看',
    tags: ['虚拟男V'],
    thumbnail: coverCycle(8),
    videoSrc: videoSourceCycle(8)
  },
  {
    id: 10,
    title: '电竞解说高燃瞬间',
    creator: 'CasterRay',
    duration: '01:58',
    views: '7.6万次观看',
    tags: ['虚拟男V'],
    thumbnail: coverCycle(9),
    videoSrc: videoSourceCycle(9)
  },
  {
    id: 11,
    title: '深夜电台 · 陪伴系列',
    creator: 'EchoWave',
    duration: '04:05',
    views: '3.3万次观看',
    tags: ['虚拟声优'],
    thumbnail: coverCycle(10),
    videoSrc: videoSourceCycle(10)
  },
  {
    id: 12,
    title: '全息舞狮春节特辑',
    creator: 'Dynasty Duo',
    duration: '02:26',
    views: '5.1万次观看',
    tags: ['虚拟singer'],
    thumbnail: coverCycle(11),
    videoSrc: videoSourceCycle(11)
  },
  {
    id: 13,
    title: '音乐制作直播：即时 Remix',
    creator: 'BeatForge',
    duration: '02:48',
    views: '4.9万次观看',
    tags: ['虚拟singer'],
    thumbnail: coverCycle(12),
    videoSrc: videoSourceCycle(12)
  },
  {
    id: 14,
    title: '虚拟野外求生挑战',
    creator: 'WildBytes',
    duration: '03:20',
    views: '2.2万次观看',
    tags: ['虚拟gamer'],
    thumbnail: coverCycle(13),
    videoSrc: videoSourceCycle(13)
  },
  {
    id: 15,
    title: '粉丝共创剧情互动剧',
    creator: 'StorySync',
    duration: '03:08',
    views: '6.8万次观看',
    tags: ['虚拟声优'],
    thumbnail: coverCycle(14),
    videoSrc: videoSourceCycle(14)
  }
]

// 监听路由变化，实现同组件跳转刷新
watch(
  () => route.query.id,
  (newId, oldId) => {
    if (newId && newId !== oldId) {
      loadVideo()
      // 记录观看历史
      recordHistory(newId)
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
  if (!danmuEnabled.value) return
  if (!danmuInput.value.trim()) return

  const video = videoRef.value
  if (!video) {
    console.warn('videoRef is null, video not ready yet.')
    return
  }

  const text = danmuInput.value.trim()
  const currentTimeSec = Math.floor(video.currentTime)

  const newDanmu = {
    id: Date.now(),
    text,
    videoTimeSec: currentTimeSec,
    videoTime: formatTime(currentTimeSec),
    user: '你自己',
    sendTime: new Date().toLocaleString(),
  }

  danmuList.value.push(newDanmu)
  showDanmu(newDanmu)
  danmuInput.value = ''
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
    await apiToggleFollow(uid, targetId)
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
  // 如果是 http、blob、data 协议，或者是本地资源路径（包含 assets），直接返回
  if (url.startsWith('http') || url.startsWith('blob:') || url.startsWith('data:') || url.includes('assets')) return url
  const cleanUrl = url.startsWith('/') ? url.slice(1) : url
  return `http://127.0.0.1:8081/${cleanUrl}`
}

// 自动加载视频
const loadVideo = async () => {
  const videoId = route.query.id
  if (!videoId) return

  const uid = getCurrentUserId()

  try {
    // 1. 获取视频详情
    const data = await getVideoById(videoId)
    if (data) {
      videoInfo.value = {
        id: data.id,
        title: data.title,
        creator: data.authorName,
        creatorId: data.authorId,
        subs: '6666', // 暂时没有订阅数
        likes: data.likes || 0,
        views: data.views || 0,
        commentsCount: data.commentsCount || 0, // 绑定评论数
        duration: data.duration,
        description: data.content,
        category: data.category,
        liked: false // 默认未点赞
      }

      // 记录观看历史 & 检查点赞状态
      if (uid) {
        addViewHistory(uid, videoId).catch(e => console.warn('记录历史失败', e))
        checkLike(uid, videoId, 'video').then(res => {
          videoInfo.value.liked = res
        }).catch(e => console.warn('检查点赞失败', e))
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
  // 使用本地数据作为推荐源，模拟“迁移过来”的效果
  const list = localVideos
  
  if (Array.isArray(list)) {
    recommendedVideos.value = list
      .filter(v => String(v.id) !== String(route.query.id)) // 排除当前视频
      .map(v => ({
        id: v.id,
        title: v.title,
        author: v.creator,
        views: v.views || 0,
        thumbnail: v.thumbnail,
        videoSrc: v.videoSrc // 确保传递 src
      }))
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

onMounted(async () => {
  loadVideo()
  updateSidebarHeight()
  window.addEventListener('resize', updateSidebarHeight)
  
  // 初始加载时记录历史
  if (route.query.id) {
    await recordHistory(route.query.id)
  }
  
  // 检查关注状态
  await checkFollowStatus()
})

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
