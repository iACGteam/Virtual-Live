<template>
  <div class="home-page">
    <aside class="sidebar">
      <div class="brand">
        <div class="logo">VL</div>
        <div class="brand-text">
          <h1>VirtuaLive</h1>
          <p>虚拟互动中心</p>
        </div>
      </div>

      <nav class="nav-links">
        <button
          v-for="link in navLinks"
          :key="link.key"
          :class="['nav-link', { active: activeNav === link.key }]"
          @click="handleNavClick(link)"
        >
          <span class="icon">{{ link.icon }}</span>
          <span>{{ link.label }}</span>
        </button>
      </nav>
    </aside>

    <main class="content">
      <section class="search-bar">
        <div class="search-input">
          <div class="search-field">
            <span class="search-icon">🔍</span>
            <input
              v-model="searchQuery"
              type="text"
              placeholder="搜索作品名或用户名..."
              @keyup.enter="handleSearch"
            >
          </div>
          <span class="search-divider"></span>
          <button class="search-btn" @click="handleSearch">搜索</button>
        </div>

        <div class="search-actions">
          <div 
            class="action-dropdown"
            @mouseenter="handlePostDropdownEnter"
            @mouseleave="handlePostDropdownLeave"
          >
            <button class="action-btn primary dropdown-toggle">投稿</button>
            <div 
              class="dropdown-menu"
              :class="{ 'dropdown-visible': showPostDropdown }"
              @mouseenter="handlePostDropdownEnter"
              @mouseleave="handlePostDropdownLeave"
            >
              <button class="dropdown-item" @click="goToUploadVideo">发布视频</button>
              <button class="dropdown-item" @click="goToGoingLive">开直播</button>
            </div>
          </div>
          <div class="avatar-dropdown">
            <button class="avatar-btn" @click="goToProfile">
              <img :src="userProfile.avatar" alt="用户头像">
            </button>
            <div class="profile-panel">
              <div class="profile-header">
                <div class="profile-avatar">
                  <img :src="userProfile.avatar" alt="用户头像">
                </div>
                <div>
                  <p class="profile-name">{{ userProfile.name }}</p>
                  <p class="profile-stats">
                    关注 {{ userProfile.followings }} · 粉丝 {{ userProfile.followers }}
                  </p>
                </div>
                <span class="profile-badge">VIP</span>
              </div>

              <section class="favorite-section">
                <div class="favorites-hover-zone">
                  <div
                    class="section-title clickable"
                    role="button"
                    tabindex="0"
                    @click="goToLikes"
                    @keydown.enter.prevent="goToLikes"
                    @keydown.space.prevent="goToLikes"
                  >
                    <span>❤️ 我的喜欢</span>
                    <span class="section-count">{{ userProfile.likes }}</span>
                  </div>
                  <div class="favorite-cards">
                    <article
                      v-for="fav in userProfile.favorites"
                      :key="fav.id"
                      class="favorite-card"
                    >
                      <div class="fav-thumb" :style="{ background: fav.gradient }">
                        <span>{{ fav.tag }}</span>
                      </div>
                      <p>{{ fav.title }}</p>
                    </article>
                  </div>
                </div>
              </section>

              <section class="quick-links">
                <button
                  v-for="link in userProfile.quickEntries"
                  :key="link.label"
                  class="quick-link"
                  @click="handleQuickEntry(link)"
                >
                  <div class="ql-left">
                    <span class="ql-icon">{{ link.icon }}</span>
                    <span>{{ link.label }}</span>
                  </div>
                  <span class="ql-value">{{ link.value }}</span>
                </button>
              </section>

              <section class="secondary-links">
                <button
                  v-for="link in userProfile.secondaryEntries"
                  :key="link.label"
                  class="secondary-link"
                >
                  <span>{{ link.icon }}</span>
                  <span>{{ link.label }}</span>
                </button>
              </section>

              <section class="profile-footer">
                <button class="logout" @click="handleLogout">退出登录</button>
                <label class="remember-toggle">
                  <input type="checkbox" v-model="userProfile.rememberLogin">
                  <span>保存登录信息</span>
                </label>
              </section>
            </div>
          </div>
        </div>
      </section>

      <section class="feed-header">
        <div class="filters">
          <button 
            :class="['filter-btn', { active: activeFilter === 'recommend' }]"
            @click="setFilter('recommend')"
          >推荐</button>
          <button 
            :class="['filter-btn', { active: activeFilter === 'following' }]"
            @click="setFilter('following')"
          >关注</button>
          <button 
            v-for="topic in topics"
            :key="topic.key"
            :class="['filter-btn', { active: activeFilter === topic.key }]"
            @click="setFilter(topic.key)"
          >{{ topic.label }}</button>
        </div>
      </section>

      <section class="video-grid">
        <article
          v-for="video in filteredVideos"
          :key="video.id"
          class="video-card"
        >
          <div class="thumbnail" :style="{ background: video.thumbnailColor }">
            <span class="duration">{{ video.duration }}</span>
          </div>
          <div class="video-meta">
            <h3>{{ video.title }}</h3>
            <p class="creator" @click.stop="goToUserProfile(video.creator)">@{{ video.creator }}</p>
            <p class="stats">{{ video.views }} · {{ video.tags.join(' · ') }}</p>
          </div>
        </article>
      </section>
    </main>
  </div>
