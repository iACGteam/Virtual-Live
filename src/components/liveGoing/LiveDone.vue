<template>
  <div class="live-container">

    <!-- 中间：直播预览 -->
    <main class="center-preview">

      <!-- 直播信息 -->
      <div class="info">
        <div class="live-photo">
          封面占位
        </div>
        <div class="title" @click="openSettings">
          直播标题
          <el-icon class="settings" @click="openSettings">
            <Setting />
          </el-icon>
        </div>

        <div class="header-right">
          <button class="back-btn" @click="goBack">
            <span class="back-icon">←</span>
            <span>返回</span>
          </button>
        </div>
      </div>

      <div class="preview-box">
        <div class="obs">
          <el-form :model="form" label-width="auto" style="max-width: 600px" class="obs-form">
            <el-form-item label="服务" class="item">
              <el-input v-model="obs.service" />
            </el-form-item>
            <el-form-item label="服务器">
              <el-input v-model="obs.url" />
            </el-form-item>
            <el-form-item label="推流码">
              <el-input v-model="obs.code" />
            </el-form-item>
          </el-form>
        </div>
      </div>
    </main>

    <!-- 右侧：互动区 -->
    <aside class="right-panel">

      <!-- 高能用户 -->
      <div class="panel-section">
        <div class="panel-title">高能用户</div>

        <div class="high-energy-list">
          <div v-for="u in highEnergyUsers" :key="u.id" class="energy-user">
            <div class="avatar" :style="{ backgroundColor: u.color }"></div>
            <div class="uid">{{ u.id }}</div>
          </div>
        </div>
      </div>

      <!-- SC 弹幕 -->
      <div class="panel-section">
        <div class="panel-title">直播互动</div>

        <div class="chat-box">
          <div v-for="m in scMessages" :key="m.user" class="sc-msg"
            :style="{ borderColor: m.color, background: m.color + '22' }">
            <div class="sc-header">
              <span class="sc-user">{{ m.user }}</span>
              <span class="sc-money">¥{{ m.amount }}</span>
            </div>
            <div class="sc-text">{{ m.msg }}</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <DanmuArea></DanmuArea>
      </div>
    </aside>




    <!-- 设置弹窗 -->
    <el-dialog v-model="settingsVisible" title="开播信息" width="520px">
      <el-form :model="form" label-width="90px">

        <!-- 封面上传 -->
        <el-form-item label="封面">
          <el-upload class="cover-uploader" action="#" :show-file-list="false" :auto-upload="false"
            :on-change="handleCoverChange">
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon>
                <Plus />
              </el-icon>
              <p>点击上传封面</p>
            </div>
          </el-upload>
        </el-form-item>

        <!-- 分区选择 -->
        <el-form-item label="分区">
          <el-select v-model="form.category" placeholder="选择直播分区">
            <el-option label="娱乐" value="娱乐" />
            <el-option label="游戏" value="游戏" />
            <el-option label="学习" value="学习" />
            <el-option label="科技" value="科技" />
          </el-select>
        </el-form-item>

        <!-- 标题 -->
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="请输入直播标题" />
        </el-form-item>

        <!-- 公告 -->
        <el-form-item label="公告">
          <el-input type="textarea" rows="3" v-model="form.notice" placeholder="直播公告内容" />
        </el-form-item>

        <!-- 观看权限 -->
        <el-form-item label="权限">
          <el-select v-model="form.permission" placeholder="选择观看权限">
            <el-option label="公开" value="public" />
            <el-option label="仅粉丝" value="fans" />
            <el-option label="私密" value="fans" />
          </el-select>
        </el-form-item>

        <!-- 话题选择 -->
        <el-form-item label="话题">
          <el-select v-model="form.topics" multiple filterable collapse-tags placeholder="选择相关话题">
            <el-option label="AI" value="AI" />
            <el-option label="Vtuber" value="Vtuber" />
            <el-option label="二次元" value="二次元" />
            <el-option label="游戏" value="游戏" />
          </el-select>
        </el-form-item>

      </el-form>

      <template #footer>
        <el-button @click="settingsVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from "vue";
