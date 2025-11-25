<template>
  <div class="profile-page">
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

    <main class="profile-content">
      <section class="search-bar">
      <div class="search-input">
        <div class="search-field">
          <span class="search-icon">🔍</span>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="搜索你感兴趣的内容..."
          >
        </div>
        <span class="search-divider"></span>
        <button class="search-btn">搜索</button>
      </div>

      <div class="search-actions">
        <div class="action-dropdown">
          <button class="action-btn primary dropdown-toggle">投稿</button>
          <div class="dropdown-menu">
            <button class="dropdown-item" @click="goToUploadVideo">发布视频</button>
            <button class="dropdown-item">开直播</button>
          </div>
        </div>
        <div class="avatar-dropdown">
          <button class="avatar-btn">
            <img :src="panel.avatar" alt="用户头像">
          </button>
          <div class="profile-panel">
            <div class="profile-header">
              <div class="profile-avatar">
                <img :src="panel.avatar" alt="用户头像">
              </div>
              <div>
                <p class="profile-name">{{ panel.name }}</p>
                <p class="profile-stats">
                  关注 {{ panel.followings }} · 粉丝 {{ panel.followers }}
                </p>
              </div>
            </div>

            <section class="favorite-section">
              <div class="favorites-hover-zone">
                <div
                  class="section-title clickable"
                  role="button"
                  tabindex="0"
                  @click="openLikesTab"
                  @keydown.enter.prevent="openLikesTab"
                  @keydown.space.prevent="openLikesTab"
                >
                  <span>❤️ 我的喜欢</span>
                  <span class="section-count">{{ panel.likes }}</span>
                </div>
                <div class="favorite-cards">
                  <article
                    v-for="fav in panel.favorites"
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
                v-for="link in panel.quickEntries"
                :key="link.label"
                class="quick-link"
                @click="handlePanelQuickEntry(link)"
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
                v-for="link in panel.secondaryEntries"
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
                <input type="checkbox" v-model="panel.rememberLogin">
                <span>保存登录信息</span>
              </label>
            </section>
          </div>
        </div>
      </div>
      </section>

      <section class="hero">
        <div class="cover"></div>
        <div class="profile-card">
          <img class="avatar" :src="user.avatar" alt="avatar">
          <div class="info">
            <div class="name-row">
              <h1>{{ user.name }}</h1>
              <button class="edit-btn" @click="openEditModal">
                ✏️
              </button>
            </div>
            <div class="stats">
              <span class="stat-item clickable" @click="openFollowModal('following')">关注 {{ user.following }}</span>
              <span class="stat-item clickable" @click="openFollowModal('followers')">粉丝 {{ user.followers }}</span>
              <span>获赞 {{ user.likes }}</span>
            </div>
            <p class="signature">{{ user.signature }}</p>
          </div>
        </div>
      </section>

      <section class="content-area" ref="tabSection">
        <div class="tabs-wrapper">
          <ul class="tabs">
            <li
              v-for="tab in tabs"
              :key="tab.key"
              :class="{ active: tab.key === activeTab }"
              @click="activeTab = tab.key"
            >
              {{ tab.label }}
              <span v-if="tab.key === 'works' && worksCount > 0">{{ worksCount }}</span>
              <span v-else-if="tab.badge">{{ tab.badge }}</span>
            </li>
          </ul>
          <div
            v-if="(activeTab === 'works' || activeTab === 'likes') && !isBatchMode"
            class="batch-manage-btn-wrapper"
          >
            <button class="batch-manage-btn" @click="enterBatchMode">
              批量管理
            </button>
          </div>
          <div
            v-if="(activeTab === 'works' || activeTab === 'likes') && isBatchMode"
            class="batch-manage-btn-wrapper"
          >
            <button class="batch-manage-btn" @click="exitBatchMode">
              退出管理
            </button>
          </div>
        </div>
        <header class="content-header">
          <div class="work-type-buttons" v-if="activeTab === 'works'">
            <button
              :class="{ active: workType === 'all' }"
              @click="workType = 'all'"
            >
              作品
            </button>
            <button
              :class="{ active: workType === 'private' }"
              @click="workType = 'private'"
            >
              私密作品
            </button>
          </div>
          <div
            v-if="activeTab === 'works'"
            class="filters"
          >
            <input
              v-model="workSearchQuery"
              type="text"
              placeholder="搜索你发布的作品"
              class="search-input"
              @keyup.enter="handleWorkSearch"
            >
            <div class="date-filter-wrapper">
              <button 
                class="date-filter-btn"
                :class="{ active: dateFilterActive }"
                @click="toggleDateFilter"
              >
               日期筛选
                <span v-if="dateFilterActive" class="filter-badge"></span>
              </button>
              <div 
                v-if="showDateFilter"
                class="date-filter-panel"
                @click.stop
              >
                <div class="date-filter-header">
                  <span>选择日期范围</span>
                  <button class="close-date-filter" @click="closeDateFilter">✕</button>
                </div>
                <div class="date-inputs">
                  <div class="date-input-group">
                    <label>开始日期</label>
                    <input
                      v-model="dateFilterStart"
                      type="date"
                      class="date-input"
                      @change="applyDateFilter"
                    >
                  </div>
                  <div class="date-input-group">
                    <label>结束日期</label>
                    <input
                      v-model="dateFilterEnd"
                      type="date"
                      class="date-input"
                      @change="applyDateFilter"
                    >
                  </div>
                </div>
                <div class="date-filter-actions">
                  <button class="clear-date-btn" @click="clearDateFilter">清除</button>
                  <button class="apply-date-btn" @click="applyDateFilter">应用</button>
                </div>
              </div>
            </div>
          </div>
        </header>

        <!-- 批量管理工具栏 -->
        <div
          v-if="isBatchMode && (activeTab === 'works' || activeTab === 'likes')"
          class="batch-toolbar"
        >
          <div class="batch-toolbar-left">
            <label class="select-all-checkbox">
              <input
                type="checkbox"
                :checked="isAllSelected"
                @change="toggleSelectAll"
              >
              <span>全选</span>
            </label>
            <span class="selected-count">
              已选 <strong>{{ selectedItems.length }}</strong> 个作品
            </span>
          </div>
          <div class="batch-toolbar-divider"></div>
          <div class="batch-toolbar-right">
            <button class="batch-action-btn delete-btn" @click="handleBatchDelete">
              <span>删除</span>
            </button>
            <button class="batch-action-btn permission-btn" @click="handleBatchPermission">
              <span>权限设置</span>
            </button>
          </div>
        </div>

        <div
          v-if="activeTab === 'works' && myWorks.length"
          class="video-grid"
        >
          <article
            v-for="video in myWorks"
            :key="video.id"
            class="video-card"
            :class="{ 'batch-mode': isBatchMode }"
          >
            <div class="thumbnail" :style="{ background: video.thumbnailColor }">
              <span class="duration">{{ video.duration }}</span>
              <label
                v-if="isBatchMode"
                class="video-checkbox"
                @click.stop
              >
                <input
                  type="checkbox"
                  :checked="selectedItems.includes(video.id)"
                  @change="toggleSelectItem(video.id)"
                >
              </label>
            </div>
            <div class="video-meta">
              <h3>{{ video.title }}</h3>
              <p class="creator">@{{ video.creator }}</p>
              <p class="stats">
                {{ video.views }} · {{ video.tags.join(' · ') }}
              </p>
            </div>
          </article>
        </div>

        <div
          v-else-if="activeTab === 'likes' && likedVideos.length"
          class="video-grid"
        >
          <article
            v-for="video in likedVideos"
            :key="video.id"
            class="video-card"
            :class="{ 'batch-mode': isBatchMode }"
          >
            <div class="thumbnail" :style="{ background: video.thumbnailColor }">
              <span class="duration">{{ video.duration }}</span>
              <label
                v-if="isBatchMode"
                class="video-checkbox"
                @click.stop
              >
                <input
                  type="checkbox"
                  :checked="selectedItems.includes(video.id)"
                  @change="toggleSelectItem(video.id)"
                >
              </label>
            </div>
            <div class="video-meta">
              <h3>{{ video.title }}</h3>
              <p class="creator">@{{ video.creator }}</p>
              <p class="stats">
                {{ video.views }} · {{ video.tags.join(' · ') }}
              </p>
            </div>
          </article>
        </div>

        <div v-else class="empty-state">
        </div>
      </section>
    </main>

    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal-card">
        <h3>编辑个人信息</h3>
        <label>
          昵称
          <input v-model="editForm.name" type="text" placeholder="输入新的昵称">
        </label>
        <label>
          简介
          <textarea v-model="editForm.signature" rows="3" placeholder="介绍一下自己"></textarea>
        </label>
        <label>
          新密码
          <input v-model="editForm.password" type="password" placeholder="输入新密码（留空则不修改）">
        </label>
        <label>
          确认密码
          <input v-model="editForm.confirmPassword" type="password" placeholder="再次输入新密码">
        </label>
        <div class="modal-actions">
          <button class="ghost" @click="closeEditModal">取消</button>
          <button class="primary" @click="saveProfile">保存</button>
        </div>
      </div>
    </div>

    <!-- 关注/粉丝弹窗 -->
    <div v-if="showFollowModal" class="follow-modal-overlay" @click.self="closeFollowModal">
      <div class="follow-modal">
        <div class="follow-modal-header">
          <div class="follow-tabs">
            <button
              :class="['follow-tab', { active: followModalTab === 'following' }]"
              @click="followModalTab = 'following'"
            >
              关注 ({{ followingList.length }})
            </button>
            <button
              :class="['follow-tab', { active: followModalTab === 'followers' }]"
              @click="followModalTab = 'followers'"
            >
              粉丝 ({{ followersList.length }})
            </button>
          </div>
          <button class="close-btn" @click="closeFollowModal">✕</button>
        </div>

        <div class="follow-modal-search">
          <div class="search-box">
            <span class="search-icon">🔍</span>
            <input
              v-model="followSearchQuery"
              type="text"
              placeholder="搜索用户名"
            >
          </div>
          <div
            class="sort-dropdown"
            @mouseenter="handleSortDropdownEnter"
            @mouseleave="handleSortDropdownLeave"
          >
            <span>{{ selectedSort }}</span>
            <span class="sort-arrow">▼</span>
            <div
              v-if="showSortDropdown"
              class="sort-dropdown-menu"
              @mouseenter="handleSortDropdownEnter"
              @mouseleave="handleSortDropdownLeave"
            >
              <div
                v-for="option in sortOptions"
                :key="option"
                :class="['sort-option', { active: selectedSort === option }]"
                @click="selectSort(option)"
              >
                {{ option }}
              </div>
            </div>
          </div>
        </div>

        <div class="follow-modal-content">
          <div
            v-for="user in displayedFollowList"
            :key="user.id"
            class="follow-user-item"
          >
            <div class="user-avatar">
              <img :src="user.avatar" :alt="user.name">
            </div>
            <div class="user-info">
              <div class="user-name-row">
                <span class="user-name">{{ user.name }}</span>
                <span v-if="user.verified" class="verified-badge">✓</span>
              </div>
              <div v-if="user.title" class="user-title">{{ user.title }}</div>
              <div v-if="user.subtitle" class="user-subtitle">{{ user.subtitle }}</div>
              <div v-if="user.description" class="user-description">{{ user.description }}</div>
              <div v-if="user.unreadWorks" class="user-unread">1个作品未看</div>
            </div>
            <button
              :class="['follow-status-btn', user.followStatus]"
              @click="toggleFollow(user)"
            >
              {{ getFollowButtonText(user.followStatus) }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { clearAuthToken, getCurrentUser } from '@/utils/auth'
import { updateMockUserPassword } from '@/data/mockUsers'
export default {
  name: 'ProfileView',
  data() {
    return {
      searchQuery: '',
      navLinks: [
        { key: 'discover', label: '发现内容', icon: '✨' },
        { key: 'live', label: '直播', icon: '📡' },
        { key: 'community', label: '社区', icon: '💬' },
        { key: 'my', label: '我的', icon: '' }
      ],
      activeNav: 'my',
      user: {
        avatar: require('@/assets/avatar.jpg'),
        name: 'zk3zy',
        isLive: true,
        following: 250,
        followers: 86,
        likes: 4,
        signature: 'yeeeee',
        sn: '43114125',
        age: 20
      },
      panel: {
        avatar: require('@/assets/avatar.jpg'),
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
      tabs: [
        { key: 'works', label: '作品', badge: null },
        { key: 'likes', label: '喜欢' },
        { key: 'history', label: '观看历史' },
      ],
      activeTab: 'works',
      workType: 'all', // 'all' 或 'private'
      workSearchQuery: '', // 作品搜索关键词
      showDateFilter: false, // 是否显示日期筛选面板
      dateFilterStart: '', // 开始日期
      dateFilterEnd: '', // 结束日期
      showEditModal: false,
      editForm: {
        name: '',
        signature: '',
        password: '',
        confirmPassword: ''
      },
      showFollowModal: false,
      followModalTab: 'following',
      followSearchQuery: '',
      showSortDropdown: false,
      selectedSort: '综合排序',
      sortOptions: ['综合排序', '最近关注', '最早关注'],
      sortDropdownTimer: null,
      isBatchMode: false,
      selectedItems: [],
      followingList: [
        {
          id: 1,
          name: '星海Nova',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=Nova',
          title: '虚拟偶像',
          description: '前11万宝子蹲好助理备注流程末尾中……(拒绝取……)',
          followStatus: 'followed',
          verified: false,
          followTime: new Date('2024-01-15').getTime()
        },
        {
          id: 2,
          name: 'DANK1NG',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=DANK1NG',
          title: '电子竞技职业选手',
          subtitle: 'TYLOO DANKING',
          followStatus: 'followed',
          verified: true,
          followTime: new Date('2024-03-20').getTime()
        },
        {
          id: 3,
          name: 'NiKo',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=NiKo',
          title: '电竞选手',
          subtitle: 'Falcons Esports职业选手',
          followStatus: 'followed',
          verified: true,
          followTime: new Date('2024-02-10').getTime()
        },
        {
          id: 4,
          name: 'reailty',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=reailty',
          followStatus: 'mutual',
          verified: false,
          followTime: new Date('2023-12-05').getTime()
        },
        {
          id: 5,
          name: '森阳(无畏契约)',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=森阳',
          description: '视频皆为节目效果',
          followStatus: 'followed',
          unreadWorks: 1,
          verified: false,
          followTime: new Date('2024-04-01').getTime()
        },
        {
          id: 6,
          name: 'LumiRay',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=LumiRay',
          title: '虚拟偶像',
          description: '全息舞台表演者，每晚8点直播',
          followStatus: 'followed',
          verified: true,
          followTime: new Date('2024-01-28').getTime()
        },
        {
          id: 7,
          name: 'KiraEcho',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=KiraEcho',
          title: 'AI虚拟主播',
          description: '24/7陪伴式直播，智能互动',
          followStatus: 'followed',
          verified: false,
          followTime: new Date('2024-03-15').getTime()
        },
        {
          id: 8,
          name: 'NebulaNova',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=NebulaNova',
          title: '虚拟歌手',
          subtitle: '原创音乐制作人',
          followStatus: 'mutual',
          verified: true,
          followTime: new Date('2023-11-20').getTime()
        }
      ],
      followersList: [
        {
          id: 101,
          name: '虚拟世界探索者',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=explorer',
          description: '热爱虚拟直播的普通用户',
          followStatus: 'not-followed',
          verified: false
        },
        {
          id: 102,
          name: 'TechGamer',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=TechGamer',
          title: '科技博主',
          followStatus: 'not-followed',
          verified: false
        },
        {
          id: 103,
          name: 'VtuberFan',
          avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=VtuberFan',
          description: '虚拟主播爱好者',
          followStatus: 'mutual',
          verified: false
        }
      ],
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
      ],
      likedVideoIds: [1, 5, 7, 10, 13, 15],
      userWorks: []
    }
  },
  created() {
    this.loadStoredProfile()
    this.loadUserWorks()
  },
  computed: {
    likedVideos() {
      return this.shortVideos.filter(video =>
        this.likedVideoIds.includes(video.id)
      )
    },
    myWorks() {
      let works = this.userWorks || []
      
      // 根据 workType 过滤
      if (this.workType === 'private') {
        // 私密作品模式下，只显示私密作品
        works = works.filter(work => work.isPrivate === true)
      }
      // workType === 'all' 时显示所有作品，不需要额外过滤
      
      // 根据搜索关键词过滤
      if (this.workSearchQuery.trim()) {
        const query = this.workSearchQuery.trim().toLowerCase()
        works = works.filter(work => {
          // 搜索标题、创作者、标签
          const titleMatch = work.title?.toLowerCase().includes(query)
          const creatorMatch = work.creator?.toLowerCase().includes(query)
          const tagsMatch = work.tags?.some(tag => tag.toLowerCase().includes(query))
          return titleMatch || creatorMatch || tagsMatch
        })
      }
      
      // 根据日期范围过滤
      if (this.dateFilterStart || this.dateFilterEnd) {
        works = works.filter(work => {
          if (!work.uploadTime) return false
          
          const uploadDate = new Date(work.uploadTime)
          const uploadDateStr = uploadDate.toISOString().split('T')[0] // YYYY-MM-DD格式
          
          // 如果只有开始日期，筛选大于等于开始日期的作品
          if (this.dateFilterStart && !this.dateFilterEnd) {
            return uploadDateStr >= this.dateFilterStart
          }
          
          // 如果只有结束日期，筛选小于等于结束日期的作品
          if (!this.dateFilterStart && this.dateFilterEnd) {
            return uploadDateStr <= this.dateFilterEnd
          }
          
          // 如果两个日期都有，筛选在范围内的作品
          if (this.dateFilterStart && this.dateFilterEnd) {
            return uploadDateStr >= this.dateFilterStart && uploadDateStr <= this.dateFilterEnd
          }
          
          return true
        })
      }
      
      return works
    },
    worksCount() {
      return this.userWorks.length
    },
    dateFilterActive() {
      return !!(this.dateFilterStart || this.dateFilterEnd)
    },
    isAllSelected() {
      const currentVideos = this.activeTab === 'works' ? this.myWorks : this.likedVideos
      return currentVideos.length > 0 && this.selectedItems.length === currentVideos.length
    },
    displayedFollowList() {
      let list = this.followModalTab === 'following' ? this.followingList : this.followersList
      
      // 搜索过滤
      if (this.followSearchQuery.trim()) {
        const query = this.followSearchQuery.toLowerCase()
        list = list.filter(user => {
          return user.name.toLowerCase().includes(query) ||
                 (user.description && user.description.toLowerCase().includes(query)) ||
                 (user.title && user.title.toLowerCase().includes(query))
        })
      }
      
      // 排序（仅对关注列表进行排序，粉丝列表不排序）
      if (this.followModalTab === 'following' && this.selectedSort !== '综合排序') {
        const sortedList = [...list]
        if (this.selectedSort === '最近关注') {
          sortedList.sort((a, b) => {
            const timeA = a.followTime || 0
            const timeB = b.followTime || 0
            return timeB - timeA // 降序，最新的在前
          })
        } else if (this.selectedSort === '最早关注') {
          sortedList.sort((a, b) => {
            const timeA = a.followTime || 0
            const timeB = b.followTime || 0
            return timeA - timeB // 升序，最早的在前
          })
        }
        return sortedList
      }
      
      return list
    }
  },
  watch: {
    '$route.query.tab': {
      immediate: true,
      handler(newTab) {
        this.syncTabFromRoute(newTab)
      }
    },
    '$route.path'() {
      // 当路由变化时，重新加载作品（从上传页面返回时）
      this.loadUserWorks()
    },
    activeTab() {
      // 切换标签页时退出批量管理模式
      if (this.isBatchMode) {
        this.exitBatchMode()
      }
    },
  },
  mounted() {
    this.consumePendingProfileTab()
    // 监听storage事件，当其他页面保存作品时自动更新
    window.addEventListener('storage', this.handleStorageChange)
    // 监听自定义事件，当上传页面保存作品时自动更新
    window.addEventListener('userWorksUpdated', this.loadUserWorks)
    // 点击外部关闭日期筛选面板
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    window.removeEventListener('storage', this.handleStorageChange)
    window.removeEventListener('userWorksUpdated', this.loadUserWorks)
    document.removeEventListener('click', this.handleClickOutside)
    if (this.sortDropdownTimer) {
      clearTimeout(this.sortDropdownTimer)
      this.sortDropdownTimer = null
    }
  },
  methods: {
    handleWorkSearch(event) {
      // 按回车键时触发搜索，由于使用了 computed，过滤会自动执行
      // 这里可以让输入框失焦，提供更好的用户体验
      if (event?.target) {
        event.target.blur()
      }
    },
    toggleDateFilter() {
      this.showDateFilter = !this.showDateFilter
    },
    closeDateFilter() {
      this.showDateFilter = false
    },
    applyDateFilter() {
      // 验证日期范围
      if (this.dateFilterStart && this.dateFilterEnd) {
        if (this.dateFilterStart > this.dateFilterEnd) {
          alert('开始日期不能晚于结束日期')
          return
        }
      }
      // 由于使用了computed，过滤会自动执行
      this.closeDateFilter()
    },
    clearDateFilter() {
      this.dateFilterStart = ''
      this.dateFilterEnd = ''
      this.closeDateFilter()
    },
    openEditModal() {
      this.editForm.name = this.user.name
      this.editForm.signature = this.user.signature
      this.editForm.password = ''
      this.editForm.confirmPassword = ''
      this.showEditModal = true
    },
    closeEditModal() {
      this.showEditModal = false
      this.editForm.password = ''
      this.editForm.confirmPassword = ''
    },
    saveProfile() {
      const trimmedName = this.editForm.name?.trim()
      const trimmedSignature = this.editForm.signature?.trim() ?? ''
      const password = this.editForm.password?.trim()
      const confirmPassword = this.editForm.confirmPassword?.trim()

      // 验证密码
      if (password || confirmPassword) {
        if (!password) {
          alert('请输入新密码')
          return
        }
        if (password.length < 6) {
          alert('密码长度至少为6位')
          return
        }
        if (password !== confirmPassword) {
          alert('两次输入的密码不一致')
          return
        }
        // 更新密码
        const currentUsername = getCurrentUser()
        if (!currentUsername) {
          alert('无法获取当前用户信息，请重新登录')
          return
        }
        const success = updateMockUserPassword(currentUsername, password)
        if (success) {
          alert('密码已更新')
        } else {
          alert('密码更新失败，请重试')
          return
        }
      }

      if (trimmedName) {
        this.user.name = trimmedName
        this.panel.name = trimmedName
      }
      this.user.signature = trimmedSignature

      this.persistProfile()
      this.showEditModal = false
    },
    loadStoredProfile() {
      try {
        const cached = localStorage.getItem('profileUser')
        if (!cached) return
        const parsed = JSON.parse(cached)
        if (parsed.name) {
          this.user.name = parsed.name
          this.panel.name = parsed.name
        }
        if (typeof parsed.signature === 'string') {
          this.user.signature = parsed.signature
        }
      } catch (err) {
        console.warn('加载本地资料失败', err)
      }
    },
    persistProfile() {
      try {
        localStorage.setItem('profileUser', JSON.stringify({
          name: this.user.name,
          signature: this.user.signature
        }))
      } catch (err) {
        console.warn('保存本地资料失败', err)
      }
    },
    loadUserWorks() {
      try {
        const works = localStorage.getItem('userWorks')
        if (works) {
          this.userWorks = JSON.parse(works)
        } else {
          this.userWorks = []
        }
        // 更新快速入口中的作品数量
        const worksEntry = this.panel.quickEntries.find(entry => entry.key === 'works')
        if (worksEntry) {
          worksEntry.value = this.userWorks.length.toString()
        }
      } catch (err) {
        console.warn('加载作品失败', err)
        this.userWorks = []
      }
    },
    saveUserWorks() {
      try {
        localStorage.setItem('userWorks', JSON.stringify(this.userWorks))
        // 更新快速入口中的作品数量
        const worksEntry = this.panel.quickEntries.find(entry => entry.key === 'works')
        if (worksEntry) {
          worksEntry.value = this.userWorks.length.toString()
        }
        // 触发自定义事件，通知其他页面更新
        window.dispatchEvent(new Event('userWorksUpdated'))
      } catch (err) {
        console.warn('保存作品失败', err)
      }
    },
    handleStorageChange(event) {
      // 当localStorage中的userWorks发生变化时，重新加载
      if (event.key === 'userWorks') {
        this.loadUserWorks()
      }
    },
    handleNavClick(link) {
      this.activeNav = link.key
      if (link.key === 'my') return
      const pathMap = {
        discover: '/',
        live: '/live',
        community: '/',
        my: '/profile'
      }
      this.$router.push(pathMap[link.key] || '/')
    },
    navigateToTab(tabKey, { scroll = true, syncQuery = true } = {}) {
      if (!tabKey) return
      if (this.activeTab !== tabKey) {
        this.activeTab = tabKey
      }
      if (syncQuery) {
        const nextQuery = { ...this.$route.query, tab: tabKey }
        this.$router.replace({ path: this.$route.path, query: nextQuery }).catch(() => {})
      }
      if (scroll) {
        this.scrollToTabSection()
      }
    },
    openLikesTab() {
      this.navigateToTab('likes')
    },
    handlePanelQuickEntry(link) {
      if (!link?.key) return
      this.navigateToTab(link.key)
    },
    consumePendingProfileTab() {
      try {
        const pendingTab = sessionStorage.getItem('pendingProfileTab')
        if (!pendingTab) return
        sessionStorage.removeItem('pendingProfileTab')
        const validTabs = this.tabs.map(t => t.key)
        if (!validTabs.includes(pendingTab)) return
        this.navigateToTab(pendingTab, { syncQuery: false })
      } catch (err) {
        console.warn('读取个人页目标标签失败', err)
      }
    },
    syncTabFromRoute(tab) {
      const validTabs = this.tabs.map(t => t.key)
      if (!tab || !validTabs.includes(tab)) return
      if (this.activeTab !== tab) {
        this.activeTab = tab
      }
      this.scrollToTabSection(false)
    },
    scrollToTabSection(withAnimation = true) {
      this.$nextTick(() => {
        const section = this.$refs.tabSection
        if (section?.scrollIntoView) {
          section.scrollIntoView({
            behavior: withAnimation ? 'smooth' : 'auto',
            block: 'start'
          })
        }
      })
    },
    handleLogout() {
      clearAuthToken()
      this.$router.push({ name: 'login' }).catch(() => {})
    },
    goToUploadVideo() {
      this.$router.push({ path: '/upload-video' }).catch(() => {})
    },
    openFollowModal(tab) {
      this.followModalTab = tab || 'following'
      this.followSearchQuery = ''
      this.showFollowModal = true
    },
    closeFollowModal() {
      this.showFollowModal = false
      this.followSearchQuery = ''
      this.showSortDropdown = false
      this.selectedSort = '综合排序'
    },
    enterBatchMode() {
      this.isBatchMode = true
      this.selectedItems = []
    },
    exitBatchMode() {
      this.isBatchMode = false
      this.selectedItems = []
    },
    toggleSelectItem(itemId) {
      const index = this.selectedItems.indexOf(itemId)
      if (index > -1) {
        this.selectedItems.splice(index, 1)
      } else {
        this.selectedItems.push(itemId)
      }
    },
    toggleSelectAll() {
      const currentVideos = this.activeTab === 'works' ? this.myWorks : this.likedVideos
      if (this.isAllSelected) {
        this.selectedItems = []
      } else {
        this.selectedItems = currentVideos.map(video => video.id)
      }
    },
    handleBatchDelete() {
      if (this.selectedItems.length === 0) {
        return
      }
      if (confirm(`确定要删除选中的 ${this.selectedItems.length} 个作品吗？`)) {
        if (this.activeTab === 'works') {
          this.userWorks = this.userWorks.filter(work => !this.selectedItems.includes(work.id))
          this.saveUserWorks()
        } else if (this.activeTab === 'likes') {
          this.likedVideoIds = this.likedVideoIds.filter(id => !this.selectedItems.includes(id))
        }
        this.selectedItems = []
        // 如果删除后没有作品了，自动退出批量管理模式
        const currentVideos = this.activeTab === 'works' ? this.myWorks : this.likedVideos
        if (currentVideos.length === 0) {
          this.exitBatchMode()
        }
      }
    },
    handleBatchPermission() {
      if (this.selectedItems.length === 0) {
        return
      }
      if (this.activeTab === 'works') {
        const count = this.selectedItems.length
        // 将选中的作品设为私密
        this.userWorks.forEach(work => {
          if (this.selectedItems.includes(work.id)) {
            work.isPrivate = true
          }
        })
        this.saveUserWorks()
        this.selectedItems = []
        alert(`已将 ${count} 个作品设为私密`)
      }
    },
    toggleFollow(user) {
      if (user.followStatus === 'followed') {
        user.followStatus = 'not-followed'
      } else if (user.followStatus === 'mutual') {
        user.followStatus = 'not-followed'
      } else {
        user.followStatus = 'followed'
      }
    },
    getFollowButtonText(status) {
      const statusMap = {
        'followed': '已关注',
        'mutual': '相互关注',
        'not-followed': '关注'
      }
      return statusMap[status] || '关注'
    },
    selectSort(option) {
      this.selectedSort = option
      this.showSortDropdown = false
      if (this.sortDropdownTimer) {
        clearTimeout(this.sortDropdownTimer)
        this.sortDropdownTimer = null
      }
    },
    handleSortDropdownEnter() {
      if (this.sortDropdownTimer) {
        clearTimeout(this.sortDropdownTimer)
        this.sortDropdownTimer = null
      }
      this.showSortDropdown = true
    },
    handleSortDropdownLeave() {
      this.sortDropdownTimer = setTimeout(() => {
        this.showSortDropdown = false
        this.sortDropdownTimer = null
      }, 300)
    },
    handleClickOutside(event) {
      // 点击外部关闭日期筛选面板
      const dateFilterWrapper = event.target.closest('.date-filter-wrapper')
      if (!dateFilterWrapper && this.showDateFilter) {
        this.closeDateFilter()
      }
    }
  }
}
</script>

<style scoped>
.profile-page {
  display: grid;
  grid-template-columns: 260px 1fr;
  min-height: 100vh;
  background: #0f1016;
  color: #fff;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
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

.profile-content {
  padding: 32px 48px 80px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.search-input {
  flex: 1;
  display: flex;
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.04);
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
  gap: 12px;
  padding: 0 14px;
  flex: 1;
}

.search-field input {
  flex: 1;
  background: transparent;
  border: none;
  color: #fff;
}

.search-field input:focus {
  outline: none;
}

.search-icon {
  color: rgba(255, 255, 255, 0.6);
}

.search-divider {
  width: 1px;
  background: rgba(255, 255, 255, 0.15);
  margin: 8px 0;
}

.search-btn {
  border: none;
  padding: 12px 22px;
  background: rgba(255, 255, 255, 0.07);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-btn {
  padding: 10px 18px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
  font-weight: 600;
}

.action-btn.primary {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  border-color: transparent;
}

.action-dropdown,
.avatar-dropdown {
  position: relative;
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

.action-dropdown:hover .dropdown-menu {
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

.avatar-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid rgba(255, 255, 255, 0.35);
  background: rgba(255, 255, 255, 0.1);
  overflow: hidden;
  padding: 0;
  cursor: pointer;
}

.avatar-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
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
  left: 0;
  width: 100%;
  height: 16px;
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
  margin-bottom: 8px;
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

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-card {
  width: min(420px, 90vw);
  background: #161821;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.45);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-card h3 {
  margin: 0;
}

.modal-card label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.85);
}

.modal-card input,
.modal-card textarea {
  border-radius: 10px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.25);
  color: #fff;
  padding: 10px 12px;
  font-size: 1rem;
}

.modal-card input:focus,
.modal-card textarea:focus {
  outline: none;
  border-color: #7366ff;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-actions .ghost,
.modal-actions .primary {
  border-radius: 10px;
  padding: 8px 18px;
  border: 1px solid rgba(255, 255, 255, 0.25);
  background: transparent;
  color: #fff;
  cursor: pointer;
}

.modal-actions .primary {
  border: none;
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
}

.hero {
  background: #13141d;
  border-radius: 24px 24px 12px 12px;
  padding-bottom: 10px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.05);
  margin-bottom: 0;
}

.cover {
  height: 80px;
  background: radial-gradient(circle at top, rgba(255, 255, 255, 0.25), transparent);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 24px 12px;
  margin-top: -32px;
}

.profile-card .avatar {
  width: 120px;
  height: 120px;
  border-radius: 28px;
  border: 4px solid #0f1016;
  object-fit: cover;
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.edit-btn {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  font-size: 0.85rem;
}

.live-tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: #ff4d67;
  font-size: 0.85rem;
}

.stats {
  display: flex;
  gap: 16px;
  color: rgba(255, 255, 255, 0.75);
}

.stat-item.clickable {
  cursor: pointer;
  transition: color 0.2s ease;
}

.stat-item.clickable:hover {
  color: #ff4d67;
}

.meta {
  display: flex;
  gap: 16px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.9rem;
}

.hero-actions {
  display: flex;
  gap: 12px;
}

.hero-actions .primary {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  border: none;
  padding: 10px 22px;
  border-radius: 14px;
  color: #fff;
}

.hero-actions .ghost {
  border: 1px solid rgba(255, 255, 255, 0.2);
  padding: 10px 22px;
  border-radius: 14px;
  background: transparent;
  color: #fff;
}

.tabs-wrapper {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  gap: 16px;
}

.tabs {
  list-style: none;
  display: flex;
  gap: 20px;
  margin: 0;
  overflow-x: auto;
  padding: 0 4px;
  flex: 1;
}

.tabs li {
  cursor: pointer;
  padding-bottom: 8px;
  border-bottom: 2px solid transparent;
  color: rgba(255, 255, 255, 0.7);
}

.tabs li.active {
  color: #ff4d67;
  border-bottom-color: #ff4d67;
}

.content-area {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px 12px 20px 20px;
  border: 1px solid rgba(255, 255, 255, 0.04);
  border-top: none;
  padding: 24px;
  margin-top: -6px;
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  gap: 16px;
}

.work-type-buttons {
  display: flex;
  gap: 10px;
}

.work-type-buttons button {
  padding: 8px 16px;
  border-radius: 12px;
  border: none;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.7);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.work-type-buttons button:hover {
  background: rgba(255, 255, 255, 0.1);
}

.work-type-buttons button.active {
  background: #ff4d67;
  color: #fff;
}

.pill-group {
  display: flex;
  gap: 10px;
  flex: 1;
}

.pill-group button {
  padding: 8px 16px;
  border-radius: 12px;
  border: none;
  background: rgba(255, 255, 255, 0.06);
  color: rgba(255, 255, 255, 0.7);
}

.pill-group button.active {
  background: #ff4d67;
  color: #fff;
}

.filters {
  display: flex;
  gap: 12px;
}

.filters .search-input {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: #fff;
  border-radius: 12px;
  padding: 8px 16px;
  outline: none;
  font-size: 14px;
  min-width: 200px;
  transition: border-color 0.3s ease, background-color 0.3s ease;
}

.filters .search-input:hover {
  border-color: rgba(255, 255, 255, 0.35);
  background-color: rgba(255, 255, 255, 0.05);
}

.filters .search-input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.filters .search-input:focus {
  border-color: rgba(255, 255, 255, 0.4);
  background-color: rgba(255, 255, 255, 0.05);
}

.filters button {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: #fff;
  border-radius: 12px;
  padding: 8px 16px;
}

.date-filter-wrapper {
  position: relative;
}

.date-filter-btn {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: #fff;
  border-radius: 12px;
  padding: 8px 16px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s ease;
  position: relative;
}

.date-filter-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
}

