<template>
  <div class="community-page">
    <div class="search-bar">
      <SearchBar></SearchBar>
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
      <div v-for="circle in circles" :key="circle.id" class="circle-card">
        <div class="circle-header">
          <img :src="circle.avatar" class="avatar">
          <div class="info">
            <p class="name">{{ circle.name }}</p>
            <p class="count">{{ circle.count }} 粉丝已加入</p>
          </div>

          <el-button class="join-btn" size="medium" @click="goDetail">加入</el-button>
          
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
      activeTab: '最新',
      navList: ['最新', '最热'],

      // 后端获取
      circles: [
        {
          id: 1,
          name: "官方的圈子",
          avatar: "@/assets/avatar.jpg",
          count: "7248",
          photos: [
            "@/assets/avatar.jpg",
            "@/assets/avatar.jpg",
            "@/assets/avatar.jpg",
            "@/assets/avatar.jpg"
          ],
          desc: "上方是图片预览,最好不要超过四张.这里是文字介绍"
        },
        {
          id: 2,
          name: "官方2号的圈子",
          avatar: "@/assets/avatar.jpg",
          count: "1.8万",
          photos: [
            "/mock/h1.jpg",
            "/mock/h2.jpg",
            "/mock/h3.jpg",
            "/mock/h4.jpg"
          ],
          desc: "上方是图片预览"
        }
      ]
    }
  },

  methods: {
    // tab 切换
    changeTab(tab) {
      this.activeTab = tab
    },

    // 点击跳转到圈子详情页
    goDetail() {
      this.$router.push(`/com-detail`)
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

.join-btn {
  margin-left: auto;
  border-radius: 15px;
  background: linear-gradient(135deg, #ff8de4 0%, #a972ff 100%);
  color: #EBEDF0;
  font-size: 0.8rem;
  border: none;
  transition: all 0.2s;
  box-shadow: 
    0 4px 10px rgba(255, 141, 228, 0.4),  /* 粉紫柔光 */
    0 2px 6px rgba(169, 114, 255, 0.3)
}

.join-btn:hover {
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
