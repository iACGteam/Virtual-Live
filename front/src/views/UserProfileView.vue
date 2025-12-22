<template>
  <div class="profile-page">
    <button class="back-btn" @click="goBack">
      <span class="back-icon">←</span> 返回
    </button>
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
        <!-- Removed action-dropdown for non-owner -->
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
                  <span class="clickable" @click.stop="handleSidebarStatClick('following')">关注 {{ panel.followings }}</span>
                  · <span class="clickable" @click.stop="handleSidebarStatClick('followers')">粉丝 {{ panel.followers }}</span>
                  · <span class="clickable" @click.stop="handleSidebarStatClick('circles')">圈子 {{ panel.circles }}</span>
                </p>
              </div>
            </div>

            <section class="favorite-section">
              <div class="favorites-hover-zone">
                <div
                  class="section-title"
                >
                  <span>❤️ 喜欢</span>
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

            <!-- Removed secondary-links and profile-footer for non-owner -->
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
              <!-- Removed edit-btn -->
              <button class="follow-btn" @click="toggleFollowUser" :class="{ 'followed': isFollowingUser }">
                {{ isFollowingUser ? '已关注' : '关注' }}
              </button>
            </div>
            <div class="stats">
              <span class="stat-item clickable" @click="openFollowModal('following')">关注 {{ user.following }}</span>
              <span class="stat-item clickable" @click="openFollowModal('followers')">粉丝 {{ user.followers }}</span>
              <span class="stat-item clickable" @click="openFollowModal('circles')">圈子 {{ user.circles }}</span>
              <span>获赞 {{ user.likes }}</span>
            </div>
            <p class="signature">{{ user.signature }}</p>
          </div>
          <!-- Removed role-card-btn -->
        </div>
        <!-- 半圆形按钮 -->
        <button 
          class="role-info-toggle-btn"
          :class="{ 'expanded': showRoleInfo }"
          @click="toggleRoleInfo"
        >
          <span class="toggle-icon">{{ showRoleInfo ? '▲' : '▼' }}</span>
        </button>
        <!-- 角色信息下拉框 -->
        <div 
          class="role-info-panel"
          :class="{ 'expanded': showRoleInfo }"
        >
          <div class="role-info-content">
            <div class="role-portrait">
              <img 
                v-if="currentRoleCard && currentRoleCard.portrait" 
                :src="currentRoleCard.portrait" 
                alt="角色肖像"
              >
              <div v-else class="role-portrait-placeholder">
                <span>🎭</span>
              </div>
            </div>
            <div class="role-details">
              <div class="role-header">
                <h2 class="role-name">{{ currentRoleCard ? currentRoleCard.name : '暂无角色' }}</h2>
              </div>
              <div class="role-tabs">
                <button
                  v-for="tab in roleTabs"
                  :key="tab.key"
                  :class="['role-tab', { active: activeRoleTab === tab.key }]"
                  @click="activeRoleTab = tab.key"
                >
                  {{ tab.label }}
                </button>
              </div>
              <div class="role-tab-content">
                <!-- 基本信息 -->
                <div v-if="activeRoleTab === 'basic'" class="role-basic-info">
                  <div class="info-item" v-if="currentRoleCard && currentRoleCard.gender">
                    <span class="info-label">性别</span>
                    <span class="info-value">{{ getGenderText(currentRoleCard.gender) }}</span>
                  </div>
                  <div class="info-item" v-if="currentRoleCard && currentRoleCard.birthday">
                    <span class="info-label">生日</span>
                    <span class="info-value">{{ formatBirthday(currentRoleCard.birthday) }}</span>
                  </div>
                  <div class="info-item" v-if="currentRoleCard && currentRoleCard.height">
                    <span class="info-label">身高</span>
                    <span class="info-value">{{ currentRoleCard.height }}cm</span>
                  </div>
                  <div class="info-item" v-if="currentRoleCard && currentRoleCard.bloodType">
                    <span class="info-label">血型</span>
                    <span class="info-value">{{ currentRoleCard.bloodType }}</span>
                  </div>
                  <div class="info-item" v-if="currentRoleCard && currentRoleCard.hobby">
                    <span class="info-label">爱好</span>
                    <span class="info-value">{{ currentRoleCard.hobby }}</span>
                  </div>
                </div>
                <!-- 标签 -->
                <div v-if="activeRoleTab === 'tags'" class="role-tags-content">
                  <div v-if="currentRoleCard && currentRoleCard.personalityTags && currentRoleCard.personalityTags.length > 0" class="tags-group">
                    <h4>性格标签</h4>
                    <div class="tags-list">
                      <span 
                        v-for="tag in currentRoleCard.personalityTags" 
                        :key="tag"
                        class="role-tag personality"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
                  <div v-if="currentRoleCard && currentRoleCard.raceTags && currentRoleCard.raceTags.length > 0" class="tags-group">
                    <h4>种族标签</h4>
                    <div class="tags-list">
                      <span 
                        v-for="tag in currentRoleCard.raceTags" 
                        :key="tag"
                        class="role-tag race"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
                  <div v-if="currentRoleCard && currentRoleCard.appearanceTags && currentRoleCard.appearanceTags.length > 0" class="tags-group">
                    <h4>外观标签</h4>
                    <div class="tags-list">
                      <span 
                        v-for="tag in currentRoleCard.appearanceTags" 
                        :key="tag"
                        class="role-tag appearance"
                      >
                        {{ tag }}
                      </span>
                    </div>
                  </div>
                </div>
                <!-- 背景故事 -->
                <div v-if="activeRoleTab === 'story'" class="role-story-content">
                  <p v-if="currentRoleCard && currentRoleCard.backgroundStory" class="story-text">
                    {{ currentRoleCard.backgroundStory }}
                  </p>
                  <p v-else class="empty-text">暂无背景故事</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="content-area" ref="tabSection" :class="{ 'role-info-expanded': showRoleInfo }">
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
          <!-- Removed batch manage buttons -->
        </div>
        <header class="content-header">
          <!-- Removed work-type-buttons (private/all) -->
          <div
            v-if="activeTab === 'works'"
            class="filters"
          >
            <input
              v-model="workSearchQuery"
              type="text"
              placeholder="搜索发布的作品"
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

        <!-- Removed batch-toolbar -->

        <div
          v-if="activeTab === 'works' && myWorks.length"
          class="video-grid"
        >
          <article
            v-for="video in myWorks"
            :key="video.id"
            class="video-card"
            @click="handleVideoClick(video)"
          >
            <div class="thumbnail" :style="getThumbnailStyle(video)">
              <span class="duration">{{ video.duration }}</span>
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
            @click="handleVideoClick(video)"
          >
            <div class="thumbnail" :style="getThumbnailStyle(video)">
              <span class="duration">{{ video.duration }}</span>
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
          v-else-if="activeTab === 'circles'"
          class="circles-container"
        >
          <!-- 该用户的圈子 -->
          <div class="circles-section">
            <h3 class="section-title">该用户创建的圈子</h3>
            <div v-if="visitedUserCircle" class="circle-card-wrapper">
              <div class="circle-card" @click="handleEnterCircle(visitedUserCircle)">
                <div class="circle-cover">
                  <img :src="visitedUserCircle.cover || visitedUserCircle.coverImageUrl || require('@/assets/logo.png')" alt="cover">
                </div>
                <div class="circle-info">
                  <h3>{{ visitedUserCircle.name }}</h3>
                  <p>{{ visitedUserCircle.description }}</p>
                  <div class="circle-stats">
                    <span>{{ visitedUserCircle.memberCount || 0 }} 粉丝</span>
                    <span>{{ visitedUserCircle.postCount || 0 }} 帖子</span>
                  </div>
                </div>
              </div>
              <div class="circle-actions">
                <button 
                  v-if="isMemberOfCircle" 
                  class="action-btn enter" 
                  @click.stop="handleEnterCircle(visitedUserCircle)"
                >
                  进入圈子
                </button>
                <button 
                  v-else 
                  class="action-btn join" 
                  @click.stop="handleJoinCircle(visitedUserCircle)"
                >
                  加入圈子
                </button>
              </div>
            </div>
            <div v-else class="no-circle-state">
              <p>该用户还未创建圈子</p>
            </div>
          </div>

          <!-- 该用户加入的圈子 -->
          <div class="circles-section">
            <h3 class="section-title">该用户加入的圈子</h3>
            <div v-if="circlesList && circlesList.length > 0" class="circles-list">
              <div v-for="circle in circlesList" :key="circle.id" class="circle-card-wrapper">
                <div class="circle-card" @click="handleEnterCircle(circle)">
                  <div class="circle-cover">
                    <img :src="circle.avatar || circle.coverImageUrl || require('@/assets/logo.png')" alt="cover">
                  </div>
                  <div class="circle-info">
                    <h3>{{ circle.name }}</h3>
                    <p>{{ circle.description }}</p>
                    <div class="circle-stats">
                      <span>{{ circle.memberCount || 0 }} 粉丝</span>
                      <span>{{ circle.postCount || 0 }} 帖子</span>
                    </div>
                  </div>
                </div>
                <div class="circle-actions">
                  <button 
                    v-if="myJoinedCircleIds.has(circle.id)" 
                    class="action-btn enter" 
                    @click.stop="handleEnterCircle(circle)"
                  >
                    进入圈子
                  </button>
                  <button 
                    v-else 
                    class="action-btn join" 
                    @click.stop="handleJoinCircle(circle)"
                  >
                    加入圈子
                  </button>
                </div>
              </div>
            </div>
            <div v-else class="no-circle-state">
              <p>该用户还未加入圈子</p>
            </div>
          </div>
        </div>

        <div v-else class="empty-state">
          <div class="icon">📭</div>
          <div class="title">暂无内容</div>
        </div>
      </section>
    </main>

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
              <button
                :class="['follow-tab', { active: followModalTab === 'circles' }]"
                @click="followModalTab = 'circles'"
              >
                圈子 ({{ circlesList.length }})
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
              {{ getFollowButtonText(user.followStatus, followModalTab) }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getCurrentUserId } from '@/utils/auth'