</template>

<script>
import avatarImg from '@/assets/avatar.jpg'
import { clearAuthToken } from '@/utils/auth'
export default {
  name: 'HomeView',
  data() {
    return {
      activeNav: 'discover',
      searchQuery: '',
      showPostDropdown: false,
      postDropdownTimer: null,
      activeFilter: 'recommend', // 'recommend', 'following', 或主题key
      topics: [
        { key: 'music', label: '音乐', icon: '🎵' },
        { key: 'dance', label: '舞蹈', icon: '💃' },
        { key: 'game', label: '游戏', icon: '🎮' },
        { key: 'tech', label: '科技', icon: '🔬' },
        { key: 'food', label: '美食', icon: '🍔' },
        { key: 'sport', label: '运动', icon: '⚽' }
      ],
      // 关注用户列表（从 ProfileView 中获取的关注用户）
      followingUsers: ['NebulaNova', 'LumiRay', 'KiraEcho', 'DANK1NG', 'NiKo', 'reailty', '森阳(无畏契约)'],
      navLinks: [
        { key: 'discover', label: '发现内容', icon: '✨' },
        { key: 'live', label: '直播', icon: '📡' },
        { key: 'community', label: '社区', icon: '💬' },
        { key: 'my', label: '我的', icon: '' }
      ],
      userProfile: {
        initials: 'VL',
        avatar: avatarImg,
        name: 'zk3zy',
        followings: 250,
        followers: 86,
        likes: '3.0万',
        favorites: [
          { id: 1, tag: '#Live', title: '治愈童声 #见面会', gradient: 'linear-gradient(125deg, #fdfcfb 0%, #e2d1c3 100%)' },
          { id: 2, tag: '#校园', title: '大学生惊喜一天', gradient: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)' },
          { id: 3, tag: '#MV', title: 'GALI 新歌上线', gradient: 'linear-gradient(135deg, #f6d365 0%, #fda085 100%)' }
        ],
        quickEntries: [
          { key: 'history', icon: '🕒', label: '观看历史', value: '30天内' },
          { key: 'works', icon: '🎬', label: '我的作品', value: '0' }
        ],
        rememberLogin: true
      },
      shortVideos: [
        {
          id: 1,
          title: '星海航线直播幕后花絮',
          creator: 'NebulaNova',
          duration: '02:18',
          views: '5.8万次观看',
          tags: ['LiveCut', 'Sci-Fi'],
          thumbnailColor: 'linear-gradient(135deg, #FF61D2 0%, #FE9090 100%)'
        },
        {
          id: 2,
          title: '虚拟偶像舞台 · 夜幕版本',
          creator: 'LumiRay',
          duration: '01:05',
          views: '3.1万次观看',
          tags: ['Dance', 'Stage'],
          thumbnailColor: 'linear-gradient(135deg, #42E695 0%, #3BB2B8 100%)'
        },
        {
          id: 3,
          title: '粉丝互动问答高能合集',
          creator: 'KiraEcho',
          duration: '03:44',
          views: '2.4万次观看',
          tags: ['Clips', 'Q&A'],
          thumbnailColor: 'linear-gradient(135deg, #A18CD1 0%, #FBC2EB 100%)'
        },
        {
          id: 4,
          title: '全息角色建模 timelapse',
          creator: 'MoriTech',
          duration: '02:57',
          views: '1.9万次观看',
          tags: ['MakingOf', '3D'],
          thumbnailColor: 'linear-gradient(135deg, #F6D365 0%, #FDA085 100%)'
        },
        {
          id: 5,
          title: '赛博朋克主题竖屏 MV',
          creator: 'Vexa',
          duration: '01:42',
          views: '4.6万次观看',
          tags: ['Music', 'Cyber'],
          thumbnailColor: 'linear-gradient(135deg, #5EFCE8 0%, #736EFE 100%)'
        },
        {
          id: 6,
          title: '直播事故剪辑：趣味合集',
          creator: 'Patchy',
          duration: '02:10',
          views: '6.2万次观看',
          tags: ['Fun', 'Live'],
          thumbnailColor: 'linear-gradient(135deg, #FAD961 0%, #F76B1C 100%)'
        },
        {
          id: 7,
          title: 'AI 虚拟形象调教日常',
          creator: 'SigmaBot',
          duration: '01:33',
          views: '3.7万次观看',
          tags: ['AI', 'BehindScenes'],
          thumbnailColor: 'linear-gradient(135deg, #FF9966 0%, #FF5E62 100%)'
        },
        {
          id: 8,
          title: '赛博城市观光 Vlog',
          creator: 'MetroMuse',
          duration: '02:05',
          views: '2.9万次观看',
          tags: ['Vlog', 'City'],
          thumbnailColor: 'linear-gradient(135deg, #8EC5FC 0%, #E0C3FC 100%)'
        },
        {
          id: 9,
          title: '虚拟美食节目 · 宇宙餐桌',
          creator: 'ChefNova',
          duration: '03:12',
          views: '4.2万次观看',
          tags: ['Food', 'Show'],
          thumbnailColor: 'linear-gradient(135deg, #FBD786 0%, #f7797d 100%)'
        },
        {
          id: 10,
          title: '电竞解说高燃瞬间',
          creator: 'CasterRay',
          duration: '01:58',
          views: '7.6万次观看',
          tags: ['Esports', 'Highlights'],
          thumbnailColor: 'linear-gradient(135deg, #43C6AC 0%, #F8FFAE 100%)'
        },
        {
          id: 11,
          title: '深夜电台 · 陪伴系列',
          creator: 'EchoWave',
          duration: '04:05',
          views: '3.3万次观看',
          tags: ['Podcast', 'Chill'],
          thumbnailColor: 'linear-gradient(135deg, #1e3c72 0%, #2a5298 100%)'
        },
        {
          id: 12,
          title: '全息舞狮春节特辑',
          creator: 'Dynasty Duo',
          duration: '02:26',
          views: '5.1万次观看',
          tags: ['Festival', 'Dance'],
          thumbnailColor: 'linear-gradient(135deg, #f5515f 0%, #9f041b 100%)'
        },
        {
          id: 13,
          title: '音乐制作直播：即时 Remix',
          creator: 'BeatForge',
          duration: '02:48',
          views: '4.9万次观看',
          tags: ['Music', 'Remix'],
          thumbnailColor: 'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'
        },
        {
          id: 14,
          title: '虚拟野外求生挑战',
          creator: 'WildBytes',
          duration: '03:20',
          views: '2.2万次观看',
          tags: ['Adventure', 'Challenge'],
          thumbnailColor: 'linear-gradient(135deg, #134E5E 0%, #71B280 100%)'
        },
        {
          id: 15,
          title: '粉丝共创剧情互动剧',
          creator: 'StorySync',
          duration: '03:08',
          views: '6.8万次观看',
          tags: ['Interactive', 'Story'],
          thumbnailColor: 'linear-gradient(135deg, #F4C4F3 0%, #FC67FA 100%)'
        }
      ]
    }
  },
  methods: {
      handleNavClick(link) {
        this.activeNav = link.key
        if (link.key === 'my') {
          this.$router.push({ path: '/profile' })
          return
        }
        const routeMap = {
          discover: '/',
          live: '/live',
          community: '/community'
        }
        const target = routeMap[link.key] || '/'
        if (this.$route.path !== target) {
          this.$router.push(target)
        }
      },
    goToLikes() {
      this.navigateToProfileTab('likes')
    },
    goToProfile() {
      this.$router.push({ path: '/profile' }).catch(() => {})
    },
    handleQuickEntry(link) {
      if (!link?.key) return
      this.navigateToProfileTab(link.key)
    },
    navigateToProfileTab(tabKey) {
      try {
        if (tabKey) {
          sessionStorage.setItem('pendingProfileTab', tabKey)
        } else {
          sessionStorage.removeItem('pendingProfileTab')
        }
      } catch (err) {
        console.warn('记录个人页目标标签失败', err)
      }
      this.$router.push({ path: '/profile' }).catch(() => {})
    },
    handleLogout() {
      clearAuthToken()
      this.$router.push({ name: 'login' }).catch(() => {})
    },
    handlePostDropdownEnter() {
      if (this.postDropdownTimer) {
        clearTimeout(this.postDropdownTimer)
        this.postDropdownTimer = null
      }
      this.showPostDropdown = true
    },
    handlePostDropdownLeave() {
      this.postDropdownTimer = setTimeout(() => {
        this.showPostDropdown = false
        this.postDropdownTimer = null
      }, 300)
    },
    goToUploadVideo() {
      this.$router.push({ path: '/upload-video' }).catch(() => {})
    },
    goToGoingLive() {
      this.$router.push({ path: '/live-going' }).catch(() => {})
    },
    goToUserProfile(creator) {
      // 跳转到个人信息页面
      // 如果需要显示特定用户的信息，可以通过 query 参数传递用户名
      this.$router.push({ path: '/profile', query: { user: creator } }).catch(() => {})
    },
    setFilter(filterType) {
      this.activeFilter = filterType
    },
    handleSearch() {
      // 搜索功能通过 v-model 和计算属性自动实现
      // 这里可以添加额外的搜索逻辑，如搜索历史记录等
    }
  },
  computed: {
      filteredVideos() {
        let videos = []
        
        // 先根据筛选条件过滤
        if (this.activeFilter === 'recommend') {
          // 推荐：显示所有视频
          videos = this.shortVideos
        } else if (this.activeFilter === 'following') {
          // 关注：只显示关注用户的视频
          videos = this.shortVideos.filter(video => 
            this.followingUsers.includes(video.creator)
          )
        } else {
          // 主题筛选：根据主题匹配标签
          const topicMap = {
            music: ['Music', 'MV', 'Remix', 'Podcast'],
            dance: ['Dance', 'Stage'],
            game: ['Esports', 'Gaming'],
            tech: ['AI', '3D', 'MakingOf', 'Sci-Fi', 'Cyber'],
            food: ['Food', 'Show'],
            sport: ['Esports', 'Challenge', 'Adventure']
          }
          const topicTags = topicMap[this.activeFilter] || []
          videos = this.shortVideos.filter(video => 
            video.tags.some(tag => topicTags.includes(tag))
          )
        }
        
        // 再根据搜索关键词过滤（搜索作品名或用户名）
        if (this.searchQuery && this.searchQuery.trim()) {
          const query = this.searchQuery.trim().toLowerCase()
          videos = videos.filter(video => 
            video.title.toLowerCase().includes(query) ||
            video.creator.toLowerCase().includes(query)
          )
        }
        
        return videos
      }
    },
    mounted() {
      // 根据当前路由设置激活的导航项
      if (this.$route.path === '/') {
        this.activeNav = 'discover'
      }
    },
    beforeUnmount() {
      if (this.postDropdownTimer) {
        clearTimeout(this.postDropdownTimer)
        this.postDropdownTimer = null
      }
  }
}
</script>

