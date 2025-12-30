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
            <button class="dropdown-item" @click="goToGoingLive">开直播</button>
            <button class="dropdown-item" @click="goToCreateCircle">创建圈子</button>
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
                  <span class="clickable" @click.stop="handleSidebarStatClick('following')">关注 {{ panel.followings }}</span>
                  · <span class="clickable" @click.stop="handleSidebarStatClick('followers')">粉丝 {{ panel.followers }}</span>
                  · <span class="clickable" @click.stop="handleSidebarStatClick('circles')">圈子 {{ panel.circles }}</span>
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
          <div class="avatar-wrapper" @click.stop="openAvatarModal">
            <img class="avatar" :src="user.avatar" alt="avatar">
            <div class="avatar-overlay">
              <span>查看大图</span>
            </div>
          </div>
          <div class="info">
            <div class="name-row">
              <h1>{{ user.name }}</h1>
              <button v-if="isOwner" class="edit-btn" @click="openEditModal">
                ✏️
              </button>
              <button v-else class="follow-btn" @click="toggleFollowUser" :class="{ 'followed': isFollowingUser }">
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
          <button v-if="isOwner" class="role-card-btn" @click="openRoleCardModal">
            🎭 {{ currentRoleCard ? '编辑角色卡' : '申请角色卡' }}
          </button>
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
            @click="handleVideoClick(video)"
          >
            <div class="thumbnail" :style="getThumbnailStyle(video)">
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
            @click="handleVideoClick(video)"
          >
            <div class="thumbnail" :style="getThumbnailStyle(video)">
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
          v-else-if="activeTab === 'history' && historyVideos.length"
          class="history-list"
        >
          <div v-if="groupedHistory.today.length" class="history-group">
            <h3 class="group-title">今天</h3>
            <div class="video-grid">
              <article
                v-for="video in groupedHistory.today"
                :key="video.id"
                class="video-card"
                @click="handleVideoClick(video)"
              >
                <div class="thumbnail" :style="getThumbnailStyle(video)">
                </div>
                <div class="video-meta">
                  <h3>{{ video.title }}</h3>
                  <p class="creator">@{{ video.creator }}</p>
                  <p class="stats">{{ video.views }}</p>
                </div>
              </article>
            </div>
          </div>
          
          <div v-if="groupedHistory.yesterday.length" class="history-group">
            <h3 class="group-title">昨天</h3>
            <div class="video-grid">
              <article
                v-for="video in groupedHistory.yesterday"
                :key="video.id"
                class="video-card"
                @click="handleVideoClick(video)"
              >
                <div class="thumbnail" :style="getThumbnailStyle(video)">
                </div>
                <div class="video-meta">
                  <h3>{{ video.title }}</h3>
                  <p class="creator">@{{ video.creator }}</p>
                  <p class="stats">{{ video.views }}</p>
                </div>
              </article>
            </div>
          </div>
          
          <div v-if="groupedHistory.beforeYesterday.length" class="history-group">
            <h3 class="group-title">前天</h3>
            <div class="video-grid">
              <article
                v-for="video in groupedHistory.beforeYesterday"
                :key="video.id"
                class="video-card"
                @click="handleVideoClick(video)"
              >
                <div class="thumbnail" :style="getThumbnailStyle(video)">
                </div>
                <div class="video-meta">
                  <h3>{{ video.title }}</h3>
                  <p class="creator">@{{ video.creator }}</p>
                  <p class="stats">{{ video.views }}</p>
                </div>
              </article>
            </div>
          </div>
        </div>

        <div
          v-else-if="activeTab === 'circles'"
          class="circles-container"
        >
          <!-- 我的圈子 -->
          <div class="circles-section">
            <h3 class="section-title">我的圈子</h3>
            <div v-if="userCircle" class="circle-card-wrapper">
              <div class="circle-card" @click="goToCircle(userCircle.id)">
                <div class="circle-cover">
                  <img :src="userCircle.cover || userCircle.coverImageUrl || require('@/assets/logo.png')" alt="cover">
                </div>
                <div class="circle-info">
                  <h3>{{ userCircle.name }}</h3>
                  <p>{{ userCircle.description }}</p>
                  <div class="circle-stats">
                    <span>{{ userCircle.memberCount || 0 }} 粉丝</span>
                    <span>{{ userCircle.postCount || 0 }} 帖子</span>
                  </div>
                </div>
              </div>
              <div class="circle-actions">
                <button class="action-btn enter" @click.stop="goToCircle(userCircle.id)">进入圈子</button>
                <button class="action-btn delete" @click.stop="handleDeleteCircle(userCircle.id)">删除圈子</button>
              </div>
            </div>
            <div v-else class="no-circle-state">
              <p>还未创建圈子</p>
              <button class="create-circle-btn" @click="goToCreateCircle">创建圈子</button>
            </div>
          </div>

          <!-- 加入的圈子 -->
          <div class="circles-section">
            <h3 class="section-title">加入的圈子</h3>
            <div v-if="circlesList && circlesList.filter(c => !c.isMyCircle).length > 0" class="circles-list">
              <div v-for="circle in circlesList.filter(c => !c.isMyCircle)" :key="circle.id" class="circle-card-wrapper">
                <div class="circle-card" @click="goToCircle(circle.id)">
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
                  <button class="action-btn enter" @click.stop="goToCircle(circle.id)">进入圈子</button>
                  <button class="action-btn quit" @click.stop="handleQuitCircle(circle.id)">退出圈子</button>
                </div>
              </div>
            </div>
            <div v-else class="no-circle-state">
              <p>还未加入其他圈子</p>
            </div>
          </div>
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
                <span v-if="followModalTab === 'circles' && user.isMyCircle" class="my-circle-badge">我的圈子</span>
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

    <!-- 申请角色卡弹窗 -->
    <div v-if="showRoleCardModal" class="modal-overlay" @click.self="closeRoleCardModal">
      <div class="role-card-modal">
        <div class="role-card-header">
          <h3>{{ currentRoleCard ? '编辑角色卡' : '申请角色卡' }}</h3>
          <button class="close-btn" @click="closeRoleCardModal">✕</button>
        </div>
        <div class="role-card-content">
          <div class="portrait-upload-wrapper">
            <label class="portrait-label">角色肖像</label>
            <div class="portrait-upload-area">
              <div 
                v-if="!roleCardForm.portrait" 
                class="portrait-placeholder"
                @click.stop="triggerPortraitInput"
              >
                <span class="upload-icon">📷</span>
                <span class="upload-text">点击上传角色肖像</span>
              </div>
              <div 
                v-else 
                class="portrait-preview"
                @click.stop="triggerPortraitInput"
              >
                <img :src="roleCardForm.portrait" alt="角色肖像预览">
                <div class="portrait-overlay">
                  <span>点击更换</span>
                </div>
              </div>
              <input
                ref="portraitInput"
                type="file"
                accept="image/*"
                style="display: none"
                @change="handlePortraitSelect"
              >
            </div>
          </div>

          <label>
            角色名称
            <input 
              v-model="roleCardForm.name" 
              type="text" 
              placeholder="输入角色名称"
              maxlength="20"
            >
          </label>
          
          <label>
            性别
            <select v-model="roleCardForm.gender">
              <option value="">请选择</option>
              <option value="male">男</option>
              <option value="female">女</option>
              <option value="other">其他</option>
            </select>
          </label>

          <label>
            生日
            <input 
              v-model="roleCardForm.birthday" 
              type="date"
            >
          </label>

          <label>
            身高 (cm)
            <input 
              v-model="roleCardForm.height" 
              type="number" 
              placeholder="输入身高"
              min="0"
              max="300"
            >
          </label>

          <label>
            爱好
            <input 
              v-model="roleCardForm.hobby" 
              type="text" 
              placeholder="输入爱好"
              maxlength="50"
            >
          </label>

          <div class="tags-section">
            <label class="tags-label">性格标签</label>
            <div class="tags-group">
              <label 
                v-for="tag in personalityTags" 
                :key="tag"
                class="tag-checkbox"
                :class="{ 'tag-checked': roleCardForm.personalityTags.includes(tag) }"
              >
                <input 
                  type="checkbox" 
                  :value="tag"
                  v-model="roleCardForm.personalityTags"
                >
                <span>{{ tag }}</span>
              </label>
            </div>
          </div>

          <div class="tags-section">
            <label class="tags-label">种族标签</label>
            <div class="tags-group">
              <label 
                v-for="tag in raceTags" 
                :key="tag"
                class="tag-checkbox"
                :class="{ 'tag-checked': roleCardForm.raceTags.includes(tag) }"
              >
                <input 
                  type="checkbox" 
                  :value="tag"
                  v-model="roleCardForm.raceTags"
                >
                <span>{{ tag }}</span>
              </label>
            </div>
          </div>

          <div class="tags-section">
            <label class="tags-label">外观标签</label>
            <div class="tags-group">
              <label 
                v-for="tag in appearanceTags" 
                :key="tag"
                class="tag-checkbox"
                :class="{ 'tag-checked': roleCardForm.appearanceTags.includes(tag) }"
              >
                <input 
                  type="checkbox" 
                  :value="tag"
                  v-model="roleCardForm.appearanceTags"
                >
                <span>{{ tag }}</span>
              </label>
            </div>
          </div>

          <label>
            背景故事
            <textarea 
              v-model="roleCardForm.backgroundStory" 
              rows="5" 
              placeholder="输入角色的背景故事"
              maxlength="500"
            ></textarea>
            <span class="char-count">{{ roleCardForm.backgroundStory.length }}/500</span>
          </label>
        </div>
        <div class="role-card-actions">
          <button class="ghost" @click="closeRoleCardModal">取消</button>
          <button class="primary" @click="submitRoleCard">{{ currentRoleCard ? '保存修改' : '提交申请' }}</button>
        </div>
      </div>
    </div>

    <!-- 头像放大弹窗 -->
    <div v-if="showAvatarModal" class="avatar-modal-overlay" @click.self="closeAvatarModal">
      <div class="avatar-modal">
        <button class="close-btn" @click="closeAvatarModal">✕</button>
        <div class="avatar-large-container">
          <img :src="user.avatar" alt="大头像" class="avatar-large">
        </div>
        <div class="avatar-actions">
          <button v-if="isOwner" class="change-avatar-btn" @click="triggerFileInput">更换头像</button>
          <input
            type="file"
            ref="avatarInput"
            accept="image/*"
            style="display: none"
            @change="handleFileChange"
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { clearAuthToken, getCurrentUser, getCurrentUserId } from '@/utils/auth'
import { 
  getUserPosts, 
  getFollowers, 
  getFollowing, 
  getUserFavorites, 
  getUserJoinedCircles, 
  getVideoById,
  getUserProfile,
  getUserVideos,
  getUserLikedVideos,
  getUserHistory,
  getUserCircles,
  getMyCreatedCircles,
  deleteVideo,
  toggleVideoLike,
  toggleFollow as apiToggleFollow,
  checkFollow,
  dissolveCircle,
  leaveCircle,
  uploadImage,
  updateUserProfile,
  getUserRoleCards,
  createRoleCard,
  updateRoleCard
} from '@/utils/api'
// 使用后端 API 更新密码；本地 mock 方法已弃用
import { getDemoAsset } from '@/utils/demoDataMap'