import { 
  getUserPosts, 
  getFollowers, 
  getFollowing, 
  getUserJoinedCircles, 
  getUserProfile,
  getUserLikedVideos,
  toggleFollow as apiToggleFollow,
  checkFollow,
  getMyCreatedCircles,
  joinCircle,
  checkCircleMembership
} from '@/utils/api'
import { getDemoAsset } from '@/utils/demoDataMap'

export default {
  name: 'UserProfileView',
  data() {
    return {
      searchQuery: '',
      user: {
        avatar: require('@/assets/avatar.jpg'),
        name: '加载中...',
        isLive: false,
        following: 0,
        followers: 0,
        circles: 0,
        likes: 0,
        signature: '',
        sn: '',
        age: 0
      },
      panel: {
        avatar: require('@/assets/avatar.jpg'),
        name: '加载中...',
        followings: 0,
        followers: 0,
        circles: 0,
        likes: '0',
        favorites: [],
        quickEntries: [
          { key: 'works', icon: '🎬', label: '作品', value: '0' }
        ],
        rememberLogin: true
      },
      tabs: [
        { key: 'works', label: '作品', badge: null },
        { key: 'likes', label: '喜欢' },
        { key: 'circles', label: '圈子' },
      ],
      visitedUserCircle: null,
      isMemberOfCircle: false,
      myJoinedCircleIds: new Set(),
      activeTab: 'works',
      workSearchQuery: '', // 作品搜索关键词
      showDateFilter: false, // 是否显示日期筛选面板
      dateFilterStart: '', // 开始日期
      dateFilterEnd: '', // 结束日期
      showFollowModal: false,
      followModalTab: 'following',
      followSearchQuery: '',
      showSortDropdown: false,
      selectedSort: '综合排序',
      sortOptions: ['综合排序', '最近关注', '最早关注'],
      sortDropdownTimer: null,
      showRoleInfo: false,
      activeRoleTab: 'basic',
      roleTabs: [
        { key: 'basic', label: '基本信息' },
        { key: 'tags', label: '标签' },
        { key: 'story', label: '背景故事' }
      ],
      followingList: [],
      circlesList: [],
      followersList: [],
      shortVideos: [],
      likedVideoIds: [],
      userWorks: [],
      isFollowingUser: false
    }
  },
  created() {
    this.loadMySectionDataFromBackend()
    this.loadUserWorksFromBackend()
    this.loadVisitedUserCircle()
  },
  computed: {
    likedVideos() {
      return this.shortVideos
    },
    myWorks() {
      let works = this.userWorks || []
      
      // 默认（公开）模式下，不显示私密作品
      works = works.filter(work => !work.isPrivate)
      
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
    displayedFollowList() {
      let list = this.followModalTab === 'following'
        ? this.followingList
        : this.followModalTab === 'followers'
          ? this.followersList
          : this.circlesList
      
      // 搜索过滤
      if (this.followSearchQuery.trim()) {
        const query = this.followSearchQuery.toLowerCase()
        list = list.filter(user => {
          return user.name.toLowerCase().includes(query) ||
                 (user.description && user.description.toLowerCase().includes(query)) ||
                 (user.title && user.title.toLowerCase().includes(query))
        })
      }
      
      // 排序（仅对关注列表进行排序，粉丝/圈子列表不排序）
      if (this.followModalTab === 'circles') {
        list.sort((a, b) => {
             if (a.isOwnerCircle && !b.isOwnerCircle) return -1;
             if (!a.isOwnerCircle && b.isOwnerCircle) return 1;
             return 0;
         })
      } else if (this.followModalTab === 'following' && this.selectedSort !== '综合排序') {
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
    },
    currentRoleCard() {
      // 从localStorage获取角色卡，直接显示最新的
      // 注意：这里应该从后端获取该用户的角色卡，但目前没有API，暂时留空或模拟
      // 实际项目中应该调用API获取 targetUserId 的角色卡
      return null
    }
  },
  watch: {
    '$route.query.id': {
      immediate: true,
      handler(newId) {
        if (newId) {
            this.loadMySectionDataFromBackend()
            this.loadUserWorksFromBackend()
            this.loadVisitedUserCircle()
        }
      }
    },
    activeTab() {
      this.loadActiveTabData()
    },
  },
  mounted() {
    // 点击外部关闭日期筛选面板
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside)
    if (this.sortDropdownTimer) {
      clearTimeout(this.sortDropdownTimer)
      this.sortDropdownTimer = null
    }
  },
  methods: {
    async loadVisitedUserCircle() {
      try {
        const queryId = this.$route.query.id || this.$route.params.id
        const uid = queryId ? parseInt(queryId) : null
        if (!uid) return
        
        const circlesPage = await getMyCreatedCircles(uid)
        if (circlesPage && circlesPage.content && circlesPage.content.length > 0) {
          this.visitedUserCircle = circlesPage.content[0]
          // Check membership
          const currentUserId = getCurrentUserId()
          if (currentUserId) {
            const isMember = await checkCircleMembership(this.visitedUserCircle.id, currentUserId)
            this.isMemberOfCircle = isMember
          }
        } else {
          this.visitedUserCircle = null
        }
      } catch (error) {
        console.error('Failed to load visited user circle:', error)
      }
    },
    async handleEnterCircle(circle) {
      this.$router.push({
        path: "/com-detail",
        query: {
          id: circle.id,
          name: circle.name,
          avatar: circle.avatar || circle.cover || circle.coverImageUrl || require('@/assets/logo.png')
        }
      });
    },
    async handleJoinCircle(circle) {
      if (!circle) return
      
      const currentUserId = getCurrentUserId()
      if (!currentUserId) {
        this.$router.push('/login')
        return
      }

      if (this.myJoinedCircleIds.has(circle.id)) {
        this.handleEnterCircle(circle)
        return
      }

      // Check follow status of the circle creator (whoever it is)
      if (circle.creatorId && circle.creatorId !== currentUserId) {
          try {
              const isFollowing = await checkFollow(currentUserId, circle.creatorId)
              if (!isFollowing) {
                const confirmFollow = confirm('加入圈子需要先关注圈主，是否关注并加入？')
                if (confirmFollow) {
                    await apiToggleFollow(circle.creatorId, currentUserId)
                    // Update local state if we just followed the visited user
                    if (circle.creatorId === this.user.id) {
                        this.isFollowingUser = true
                        this.user.followers++
                    }
                } else {
                    return // User cancelled
                }
              }
          } catch (e) {
              console.warn('Check follow status failed', e)
              // Continue to try joining even if check failed (or handle error)
          }
      }
      
      // General join logic
      try {
          await joinCircle(circle.id, currentUserId)
          this.myJoinedCircleIds.add(circle.id)
          if (circle.id === this.visitedUserCircle?.id) {
              this.isMemberOfCircle = true
          }
          this.handleEnterCircle(circle)
      } catch (error) {
          // Handle duplicate entry error gracefully
          if (error.message && error.message.includes('Duplicate entry')) {
              this.myJoinedCircleIds.add(circle.id)
              if (circle.id === this.visitedUserCircle?.id) {
                  this.isMemberOfCircle = true
              }
              this.handleEnterCircle(circle)
              return
          }
          console.error('Failed to join circle:', error)
          alert('加入圈子失败: ' + (error.message || '未知错误'))
      }
    },
    async loadMySectionDataFromBackend() {
      const currentUid = getCurrentUserId()
      const queryId = this.$route.query.id || this.$route.params.id
      const uid = queryId ? parseInt(queryId) : null
      
      if (!uid) return

      try {
        // 0. 检查关注状态
        if (currentUid && currentUid !== uid) {
            try {
                const status = await checkFollow(currentUid, uid)
                this.isFollowingUser = status
            } catch (e) {
                console.warn('Check follow status failed', e)
            }
        }

        // 1. 获取用户个人资料
        const profile = await getUserProfile(uid)
        if (profile) {
            this.user = {
                ...this.user,
                id: profile.id,
                name: profile.username,
                avatar: profile.avatarUrl || this.user.avatar,
                following: profile.followingCount || 0,
                followers: profile.followersCount || 0,
                circles: profile.circlesCount || 0,
                likes: profile.likesCount || 0,
                signature: profile.introduction || '暂无简介',
            }
            
            this.panel = {
                ...this.panel,
                name: profile.username,
                avatar: profile.avatarUrl || this.panel.avatar,
                followings: profile.followingCount || 0,
                followers: profile.followersCount || 0,
                circles: profile.circlesCount || 0,
                likes: String(profile.likesCount || 0),
            }
        }
        
        // 2. 加载关注列表
        const followingRes = await getFollowing(uid, 0, 100)
        if (followingRes && followingRes.content) {
          this.followingList = followingRes.content.map(item => {
            const targetUser = item.following || item
            return {
              id: targetUser.userId || targetUser.id,
              name: targetUser.username || targetUser.name || '未知用户',
              avatar: targetUser.avatarUrl || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + (targetUser.username || 'user'),
              title: targetUser.title || '用户',
              description: targetUser.introduction || '暂无简介',
              followStatus: 'followed',
              verified: false,
              followTime: item.followDate ? new Date(item.followDate).getTime() : new Date().getTime()
            }
          })
        } else {
          this.followingList = []
        }

        // 3. 加载粉丝列表
        const followersRes = await getFollowers(uid, 0, 100)
        if (followersRes && followersRes.content) {
          const followingIds = new Set(this.followingList.map(u => u.id))
          this.followersList = followersRes.content.map(item => {
            const targetUser = item.follower || item
            return {
              id: targetUser.userId || targetUser.id,
              name: targetUser.username || targetUser.name || '未知用户',
              avatar: targetUser.avatarUrl || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + (targetUser.username || 'user'),
              description: targetUser.introduction || '暂无简介',
              followStatus: followingIds.has(targetUser.userId || targetUser.id) ? 'mutual' : 'not-followed',
              verified: false
            }
          })
        } else {
          this.followersList = []
        }

        // 4. 加载圈子列表
        const circlesRes = await getUserJoinedCircles(uid, 0, 100)
        if (circlesRes && circlesRes.content) {
          if (typeof circlesRes.totalElements === 'number') {
            this.user.circles = circlesRes.totalElements
          }

          this.circlesList = circlesRes.content
            .filter(c => c.creatorId !== uid)
            .map(circle => ({
            id: circle.id,
            name: circle.name,
            avatar: circle.avatarUrl || circle.coverImageUrl || require('@/assets/community/avatar1.jpg'),
            title: '圈子',
            description: circle.description || '暂无描述',
            creatorId: circle.creatorId,
            isOwnerCircle: circle.creatorId === uid
          }))
        } else {
          this.circlesList = []
        }

        // Load MY joined circles to check membership
        if (currentUid) {
            try {
                const myCirclesRes = await getUserJoinedCircles(currentUid, 0, 100)
                if (myCirclesRes && myCirclesRes.content) {
                    this.myJoinedCircleIds = new Set(myCirclesRes.content.map(c => c.id))
                }
            } catch (e) {
                console.warn('Failed to load my joined circles', e)
            }
        }

          // 5. 加载喜欢列表用于预览
          const likesRes = await getUserLikedVideos(uid, 0, 3)
        const likedVideos = likesRes.content || []
        this.panel.favorites = likedVideos.map(v => ({
            id: v.id,
            tag: v.category || '#视频',
            title: v.title,
            gradient: v.coverImageUrl ? `url(${v.coverImageUrl}) center/cover no-repeat` : 'linear-gradient(135deg, #fdfcfb 0%, #e2d1c3 100%)'
        }))

        // 6. 加载当前标签页数据
        this.loadActiveTabData()
        
      } catch (e) {
        console.error('Failed to load profile data', e)
      }
    },

    async loadActiveTabData() {
        const queryId = this.$route.query.id || this.$route.params.id
        const uid = queryId ? parseInt(queryId) : null
        if (!uid) return
        
        try {
            if (this.activeTab === 'works') {
                // Works are handled by loadUserWorks
            } else if (this.activeTab === 'likes') {
                const res = await getUserLikedVideos(uid, 0, 20)
                const videos = res.content || []
                this.shortVideos = videos.map(v => ({
                    id: v.id,
                    title: v.title,
                    creator: v.authorName,
                    duration: this.formatDuration(v.duration),
                    views: `${v.views}次观看`,
                    tags: v.tags ? v.tags.split(',') : [],
                    thumbnailColor: v.coverImageUrl ? `url(${v.coverImageUrl}) center/cover no-repeat` : 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)',
                }))
                this.likedVideoIds = this.shortVideos.map(v => v.id)
            }
        } catch (e) {
            console.error('Failed to load tab data', e)
        }
    },
    
    formatDuration(seconds) {
        if (!seconds) return '00:00'
        const m = Math.floor(seconds / 60)
        const s = seconds % 60
        return `${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}`
    },

    resolveUrl(url) {
      if (!url) return ''
      const demoAsset = getDemoAsset(url, 'image')
      if (demoAsset) return demoAsset
      
      if (url.startsWith('http') || url.startsWith('blob:') || url.startsWith('data:')) return url
      
      if (url.includes('assets')) {
          let assetPath = url.replace('@/', '/');
          if (!assetPath.startsWith('/')) {
              assetPath = '/' + assetPath;
          }
          return assetPath;
      }

      const cleanUrl = url.startsWith('/') ? url.slice(1) : url
      return `http://127.0.0.1:8081/${cleanUrl}`
    },
    async loadUserWorksFromBackend(page = 0, size = 20) {
      const queryId = this.$route.query.id || this.$route.params.id
      const uid = queryId ? parseInt(queryId) : null
      if (!uid) return
      
      const pageData = await getUserPosts(uid, page, size)
      const list = Array.isArray(pageData?.content) ? pageData.content : []
      const mapDuration = (sec) => {
        if (!sec && sec !== 0) return ''
        const s = Math.max(0, parseInt(sec, 10) || 0)
        const mm = String(Math.floor(s / 60)).padStart(2, '0')
        const ss = String(s % 60).padStart(2, '0')
        return `${mm}:${ss}`
      }
      this.userWorks = list.map(v => {
        const tags = v.tags ? String(v.tags).split(',').map(t => t.trim()).filter(Boolean) : []
        const isPrivate = tags.includes('__PRIVATE__')
        const displayTags = tags.filter(t => t !== '__PRIVATE__')
        
        return {
          id: v.id,
          title: v.title,
          creator: v.authorName || this.user.name,
          duration: mapDuration(v.duration),
          tags: displayTags,
          thumbnail: this.resolveUrl(v.coverImageUrl),
          views: v.views || 0,
          uploadTime: v.createdAt || null,
          isPrivate: isPrivate
        }
      })
      // 更新快速入口中的作品数量
      const worksEntry = this.panel.quickEntries.find(entry => entry.key === 'works')
      if (worksEntry) worksEntry.value = this.userWorks.length.toString()
    },
    handleWorkSearch(event) {
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
      if (this.dateFilterStart && this.dateFilterEnd) {
        if (this.dateFilterStart > this.dateFilterEnd) {
          alert('开始日期不能晚于结束日期')
          return
        }
      }
      this.closeDateFilter()
    },
    clearDateFilter() {
      this.dateFilterStart = ''
      this.dateFilterEnd = ''
      this.closeDateFilter()
    },
    navigateToTab(tabKey, { scroll = true, syncQuery = true } = {}) {
      if (!tabKey) return
      if (this.activeTab !== tabKey) {
        this.activeTab = tabKey
      }
      if (scroll) {
        this.scrollToTabSection()
      }
    },
    handlePanelQuickEntry(link) {
      if (!link?.key) return
      this.navigateToTab(link.key)
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
    handleSidebarStatClick(tab) {
        this.openFollowModal(tab)
    },
    async toggleFollowUser() {
      const myId = getCurrentUserId()
      if (!myId) {
        alert('请先登录')
        return
      }
      
      const targetId = this.user.id
      if (!targetId) return

      try {
        await apiToggleFollow(targetId, myId)
        this.isFollowingUser = !this.isFollowingUser
        // Reload data to update counts
        this.loadMySectionDataFromBackend()
      } catch (error) {
        console.error('关注操作失败', error)
        alert('操作失败: ' + (error.message || '未知错误'))
      }
    },
    async toggleFollow(user) {
      const myId = getCurrentUserId()
      if (!myId) {
        alert('请先登录')
        return
      }

      try {
        await apiToggleFollow(user.id, myId)
        await this.loadMySectionDataFromBackend()
      } catch (error) {
        console.error('关注操作失败', error)
        alert('操作失败: ' + (error.message || '未知错误'))
      }
    },
    getFollowButtonText(status, tab = 'following') {
      if (tab === 'circles') {
        const circleMap = {
          'joined': '已加入',
          'followed': '加入圈子',
          'not-followed': '加入圈子'
        }
        return circleMap[status] || '加入圈子'
      }

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
    getThumbnailStyle(video) {
      let thumbnailUrl = video.thumbnail
      if (thumbnailUrl && thumbnailUrl !== this.defaultThumbnail) {
        return {
          backgroundImage: `url(${thumbnailUrl})`,
          backgroundSize: 'cover',
          backgroundPosition: 'center',
          backgroundRepeat: 'no-repeat',
          backgroundColor: video.thumbnailColor || '#f5f5f5',
        }
      }
      return { background: video.thumbnailColor || '#f5f5f5' }
    },
    handleVideoClick(video) {
      this.$router.push({ 
        path: '/video', 
        query: { id: video.id } 
      }).catch(() => {})
    },
    toggleRoleInfo() {
      this.showRoleInfo = !this.showRoleInfo
    },
    getGenderText(gender) {
      const genderMap = {
        'male': '男',
        'female': '女',
        'other': '其他'
      }
      return genderMap[gender] || gender
    },
    formatBirthday(birthday) {
      if (!birthday) return ''
      const date = new Date(birthday)
      const month = date.getMonth() + 1
      const day = date.getDate()
      return `${month}月${day}日`
    },
    goBack() {
      this.$router.back()
    }
  }
}
</script>

<style scoped>
.profile-page {
  display: block; /* Changed from grid to block since sidebar is removed */
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
  position: relative;
}

.profile-content {
  padding: 80px 48px 80px;
  display: flex;
  flex-direction: column;
  gap: 32px;
  max-width: 1200px; /* Limit width for better readability */
  margin: 0 auto; /* Center content */
}

.back-btn {
  position: absolute;
  top: 32px;
  left: 48px;
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

/* ... Copy rest of styles from ProfileView.vue but remove sidebar related styles ... */

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
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  overflow: hidden;
  transition: border-color 0.3s ease, background-color 0.3s ease, box-shadow 0.3s ease;
}

.search-input:hover {
  border-color: rgba(255, 105, 180, 0.5);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 12px rgba(255, 105, 180, 0.15);
}

.search-input:focus-within {
  border-color: rgba(255, 105, 180, 0.6);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.2);
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
  color: #2d2d2d;
}

.search-field input:focus {
  outline: none;
}

.search-icon {
  color: rgba(45, 45, 45, 0.6);
}

.search-divider {
  width: 1px;
  background: rgba(255, 105, 180, 0.2);
  margin: 8px 0;
}

.search-btn {
  padding: 12px 24px;
  border: 1px solid rgba(255, 105, 180, 0.5);
  border-radius: 0 12px 12px 0;
  background: rgba(255, 255, 255, 0.9);
  color: #ff69b4;
  font-weight: 600;
  cursor: pointer;
  border-left: 1px solid rgba(255, 105, 180, 0.5);
  transition: all 0.25s ease;
}

.search-btn:hover {
  background: rgba(255, 105, 180, 0.06);
  box-shadow: 0 4px 14px rgba(255, 105, 180, 0.25);
  transform: translateY(-1px);
  border-color: rgba(255, 105, 180, 0.8);
}

.search-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.avatar-dropdown {
  position: relative;
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
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 24px;
  padding: 20px;
  box-shadow: 0 12px 40px rgba(255, 105, 180, 0.25);
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
  background: rgba(255, 105, 180, 0.1);
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
  color: #2d2d2d;
}

.profile-stats {
  margin: 4px 0 0;
  color: rgba(45, 45, 45, 0.65);
  font-size: 0.9rem;
}

.favorite-section {
  background: rgba(255, 105, 180, 0.05);
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
  color: #2d2d2d;
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
  background: rgba(255, 255, 255, 0.6);
  border-radius: 14px;
  padding: 6px;
  font-size: 0.75rem;
  border: 1px solid rgba(255, 105, 180, 0.1);
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
  color: rgba(45, 45, 45, 0.75);
  line-height: 1.2;
}

.quick-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 16px;
}

