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
      <!-- <div class="avatar-wrapper">
        <AvatarSwitcher />
      </div> -->
    </div>

  </div>
</template>

<script>
import { useRouter } from 'vue-router'
import VideoPlayer from './VideoPlayer.vue'
import DanmuArea from './DanmuArea.vue'
import GiftArea from './GiftArea.vue'
import LiveInfo from './LiveInfo.vue'
// import AvatarSwitcher from './AvatarSwitcher.vue'

export default {
  components: {
    VideoPlayer,
    DanmuArea,
    GiftArea,
    LiveInfo,
    // AvatarSwitcher
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
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  position: relative;
}

.body-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: 
    radial-gradient(circle at 20% 30%, rgba(255, 105, 180, 0.08) 0%, transparent 50%),
    radial-gradient(circle at 80% 70%, rgba(147, 112, 219, 0.08) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}


.back-btn {
  position: fixed;
  top: 20px;
  left: 20px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  color: #2d2d2d;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 999;
  border: 1px solid rgba(255, 105, 180, 0.3);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.15);
}

.back-btn:hover {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.3);
  transform: translateY(-2px);
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
  background: rgba(0, 0, 0, 0.3);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 20px rgba(255, 105, 180, 0.15);
}

.info-wrapper {
  flex: 1; /* 20% */
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 105, 180, 0.2);
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
  flex: 1;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.gift-wrapper {
  /* flex: 3; */
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(147, 112, 219, 0.2);
}

/* .avatar-wrapper {
  flex: 3;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid rgba(72, 209, 204, 0.2);
} */
</style>