export default {
  name: 'ProfileView',
  data() {
    return {
      searchQuery: '',
      navLinks: [
        { key: 'discover', label: '发现内容', icon: '✨' },
        { key: 'live', label: '直播', icon: '📡' },
        { key: 'community', label: '社区', icon: '💬' },
        { key: 'my', label: '我的', icon: '👤' }
      ],
      activeNav: 'my',
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
          { key: 'history', icon: '🕒', label: '观看历史', value: '0' },
          { key: 'works', icon: '🎬', label: '我的作品', value: '0' }
        ],
        rememberLogin: true
      },
      tabs: [
        { key: 'works', label: '作品', badge: null },
        { key: 'likes', label: '喜欢' },
        { key: 'history', label: '观看历史' },
        { key: 'circles', label: '圈子' },
      ],
      activeTab: 'works',
      userCircle: null, // 用户创建的圈子
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
      roleCards: [],
      showRoleCardModal: false,
      roleCardForm: {
        portrait: '',
        name: '',
        gender: '',
        birthday: '',
        height: '',
        hobby: '',
        personalityTags: [],
        raceTags: [],
        appearanceTags: [],
        backgroundStory: ''
      },
      personalityTags: ['元气', '冷感', '温柔', '傲娇', '社恐', '疯批'],
      raceTags: ['普通人', '精灵', '魔族', 'AI', '兽耳少女'],
      appearanceTags: ['长发', '双马尾', '异色瞳', '兽耳'],
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
      historyVideos: [],
      likedVideoIds: [1, 5, 7, 10, 13, 15],
      userWorks: [],
      isOwner: true,
      isFollowingUser: false,
      showAvatarModal: false
    }
  },
  created() {
    this.loadStoredProfile()
    // 先尝试从后端加载我的作品，失败时回退到本地存储
    this.loadUserWorksFromBackend().catch(() => {
      this.loadUserWorks()
    })
    // 加载“我的”页面依赖的用户独有数据（关注/粉丝/圈子/收藏）
    this.loadMySectionDataFromBackend()
    this.loadUserCircle()
  },
  computed: {
    groupedHistory() {
      const groups = {
        today: [],
        yesterday: [],
        beforeYesterday: []
      }
      
      const today = new Date()
      today.setHours(0, 0, 0, 0)
      
      const yesterday = new Date(today)
      yesterday.setDate(yesterday.getDate() - 1)
      
      const beforeYesterday = new Date(today)
      beforeYesterday.setDate(beforeYesterday.getDate() - 2)
      
      this.historyVideos.forEach(video => {
        if (!video.viewedAt) return
        const viewDate = new Date(video.viewedAt)
        viewDate.setHours(0, 0, 0, 0)
        
        if (viewDate.getTime() === today.getTime()) {
          groups.today.push(video)
        } else if (viewDate.getTime() === yesterday.getTime()) {
          groups.yesterday.push(video)
        } else if (viewDate.getTime() === beforeYesterday.getTime()) {
          groups.beforeYesterday.push(video)
        }
      })
      
      return groups
    },
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
      } else {
        // 默认（公开）模式下，不显示私密作品
        works = works.filter(work => !work.isPrivate)
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
             if (a.isMyCircle && !b.isMyCircle) return -1;
             if (!a.isMyCircle && b.isMyCircle) return 1;
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
      if (this.roleCards && this.roleCards.length > 0) {
        // 按提交时间排序，取最新的
        const sortedCards = [...this.roleCards].sort((a, b) => {
          const timeA = new Date(a.submitTime || 0).getTime()
          const timeB = new Date(b.submitTime || 0).getTime()
          return timeB - timeA
        })
        return sortedCards[0]
      }
      return null
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
      this.loadActiveTabData()
      // 切换标签页时退出批量管理模式
      if (this.isBatchMode) {
        this.exitBatchMode()
      }
    },
  },
  mounted() {
    this.loadMySectionDataFromBackend()
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
    openAvatarModal() {
      console.log('Open avatar modal clicked')
      this.showAvatarModal = true
    },
    closeAvatarModal() {
      this.showAvatarModal = false
    },
    triggerFileInput() {
      this.$refs.avatarInput.click()
    },
    async handleFileChange(event) {
      const file = event.target.files[0]
      if (!file) return

      try {
        // Upload image
        const uploadResult = await uploadImage(file)
        let imageUrl = ''
        if (uploadResult && typeof uploadResult === 'object') {
            imageUrl = uploadResult.url
        } else {
            imageUrl = uploadResult
        }
        
        // Update user profile
        const userId = getCurrentUserId()
        const updatedProfile = await updateUserProfile(userId, {
          avatar: imageUrl
        })

        // Update local state
        if (updatedProfile && updatedProfile.avatarUrl) {
          this.user.avatar = updatedProfile.avatarUrl
          this.panel.avatar = updatedProfile.avatarUrl
        } else {
          this.user.avatar = imageUrl
          this.panel.avatar = imageUrl
        }
        
        // Close modal
        this.closeAvatarModal()
        
        alert('头像更新成功')
      } catch (error) {
        console.error('Failed to update avatar:', error)
        alert('头像更新失败: ' + (error.message || '未知错误'))
      }
    },
    async loadUserCircle() {
      try {
        const userId = getCurrentUserId()
        if (!userId) return
        const circlesPage = await getMyCreatedCircles(userId)
        if (circlesPage && circlesPage.content && circlesPage.content.length > 0) {
          this.userCircle = circlesPage.content[0] // 假设一个用户只能创建一个圈子
        } else {
          this.userCircle = null
        }
      } catch (error) {
        console.error('Failed to load user circle:', error)
      }
    },
    goToCreateCircle() {
      this.$router.push('/community/create')
    },
    goToCircle(circleId) {
      // 获取圈子信息以便传递给详情页
      const circle = this.circlesList.find(c => c.id === circleId) || 
                     (this.userCircle && this.userCircle.id === circleId ? this.userCircle : null)
      
      if (circle) {
        this.$router.push({
          path: "/com-detail",
          query: {
            id: circle.id,
            name: circle.name,
            avatar: circle.avatar || circle.cover || circle.coverImageUrl
          }
        })
      } else {
        // Fallback if circle not found in list (shouldn't happen often)
        this.$router.push({
          path: "/com-detail",
          query: { id: circleId }
        })
      }
    },
    async handleDeleteCircle(circleId) {
      if (!confirm('确定要删除这个圈子吗？此操作不可撤销。')) return
      
      const userId = getCurrentUserId()
      try {
        await dissolveCircle(circleId, userId)
        alert('圈子已删除')
        this.userCircle = null
        this.loadMySectionDataFromBackend() // Refresh lists
      } catch (error) {
        console.error('删除圈子失败', error)
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    },
    async handleQuitCircle(circleId) {
      if (!confirm('确定要退出这个圈子吗？')) return
      
      const userId = getCurrentUserId()
      try {
        await leaveCircle(circleId, userId)
        alert('已退出圈子')
        this.loadMySectionDataFromBackend() // Refresh lists
      } catch (error) {
        console.error('退出圈子失败', error)
        alert('退出失败: ' + (error.message || '未知错误'))
      }
    },
    async loadMySectionDataFromBackend() {
      const currentUid = getCurrentUserId()
      const queryId = this.$route.query.id
      const uid = queryId ? parseInt(queryId) : currentUid
      
      if (!uid) return

      this.isOwner = (uid === currentUid)
      
      try {
        // 0. 如果不是本人，检查关注状态
        if (!this.isOwner && currentUid) {
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
                avatar: profile.avatarUrl || require('@/assets/avatar.jpg'),
                following: profile.followingCount || 0,
                followers: profile.followersCount || 0,
                circles: profile.circlesCount || 0,
                likes: profile.likesCount || 0,
                signature: profile.introduction || '暂无简介',
            }
            
            if (this.isOwner) {
                this.panel = {
                    ...this.panel,
                    name: profile.username,
                    avatar: profile.avatarUrl || require('@/assets/avatar.jpg'),
                    followings: profile.followingCount || 0,
                    followers: profile.followersCount || 0,
                    circles: profile.circlesCount || 0,
                    likes: String(profile.likesCount || 0),
                }
            }
        }
        
        // 如果不是本人，单独加载本人的 Sidebar 数据
        if (!this.isOwner && currentUid) {
             const myProfile = await getUserProfile(currentUid)
             if (myProfile) {
                 this.panel = {
                    ...this.panel,
                    name: myProfile.username,
                    avatar: myProfile.avatarUrl || this.panel.avatar,
                    followings: myProfile.followingCount || 0,
                    followers: myProfile.followersCount || 0,
                    circles: myProfile.circlesCount || 0,
                    likes: String(myProfile.likesCount || 0),
                 }
             }
        }

        // 2. 加载关注列表
        const followingRes = await getFollowing(uid, 0, 100)
        if (followingRes && followingRes.content) {
          this.followingList = followingRes.content.map(item => {
            // 后端返回的是 UserFollow 对象，包含 follower 和 following
            // 在关注列表中，我是 follower，我要看的是 following (被关注者)
            const targetUser = item.following || item
            return {
              id: targetUser.userId || targetUser.id,
              name: targetUser.username || targetUser.name || '未知用户',
              avatar: targetUser.avatarUrl || 'https://api.dicebear.com/7.x/avataaars/svg?seed=' + (targetUser.username || 'user'),
              title: targetUser.title || '用户',
              description: targetUser.introduction || '暂无简介',
              followStatus: 'followed', // 在关注列表中，状态肯定是已关注
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
            // 在粉丝列表中，我是 following，我要看的是 follower (粉丝)
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
          // 使用列表的总数来更新显示，确保一致性
          if (typeof circlesRes.totalElements === 'number') {
            this.user.circles = circlesRes.totalElements
            if (this.isOwner) this.panel.circles = circlesRes.totalElements
          }

          this.circlesList = circlesRes.content.map(circle => ({
            id: circle.id,
            name: circle.name,
            avatar: circle.avatarUrl || circle.coverImageUrl || require('@/assets/community/avatar1.jpg'),
            title: '圈子',
            description: circle.description || '暂无描述',
            creatorId: circle.creatorId,
            isMyCircle: String(circle.creatorId) === String(uid)
          }))
        } else {
          this.circlesList = []
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

        // 7. 加载角色卡
        await this.loadRoleCards(uid)
        
      } catch (e) {
        console.error('Failed to load profile data', e)
      }
    },

    async loadActiveTabData() {
        const uid = this.$route.query.id || getCurrentUserId()
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
                    circleId: v.circleId,
                    circleName: v.circleName
                }))
                this.likedVideoIds = this.shortVideos.map(v => v.id)
            } else if (this.activeTab === 'history') {
                const res = await getUserHistory(uid, 0, 50)
                const historyItems = res.content || []
                this.historyVideos = historyItems.map(item => ({
                    ...item.video,
                    viewedAt: item.viewedAt,
                    creator: item.video.authorName,
                    duration: this.formatDuration(item.video.duration),
                    views: `${item.video.views}次观看`,
                    tags: item.video.tags ? item.video.tags.split(',') : [],
                    thumbnailColor: item.video.coverImageUrl ? `url(${item.video.coverImageUrl}) center/cover no-repeat` : 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)',
                }))
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

    async loadMySectionDataFromBackend_OLD() {
      const uid = getCurrentUserId()
      const uname = getCurrentUser()
      if (uname) {
        this.user.name = uname
        this.panel.name = uname
      }
      if (!uid) return
      try {
        // 关注/粉丝列表计数
        const [followersPage, followingPage] = await Promise.all([
          getFollowers(uid, 0, 50),
          getFollowing(uid, 0, 50)
        ])
        const followersCount = followersPage?.totalElements ?? (Array.isArray(followersPage?.content) ? followersPage.content.length : 0)
        const followingCount = followingPage?.totalElements ?? (Array.isArray(followingPage?.content) ? followingPage.content.length : 0)
        this.user.followers = followersCount
        this.user.following = followingCount
        this.panel.followers = followersCount
        this.panel.followings = followingCount

        // 加入的圈子计数
        const circlesPage = await getUserJoinedCircles(uid, 0, 50)
        const circlesCount = circlesPage?.totalElements ?? (Array.isArray(circlesPage?.content) ? circlesPage.content.length : 0)
        this.panel.circles = circlesCount
        this.user.circles = circlesCount

        // 收藏（我的喜欢）列表，映射为预览卡片（少量展示）
        const favPage = await getUserFavorites(uid, 'post', 0, 20)
        const favList = Array.isArray(favPage?.content) ? favPage.content : []
        // 从收藏的内容ID拉取视频标题（仅取前几项做预览）
        const previewCount = Math.min(3, favList.length)
        const previewFetch = favList.slice(0, previewCount).map(async fav => {
          const vidId = fav.contentId
          try {
            const v = await getVideoById(vidId)
            return {
              id: vidId,
              tag: '#收藏',
              title: v?.title || `视频 ${vidId}`,
              gradient: 'linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%)'
            }
          } catch {
            return { id: vidId, tag: '#收藏', title: `视频 ${vidId}`, gradient: 'linear-gradient(135deg, #f6d365 0%, #fda085 100%)' }
          }
        })
        const preview = await Promise.all(previewFetch)
        this.panel.favorites = preview
        // 喜欢总数（以收藏数近似呈现）
        this.panel.likes = String(favList.length)

        // 6. 加载角色卡
        await this.loadRoleCards(uid)
      } catch (err) {
        console.warn('加载我的数据失败', err)
      }
    },
    async loadRoleCards(userId) {
      try {
        const res = await getUserRoleCards(userId)
        if (res && Array.isArray(res)) {
          this.roleCards = res
        } else {
          this.roleCards = []
        }
      } catch (error) {
        console.warn('加载角色卡失败', error)
        this.roleCards = []
      }
    },
    resolveUrl(url) {
      if (!url) return ''
      // Check demo asset map first
      const demoAsset = getDemoAsset(url, 'image')
      if (demoAsset) return demoAsset
      
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
    },
    async loadUserWorksFromBackend(page = 0, size = 20) {
      const uid = getCurrentUserId()
      if (!uid) throw new Error('未登录或缺少用户ID')
      const pageData = await getUserPosts(uid, page, size)
      // pageData 形如 { content: [...], totalElements, totalPages, ... }
      const list = Array.isArray(pageData?.content) ? pageData.content : []
      // 将后端 VideoDto 映射到页面需要的字段
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
    async saveProfile() {
      const trimmedName = this.editForm.name?.trim()
      const trimmedSignature = this.editForm.signature?.trim() ?? ''
      const password = this.editForm.password?.trim()
      const confirmPassword = this.editForm.confirmPassword?.trim()

      // 验证密码（若填写则必须通过校验）
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
      }

      // 准备要发送到后端的字段（只包含需要更新的字段）
      const payload = {}
      if (trimmedName) payload.name = trimmedName
      if (trimmedSignature !== null) payload.signature = trimmedSignature
      if (password) {
        payload.password = password
        payload.confirmPassword = confirmPassword
      }

      // 如果有要更新的内容，调用后端 API；否则仅关闭弹窗
      if (Object.keys(payload).length > 0) {
        const userId = getCurrentUserId()
        if (!userId) {
          alert('无法获取当前用户信息，请重新登录')
          return
        }

        try {
          const updatedProfile = await updateUserProfile(userId, payload)
          // 更新本地显示（以后端返回为准）
          if (updatedProfile) {
            if (updatedProfile.username) {
              this.user.name = updatedProfile.username
              this.panel.name = updatedProfile.username
            }
            if (typeof updatedProfile.introduction === 'string') {
              this.user.signature = updatedProfile.introduction
            }
            if (updatedProfile.avatarUrl) {
              this.user.avatar = updatedProfile.avatarUrl
              this.panel.avatar = updatedProfile.avatarUrl
            }
          }
          alert('资料已更新')
        } catch (err) {
          console.error('更新资料失败', err)
          alert('更新失败: ' + (err.message || '请重试'))
          return
        }
      }

      // 清理并关闭
      this.editForm.password = ''
      this.editForm.confirmPassword = ''
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
      this.loadUserWorksFromBackend()
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
        community: '/community',
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
    goToGoingLive() {
      // 从个人页“开直播”入口跳转到直播管理页（开播设置）
      this.$router.push({ path: '/live-manage' }).catch(() => {})
    },
    goToCreateCircle() {
      this.$router.push({ path: '/create-circle' }).catch(() => { })
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
    handleSidebarStatClick(tab) {
      if (this.isOwner) {
        this.openFollowModal(tab)
      } else {
        // 如果不是本人，点击侧边栏（我的）数据时，跳转到我的个人主页
        const currentUid = getCurrentUserId()
        if (currentUid) {
          this.$router.push({ path: '/profile', query: { id: currentUid } })
        }
      }
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
    async handleBatchDelete() {
      if (this.selectedItems.length === 0) {
        return
      }
      if (confirm(`确定要删除选中的 ${this.selectedItems.length} 个作品吗？`)) {
        const uid = getCurrentUserId()
        try {
          if (this.activeTab === 'works') {
            // 调用后端API删除视频
            await Promise.all(this.selectedItems.map(id => deleteVideo(id, uid)))
            // 重新加载列表
            await this.loadUserWorksFromBackend()
          } else if (this.activeTab === 'likes') {
            // 调用后端API取消点赞
            await Promise.all(this.selectedItems.map(id => toggleVideoLike(id, uid)))
            // 重新加载喜欢列表
            const likesRes = await getUserLikedVideos(uid)
            // 这里需要更新 activeTabData 或者直接刷新页面，简单起见重新加载当前标签页数据
            this.loadActiveTabData()
          }
          
          this.selectedItems = []
          // 如果删除后没有作品了，自动退出批量管理模式
          const currentVideos = this.activeTab === 'works' ? this.myWorks : this.likedVideos
          if (currentVideos.length === 0) {
            this.exitBatchMode()
          }
          
          // 提示成功
          // alert('操作成功') 
        } catch (error) {
          console.error('批量操作失败', error)
          alert('操作失败，请重试')
        }
      }
    },
    // 权限设置功能已移除
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
      if (this.followModalTab === 'circles') {
          this.goToCircle(user.id)
          return
      }

      const myId = getCurrentUserId()
      if (!myId) {
        alert('请先登录')
        return
      }

      try {
        await apiToggleFollow(user.id, myId)
        
        // 重新加载社交数据以确保一致性
        await this.loadMySectionDataFromBackend()
        
      } catch (error) {
        console.error('关注操作失败', error)
        alert('操作失败: ' + (error.message || '未知错误'))
      }
    },
    getFollowButtonText(status, tab = 'following') {
      if (tab === 'circles') {
        return '进入圈子'
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
      // 优先使用上传的封面
      let thumbnailUrl = video.thumbnail
      
      // 如果有coverKey，尝试从sessionStorage读取
      if (video.coverKey && sessionStorage.getItem(video.coverKey)) {
        thumbnailUrl = sessionStorage.getItem(video.coverKey)
      }
      
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
      // 在批量管理模式下，不跳转
      if (this.isBatchMode) {
        return
      }
      
      // 如果是圈子帖子，跳转到圈子详情页
      if (video.circleId) {
        this.$router.push({
          path: '/com-detail',
          query: { 
            id: video.circleId,
            name: video.circleName,
            highlightPostId: video.id // 传递帖子ID以便高亮或定位（需ComDetail支持）
          }
        }).catch(() => {})
        return
      }

      // 跳转到视频页面，传递视频ID
      this.$router.push({ 
        path: '/video', 
        query: { id: video.id } 
      }).catch(() => {})
    },
    openRoleCardModal() {
      // 如果有现有角色卡，加载数据到表单
      if (this.currentRoleCard) {
        this.roleCardForm = {
          portrait: this.currentRoleCard.portrait || '',
          name: this.currentRoleCard.name || '',
          gender: this.currentRoleCard.gender || '',
          birthday: this.currentRoleCard.birthday || '',
          height: this.currentRoleCard.height || '',
          hobby: this.currentRoleCard.hobby || '',
          personalityTags: this.currentRoleCard.personalityTags ? [...this.currentRoleCard.personalityTags] : [],
          raceTags: this.currentRoleCard.raceTags ? [...this.currentRoleCard.raceTags] : [],
          appearanceTags: this.currentRoleCard.appearanceTags ? [...this.currentRoleCard.appearanceTags] : [],
          backgroundStory: this.currentRoleCard.backgroundStory || ''
        }
      } else {
        // 如果没有角色卡，重置表单
        this.roleCardForm = {
          portrait: '',
          name: '',
          gender: '',
          birthday: '',
          height: '',
          hobby: '',
          personalityTags: [],
          raceTags: [],
          appearanceTags: [],
          backgroundStory: ''
        }
      }
      this.showRoleCardModal = true
    },
    closeRoleCardModal() {
      this.showRoleCardModal = false
      // 重置表单
      this.roleCardForm = {
        portrait: '',
        name: '',
        gender: '',
        birthday: '',
        height: '',
        hobby: '',
        personalityTags: [],
        raceTags: [],
        appearanceTags: [],
        backgroundStory: ''
      }
      // 重置文件输入
      if (this.$refs.portraitInput) {
        this.$refs.portraitInput.value = ''
      }
    },
    async submitRoleCard() {
      // 表单验证
      if (!this.roleCardForm.name || !this.roleCardForm.name.trim()) {
        alert('请输入角色名称')
        return
      }
      if (!this.roleCardForm.gender) {
        alert('请选择性别')
        return
      }
      if (!this.roleCardForm.birthday) {
        alert('请选择生日')
        return
      }
      if (!this.roleCardForm.height || this.roleCardForm.height <= 0) {
        alert('请输入有效的身高')
        return
      }
      if (this.roleCardForm.personalityTags.length === 0) {
        alert('请至少选择一个性格标签')
        return
      }
      if (this.roleCardForm.raceTags.length === 0) {
        alert('请至少选择一个种族标签')
        return
      }
      if (this.roleCardForm.appearanceTags.length === 0) {
        alert('请至少选择一个外观标签')
        return
      }
      if (!this.roleCardForm.backgroundStory || !this.roleCardForm.backgroundStory.trim()) {
        alert('请输入背景故事')
        return
      }

      // 保存角色卡数据
      try {
        const userId = getCurrentUserId()
        if (!userId) {
            alert('请先登录')
            return
        }

        const cardData = {
            ...this.roleCardForm,
            portrait: this.roleCardForm.portrait || '',
            name: this.roleCardForm.name.trim(),
            hobby: this.roleCardForm.hobby.trim(),
            backgroundStory: this.roleCardForm.backgroundStory.trim(),
        }

        if (this.currentRoleCard) {
          // 编辑模式：更新现有角色卡
          await updateRoleCard(userId, this.currentRoleCard.id, cardData)
          alert('角色卡已更新成功！')
        } else {
          // 新建模式：创建新角色卡
          await createRoleCard(userId, cardData)
          alert('角色卡已创建成功！')
        }
        
        // 重新加载角色卡
        await this.loadRoleCards(userId)
        
        this.closeRoleCardModal()
        // 自动展开角色信息面板
        if (!this.showRoleInfo) {
          this.showRoleInfo = true
        }
      } catch (err) {
        console.error('保存角色卡失败', err)
        alert('提交失败: ' + (err.message || '未知错误'))
      }
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
    triggerPortraitInput() {
      this.$refs.portraitInput?.click()
    },
    handlePortraitSelect(event) {
      const file = event.target.files?.[0]
      if (file) {
        // 检查文件类型
        if (!file.type.startsWith('image/')) {
          alert('请选择图片文件')
          // 重置文件输入
          if (this.$refs.portraitInput) {
            this.$refs.portraitInput.value = ''
          }
          return
        }
        // 检查文件大小（限制为5MB）
        const maxSize = 5 * 1024 * 1024
        if (file.size > maxSize) {
          alert('图片文件过大（超过5MB），请选择较小的图片')
          // 重置文件输入
          if (this.$refs.portraitInput) {
            this.$refs.portraitInput.value = ''
          }
          return
        }
        // 将图片转换为base64
        const reader = new FileReader()
        reader.onload = (e) => {
          this.roleCardForm.portrait = e.target.result
        }
        reader.onerror = () => {
          alert('读取图片文件失败，请重试')
          // 重置文件输入
          if (this.$refs.portraitInput) {
            this.$refs.portraitInput.value = ''
          }
        }
        reader.readAsDataURL(file)
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
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  font-family: 'Segoe UI', 'PingFang SC', sans-serif;
  position: relative;
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 105, 180, 0.2);
  display: flex;
  flex-direction: column;
  gap: 32px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
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
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.4);
}

.brand-text h1 {
  font-size: 1.2rem;
  margin: 0;
}

.brand-text p {
  margin: 4px 0 0;
  color: rgba(45, 45, 45, 0.6);
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
  color: rgba(45, 45, 45, 0.75);
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
  background: linear-gradient(135deg, rgba(255, 105, 180, 0.15) 0%, rgba(147, 112, 219, 0.15) 100%);
  color: #ff69b4;
  border: 1px solid rgba(255, 105, 180, 0.3);
}

.cta-card {
  margin-top: auto;
  padding: 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
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
  background: rgba(255, 255, 255, 0.3);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.cta-btn:hover {
  background: rgba(255, 255, 255, 0.4);
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
  /* 保留左侧描边，让按钮与输入框视觉上闭合（与首页一致） */
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

.action-btn {
  padding: 10px 18px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #ff69b4;
  font-weight: 600;
}

.action-btn.primary {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 105, 180, 0.6);
  color: #ff69b4;
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
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 14px;
  padding: 10px 8px 8px;
  display: flex;
  flex-direction: column;
  gap: 6px;
  box-shadow: 0 8px 32px rgba(255, 105, 180, 0.2);
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
  color: #2d2d2d;
  text-align: left;
  cursor: pointer;
  font-weight: 500;
  transition: background 0.2s ease;
}

.dropdown-item:hover {
  background: rgba(255, 105, 180, 0.1);
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

.profile-badge {
  margin-left: auto;
  padding: 4px 10px;
  border-radius: 999px;
  border: 1px solid rgba(255, 105, 180, 0.4);
  background: linear-gradient(135deg, rgba(255, 105, 180, 0.1) 0%, rgba(147, 112, 219, 0.1) 100%);
  color: #ff69b4;
  font-size: 0.75rem;
  letter-spacing: 0.08em;
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

.secondary-links {
  display: flex;
  flex-direction: column;
  gap: 6px;
  margin-bottom: 16px;
}

.secondary-link {
  background: transparent;
  border: none;
  color: rgba(45, 45, 45, 0.75);
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 0;
  cursor: pointer;
  transition: color 0.2s ease;
}

.secondary-link:hover {
  color: #ff69b4;
}

.profile-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logout {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: transparent;
  color: #2d2d2d;
  border-radius: 999px;
  padding: 6px 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.logout:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.remember-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 0.85rem;
  color: rgba(45, 45, 45, 0.7);
}

.remember-toggle input {
  accent-color: #ff69b4;
}

.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.45);
  backdrop-filter: blur(6px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal-card {
  width: min(420px, 90vw);
  background: rgba(255, 255, 255, 0.98);
  border-radius: 16px;
  padding: 24px;
  border: 1px solid rgba(255, 105, 180, 0.18);
  box-shadow: 0 20px 60px rgba(255, 105, 180, 0.25);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-card h3 {
  margin: 0;
  color: #2d2d2d;
}

.modal-card label {
  display: flex;
  flex-direction: column;
  gap: 6px;
  font-size: 0.9rem;
  color: rgba(45, 45, 45, 0.85);
}

.modal-card input,
.modal-card textarea {
  border-radius: 10px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  padding: 10px 12px;
  font-size: 1rem;
}

.modal-card input:focus,
.modal-card textarea:focus {
  outline: none;
  border-color: #ff69b4;
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.15);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.modal-actions .ghost,
.modal-actions .primary {
  border-radius: 10px;
  padding: 9px 18px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.modal-actions .ghost {
  border: 1px solid rgba(45, 45, 45, 0.15);
  background: #fff;
  color: rgba(45, 45, 45, 0.8);
}

.modal-actions .ghost:hover {
  border-color: rgba(255, 105, 180, 0.45);
  color: #ff69b4;
  background: rgba(255, 105, 180, 0.08);
}

.modal-actions .primary {
  border: none;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
  box-shadow: 0 10px 24px rgba(255, 105, 180, 0.3);
}

.modal-actions .primary:hover {
  transform: translateY(-1px);
  box-shadow: 0 14px 30px rgba(255, 105, 180, 0.4);
}

/* 角色卡弹窗样式 */
.role-card-modal {
  width: min(600px, 90vw);
  max-height: 90vh;
  background: rgba(255, 255, 255, 0.98);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 20px 60px rgba(255, 105, 180, 0.3);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.role-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
}

.role-card-header h3 {
  margin: 0;
  color: #2d2d2d;
  font-size: 1.3rem;
  font-weight: 600;
}

.role-card-header .close-btn {
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

.role-card-header .close-btn:hover {
  background: rgba(255, 105, 180, 0.2);
  color: #ff69b4;
}

.role-card-content {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.role-card-content label {
  display: flex;
  flex-direction: column;
  gap: 8px;
  font-size: 0.95rem;
  color: #2d2d2d;
  font-weight: 500;
}

.role-card-content input,
.role-card-content select,
.role-card-content textarea {
  border-radius: 10px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  padding: 10px 12px;
  font-size: 0.95rem;
  transition: all 0.2s ease;
}

.role-card-content input:focus,
.role-card-content select:focus,
.role-card-content textarea:focus {
  outline: none;
  border-color: #ff69b4;
  background: rgba(255, 255, 255, 1);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.2);
}

.role-card-content textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.char-count {
  align-self: flex-end;
  font-size: 0.85rem;
  color: rgba(45, 45, 45, 0.6);
  margin-top: 4px;
}

.portrait-upload-wrapper {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.portrait-label {
  font-size: 0.95rem;
  color: #2d2d2d;
  font-weight: 500;
}

.portrait-upload-area {
  position: relative;
  width: 100%;
  cursor: pointer;
}

.portrait-placeholder {
  width: 100%;
  height: 200px;
  border: 2px dashed rgba(255, 105, 180, 0.3);
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: rgba(255, 105, 180, 0.05);
  transition: all 0.3s ease;
}

.portrait-placeholder:hover {
  border-color: rgba(255, 105, 180, 0.5);
  background: rgba(255, 105, 180, 0.1);
}

.upload-icon {
  font-size: 3rem;
  opacity: 0.6;
}

.upload-text {
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.9rem;
}

.portrait-preview {
  width: 100%;
  height: 200px;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  border: 2px solid rgba(255, 105, 180, 0.3);
}

.portrait-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.portrait-overlay {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
  color: #fff;
  font-size: 0.9rem;
  font-weight: 500;
}

.portrait-preview:hover .portrait-overlay {
  opacity: 1;
}

.tags-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tags-label {
  font-weight: 500;
  color: #2d2d2d;
  font-size: 0.95rem;
}

.tags-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-checkbox {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 105, 180, 0.1);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
  color: #2d2d2d;
}

.tag-checkbox:hover {
  background: rgba(255, 105, 180, 0.15);
  border-color: rgba(255, 105, 180, 0.5);
}

.tag-checkbox input[type="checkbox"] {
  margin: 0;
  cursor: pointer;
  accent-color: #ff69b4;
  width: 16px;
  height: 16px;
}

.tag-checkbox input[type="checkbox"]:checked + span {
  color: #ff69b4;
  font-weight: 500;
}

.tag-checkbox.tag-checked {
  background: rgba(255, 105, 180, 0.2);
  border-color: #ff69b4;
}

.tag-checkbox.tag-checked span {
  color: #ff69b4;
  font-weight: 500;
}

.role-card-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid rgba(255, 105, 180, 0.2);
}

.role-card-actions .ghost,
.role-card-actions .primary {
  border-radius: 10px;
  padding: 10px 24px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.2s ease;
}

.role-card-actions .ghost:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.role-card-actions .primary {
  border: none;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
  font-weight: 600;
}

.role-card-actions .primary:hover {
  opacity: 0.9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.3);
}

.role-card-content::-webkit-scrollbar {
  width: 6px;
}

.role-card-content::-webkit-scrollbar-track {
  background: transparent;
}

.role-card-content::-webkit-scrollbar-thumb {
  background: rgba(255, 105, 180, 0.3);
  border-radius: 3px;
}

.role-card-content::-webkit-scrollbar-thumb:hover {
  background: rgba(255, 105, 180, 0.5);
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

/* Avatar Modal Styles */
.avatar-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.avatar-modal {
  background: transparent;
  padding: 20px;
  border-radius: 12px;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.avatar-large-container {
  width: 400px;
  height: 400px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.avatar-large {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.5);
}

.avatar-actions {
  display: flex;
  gap: 10px;
}

.change-avatar-btn {
  background: #ff69b4;
  color: white;
  border: none;
  padding: 10px 24px;
  border-radius: 24px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.3);
}

.change-avatar-btn:hover {
  background: #ff1493;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(255, 105, 180, 0.4);
}

.avatar-wrapper {
  position: relative;
  cursor: pointer;
  width: 120px;
  height: 120px;
  border-radius: 28px;
  overflow: hidden;
  border: 4px solid rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.3);
}

.avatar-wrapper .avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border: none;
  border-radius: 0;
  box-shadow: none;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay span {
  color: white;
  font-size: 0.9rem;
  font-weight: 500;
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

.edit-btn {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: rgba(45, 45, 45, 0.8);
  border-radius: 50%;
  width: 32px;
  height: 32px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s ease;
}

.edit-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
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

.live-tag {
  padding: 4px 10px;
  border-radius: 999px;
  background: #ff69b4;
  font-size: 0.85rem;
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

.role-card-btn {
  margin-left: auto;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.9);
  color: #ff69b4;
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 12px;
  font-weight: 600;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
}

.role-card-btn:hover {
  background: rgba(255, 255, 255, 1);
  border-color: rgba(255, 105, 180, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.25);
}

.meta {
  display: flex;
  gap: 16px;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.9rem;
}

/* 角色信息下拉框样式 */
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

.hero-actions {
  display: flex;
  gap: 12px;
}

.hero-actions .primary {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  border: none;
  padding: 10px 22px;
  border-radius: 14px;
  color: #fff;
}

.hero-actions .ghost {
  border: 1px solid rgba(255, 105, 180, 0.3);
  padding: 10px 22px;
  border-radius: 14px;
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  transition: all 0.2s ease;
}

.hero-actions .ghost:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
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

.work-type-buttons {
  display: flex;
  gap: 10px;
}

.work-type-buttons button {
  padding: 8px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: rgba(45, 45, 45, 0.7);
  cursor: pointer;
  transition: all 0.2s ease;
  font-size: 0.9rem;
}

.work-type-buttons button:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.work-type-buttons button.active {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  color: #fff;
  border-color: transparent;
}

.pill-group {
  display: flex;
  gap: 10px;
  flex: 1;
}

.pill-group button {
  padding: 8px 16px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: rgba(45, 45, 45, 0.7);
  transition: all 0.2s ease;
}

.pill-group button:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.pill-group button.active {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  color: #fff;
  border-color: transparent;
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

.filters button {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  border-radius: 12px;
  padding: 8px 16px;
  transition: all 0.2s ease;
}

.filters button:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
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

.batch-manage-btn-wrapper {
  display: flex;
  align-items: center;
}

.batch-manage-btn {
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  border-radius: 12px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.2s ease;
}

.batch-manage-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.2);
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
  color: #2d2d2d;
  font-size: 0.9rem;
  user-select: none;
}

.select-all-checkbox input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: #ff69b4;
}

.selected-count {
  color: rgba(45, 45, 45, 0.7);
  font-size: 0.9rem;
}

.selected-count strong {
  color: #2d2d2d;
  font-weight: 600;
}

.batch-toolbar-divider {
  width: 1px;
  height: 24px;
  background: rgba(255, 105, 180, 0.2);
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
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  border-radius: 10px;
  color: #2d2d2d;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
}

.batch-action-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.batch-action-btn.delete-btn:hover {
  border-color: rgba(255, 77, 103, 0.5);
  color: #ff69b4;
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
  accent-color: #ff69b4;
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

.history-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.history-group {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.group-title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2d2d2d;
  padding-left: 12px;
  border-left: 4px solid #ff69b4;
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

.action-btn.delete, .action-btn.quit {
  background: #fff;
  border-color: #ff4d4f;
  color: #ff4d4f;
}

.action-btn.delete:hover, .action-btn.quit:hover {
  background: #fff1f0;
}

.no-circle-state {
  text-align: center;
  padding: 40px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 12px;
  color: #666;
}

.create-circle-btn {
  margin-top: 16px;
  padding: 8px 24px;
  background: #ff69b4;
  color: #fff;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.create-circle-btn:hover {
  background: #ff4da6;
  transform: translateY(-1px);
}

.my-circle-badge {
  font-size: 0.75rem;
  background: #ff69b4;
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  margin-left: 8px;
  vertical-align: middle;
}

.circle-cover {
  width: 100px;
  height: 100px;
  border-radius: 8px;
  overflow: hidden;
  margin-right: 16px;
  flex-shrink: 0;
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

.create-circle-btn {
  padding: 10px 24px;
  background: #ff69b4;
  color: white;
  border: none;
  border-radius: 20px;
  font-size: 1rem;
  cursor: pointer;
  transition: background 0.2s;
}

.create-circle-btn:hover {
  background: #ff4da6;
}
</style>

