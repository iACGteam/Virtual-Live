<template>
  <div class="community-page">
    <div class="search-bar">
      <SearchBar v-model="searchQuery" @search="handleSearch"></SearchBar>
    </div>

    <!-- Tabs -->
    <div class="nav-tabs">
      <div v-for="item in navList" :key="item" :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="changeTab(item)">
        {{ item }}
      </div>
    </div>

    <!-- Circle List -->
    <div class="circle-list" v-loading="loading">
      <div v-if="!loading && circles.length === 0" class="empty-circles">
        {{ emptyMessage }}
      </div>
      <div v-for="circle in circles" :key="circle.id" class="circle-card">
        <div class="circle-content-wrapper">
          <!-- Left: Cover Image -->
          <div class="circle-cover-wrapper">
            <img :src="circle.coverUrl || defaultAvatar" class="circle-cover-img" @error="handleImageError">
          </div>
          
          <!-- Right: Info & Actions -->
          <div class="circle-main-info">
            <div class="info-header">
              <div class="info-text">
                <p class="name">
                  {{ circle.name }}
                  <span v-if="circle.isMyCircle" class="my-circle-badge">我的圈子</span>
                </p>
                <p class="count">{{ circle.memberCount }} 粉丝已加入</p>
              </div>
              
              <div class="join-area">
                <div class="join-btn-wrap">
                  <!-- My Circles: Enter / Dissolve -->
                  <div class="join-actions" v-if="activeTab === '我的圈子'">
                    <el-button class="action-btn exit-btn" type="danger" size="medium" @click="handleDissolve(circle)">
                      解散
                    </el-button>
                    <el-button class="action-btn enter-btn" size="medium" @click="enterCircle(circle)">
                      进入
                    </el-button>
                  </div>
                  
                  <!-- Other Tabs: Enter (if joined) or Join -->
                  <div class="join-actions" v-else-if="circle.followed">
                    <el-button class="action-btn enter-btn" size="medium" @click="enterCircle(circle)">
                      进入
                    </el-button>
                  </div>
                  
                  <div v-else class="join-single">
                    <el-button class="action-btn join-btn" size="medium" @click="handleJoin(circle)">加入</el-button>
                  </div>
                </div>
              </div>
            </div>

            <div class="circle-desc" v-if="circle.description">
              {{ circle.description }}
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SearchBar from '../SearchBar.vue'
import { getCircles, getMyCreatedCircles, getUserJoinedCircles, joinCircle, leaveCircle, dissolveCircle, checkFollow, toggleFollow } from '@/utils/api'
import { getCurrentUserId } from '@/utils/auth'
import { ElMessage, ElMessageBox } from 'element-plus'

export default {
  components: { SearchBar },

  data() {
    return {
      searchQuery: '',
      activeTab: '关注', // Default tab
      navList: ['关注', '最新', '最热', '所有圈子'],
      circles: [],
      loading: false,
      joinedCircleIds: [], // To track joined status
      defaultAvatar: require('@/assets/logo.png'),
      page: 0,
      size: 20
    }
  },

  computed: {
    emptyMessage() {
      if (this.activeTab === '我的圈子') return '你还没有创建任何圈子'
      if (this.activeTab === '关注') return '你还没有加入任何圈子'
      return '暂无圈子数据'
    }
  },

  async mounted() {
    await this.fetchJoinedStatus()
    this.fetchData()
  },

  methods: {
    async fetchJoinedStatus() {
      const userId = getCurrentUserId()
      if (!userId) return
      try {
        const res = await getUserJoinedCircles(userId, 0, 100)
        if (res && res.content) {
          this.joinedCircleIds = res.content.map(c => c.id)
        }
      } catch (e) {
        console.error('Failed to fetch joined circles', e)
      }
    },

    async fetchData() {
      this.loading = true
      this.circles = []
      const userId = getCurrentUserId()
      
      try {
        let res
        if (this.activeTab === '关注') {
          if (!userId) {
             this.circles = []
             this.loading = false
             return
          }
          res = await getUserJoinedCircles(userId, this.page, this.size)
        } else if (this.activeTab === '我的圈子') {
          if (!userId) {
             this.circles = []
             this.loading = false
             return
          }
          res = await getMyCreatedCircles(userId, this.page, this.size)
        } else if (this.activeTab === '最新') {
          res = await getCircles(this.page, this.size, 'new')
        } else if (this.activeTab === '最热') {
          res = await getCircles(this.page, this.size, 'hot')
        } else if (this.activeTab === '所有圈子') {
          res = await getCircles(this.page, this.size, 'random')
        }

        if (res && res.content) {
          this.circles = res.content.map(c => ({
            ...c,
            avatar: c.avatarUrl,
            coverUrl: c.cover || c.coverUrl || c.coverImageUrl,
            followed: this.joinedCircleIds.includes(c.id) || (this.activeTab === '关注'),
            isMyCircle: c.creatorId === userId
          }))
        }
      } catch (e) {
        console.error('Fetch error', e)
      } finally {
        this.loading = false
      }
    },

    changeTab(tab) {
      this.activeTab = tab
      this.page = 0
      this.fetchData()
    },

    async handleJoin(circle) {
      const userId = getCurrentUserId()
      if (!userId) {
        this.$router.push('/login')
        return
      }
      
      try {
        // 1. Check if following the creator
        if (circle.creatorId && circle.creatorId !== userId) {
          const isFollowing = await checkFollow(userId, circle.creatorId)
          if (!isFollowing) {
            try {
              await ElMessageBox.confirm(
                '加入圈子需要先关注圈主，是否关注并加入？',
                '提示',
                {
                  confirmButtonText: '关注并加入',
                  cancelButtonText: '取消',
                  type: 'info'
                }
              )
              // User confirmed, follow the creator
              await toggleFollow(circle.creatorId, userId)
              ElMessage.success('已关注圈主')
            } catch (cancel) {
              // User cancelled
              return
            }
          }
        }

        // 2. Join the circle
        await joinCircle(circle.id, userId)
        ElMessage.success('加入成功')
        circle.followed = true
        this.joinedCircleIds.push(circle.id)
        circle.memberCount++
      } catch (e) {
        // If already joined (duplicate entry), treat as success
        if (e.message && e.message.includes('Duplicate entry')) {
             ElMessage.success('你已经是该圈子成员')
             circle.followed = true
             if (!this.joinedCircleIds.includes(circle.id)) {
                 this.joinedCircleIds.push(circle.id)
             }
             return
        }
        ElMessage.error('加入失败: ' + (e.message || '未知错误'))
      }
    },

    async handleExit(circle) {
      const userId = getCurrentUserId()
      if (!userId) return
      try {
        await leaveCircle(circle.id, userId)
        ElMessage.success('已退出圈子')
        circle.followed = false
        this.joinedCircleIds = this.joinedCircleIds.filter(id => id !== circle.id)
        circle.memberCount--
        if (this.activeTab === '关注') {
          this.circles = this.circles.filter(c => c.id !== circle.id)
        }
      } catch (e) {
        ElMessage.error('退出失败')
      }
    },

    async handleDissolve(circle) {
      const userId = getCurrentUserId()
      try {
        await ElMessageBox.confirm('确定要解散这个圈子吗？此操作不可恢复。', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await dissolveCircle(circle.id, userId)
        ElMessage.success('圈子已解散')
        this.circles = this.circles.filter(c => c.id !== circle.id)
      } catch (e) {
        if (e !== 'cancel') {
           ElMessage.error('解散失败')
        }
      }
    },

    enterCircle(circle) {
      this.goDetail(circle)
    },

    handleSearch() {
      // Implement search if needed
    },

    goDetail(circle) {
      this.$router.push({
        path: "/com-detail",
        query: {
          id: circle.id,
          name: circle.name,
          avatar: circle.avatar
        }
      });
    },
    
    handleImageError(e) {
      e.target.src = this.defaultAvatar
    }
  }
}
</script>


<style scoped>
.scrollbar-flex-content {
  display: flex;
  width: fit-content;
}

.community-page {
  padding: 20px;
  color: #2d2d2d;
  width: 80%;
}

.search-bar {
  width: 80%;
  margin: 0px 0px 20px 30px;
}

.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 18px;
  margin-bottom: 20px;
  cursor: pointer;
}