.date-filter-btn.active {
  background: rgba(255, 77, 103, 0.15);
  border-color: rgba(255, 77, 103, 0.4);
  color: #ff4d67;
}

.filter-badge {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ff4d67;
  display: inline-block;
}

.date-filter-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 320px;
  background: #1f2130;
  border: 1px solid rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 12px 32px rgba(0, 0, 0, 0.4);
  z-index: 20;
}

.date-filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #fff;
}

.close-date-filter {
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(255, 255, 255, 0.1);
  color: rgba(255, 255, 255, 0.7);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;
}

.close-date-filter:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.date-inputs {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 16px;
}

.date-input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.date-input-group label {
  font-size: 0.85rem;
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
}

.date-input {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.date-input:focus {
  outline: none;
  border-color: #ff4d67;
  background: rgba(0, 0, 0, 0.4);
}

.date-input::-webkit-calendar-picker-indicator {
  filter: invert(1);
  cursor: pointer;
}

.date-filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.clear-date-btn,
.apply-date-btn {
  padding: 8px 16px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.clear-date-btn {
  background: transparent;
  color: rgba(255, 255, 255, 0.7);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.clear-date-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.apply-date-btn {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  color: #fff;
}

.apply-date-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.batch-manage-btn-wrapper {
  display: flex;
  align-items: center;
}

.batch-manage-btn {
  border: 1px solid rgba(255, 255, 255, 0.2);
  background: transparent;
  color: rgba(255, 255, 255, 0.9);
  border-radius: 12px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.batch-manage-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.3);
}

.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 12px;
  margin-bottom: 24px;
}

.batch-toolbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.select-all-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  user-select: none;
}

