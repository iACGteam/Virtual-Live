<template>
  <div class="community-page">

    <div class="banner">
      <el-icon class="banner-icon">
        <ChatDotRound />
      </el-icon>
      <div class="banner-title">社区 Community</div>
    </div>


    <!-- <div class="category-bar">
      <button v-for="tab in tabs" :key="tab.key" :class="['category-btn', { active: activeCategory === tab.key }]"
        @click="activeCategory = tab.key; switchCategory(tab)">
        {{ tab.label }}
      </button>
    </div> -->

    <div class="main-container">
      <div class="content-area">
        <!-- <div class="input-container">
          <el-input v-model="input2" class="responsive-input" placeholder="Type something"
            :prefix-icon="Search"><template #prefix>
              <el-icon class="el-input__icon">
                <search />
              </el-icon>
            </template></el-input>
        </div>
        <PostCard v-for="(item, index) in posts" :key="index" :post="item" /> -->
        <New></New>
      </div>

      <div class="sidebar">
        <HotRank />
        <PopularTopics />
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'

import PostCard from './PostCard.vue'
import HotRank from './HotRank.vue'
import PopularTopics from './PopularTopics.vue'
import New from './New.vue'

const activeCategory = ref('hot')

const tabs = [
  { key: 'hot', label: '热门' },
  { key: 'new', label: '最新' },
  { key: 'follow', label: '关注' }
]

const posts = ref([
  { title: '标题', author: '作者1', likes: 120 },
  { title: '虚拟形象？', author: '用户B', likes: 98 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 },
  { title: '如何发送弹幕', author: '用户C', likes: 67 }
])

const switchCategory = (tab) => {
  console.log('切换分类:', tab.paneName)
  // 根据分类刷新数据
}
</script>

<style scoped>
.category-bar {
  display: flex;
  gap: 10px;
  padding: 10px 0;
  margin-left: 45px;
}

.category-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 6px;
  background-color: #2c2c2c;
  color: #aaa;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s;
}

.category-btn:hover {
  background-color: #3a3a3a;
  color: #fff;
}

.category-btn.active {
  background: linear-gradient(90deg, rgb(253, 185, 197) , rgb(251, 197, 206));
  color: #fff;
}


.community-page {
  width: 100%;
  padding-top: 10px;
}

.banner {
  width: 90%;
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 30px 40px;
  background: linear-gradient(90deg, #3a1c71 0%, #d76d77 100%);
  color: white;
}

.banner-icon {
  font-size: 48px;
}

.banner-title {
  font-size: 32px;
  font-weight: bold;
}

.tab {
  color: white;
}

.tab :hover {
  color: gray;
}

.main-container {
  display: flex;
  padding: 20px 40px;
  gap: 20px;
}

/* 左 80% */
.content-area {
  flex: 4;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 右 20% */
.sidebar {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.sidebar>* {
  background: #1c1c1c;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
</style>
