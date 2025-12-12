<template>
  <div class="community-page">
    <div class="search-bar">
      <SearchBar v-model="searchQuery" @search="handleSearch"></SearchBar>
    </div>


    <!-- 最新 / 最热 -->
    <div class="nav-tabs">
      <div v-for="item in navList" :key="item" :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="activeTab = item">
        {{ item }}
      </div>
    </div>

    <!-- 粉丝圈列表 -->
    <div class="circle-list">
      <div v-if="filteredCircles.length === 0" class="empty-circles">未加入任何圈子，快去探索吧～</div>
      <div v-for="circle in filteredCircles" :key="circle.id" class="circle-card">
        <div class="circle-header">
          <img :src="circle.avatar" class="avatar">
          <div class="info">
            <p class="name">{{ circle.name }}</p>
            <p class="count">{{ circle.count }} 粉丝已加入</p>
          </div>

          <div class="join-area">
            <div class="join-btn-wrap">
              <div class="join-actions" v-if="circle.followed">
                <el-button class="action-btn enter-btn" size="medium" @click="enterCircle(circle)">
                  进入
                </el-button>
                <el-button class="action-btn exit-btn" size="medium" @click="exitCircle(circle)">
                  退出
                </el-button>
              </div>
              <div v-else class="join-single">
                <el-button class="action-btn join-btn" size="medium" @click="followCircle(circle)">加入</el-button>
                <span class="join-note">未关注的圈子需要粉丝等级≥3 方可加入</span>
              </div>
            </div>
          </div>
          
        </div>

        <!-- 图片预览行 -->
        <div class="circle-photos">
          <el-scrollbar>
            <div class="scrollbar-flex-content">
              <img v-for="img in circle.photos" :key="img" :src="img" class="photo">
            </div>
          </el-scrollbar>
        </div>

        

        <div class="circle-desc" v-if="circle.desc">
          {{ circle.desc }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import SearchBar from '../SearchBar.vue'

export default {
  components: { SearchBar },

  data() {
    return {
      searchQuery: '',
      activeTab: '关注',
      navList: ['关注', '最新', '最热'],

      // 后端获取
      circles: [
        {
          id: 1,
          name: "阿萨Aza的圈子",
          avatar: require("@/assets/community/avatar1.jpg"),
          count: "7248",
          followed: true,
          photos: [
            require("@/assets/community/aza.jpg"),
            require("@/assets/community/aza.jpg"),
            require("@/assets/community/aza.jpg"),
            require("@/assets/community/aza.jpg"),
          ],
          desc: "VirtuaRealProject所属六期生虚拟主播“阿萨Aza是一个爱唱歌、爱打游戏的普普通通打工仔"
        },
        {
          id: 2,
          name: "KONG控的圈子",
          avatar: require("@/assets/community/avatar3.jpg"),
          count: "1.8万",
          followed: false,
          photos: [
            require("@/assets/community/k.jpg"),
            require("@/assets/community/k.jpg"),
            require("@/assets/community/k.jpg"),
            require("@/assets/community/k.jpg"),
          ],
          desc: "这里是对控的碎碎念备忘录"
        }
      ]
    }
  },

  methods: {
    // tab 切换
    changeTab(tab) {
      this.activeTab = tab
    },

    followCircle(circle) {
      circle.followed = true
    },

    exitCircle(circle) {
      circle.followed = false
    },

    enterCircle(circle) {
      if (!circle.followed) {
        alert('请先关注该圈子');
        return;
      }
      this.goDetail(circle)
    },

    handleSearch() {
      // 触发搜索，实际过滤由 computed 完成
    },

    // 点击跳转到圈子详情页
    goDetail(circle) {
      this.$router.push({
        path: "/com-detail",
        query: {
          id: circle.id,
          name: circle.name,
          avatar: circle.avatar
        }
      });
    }
  }

,  computed: {
    filteredCircles() {
      const keyword = this.searchQuery.trim().toLowerCase();
      let list = this.circles;

      if (keyword) {
        list = list.filter(c => (c.name || '').toLowerCase().includes(keyword));
      }

      if (this.activeTab === '关注') {
        list = list.filter(c => c.followed);
      }

      if (this.activeTab === '最热') {
        // 简单按粉丝数排序，注意字符串转数字
        list = [...list].sort((a, b) => parseInt((b.count + '').replace(/\D/g, '') || 0) - parseInt((a.count + '').replace(/\D/g, '') || 0));
      }

      // '最新' 暂按原顺序
      return list;
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
  color: #fff;
  width: 80%;
}

.search-bar {
  width: 80%;
  margin:0px 0px 20px 30px;
}

.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 18px;
  margin-bottom: 20px;
}

.tab-item {
  cursor: pointer;
  color: rgba(45, 45, 45, 0.8);
  transition: all 0.2s;
}

.tab-item.active {
  color: black;
  font-weight: bold;
  border-bottom: 2px solid #8b5cf6;
}

.tab-item:hover {
  transform: translateY(-2px);
}

.circle-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 90%;
}

.empty-circles {
  padding: 32px;
  text-align: center;
  color: #999;
  font-size: 16px;
  background: #fff;
  border: 1px dashed #ddd;
  border-radius: 8px;
}

.circle-card {
  background: #fefbff;
  border-radius: 16px;
  border: 2px solid rgba(255, 105, 180, 0.2);
  padding: 16px;
  
}

.circle-header {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.avatar {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  margin-right: 12px;
}

.info .name {
  font-size: 18px;
  font-weight: 600;
  
}

.info .name {
  font-size: 18px;
  font-weight: 600;

  background: linear-gradient(90deg, #ff87e0, #a67bff, #7aa8ff);
  -webkit-background-clip: text;
  color: transparent;

  /* text-shadow:
    0 0 6px rgba(255, 135, 224, 0.6),
    0 0 12px rgba(166, 123, 255, 0.5),
    0 0 18px rgba(122, 168, 255, 0.4); */
}

.info .count {
  color: #ccc;
  font-size: 14px;
  color: rgba(45, 45, 45, 0.7);
}

.action-btn {
  border-radius: 15px;
  color: #fff;
  font-size: 0.8rem;
  border: none;
  transition: all 0.2s;
  box-shadow:
    0 4px 10px rgba(255, 141, 228, 0.4),
    0 2px 6px rgba(169, 114, 255, 0.3);
}

.enter-btn {
  background: linear-gradient(135deg, #7aa8ff 0%, #409eff 100%);
}

.exit-btn {
  background: linear-gradient(135deg, #ff9bb1 0%, #ff6b81 100%);
}

.join-btn {
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
}

.join-area {
  margin-left: auto;
  display: flex;
  align-items: flex-start;
}

.join-btn-wrap {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.join-actions {
  display: flex;
  gap: 8px;
}

.join-single {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
}

.join-note {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.7);
  white-space: nowrap;
  text-align: right;
}

.action-btn:hover {
  color: white;
  transform: translateY(-3px);
  box-shadow:
    0 6px 14px rgba(255, 141, 228, 0.5),
    0 4px 10px rgba(169, 114, 255, 0.4);
}

.circle-photos {
  display: flex;
  gap: 30px;
  margin-bottom: 10px;
}

.photo {
  width: 160px;
  height: 240px;
  border-radius: 12px;
  object-fit: cover;
  gap: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 10px;
  text-align: center;
  border-radius: 24px;
  overflow: hidden;
}

.circle-desc {
  color: #ccc;
  font-size: 14px;
  padding-left: 4px;
  color: rgba(45, 45, 45, 0.7);
}
</style>