.select-all-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #ff4d67;
}

.selected-count {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.selected-count strong {
  color: rgba(255, 255, 255, 0.9);
  font-weight: 600;
}

.batch-toolbar-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 255, 255, 0.12);
  flex-shrink: 0;
}

.batch-toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-left: auto;
}

.batch-action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: transparent;
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.batch-action-btn:hover {
  background: rgba(255, 255, 255, 0.08);
  border-color: rgba(255, 255, 255, 0.2);
}

.batch-action-btn.delete-btn:hover {
  border-color: rgba(255, 77, 103, 0.5);
  color: #ff4d67;
}

.batch-icon {
  font-size: 1rem;
}

.video-card.batch-mode {
  position: relative;
}

.video-checkbox {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 24px;
  height: 24px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.video-checkbox:hover {
  background: rgba(0, 0, 0, 0.8);
}

.video-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #ff4d67;
}

.empty-state {
  border: 1px dashed rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  padding: 60px 20px;
  text-align: center;
  color: rgba(255, 255, 255, 0.65);
}

.empty-state .icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
}

.empty-state .title {
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #fff;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.video-card {
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.04);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.thumbnail {
  position: relative;
  padding-top: 60%;
  border-radius: 18px 18px 0 0;
  overflow: hidden;
}

.thumbnail .duration {
  position: absolute;
  bottom: 10px;
  right: 12px;
  padding: 4px 10px;
  border-radius: 999px;
  background: rgba(0, 0, 0, 0.35);
  font-size: 0.78rem;
}

