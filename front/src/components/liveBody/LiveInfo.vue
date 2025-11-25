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
  background-color: #1c1c1c;
  padding: 16px;
  width: 100%;
  color: #fff;
  border-radius: 16px;
  outline: none;
  border-color: #0f1016;
  box-sizing: border-box;
  line-height: 1.6;
  font-size: 14px;
  overflow: hidden;
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
  color: #ffffff;
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

.share-btn:hover {
    color: gray;

}


.online-count {
  margin: 10px 0;
  color: #cccccc;
  font-size: 14px;
}


.description {
  margin-top: 10px;
  padding: 10px;
  background: rgba(255,255,255,0.07);
  border-radius: 6px;
  color: #e4e4e4;
  font-size: 13px;
}
</style>
