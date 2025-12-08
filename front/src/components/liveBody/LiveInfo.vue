<template>
  <div class="live-info">
    <div class="info-header">
      <h2 class="streamer-name">主播昵称</h2>
      <el-icon :size="20" class="share-btn"><Share /></el-icon>
    </div>

    <p class="online-count">在线人数：1234</p>

    <div class="description">
      欢迎来到直播间！这里是主播的直播介绍内容，可以放置直播说明、互动规则或粉丝群信息等。
    </div>
  </div>
</template>

<script>
export default {}
</script>

<style scoped>
.live-info {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 16px;
  width: 100%;
  color: #2d2d2d;
  border-radius: 16px;
  outline: none;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-sizing: border-box;
  line-height: 1.6;
  font-size: 14px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
}


.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}


.streamer-name {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #2d2d2d;
}


.share-btn {
  /* padding: 6px 12px;
  background: #1a73e8;
  border: none;
  color: white;
  font-size: 13px;
  border-radius: 6px;
  cursor: pointer;
  transition: 0.2s; */
  cursor: pointer;
  
}

.share-btn {
  color: rgba(45, 45, 45, 0.7);
  transition: color 0.2s ease;
}

.share-btn:hover {
    color: #ff69b4;
}


.online-count {
  margin: 10px 0;
  color: rgba(45, 45, 45, 0.7);
  font-size: 14px;
}


.description {
  margin-top: 10px;
  padding: 10px;
  background: rgba(255, 105, 180, 0.05);
  border-radius: 6px;
  color: rgba(45, 45, 45, 0.8);
  font-size: 13px;
  border: 1px solid rgba(255, 105, 180, 0.1);
}
</style>
