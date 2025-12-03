<template>
  <div class="community-page">
      <div class="banner">
        <el-icon class="banner-icon">
          <ChatDotRound />
        </el-icon>
        <div class="banner-title">社区 Community</div>
      </div>

      <div class="main-container">
        <div class="content-area">
          <ComDetail></ComDetail>
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

import HotRank from './HotRank.vue'
import PopularTopics from './PopularTopics.vue'
import ComDetail from './ComDetailCopy.vue'

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
  border-radius: 12px;
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
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);

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
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}
</style>