<style scoped>
.home-page {
  display: grid;
  grid-template-columns: 260px 1fr;
  height: 100vh;
  background: #0f1016;
  color: #fff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft Yahei', sans-serif;
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  letter-spacing: 1px;
}

.brand-text h1 {
  font-size: 1.2rem;
  margin: 0;
}

.brand-text p {
  margin: 4px 0 0;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.85rem;
}

.nav-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.75);
  font-size: 0.95rem;
  text-align: left;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.nav-link .icon {
  font-size: 1.1rem;
}

.nav-link.active,
.nav-link:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.cta-card {
  margin-top: auto;
  padding: 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ff6cab 0%, #7366ff 100%);
  text-align: left;
}

.cta-card h3 {
  margin: 0 0 8px;
}

.cta-card p {
  margin: 0 0 16px;
  color: rgba(255, 255, 255, 0.85);
}

.cta-btn {
  width: 100%;
  padding: 10px 0;
  border: none;
  border-radius: 12px;
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.content {
  padding: 40px 48px;
  overflow-y: auto;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.search-input {
  flex: 1;
  max-width: none;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.12);
  overflow: hidden;
  transition: border-color 0.3s ease, background-color 0.3s ease;
}

.search-input:hover {
  border-color: rgba(255, 255, 255, 0.25);
  background-color: rgba(255, 255, 255, 0.08);
}

.search-input:focus-within {
  border-color: rgba(255, 255, 255, 0.3);
  background-color: rgba(255, 255, 255, 0.08);
}

.search-field {
  display: flex;
  align-items: center;
  flex: 1;
  gap: 12px;
  padding: 0 10px;
}

.search-field input {
  flex: 1;
  border: none;
  background: transparent;
  color: #fff;
  font-size: 0.95rem;
}

.search-field input:focus {
  outline: none;
}

.search-field input::placeholder {
  color: rgba(255, 255, 255, 0.55);
}

.search-icon {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.7);
}