.quick-link {
  background: rgba(255, 105, 180, 0.05);
  border: 1px solid rgba(255, 105, 180, 0.1);
  border-radius: 14px;
  padding: 10px 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #2d2d2d;
  cursor: pointer;
  transition: background 0.2s ease, border-color 0.2s ease, transform 0.2s ease;
}

.quick-link:hover,
.quick-link:focus-visible {
  background: rgba(255, 105, 180, 0.12);
  border-color: rgba(255, 105, 180, 0.3);
  transform: translateX(2px);
}

.ql-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 500;
}

.ql-value {
  color: rgba(45, 45, 45, 0.65);
}

.hero {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 24px 24px 12px 12px;
  padding-bottom: 10px;
  overflow: visible;
  border: 1px solid rgba(255, 105, 180, 0.2);
  margin-bottom: 0;
  box-shadow: 0 4px 20px rgba(255, 105, 180, 0.15);
  position: relative;
}

.cover {
  height: 80px;
  background: linear-gradient(135deg, rgba(255, 105, 180, 0.15) 0%, rgba(147, 112, 219, 0.15) 50%, rgba(72, 209, 204, 0.1) 100%);
}

.profile-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 0 24px 12px;
  margin-top: -32px;
  position: relative;
}

.profile-card .avatar {
  width: 120px;
  height: 120px;
  border-radius: 28px;
  border: 4px solid rgba(255, 255, 255, 0.9);
  object-fit: cover;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.3);
}

