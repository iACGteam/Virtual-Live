<template>
  <div class="community-page">
      <div class="banner">
        <el-icon class="banner-icon">
          <ChatDotRound />
        </el-icon>
        <div class="banner-title">社区 Community</div>
      </div>

      <div class="main-container">
        <div class="content-area full-width">
          <CommunityBody></CommunityBody>
        </div>
      </div>
    </div>
</template>

<script setup>

import { ref } from 'vue'
import { ChatDotRound } from '@element-plus/icons-vue'

import CommunityBody from './CommunityBody.vue'

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
  justify-content: center;
  padding: 18px 36px;
  gap: 20px;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
}

/* 内容区域占满宽度，移除侧边栏后界面更简洁 */
.content-area {
  width: 100%;
  max-width: 1100px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.content-area.full-width {
  padding: 12px;
  background: white;
  border-radius: 10px;
  box-shadow: 0 6px 20px rgba(16, 24, 40, 0.06);
}
</style>
