<template>
  <div class="body-container">

     <div class="back-btn" @click="goBack">
      ← 返回
    </div>

    <!-- 左侧区域（75%） -->
    <div class="left-area">
      <div class="video-wrapper">
        <VideoPlayer />
      </div>

      <div class="info-wrapper">
        <LiveInfo />
      </div>
    </div>

    <!-- 右侧区域（25%） -->
    <div class="right-area">
      <div class="danmu-wrapper">
        <DanmuArea />
      </div>
      <div class="gift-wrapper">
        <GiftArea />
      </div>
      <div class="avatar-wrapper">
        <AvatarSwitcher />
      </div>
    </div>

  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import VideoPlayer from './VideoPlayer.vue'
import DanmuArea from './DanmuArea.vue'
import GiftArea from './GiftArea.vue'
import LiveInfo from './LiveInfo.vue'
import AvatarSwitcher from './AvatarSwitcher.vue'

export default {
  components: {
    VideoPlayer,
    DanmuArea,
    GiftArea,
    LiveInfo,
    AvatarSwitcher
  },
  methods: {
    goBack() {
      this.$router.back()
    }
  }
}
</script>




<style>

.body-container {
  height: 100%;
  display: flex;
  width: 100%;
  /* height: calc(100vh - 80px); */
  height: 100vh;
  background-color: black;
}


.back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  padding: 6px 12px;
  background: rgba(0,0,0,0.6);
  color: #fff;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s;
  z-index: 999;
  
}

.back-btn:hover {
  background: rgba(255,255,255,0.2);
}


/* 左侧 = 75% */
.left-area {
  flex: 3;
  display: flex;
  flex-direction: column;
  padding: 10px;
  gap: 10px;
}

.video-wrapper {
  flex: 4; /* 80% */
  background: #000;
  border-radius: 6px;
  overflow: hidden;
}

.info-wrapper {
  flex: 1; /* 20% */
  background: black;
  border-radius: 6px;
  overflow: hidden;
}

/* 右侧 = 25% */
.right-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 10px;
  gap: 10px;
}

/* 右侧各区块比例：40% / 30% / 30% */
.danmu-wrapper {
  flex: 4;
  background: black;
  border-radius: 6px;
  overflow: hidden;
}

.gift-wrapper {
  flex: 3;
  background: black;
  border-radius: 6px;
  overflow: hidden;
}

.avatar-wrapper {
  flex: 3;
  background: black;
  border-radius: 6px;
  overflow: hidden;
}
</style>
