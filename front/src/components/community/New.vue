<template>
  <div class="community-page">
    <!-- 搜索栏 -->
    <el-input
      v-model="search"
      placeholder="搜索你感兴趣的主播或圈子"
      class="search-bar"
      prefix-icon="Search"
      clearable
    />

    <!-- 最新 / 最热 -->
    <div class="nav-tabs">
      <div 
        v-for="item in navList"
        :key="item"
        :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="activeTab = item"
      >
        {{ item }}
      </div>
    </div>

    <!-- 粉丝圈列表 -->
    <div class="circle-list">
      <div 
        v-for="circle in circles"
        :key="circle.id"
        class="circle-card"
      >
        <div class="circle-header">
          <img :src="circle.avatar" class="avatar">
          <div class="info">
            <p class="name">{{ circle.name }}</p>
            <p class="count">{{ circle.count }} 粉丝已加入</p>
          </div>

          <el-button class="join-btn" size="small">加入</el-button>
        </div>

        <!-- 图片预览行 -->
        <div class="circle-photos">
          <img 
            v-for="img in circle.photos"
            :key="img"
            :src="img"
            class="photo"
          >
        </div>

        <div class="circle-desc" v-if="circle.desc">
          {{ circle.desc }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const search = ref('')
const activeTab = ref('最新')
const navList = ['最新','最热']

// 模拟数据（你后面可以改为 axios 获取）
const circles = ref([
  {
    id: 1,
    name: "薛之谦的圈子",
    avatar: "/mock/xzq.jpg",
    count: "7248",
    photos: [
      "/mock/s1.jpg",
      "/mock/s2.jpg",
      "/mock/s3.jpg"
    ],
    desc: "抖音也有超话了哈哈哈哈"
  },
  {
    id: 2,
    name: "黄子弘凡的圈子",
    avatar: "/mock/hz1.jpg",
    count: "1.8万",
    photos: [
      "/mock/h1.jpg",
      "/mock/h2.jpg",
      "/mock/h3.jpg"
    ],
    desc: "各位蟹黄堡，大家好"
  }
])
</script>

<style scoped>
.community-page {
  padding: 20px;
  color: #2d2d2d;
  width: 80%;
}

.search-bar {
  margin-bottom: 20px;
  width: 90%;
}

.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 18px;
  margin-bottom: 20px;
}

.tab-item {
  cursor: pointer;
  color: rgba(45, 45, 45, 0.6);
  transition: color 0.2s ease;
}

.tab-item:hover {
  color: #ff69b4;
}

.tab-item.active {
  color: #ff69b4;
  font-weight: bold;
  border-bottom: 2px solid #ff69b4;
}

.circle-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 90%;
}

.circle-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 16px;
  padding: 16px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
  transition: all 0.3s ease;
}

.circle-card:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 105, 180, 0.4);
  box-shadow: 0 8px 25px rgba(255, 105, 180, 0.25);
  transform: translateY(-2px);
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
  color: #2d2d2d;
}

.info .count {
  color: rgba(45, 45, 45, 0.6);
  font-size: 14px;
}

.join-btn {
  margin-left: auto;
  border-radius: 15px;
}

.circle-photos {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.photo {
  width: 33%;
  border-radius: 12px;
  object-fit: cover;
  height: 120px;
}

.circle-desc {
  color: rgba(45, 45, 45, 0.7);
  font-size: 14px;
  padding-left: 4px;
}
</style>