.video-meta {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.video-meta h3 {
  margin: 0;
  font-size: 1rem;
}

.video-meta .creator {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
}

.video-meta .stats {
  margin: 0;
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.85rem;
}

/* 关注/粉丝弹窗样式 */
.follow-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 200;
  backdrop-filter: blur(4px);
}

.follow-modal {
  width: min(500px, 90vw);
  max-height: 80vh;
  background: #161821;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 30px 60px rgba(0, 0, 0, 0.5);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.follow-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.follow-tabs {
  display: flex;
  gap: 24px;
}

.follow-tab {
  background: transparent;
  border: none;
  color: rgba(255, 255, 255, 0.6);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s ease;
}

.follow-tab.active {
  color: #ff4d67;
}

.follow-tab:hover {
  color: rgba(255, 255, 255, 0.9);
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease, color 0.2s ease;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
}

.follow-modal-search {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
}

.search-box input {
  flex: 1;
  background: transparent;
  border: none;
  color: #fff;
  font-size: 0.9rem;
}

.search-box input:focus {
  outline: none;
}

.search-box input::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

.search-icon {
  color: rgba(255, 255, 255, 0.5);
  font-size: 0.9rem;
}

.sort-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s ease;
}

.sort-dropdown:hover {
  background: rgba(255, 255, 255, 0.08);
}