.info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.info h1 {
  margin: 0;
  color: #2d2d2d;
  font-size: 1.5rem;
  font-weight: 600;
}

.signature {
  margin: 0;
  color: rgba(45, 45, 45, 0.7);
  font-size: 0.95rem;
  line-height: 1.5;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.follow-btn {
  background: #ff69b4;
  color: white;
  border: none;
  border-radius: 999px;
  padding: 6px 16px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-left: 12px;
}

.follow-btn:hover {
  background: #ff1493;
  transform: translateY(-1px);
}

.follow-btn.followed {
  background: rgba(0, 0, 0, 0.1);
  color: rgba(0, 0, 0, 0.6);
}

.stats {
  display: flex;
  gap: 16px;
  color: #000;
}

.stat-item.clickable {
  cursor: pointer;
  transition: color 0.2s ease;
  color: #000;
}

.stat-item.clickable:hover {
  color: #ff69b4;
}

.role-info-toggle-btn {
  position: absolute;
  bottom: -20px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 40px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-top: none;
  border-radius: 0 0 30px 30px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  z-index: 10;
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.role-info-toggle-btn:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(255, 105, 180, 0.5);
  box-shadow: 0 6px 16px rgba(255, 105, 180, 0.3);
}

.role-info-toggle-btn .toggle-icon {
  color: #ff69b4;
  font-size: 1.2rem;
  transition: transform 0.3s ease;
}

.role-info-toggle-btn.expanded .toggle-icon {
  transform: rotate(180deg);
}

.role-info-panel {
  max-height: 0;
  overflow: hidden;
  transition: max-height 0.5s ease;
  background: rgba(255, 255, 255, 0.95);
  border-top: 1px solid rgba(255, 105, 180, 0.2);
}

.role-info-panel.expanded {
  max-height: 1000px;
}

.role-info-content {
  display: flex;
  gap: 24px;
  padding: 24px;
  min-height: 400px;
}

.role-portrait {
  width: 300px;
  height: 400px;
  flex-shrink: 0;
  border-radius: 16px;
  overflow: hidden;
  background: rgba(255, 105, 180, 0.1);
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.role-portrait img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.role-portrait-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 4rem;
  color: rgba(255, 105, 180, 0.3);
}

.role-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.role-header {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.role-name {
  margin: 0;
  font-size: 2rem;
  font-weight: 700;
  color: #2d2d2d;
}

.role-tabs {
  display: flex;
  gap: 8px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  padding-bottom: 8px;
}

.role-tab {
  background: transparent;
  border: none;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.95rem;
  padding: 8px 16px;
  cursor: pointer;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.role-tab:hover {
  color: #ff69b4;
  background: rgba(255, 105, 180, 0.05);
}

.role-tab.active {
  color: #ff69b4;
  background: rgba(255, 105, 180, 0.1);
  font-weight: 600;
}

.role-tab-content {
  flex: 1;
  padding: 16px 0;
  min-height: 200px;
}

.role-basic-info {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 0;
  border-bottom: 1px solid rgba(255, 105, 180, 0.1);
}

.info-label {
  min-width: 80px;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.9rem;
}

.info-value {
  color: #2d2d2d;
  font-size: 0.95rem;
  font-weight: 500;
}

.role-tags-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.tags-group h4 {
  margin: 0 0 12px;
  color: #2d2d2d;
  font-size: 1rem;
  font-weight: 600;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.role-tag {
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
}

.role-tag.personality {
  background: rgba(255, 105, 180, 0.15);
  color: #ff69b4;
  border: 1px solid rgba(255, 105, 180, 0.3);
}

.role-tag.race {
  background: rgba(147, 112, 219, 0.15);
  color: #9370db;
  border: 1px solid rgba(147, 112, 219, 0.3);
}

.role-tag.appearance {
  background: rgba(72, 209, 204, 0.15);
  color: #48d1cc;
  border: 1px solid rgba(72, 209, 204, 0.3);
}

.role-story-content {
  padding: 16px 0;
}

.story-text {
  color: #2d2d2d;
  font-size: 0.95rem;
  line-height: 1.8;
  white-space: pre-wrap;
  margin: 0;
}

.empty-text {
  color: rgba(45, 45, 45, 0.5);
  font-size: 0.9rem;
  text-align: center;
  padding: 40px 0;
  margin: 0;
}

.content-area.role-info-expanded {
  margin-top: 20px;
  transition: margin-top 0.5s ease;
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
  color: rgba(45, 45, 45, 0.7);
  transition: color 0.2s ease;
}

.tabs li:hover {
  color: rgba(45, 45, 45, 0.9);
}

.tabs li.active {
  color: #ff69b4;
  border-bottom-color: #ff69b4;
}

.content-area {
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
  border-radius: 12px 12px 20px 20px;
  border: 1px solid rgba(255, 105, 180, 0.2);
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

.filters {
  display: flex;
  gap: 12px;
}

.filters .search-input {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  border-radius: 12px;
  padding: 8px 16px;
  outline: none;
  font-size: 14px;
  min-width: 200px;
  transition: border-color 0.3s ease, background-color 0.3s ease, box-shadow 0.3s ease;
}

.filters .search-input:hover {
  border-color: rgba(255, 105, 180, 0.5);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
}

.filters .search-input::placeholder {
  color: rgba(45, 45, 45, 0.5);
}

.filters .search-input:focus {
  border-color: rgba(255, 105, 180, 0.6);
  background-color: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}

.date-filter-wrapper {
  position: relative;
}

.date-filter-btn {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
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
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.date-filter-btn.active {
  background: rgba(255, 77, 103, 0.15);
  border-color: rgba(255, 77, 103, 0.4);
  color: #ff69b4;
}

.filter-badge {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #ff69b4;
  display: inline-block;
}

.date-filter-panel {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 320px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 12px 32px rgba(255, 105, 180, 0.25);
  z-index: 20;
}

.date-filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  font-weight: 600;
  color: #2d2d2d;
}

.close-date-filter {
  width: 24px;
  height: 24px;
  border: none;
  background: rgba(255, 105, 180, 0.1);
  color: rgba(45, 45, 45, 0.7);
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  transition: all 0.2s ease;
}

.close-date-filter:hover {
  background: rgba(255, 105, 180, 0.2);
  color: #ff69b4;
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
  color: rgba(45, 45, 45, 0.7);
  font-weight: 500;
}

.date-input {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  border-radius: 10px;
  padding: 10px 12px;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.date-input:focus {
  outline: none;
  border-color: #ff69b4;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.2);
}

.date-input::-webkit-calendar-picker-indicator {
  filter: none;
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
  color: rgba(45, 45, 45, 0.7);
  border: 1px solid rgba(255, 105, 180, 0.3);
}

.clear-date-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.apply-date-btn {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
}

.apply-date-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.empty-state {
  border: 1px dashed rgba(255, 105, 180, 0.3);
  border-radius: 16px;
  padding: 60px 20px;
  text-align: center;
  color: rgba(45, 45, 45, 0.65);
  background: rgba(255, 255, 255, 0.5);
}

.empty-state .icon {
  font-size: 2.5rem;
  margin-bottom: 16px;
}

.empty-state .title {
  font-size: 1.2rem;
  margin-bottom: 8px;
  color: #2d2d2d;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.video-card {
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.2);
  overflow: hidden;
  display: flex;
  flex-direction: column;
  transition: all 0.3s ease;
  cursor: pointer;
}

.video-card:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 105, 180, 0.4);
  box-shadow: 0 8px 25px rgba(255, 105, 180, 0.25);
  transform: translateY(-4px);
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
  background: rgba(0, 0, 0, 0.75);
  color: #fff;
  font-size: 0.78rem;
  font-weight: 500;
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
  color: #2d2d2d;
}

.video-meta .creator {
  margin: 0;
  color: rgba(45, 45, 45, 0.7);
  font-size: 0.9rem;
}

.video-meta .stats {
  margin: 0;
  color: rgba(45, 45, 45, 0.5);
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
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 20px 60px rgba(255, 105, 180, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.follow-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
}

.follow-tabs {
  display: flex;
  gap: 24px;
}

.follow-tab {
  background: transparent;
  border: none;
  color: rgba(45, 45, 45, 0.6);
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  padding: 0;
  transition: color 0.2s ease;
}

.follow-tab.active {
  color: #ff69b4;
}

.follow-tab:hover {
  color: #ff69b4;
}

.close-btn {
  width: 32px;
  height: 32px;
  border: none;
  background: rgba(255, 105, 180, 0.1);
  color: rgba(45, 45, 45, 0.8);
  border-radius: 50%;
  cursor: pointer;
  font-size: 1.2rem;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s ease, color 0.2s ease;
}

.close-btn:hover {
  background: rgba(255, 105, 180, 0.2);
  color: #ff69b4;
}

.follow-modal-search {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
}

.search-box {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 12px;
  transition: all 0.2s ease;
}

.search-box:focus-within {
  border-color: rgba(255, 105, 180, 0.5);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
}

.search-box input {
  flex: 1;
  background: transparent;
  border: none;
  color: #2d2d2d;
  font-size: 0.9rem;
}

.search-box input:focus {
  outline: none;
}

.search-box input::placeholder {
  color: rgba(45, 45, 45, 0.5);
}

.search-icon {
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.9rem;
}

.sort-dropdown {
  position: relative;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 12px;
  color: rgba(45, 45, 45, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.sort-dropdown:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 105, 180, 0.5);
}

.sort-arrow {
  font-size: 0.7rem;
  color: rgba(45, 45, 45, 0.6);
}

.sort-dropdown-menu {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  min-width: 140px;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(255, 105, 180, 0.25);
  overflow: hidden;
  z-index: 10;
}

.sort-option {
  padding: 12px 16px;
  color: rgba(45, 45, 45, 0.8);
  font-size: 0.9rem;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.sort-option:hover {
  background: rgba(255, 105, 180, 0.1);
  color: #ff69b4;
}

.sort-option.active {
  color: #ff69b4;
  background: rgba(255, 105, 180, 0.15);
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
  background: rgba(255, 105, 180, 0.05);
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: rgba(255, 105, 180, 0.1);
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
  color: #2d2d2d;
  font-size: 0.95rem;
}

.verified-badge {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background: #48d1cc;
  color: #fff;
  font-size: 0.7rem;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-title {
  color: rgba(45, 45, 45, 0.8);
  font-size: 0.85rem;
  font-weight: 500;
}

.user-subtitle {
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.8rem;
}

.user-description {
  color: rgba(45, 45, 45, 0.7);
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
  color: #ff69b4;
  font-size: 0.8rem;
  margin-top: 2px;
}

.follow-status-btn {
  padding: 8px 20px;
  border-radius: 20px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  font-size: 0.85rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
  white-space: nowrap;
}

.follow-status-btn.followed {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.3);
  color: #ff69b4;
}

.follow-status-btn.mutual {
  background: rgba(255, 105, 180, 0.15);
  border-color: rgba(255, 105, 180, 0.4);
  color: #ff69b4;
}

.follow-status-btn.not-followed {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
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
  background: rgba(255, 105, 180, 0.3);
  border-radius: 3px;
}

.follow-modal-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 105, 180, 0.5);
}

/* 圈子相关样式 */
.circles-container {
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 32px;
}

.circles-section {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.section-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d2d2d;
  padding-left: 12px;
  border-left: 4px solid #ff69b4;
}

.circles-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.circle-card-wrapper {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  overflow: hidden;
  transition: transform 0.2s, box-shadow 0.2s;
  display: flex;
  flex-direction: column;
}

.circle-card-wrapper:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.circle-card {
  display: flex;
  padding: 16px;
  cursor: pointer;
  flex: 1;
}

.circle-cover {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  margin-right: 16px;
}

.circle-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.circle-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.circle-info h3 {
  margin: 0 0 8px;
  font-size: 1.1rem;
  color: #2d2d2d;
}

.circle-info p {
  margin: 0 0 8px;
  font-size: 0.9rem;
  color: #666;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.circle-stats {
  display: flex;
  gap: 16px;
  font-size: 0.85rem;
  color: #999;
}

.circle-actions {
  padding: 12px 16px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.action-btn {
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
}

.action-btn.enter {
  background: #ff69b4;
  color: #fff;
}

.action-btn.enter:hover {
  background: #ff4da6;
}

.action-btn.join {
  background: #fff;
  border-color: #ff69b4;
  color: #ff69b4;
}

.action-btn.join:hover {
  background: #fff0f5;
}

.no-circle-state {
  text-align: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  color: #666;
}

.circle-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding-right: 100px; /* 为按钮留出空间 */
}

.circle-info h3 {
  margin: 0 0 8px;
  font-size: 1.2rem;
  color: #333;
}

.circle-info p {
  margin: 0 0 12px;
  color: #666;
  font-size: 0.9rem;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.circle-stats {
  display: flex;
  gap: 16px;
  color: #999;
  font-size: 0.85rem;
}

.circle-action {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
}

.enter-circle-btn,
.join-circle-btn {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.enter-circle-btn {
  background: #f0f0f0;
  color: #333;
}

.enter-circle-btn:hover {
  background: #e0e0e0;
}

.join-circle-btn {
  background: #ff69b4;
  color: white;
}

.join-circle-btn:hover {
  background: #ff4da6;
}

.no-circle-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.no-circle-state p {
  margin-bottom: 16px;
  font-size: 1.1rem;
}
</style>