.search-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.15);
}

.search-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 0 12px 12px 0;
  background: rgba(255, 255, 255, 0.07);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  border-left: 1px solid rgba(255, 255, 255, 0.15);
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  justify-content: flex-end;
}

.avatar-dropdown {
  position: relative;
}

.action-dropdown {
  position: relative;
}

.action-btn {
  padding: 10px 18px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease;
}

.action-btn.primary {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  border-color: transparent;
}

.action-btn.secondary:hover {
  background: rgba(255, 255, 255, 0.15);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  min-width: 160px;
  background: rgba(15, 16, 22, 0.95);
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 14px;
  padding: 10px 8px 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.35);
  opacity: 0;
  pointer-events: none;
  transform: translateY(8px);
  transition: opacity 0.15s ease, transform 0.15s ease;
  z-index: 5;
}

.dropdown-menu::before {
  content: '';
  position: absolute;
  top: -12px;
  left: 0;
  width: 100%;
  height: 12px;
}

.action-dropdown:hover .dropdown-menu,
.dropdown-menu.dropdown-visible {
  opacity: 1;
  pointer-events: auto;
  transform: translateY(0);
}

.dropdown-item {
  padding: 10px 14px;
  border: none;
  border-radius: 10px;
  background: transparent;
  color: #fff;
  text-align: left;
  cursor: pointer;
  font-weight: 500;
}