.sort-arrow {
  font-size: 0.7rem;
  color: rgba(255, 255, 255, 0.5);
}

.sort-dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 140px;
  background: #1f2130;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  overflow: hidden;
  z-index: 10;
}

.sort-option {
  padding: 12px 16px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.sort-option:hover {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.sort-option.active {
  color: #ff4d67;
  background: rgba(255, 77, 103, 0.1);
}

.follow-modal-content {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.follow-user-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 24px;
  transition: background 0.2s ease;
}

.follow-user-item:hover {
  background: rgba(255, 255, 255, 0.03);
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.1);
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
  min-width: 0;
}

.user-name-row {
  display: flex;
  align-items: center;
  gap: 6px;
}

.user-name {
  font-weight: 600;
  color: #fff;
  font-size: 0.95rem;
}

.verified-badge {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #1da1f2;
  color: #fff;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-title {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.85rem;
  font-weight: 500;
}

.user-subtitle {
  color: rgba(255, 255, 255, 0.6);
  font-size: 0.8rem;
}

.user-description {
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.85rem;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.user-unread {
  color: #ff4d67;
  font-size: 0.8rem;
  margin-top: 2px;
}

.follow-status-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  background: transparent;
  color: #fff;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  white-space: nowrap;
}

.follow-status-btn.followed {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
}

.follow-status-btn.mutual {
  background: rgba(255, 77, 103, 0.2);
  border-color: rgba(255, 77, 103, 0.4);
  color: #ff4d67;
}

.follow-status-btn.not-followed {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  border-color: transparent;
}

.follow-status-btn:hover {
  opacity: 0.9;
  transform: scale(1.05);
}

/* 滚动条样式 */
.follow-modal-content::-webkit-scrollbar {
  width: 6px;
}

.follow-modal-content::-webkit-scrollbar-track {
  background: transparent;
}

.follow-modal-content::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 3px;
}

.follow-modal-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 255, 255, 0.3);
}
</style>