import { Plus, Camera, Monitor, ChromeFilled, MagicStick, Setting } from "@element-plus/icons-vue";
import DanmuArea from "@/components/liveGoing/DanmuArea.vue";

export default {
  components: {
    Plus,
    Camera,
    Monitor,
    ChromeFilled,
    Setting,
    MagicStick,
    DanmuArea,
  },
  data() {
    return {
      settingsVisible: false,
      form: {
        title: "直播标题",
        isPublic: true,
        allowDanmu: true,
      },
      obs: {
        service: "服务",
        url: "1.1.1.1:1111",
        code: "tuiliuma",
      },
      highEnergyUsers: [
        { id: "User_001", color: "#ff9eb5" },
        { id: "User_002", color: "#b19dff" }
      ],

      scMessages: [
        { user: "Alice", amount: 50, msg: "主播加油！好喜欢你！", color: "#ff8a80" },
        { user: "Bob", amount: 20, msg: "来支持一下！", color: "#ffb74d" },
        { user: "Chika", amount: 10, msg: "晚上好~", color: "#81d4fa" }
      ],

      volume: 50,
      msg: "",
    };
  }, methods: {
    openSettings() {
      this.settingsVisible = true;
    },
    saveSettings() {
      console.log("保存设置：", this.form);
      this.settingsVisible = false;
    },
    goBack() {
      this.$router.push("/")
    },
  },
};
</script>

<style scoped>

.header-right {
  position: absolute;
  right: 150px;

}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 10px;
  color: #2d2d2d;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
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
  height: calc(100vh - 30px);
  background: #fff4f7;
  /* 淡粉背景 */
  color: #444;
  padding-top: 30px;
}

/* 中间预览 */
.center-preview {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
}

.info {
  margin-left: 10rem;
  gap: 20px;
  width: 100%;
  display: flex;
  flex-direction: row;
  flex: 1;
  background: transparent;
  position: relative;
  /* 不要黑色 */
}


.live-photo {
  height: 90%;
  width: 100px;
  background: white;
  color: black;
}

.title {
  margin-top: 12px;
  color: #d6458d;
  /* 粉色标题，适合浅色主题 */
  font-size: 1.3rem;
  font-weight: bold;
}

.settings {
  color: black;
  padding-top: 1px;
  margin-left: 5px;
  transition: all 0.3s;

}

.settings :hover {
  transform: translateY(-2px);
}

/* 中间预览区域 */
.preview-box {
  width: 90%;
  height: 90%;
  background: white;
  border-radius: 12px;
  border: 2px solid #ffd6e8;
  box-shadow: 0 0 12px rgba(255, 150, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
}

.obs-form :deep(.el-form-item__label) {
  color: black
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

/* 右侧区域（白色 + 粉色边框） */
.right-panel {
  width: 300px;
  background-color: white;
  padding: 10px;
  border-left: 3px solid #ffbcd4;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 卡片 */
.panel-section {
  background: #fff;
  border: 1px solid #ffd6e8;
  box-shadow: 0 0 8px rgba(255, 130, 255, 0.12);
  padding: 12px;
  border-radius: 10px;
}

.panel-title {
  font-size: 15px;
  font-weight: bold;
  color: #d6458d;
  margin-bottom: 10px;
}


.high-energy-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.energy-user {
  display: flex;
  align-items: center;
  gap: 10px;
}


.avatar {
  width: 28px;
  height: 28px;
  border-radius: 50%;
}

.uid {
  font-size: 14px;
  color: #444;
}

/* SC 消息 */
.chat-box {
  background: #fff0f7;
  border-radius: 8px;
  padding: 10px;
  height: 160px;
  overflow-y: auto;
}

.sc-msg {
  border-left: 5px solid;
  padding: 8px;
  margin-bottom: 10px;
  border-radius: 6px;
}

.sc-header {
  display: flex;
  justify-content: space-between;
  font-weight: bold;
}

.sc-user {
  color: #d6458d;
}

.sc-money {
  color: #ff4081;
}

.sc-text {
  margin-top: 4px;
  color: #444;
}

/* 输入区保持白色 */
.chat-input {
  margin-top: auto;
  background-color: white;
}
</style>