.dropdown-item:hover {
  background: rgba(255, 255, 255, 0.08);
}

.profile-panel {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  width: 320px;
  background: linear-gradient(180deg, rgba(27, 29, 39, 0.98) 0%, rgba(12, 13, 18, 0.98) 100%);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 24px;
  padding: 20px;
  box-shadow: 0 24px 60px rgba(0, 0, 0, 0.55);
  opacity: 0;
  pointer-events: none;
  transform: translateY(8px);
  transition: opacity 0.18s ease, transform 0.18s ease;
  z-index: 10;
}

.profile-panel::before {
  content: '';
  position: absolute;
  top: -16px;
  right: 0;
  width: 48px;
  height: 16px;
  pointer-events: auto;
}

.avatar-dropdown:hover .profile-panel {
  opacity: 1;
  pointer-events: auto;
  transform: translateY(0);
}

.profile-header {
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 18px;
}

.profile-avatar {
  width: 54px;
  height: 54px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.12);
  overflow: hidden;
}

.profile-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.profile-name {
  margin: 0;
  font-weight: 600;
}

.profile-stats {
  margin: 4px 0 0;
  color: rgba(255, 255, 255, 0.65);
  font-size: 0.9rem;
}

.profile-badge {
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  font-size: 0.75rem;
  letter-spacing: 0.08em;
}

