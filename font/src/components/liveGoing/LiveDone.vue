<template>
  <div class="live-container">
    <!-- 左侧：场景、素材 -->
    <aside class="left-panel">
        
        <div class="header-right">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
          <span>返回</span>
        </button>
      </div>


      <div class="panel-section">
        <div class="panel-title">场景</div>
        <el-menu default-active="1-1" class="menu">
          <el-menu-item index="1-1" class="menu-item">场景1</el-menu-item>
          <el-menu-item index="1-2" class="menu-item">场景2</el-menu-item>
          <el-menu-item index="1-3" class="menu-item">场景3</el-menu-item>
        </el-menu>
      </div>

      <div class="panel-section">
        <div class="panel-title">素材</div>
        <div class="material-box">
          点击这里添加素材...
        </div>
      </div>

      <div class="panel-section">
        <div class="panel-title">常用工具</div>
        <div class="tool-box">
          点击“开始直播”后可使用
        </div>
      </div>
    </aside>

    <!-- 中间：直播预览 -->
    <main class="center-preview">
      <div class="preview-box">
        <div class="add-material">
          <el-icon><Plus /></el-icon>
          <span>添加素材</span>
        </div>

        <div class="preview-actions">
          <div class="icon-box"><el-icon><Camera /></el-icon> 摄像头</div>
          <div class="icon-box"><el-icon><Monitor /></el-icon> 窗口</div>
          <div class="icon-box"><el-icon><Monitor /></el-icon> 显示器</div>
          <div class="icon-box"><el-icon><ChromeFilled /></el-icon> 浏览器</div>
          <div class="icon-box"><el-icon><MagicStick /></el-icon> 虚拟形象</div>
          

        </div>
      </div>
    </main>

    <!-- 右侧：互动区 -->
    <aside class="right-panel">
      <div class="panel-section">
        <div class="panel-title">高能用户</div>
        <div class="high-energy">暂无高能用户</div>
      </div>

      <div class="panel-section">
        <div class="panel-title">直播互动</div>
        <div class="chat-box">
          此处显示SC弹幕、礼物、互动消息
        </div>
      </div>

      <div class="chat-input">
        <!-- <el-input v-model="msg" placeholder="输入文字" maxlength="40">
          <template #append>
            <el-button type="primary">发送</el-button>
          </template>
        </el-input> -->
        <DanmuArea></DanmuArea>
      </div>
    </aside>
  </div>

  <!-- <footer class="bottom-bar">
    <div class="bar-left">
      音量：
      <el-slider v-model="volume" style="width: 150px" />
      清晰度：100%
    </div>

    <div class="bar-right">
      CPU: 3.1% | 内存: 1.7%
      <el-button type="danger" size="large">开始直播</el-button>
    </div>
  </footer> -->
</template>

<script setup>
import { ref } from "vue";
import { Plus, Camera, Monitor, ChromeFilled, MagicStick } from "@element-plus/icons-vue";
import DanmuArea from "@/components/liveBody/DanmuArea.vue";



const volume = ref(50);
const msg = ref("");
</script>

<style scoped>

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}


.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateX(-2px);
}

.back-btn:active {
  transform: translateX(0);
}

.back-icon {
  font-size: 1.2rem;
  font-weight: 600;
}

.live-container {
  display: flex;
  height: calc(100vh - 70px); /* header 高度你可以调整 */
  background-color: #181818;
  color: #ddd;
  padding-top: 30px;
}

/* 左侧面板 */
.left-panel {
  width: 240px;
  background-color: #1f1f1f;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.menu {
    border-radius: 12px;
    overflow: hidden;
    background-color: #1f1f1f;
    color: white;
    box-shadow: none;
    border: none;
}

.menu :hover {
    background-color: gray;
}

.menu-item {
    color: white;
    box-shadow: none;
    
}

.panel-section {
  background: #262626;
  padding: 10px;
  border-radius: 6px;
}

.panel-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 8px;
}

.material-box,
.tool-box,
.high-energy,
.chat-box {
  background: #303030;
  border-radius: 6px;
  padding: 12px;
  height: 120px;
  color: #aaa;

}

/* 中间预览 */
.center-preview {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.preview-box {
  width: 90%;
  height: 90%;
  background: black;
  border-radius: 8px;
  position: relative;
}

.add-material {
  position: absolute;
  top: 40%;
  left: 45%;
  text-align: center;
  color: white;
}

.preview-actions {
  position: absolute;
  bottom: 80px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 20px;
}

.icon-box {
  padding: 6px 12px;
  background: #444;
  border-radius: 8px;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 右侧面板 */
.right-panel {
  width: 280px;
  background-color: #1f1f1f;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.chat-input {
  margin-top: auto;
  background-color: #2a2a2a;
}

</style>
