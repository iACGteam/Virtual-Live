<template>
  <div class="live-player">
    <!-- 占位 div，实际项目可替换为视频播放器/流播放器 -->
    <div class="player-placeholder">
      <div class="studio">LIVE</div>
      <div class="placeholder-text">直播播放器占位（这里先用 div 替代）</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "LivePlayer",
  mounted() {
    // 这里可以初始化真实播放器（例如 hls.js / flv.js / video.js）等
  },
};
</script>

<style scoped>
.live-player {
  height: 100%;
  width: 100%;
  display: flex;
  align-items: stretch;
  justify-content: stretch;
}

/* 大播放器占位样式，仿直播画面 */
.player-placeholder {
  flex: 1;
  border-radius: 12px;
  background-image: linear-gradient(120deg, rgba(255,255,255,0.02), rgba(255,255,255,0.01));
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #e6eef8;
  padding: 20px;
  box-sizing: border-box;
  border: 1px solid rgba(255,255,255,0.04);
  position: relative;
}

/* 画面角标 */
.studio {
  position: absolute;
  left: 18px;
  top: 18px;
  background: rgba(255,0,0,0.9);
  color: #fff;
  font-weight: 800;
  padding: 6px 10px;
  border-radius: 999px;
  font-size: 12px;
  letter-spacing: 1px;
}

.placeholder-text {
  font-size: 18px;
  color: rgba(255,255,255,0.8);
}
</style>