.tab-item {
  padding: 8px 16px;
  border-radius: 20px;
  transition: all 0.3s;
  color: #ff69b4;
  background: rgba(255, 105, 180, 0.1);
}

.tab-item.active {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  color: #fff;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.3);
}

.tab-item:hover {
  background: rgba(255, 105, 180, 0.2);
  transform: translateY(-2px);
}

.circle-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 90%;
}

.empty-circles {
  text-align: center;
  padding: 40px;
  color: #666;
  font-size: 1.1rem;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 16px;
  border: 1px dashed rgba(255, 105, 180, 0.3);
}

.circle-card {
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  padding: 20px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.circle-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(255, 105, 180, 0.15);
  border-color: rgba(255, 105, 180, 0.4);
}

.circle-content-wrapper {
  display: flex;
  gap: 20px;
}

.circle-cover-wrapper {
  width: 160px;
  height: 120px;
  flex-shrink: 0;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.circle-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.circle-card:hover .circle-cover-img {
  transform: scale(1.05);
}

.circle-main-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.name {
  font-size: 1.3rem;
  font-weight: bold;
  margin: 0 0 6px 0;
  color: #2d2d2d;
}

.count {
  font-size: 0.9rem;
  color: #666;
  margin: 0;
}

.join-area {
  display: flex;
  align-items: center;
}

.join-btn-wrap, .join-actions, .join-single {
  display: flex;
  align-items: center;
}

.action-btn {
  border-radius: 20px;
  padding: 8px 24px;
  font-weight: 600;
}

.enter-btn {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  border: none;
  color: #fff;
}

.exit-btn {
  background: transparent;
  border: 1px solid #ccc;
  color: #666;
  margin-right: 10px;
}

.exit-btn:hover {
  border-color: #999;
  color: #333;
  background: rgba(0, 0, 0, 0.05);
}

.join-btn {
  background: transparent;
  border: 1px solid #ff69b4;
  color: #ff69b4;
}

.join-btn:hover {
  background: rgba(255, 105, 180, 0.1);
}

.join-note {
  font-size: 0.8rem;
  color: rgba(255, 255, 255, 0.4);
  margin-left: 10px;
}

.circle-desc {
  font-size: 0.95rem;
  color: #555;
  line-height: 1.5;
  margin-top: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.my-circle-badge {
  display: inline-block;
  font-size: 0.75rem;
  color: #fff;
  background: linear-gradient(135deg, #ff69b4 0%, #ff1493 100%);
  padding: 2px 8px;
  border-radius: 10px;
  margin-left: 8px;
  vertical-align: middle;
  font-weight: normal;
  box-shadow: 0 2px 6px rgba(255, 105, 180, 0.3);
}
</style>