.favorite-section {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 18px;
  padding: 12px;
  margin-bottom: 16px;
}

.favorites-hover-zone {
  position: relative;
}

.section-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-weight: 600;
}

.section-title.clickable {
  cursor: pointer;
}

.favorite-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  padding: 12px 0 0;
  opacity: 0;
  pointer-events: none;
  transform: translateY(-6px);
  max-height: 0;
  overflow: hidden;
  transition: opacity 0.18s ease, transform 0.18s ease, max-height 0.18s ease;
}

.favorites-hover-zone:hover .favorite-cards,
.favorites-hover-zone:focus-within .favorite-cards {
  opacity: 1;
  pointer-events: auto;
  transform: translateY(0);
  max-height: 200px;
}

.favorite-card {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 14px;
  padding: 6px;
  font-size: 0.75rem;
}

.fav-thumb {
  border-radius: 10px;
  height: 60px;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  padding: 6px;
  font-size: 0.65rem;
  font-weight: 600;
}

.favorite-card p {
  margin: 6px 0 0;
  color: rgba(255, 255, 255, 0.75);
  line-height: 1.2;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.quick-link {
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-radius: 14px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}

.quick-link:hover,
.quick-link:focus-visible {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateX(2px);
}

.ql-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.ql-value {
  color: rgba(255, 255, 255, 0.65);
}

.secondary-links {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.secondary-link {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.75);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  cursor: pointer;
}

.profile-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logout {
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: transparent;
  color: #fff;
  border-radius: 999px;
  padding: 6px 14px;
  cursor: pointer;
}

.remember-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
}

.remember-toggle input {
  accent-color: #7366ff;
}
.avatar-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  letter-spacing: 0.05em;
  cursor: pointer;
  overflow: hidden;
  padding: 0;
}

.avatar-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.feed-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 24px;
  margin-bottom: 32px;
}

.eyebrow {
  text-transform: uppercase;
  letter-spacing: 0.2em;
  font-size: 0.75rem;
  color: rgba(255, 255, 255, 0.5);
  margin: 0 0 8px;
}

.feed-header h2 {
  margin: 0 0 8px;
  font-size: 1.75rem;
}

.subtext {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.filter-btn {
  padding: 10px 16px;
  border-radius: 999px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease, border-color 0.2s ease;
  white-space: nowrap;
}

.filter-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.3);
}

.filter-btn.active {
  background: #fff;
  color: #0f1016;
  border-color: #fff;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.video-card {
  background: rgba(255, 255, 255, 0.04);
  border-radius: 18px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

.thumbnail {
  position: relative;
  border-radius: 14px;
  height: 160px;
  overflow: hidden;
}

.duration {
  position: absolute;
  bottom: 12px;
  right: 12px;
  padding: 4px 10px;
  background: rgba(0, 0, 0, 0.65);
  border-radius: 999px;
  font-size: 0.8rem;
}

.video-meta h3 {
  margin: 0;
  font-size: 1rem;
}

.creator {
  margin: 4px 0;
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: color 0.2s ease;
}

.creator:hover {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: underline;
}

.stats {
  margin: 0;
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
}

@media (max-width: 960px) {
  .home-page {
    grid-template-columns: 1fr;
  }

  .video-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .search-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-actions {
    width: 100%;
    justify-content: space-between;
  }

  .sidebar {
    position: sticky;
    top: 0;
    flex-direction: row;
    align-items: center;
    gap: 16px;
    border-right: none;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    overflow-x: auto;
  }

  .nav-links {
    flex-direction: row;
  }

  .content {
    padding: 24px;
  }

  .feed-header {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
