<template>
  <div class="live-container">
    <!-- 主内容区 -->
    <div class="main-content">
      <!-- 左侧区域：顶部栏 + 推流设置 -->
      <div class="left-wrapper">
        <!-- 顶部栏（仅作用于左侧区域） -->
        <div class="top-bar">
          <div class="top-left">
            <div class="top-cover" @click="openSettings">
              <img
                v-if="form.coverUrl"
                :src="form.coverUrl"
                alt="封面"
                class="top-cover-img"
              />
              <div v-else class="top-cover-placeholder">
                <el-icon><PictureFilled /></el-icon>
              </div>
            </div>
            <div class="top-info">
              <div class="title-row" @click="openSettings">
                <span class="live-title">
                  {{ form.title || '游戏时间' }}
                </span>
                <el-icon class="edit-icon"><Edit /></el-icon>
              </div>
              <div class="meta-row">
                <span class="live-category">{{ form.category || '精灵宝可梦' }}</span>
                <span class="dot-sep">·</span>
                <el-icon class="lock-icon"><Lock /></el-icon>
                <span class="live-permission">{{ form.permission === 'public' ? '公开直播' : '私密直播' }}</span>
              </div>
            </div>
          </div>
          <!-- 顶部栏右侧：退出按钮 -->
          <div class="top-right">
            <button class="exit-btn" @click="handleExit">退出</button>
          </div>
        </div>

        <!-- 左侧面板：推流设置 + 右侧教程 -->
        <aside class="left-panel">
          <div class="streaming-setup">
            <!-- 左侧推流配置 -->
            <div class="setup-content">
              <h3 class="setup-title">推流码推流</h3>
              <p class="setup-hint">按右侧步骤打开 OBS 设置，将下方内容粘贴至 OBS 中</p>
              
              <div class="form-item">
                <label>服务</label>
                <el-input v-model="obs.service" readonly />
              </div>

              <div class="form-item">
                <label>服务器</label>
                <div class="input-with-copy">
                  <el-input v-model="obs.url" readonly />
                  <el-button class="copy-btn" @click="copyToClipboard(obs.url)">复制</el-button>
                </div>
              </div>

              <div class="protocol-select">
                <el-radio v-model="obs.protocol" label="rtmp">rtmp</el-radio>
              </div>
              
              <div class="form-item">
                <label>推流码</label>
                <div class="input-with-copy">
                  <el-input v-model="obs.code" readonly />
                  <el-button class="copy-btn" @click="copyToClipboard(obs.code)">复制</el-button>
                </div>
              </div>
            </div>

            <!-- 右侧 OBS 教程 -->
            <div class="obs-tutorial">
              <h3 class="tutorial-title">OBS 设置</h3>
              <ol class="tutorial-steps">
                <li>
                  <span class="step-index">1、</span>
                  打开 OBS 设置 → 直播，将左侧服务器与推流码粘贴到对应位置
                </li>
                <li>
                  <span class="step-index">2、</span>
                  完成设置后，点击 <span class="highlight-text">开始直播</span> 按钮
                </li>
              </ol>

              <div class="tutorial-card">
                <img :src="obsImages.step1" alt="OBS 设置步骤一" class="tutorial-img" />
              </div>

              <div class="tutorial-card">
                <img :src="obsImages.step2" alt="OBS 设置步骤二" class="tutorial-img" />
              </div>
            </div>
          </div>
        
        <!-- 底部控制栏 -->
        <div class="bottom-controls">
          <!-- 输出（音量）控制 -->
          <div class="audio-item">
            <el-icon class="audio-icon"><VideoPlay /></el-icon>
            <el-icon class="caret-icon"><ArrowUp /></el-icon>
            <el-slider v-model="audioOutput" :max="100" :show-tooltip="false" class="audio-slider" />
            <span class="audio-value">{{ audioOutput }}%</span>
          </div>
          
          <!-- 输入（麦克风）控制 -->
          <div class="audio-item">
            <el-icon class="audio-icon"><Microphone /></el-icon>
            <el-icon class="caret-icon"><ArrowUp /></el-icon>
            <el-slider v-model="audioInput" :max="100" :show-tooltip="false" class="audio-slider" />
            <span class="audio-value">{{ audioInput }}%</span>
          </div>
          
          <!-- 设置按钮 -->
          <div class="action-buttons">
            <el-button class="action-btn" @click="openSettings">
              <el-icon><Setting /></el-icon>
            </el-button>
          </div>
        </div>
        </aside>

      </div>

      <!-- 右侧面板：观众和互动（不包含顶部栏） -->
      <aside class="right-panel">
        <!-- 房间观众 -->
        <div class="panel-section">
          <h3 class="section-title">房间观众</h3>
          <div class="audience-tabs">
            <div
              v-for="tab in audienceTabs"
              :key="tab.key"
              :class="['tab-item', { active: activeAudienceTab === tab.key }]"
              @click="activeAudienceTab = tab.key"
            >
              {{ tab.label }}
            </div>
          </div>
          <p class="section-desc">展示本场直播的在线观众和不同时间段的榜单</p>
        </div>
        
        <!-- 直播互动 -->
        <div class="panel-section interaction-section">
          <h3 class="section-title">直播互动</h3>
          <p class="section-desc">展示本场直播收到的打赏记录包括礼物、大航海、醒目留言</p>
          <div class="gift-records">
            <div v-for="gift in giftRecords" :key="gift.id" class="gift-item">
              <span class="gift-user">{{ gift.user }}</span>
              <span class="gift-name">{{ gift.name }}</span>
              <span class="gift-amount">¥{{ gift.amount }}</span>
            </div>
          </div>
          <p class="section-desc">展示本场直播的弹幕互动消息</p>
          <div class="danmu-messages">
            <div v-for="msg in danmuMessages" :key="msg.id" class="danmu-msg">
              <span class="msg-user">{{ msg.user }}:</span>
              <span class="msg-content">{{ msg.content }}</span>
            </div>
          </div>
          <div class="chat-input-box">
            <el-input
              v-model="chatInput"
              placeholder="请输入文字"
              maxlength="20"
              show-word-limit
            />
            <el-button class="send-btn" @click="sendChat">发送</el-button>
          </div>
        </div>
      </aside>
    </div>

    <!-- 开播信息弹窗 -->
    <el-dialog
      v-model="settingsVisible"
      title="开播信息"
      width="520px"
      :close-on-click-modal="false"
      :close-on-press-escape="true"
      top="5vh"
    >
      <div class="dialog-content">
        <p class="dialog-hint">用心填写以下信息有助于获得更多忠实观众哦~</p>
        
        <div class="form-group">
          <label class="required">封面</label>
          <el-upload
            class="cover-uploader"
            action="#"
            :show-file-list="false"
            :auto-upload="false"
            :on-change="handleCoverChange"
          >
            <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-preview" />
            <div v-else class="cover-placeholder">
              <el-icon><Plus /></el-icon>
              <p>点击上传封面</p>
            </div>
          </el-upload>
        </div>
        
        <div class="form-group">
          <label class="required">标题</label>
          <div class="input-with-edit">
            <el-input v-model="form.title" placeholder="游戏时间" />
            <el-icon class="edit-icon-small"><Edit /></el-icon>
          </div>
        </div>
        
        <div class="form-group">
          <label class="required">分区</label>
          <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
            <el-option label="精灵宝可梦" value="精灵宝可梦" />
            <el-option label="游戏" value="游戏" />
            <el-option label="娱乐" value="娱乐" />
            <el-option label="学习" value="学习" />
            <el-option label="科技" value="科技" />
          </el-select>
        </div>
        
        <div class="form-group">
          <label>直播标签</label>
          <div class="tags-section">
            <el-tag
              v-for="tag in form.tags"
              :key="tag"
              closable
              @close="removeTag(tag)"
              class="live-tag"
            >
              {{ tag }}
            </el-tag>
            <el-button class="add-tag-btn" @click="showAddTag = true">+ 新增标签</el-button>
          </div>
          <p class="tag-hint">添加标签,可带来更多观众</p>
        </div>
        
        <div class="form-group">
          <label>显示位置</label>
          <el-switch v-model="form.showLocation" active-text="开" inactive-text="关" />
        </div>
        
        <div class="form-group">
          <label>公告①</label>
          <el-input
            type="textarea"
            v-model="form.announcement"
            :rows="2"
            placeholder="请输入公告内容"
            maxlength="60"
            show-word-limit
          />
        </div>
        
        <div class="form-group">
          <label>观看权限</label>
          <el-select v-model="form.permission" placeholder="请选择" style="width: 100%">
            <el-option label="公开直播" value="public" />
            <el-option label="仅粉丝" value="fans" />
            <el-option label="私密" value="private" />
          </el-select>
        </div>
        
        <div class="form-group">
          <label>话题</label>
          <el-select v-model="form.topic" placeholder="请选择" style="width: 100%">
            <el-option label="游戏" value="游戏" />
            <el-option label="娱乐" value="娱乐" />
            <el-option label="学习" value="学习" />
          </el-select>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="closeSettings">取消</el-button>
        <el-button @click="saveSettings">保存</el-button>
        <el-button type="primary" :disabled="isWaiting" @click="startLive">
          {{ isWaiting ? '等待中...' : '开始直播' }}
        </el-button>
      </template>
    </el-dialog>

    <!-- 添加标签对话框 -->
    <el-dialog v-model="showAddTag" title="新增标签" width="400px">
      <el-input v-model="newTag" placeholder="请输入标签名称" />
      <template #footer>
        <el-button @click="showAddTag = false">取消</el-button>
        <el-button type="primary" @click="addTag">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref } from "vue";
import {
  Plus,
  Edit,
  Lock,
  ArrowDown,
  ArrowUp,
  Microphone,
  VideoPlay,
  Monitor,
  VideoCamera,
  Setting,
  PictureFilled,
} from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import obsStep1 from "@/assets/1.png";
import obsStep2 from "@/assets/2.png";

export default {
  components: {
    Plus,
    Edit,
    Lock,
    ArrowDown,
    ArrowUp,
    Microphone,
    VideoPlay,
    Monitor,
    VideoCamera,
    Setting,
    PictureFilled,
  },
  data() {
    return {
      settingsVisible: false, // 默认不显示开播信息弹窗
      showAddTag: false,
      newTag: "",
      activeAudienceTab: "online",
      isWaiting: false,
      audioInput: 100,
      audioOutput: 100,
      chatInput: "",
      form: {
        title: "游戏时间",
        category: "精灵宝可梦",
        coverUrl: "",
        tags: [],
        showLocation: false,
        announcement: "",
        permission: "public",
        topic: "",
      },
      obs: {
        service: "自定义",
        url: "rtmp://localhost:1935/live",
        protocol: "rtmp",
        code: "my_secret key 2025",
      },
      obsImages: {
        step1: obsStep1,
        step2: obsStep2,
      },
      audienceTabs: [
        { key: "online", label: "在线榜" },
        { key: "daily", label: "日榜" },
        { key: "weekly", label: "周榜" },
        { key: "monthly", label: "月榜" },
      ],
      giftRecords: [
        { id: 1, user: "用户A", name: "礼物1", amount: 50 },
        { id: 2, user: "用户B", name: "礼物2", amount: 30 },
      ],
      danmuMessages: [
        { id: 1, user: "用户1", content: "主播加油！" },
        { id: 2, user: "用户2", content: "好喜欢这个直播！" },
      ],
    };
  },
  computed: {
  },
  methods: {
    openSettings() {
      this.settingsVisible = true;
    },
    handleExit() {
      // 顶部栏右侧浅色“退出”按钮：返回个人主页
      this.$router.push({ path: '/profile' }).catch(() => {});
    },
    handleCoverChange(file) {
      const reader = new FileReader();
      reader.onload = (e) => {
        this.form.coverUrl = e.target.result;
      };
      reader.readAsDataURL(file.raw);
    },
    copyToClipboard(text) {
      navigator.clipboard.writeText(text).then(() => {
        ElMessage.success("已复制到剪贴板");
      });
    },
    removeTag(tag) {
      this.form.tags = this.form.tags.filter((t) => t !== tag);
    },
    addTag() {
      if (this.newTag.trim()) {
        this.form.tags.push(this.newTag.trim());
        this.newTag = "";
        this.showAddTag = false;
      }
    },
    sendChat() {
      if (this.chatInput.trim()) {
        this.danmuMessages.push({
          id: Date.now(),
          user: "我",
          content: this.chatInput,
        });
        this.chatInput = "";
      }
    },
    closeSettings() {
      this.settingsVisible = false;
    },
    saveSettings() {
      // 验证必填项
      if (!this.form.title || !this.form.title.trim()) {
        ElMessage.warning("请输入直播标题");
        return;
      }
      if (!this.form.category) {
        ElMessage.warning("请选择直播分区");
        return;
      }
      
      // 保存数据（这里可以调用API保存到后端）
      console.log("保存直播间设置：", this.form);
      
      // 可以在这里调用API保存
      // await this.$api.saveLiveSettings(this.form);
      
      ElMessage.success("保存成功");
      // 保存后不关闭弹窗，让用户可以继续编辑或开始直播
    },
    startLive() {
      // 验证必填项
      if (!this.form.title || !this.form.title.trim()) {
        ElMessage.warning("请输入直播标题");
        return;
      }
      if (!this.form.category) {
        ElMessage.warning("请选择直播分区");
        return;
      }
      
      // 先保存设置
      console.log("保存并开始直播：", this.form);
      
      // 可以在这里调用API保存并开始直播
      // await this.$api.startLive(this.form);
      
      this.isWaiting = true;
      ElMessage.success("正在开始直播...");
      
      // 模拟开始直播过程
      setTimeout(() => {
        this.isWaiting = false;
        this.settingsVisible = false;
        ElMessage.success("直播已开始");
        // 可以在这里跳转到直播页面或更新状态
      }, 2000);
    },
  },
};
</script>

<style scoped>
.live-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft Yahei', sans-serif;
}

/* 顶部栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 240, 248, 0.95) 100%);
}

.top-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.top-cover {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  flex-shrink: 0;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.3);
}

.top-cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.top-cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.12);
  color: rgba(255, 255, 255, 0.7);
}

.top-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-row {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: rgba(45, 45, 45, 0.7);
}

.dot-sep {
  color: rgba(45, 45, 45, 0.4);
}

.live-title {
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
}

.edit-icon {
  font-size: 14px;
  opacity: 0.7;
}

.live-category {
  font-size: 14px;
  color: rgba(45, 45, 45, 0.7);
}

.lock-icon {
  font-size: 14px;
  opacity: 0.7;
}

.live-permission {
  font-size: 14px;
  color: rgba(45, 45, 45, 0.7);
}

.top-right {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: rgba(45, 45, 45, 0.75);
  transition: color 0.2s;
}

.top-right:hover {
  color: #ff69b4;
}

.exit-btn {
  padding: 6px 14px;
  border-radius: 999px;
  border: 1px solid rgba(255, 105, 180, 0.4);
  background: rgba(255, 255, 255, 0.8);
  color: rgba(45, 45, 45, 0.85);
  font-size: 13px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.exit-btn:hover {
  background: rgba(255, 105, 180, 0.08);
  border-color: rgba(255, 105, 180, 0.8);
  color: #ff69b4;
}

.setup-hint {
  font-size: 14px;
}

.arrow-down {
  font-size: 16px;
  color: #ff4081;
}

/* 主内容区 */
.main-content {
  display: flex;
  flex: 1;
  overflow: hidden;
  margin: 16px 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(255, 105, 180, 0.15);
}

/* 左侧整体区域：顶部栏 + 推流设置 */
.left-wrapper {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  border-right: 1px solid rgba(255, 105, 180, 0.2);
}

/* 左侧面板 */
.left-panel {
  flex: 1;
  min-width: 400px;
  display: flex;
  flex-direction: column;
  background: #ffffff;
}

.streaming-setup {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  display: flex;
  gap: 40px; /* 加大左侧推流配置与右侧教程之间的横向间距 */
  align-items: flex-start;
  justify-content: space-between; /* 让左侧推流配置靠左，右侧教程贴近主内容区右边界 */
}

.cover-preview-card {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 105, 180, 0.15);
  border-radius: 14px;
  padding: 16px;
  margin-bottom: 20px;
}

.cover-preview-img {
  width: 100%;
  height: 160px;
  object-fit: cover;
  border-radius: 10px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.25);
}

.cover-preview-empty {
  height: 160px;
  border-radius: 10px;
  border: 1px dashed rgba(255, 105, 180, 0.3);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: rgba(45, 45, 45, 0.7);
  gap: 6px;
}

.cover-preview-icon {
  font-size: 28px;
  opacity: 0.8;
}

.cover-preview-footer {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: rgba(45, 45, 45, 0.8);
}

.mode-tabs {
  display: flex;
  margin-bottom: 20px;
}

.tab-item {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.2s;
}

.tab-item.active {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  border-color: transparent;
  color: #fff;
}

.setup-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 8px;
}

.setup-content {
  max-width: 600px; /* 加宽整体推流配置区域，使输入框可以更长 */
}

.setup-hint {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.6);
  margin-bottom: 20px;
  line-height: 1.5;
}

/* OBS 教程区域 */
.obs-tutorial {
  flex: 1;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 14px;
  padding: 16px 18px 18px;
  box-shadow: 0 8px 24px rgba(15, 16, 22, 0.08);
  border: 1px solid rgba(255, 105, 180, 0.14);
}

.tutorial-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
}

.tutorial-steps {
  margin: 0 0 12px 0;
  padding-left: 0;
  list-style: none;
  font-size: 13px;
  color: rgba(45, 45, 45, 0.8);
  line-height: 1.6;
}

.tutorial-steps li + li {
  margin-top: 6px;
}

.step-index {
  font-weight: 600;
  color: rgba(45, 45, 45, 0.9);
}

.highlight-text {
  color: #ff69b4;
  font-weight: 600;
}

.tutorial-card {
  background: #111827;
  border-radius: 14px;
  padding: 12px;
  margin-bottom: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.4);
}

.tutorial-img {
  width: 100%;
  display: block;
  border-radius: 10px;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: rgba(45, 45, 45, 0.85);
  margin-bottom: 8px;
}

.input-with-copy {
  display: flex;
  gap: 8px;
}

:deep(.setup-content .el-input),
:deep(.setup-content .el-select),
:deep(.setup-content .el-radio-group) {
  max-width: 420px; /* 再加长普通输入框 */
}

:deep(.setup-content .input-with-copy .el-input) {
  flex: 1;
  min-width: 320px;
  max-width: 380px; /* 带复制按钮的输入框也再加长 */
}

.copy-btn {
  padding: 8px 16px;
  background: #ffffff;
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.copy-btn:hover {
  background: rgba(255, 105, 180, 0.06);
  border-color: rgba(255, 105, 180, 0.8);
}

.protocol-select {
  margin-bottom: 16px;
}

.new-feature {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  padding: 12px;
  background: rgba(255, 64, 129, 0.1);
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}

.new-feature:hover {
  background: rgba(255, 64, 129, 0.2);
}

.new-tag {
  background: #ff4081;
  color: #fff;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
}

.feature-text {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
}

/* 底部控制栏 */
.bottom-controls {
  padding: 12px 20px;
  border-top: 1px solid rgba(255, 105, 180, 0.2);
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: nowrap;
}

.audio-item {
  display: flex;
  align-items: center;
  gap: 6px;
  flex-shrink: 0;
}

.audio-icon {
  font-size: 18px;
  color: rgba(45, 45, 45, 0.9);
}

.caret-icon {
  font-size: 10px;
  color: rgba(45, 45, 45, 0.6);
  cursor: pointer;
  transition: color 0.2s;
}

.caret-icon:hover {
  color: rgba(255, 255, 255, 0.9);
}

.audio-slider {
  width: 120px;
  flex-shrink: 0;
}

/* Element Plus 滑块样式覆盖 */
:deep(.audio-slider .el-slider__runway) {
  background-color: rgba(255, 255, 255, 0.2);
  height: 4px;
}

:deep(.audio-slider .el-slider__bar) {
  background-color: #ff4081;
  height: 4px;
}

:deep(.audio-slider .el-slider__button) {
  width: 12px;
  height: 12px;
  background-color: #fff;
  border: 2px solid #ff4081;
}

.audio-value {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.9);
  min-width: 35px;
  text-align: right;
  font-weight: 500;
}

.mute-btn {
  padding: 6px;
  min-width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 105, 180, 0.25);
  color: rgba(45, 45, 45, 0.9);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mute-btn:hover {
  background: rgba(255, 105, 180, 0.08);
}

.mute-btn.muted {
  background: rgba(255, 105, 180, 0.18);
  border-color: #ff69b4;
  color: #ff69b4;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
  margin-left: auto;
  flex-shrink: 0;
}

.action-btn {
  padding: 6px;
  min-width: 32px;
  height: 32px;
  background: rgba(255, 255, 255, 0.9);
  border-color: rgba(255, 105, 180, 0.25);
  color: rgba(45, 45, 45, 0.9);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.action-btn:hover {
  background: rgba(255, 105, 180, 0.08);
}

.action-btn.recording {
  background: rgba(255, 105, 180, 0.2);
  border-color: #ff69b4;
  color: #ff69b4;
}


/* 弹窗内容样式 */
.dialog-content {
  padding: 0;
  max-height: 70vh;
  overflow-y: auto;
}

.dialog-hint {
  font-size: 12px;
  color: rgba(0, 0, 0, 0.6);
  margin-bottom: 16px;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 14px;
}

.form-group label {
  display: block;
  font-size: 13px;
  color: rgba(0, 0, 0, 0.85);
  margin-bottom: 6px;
  font-weight: 500;
}

.form-group label.required::before {
  content: "*";
  color: #ff4081;
  margin-right: 4px;
}

.cover-uploader {
  position: relative;
}

.cover-preview {
  width: 100%;
  height: 150px;
  object-fit: cover;
  border-radius: 6px;
}

.cover-placeholder {
  width: 100%;
  height: 150px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border: 2px dashed #d9d9d9;
  border-radius: 6px;
  color: rgba(0, 0, 0, 0.45);
  cursor: pointer;
  transition: all 0.3s;
  font-size: 12px;
}

.cover-placeholder:hover {
  border-color: #ff4081;
  background: #fff5f7;
}

.input-with-edit {
  display: flex;
  align-items: center;
  gap: 8px;
}

.edit-icon-small {
  font-size: 16px;
  color: rgba(0, 0, 0, 0.45);
  cursor: pointer;
  transition: color 0.2s;
}

.edit-icon-small:hover {
  color: #ff4081;
}

.tags-section {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.live-tag {
  background: rgba(255, 64, 129, 0.2);
  border-color: #ff4081;
  color: #ff4081;
}

.add-tag-btn {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: rgba(255, 255, 255, 0.8);
}

.tag-hint {
  font-size: 11px;
  color: rgba(0, 0, 0, 0.45);
  margin: 4px 0 0 0;
}

/* 右侧面板 */
.right-panel {
  width: 360px; /* 适当加宽右侧区域 */
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  border-left: 1px solid rgba(255, 105, 180, 0.2);
  background: #ffffff;
  padding: 20px;
  overflow-y: auto;
}

.panel-section {
  margin-bottom: 16px;
  padding: 16px 14px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.9);
  box-shadow: 0 4px 14px rgba(15, 16, 22, 0.06);
  border: 1px solid rgba(255, 105, 180, 0.16);
  /* 即使内容较少也保持一定的视觉高度 */
  min-height: 220px;
}

/* 让“直播互动”这一块撑满右侧剩余空间 */
.interaction-section {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.audience-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.audience-tabs .tab-item {
  padding: 6px 12px;
  font-size: 12px;
}

.section-desc {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.6);
  margin-bottom: 12px;
  line-height: 1.5;
}

.session-data {
  font-size: 14px;
  color: rgba(45, 45, 45, 0.8);
  padding: 8px;
  background: rgba(255, 105, 180, 0.05);
  border-radius: 6px;
}

.interaction-icons {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.interaction-icons .el-icon {
  font-size: 20px;
  color: rgba(45, 45, 45, 0.7);
}

.gift-records {
  max-height: 120px;
  overflow-y: auto;
  margin-bottom: 16px;
  padding: 4px 0;
  border-radius: 8px;
  background: rgba(255, 105, 180, 0.03);
}

.gift-item {
  display: flex;
  justify-content: space-between;
  padding: 8px;
  background: rgba(255, 105, 180, 0.05);
  border-radius: 6px;
  margin-bottom: 8px;
  font-size: 12px;
}

.gift-user {
  color: rgba(45, 45, 45, 0.85);
}

.gift-name {
  color: rgba(45, 45, 45, 0.6);
}

.gift-amount {
  color: #ff69b4;
  font-weight: 600;
}

.danmu-messages {
  max-height: 220px; /* 提高最大高度，显示更多弹幕 */
  overflow-y: auto;
  margin-bottom: 20px;
  padding: 8px;
  background: rgba(255, 105, 180, 0.05);
  border-radius: 8px;
  min-height: 140px; /* 提高最小高度，即使弹幕少也占更大空间 */
}

.danmu-msg {
  font-size: 12px;
  margin-bottom: 8px;
  line-height: 1.5;
}

.msg-user {
  color: #ff69b4;
  font-weight: 600;
}

.msg-content {
  color: rgba(45, 45, 45, 0.8);
}

.chat-input-box {
  display: flex;
  gap: 8px;
}

.send-btn {
  background: #ffffff;
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
}

.send-btn:hover {
  background: rgba(255, 105, 180, 0.06);
  border-color: rgba(255, 105, 180, 0.8);
}

/* Element Plus 弹窗样式覆盖 */
:deep(.el-dialog) {
  background: #fff;
  border-radius: 12px;
  margin-top: 5vh !important;
}

:deep(.el-dialog__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

:deep(.el-dialog__title) {
  font-size: 16px;
  font-weight: 600;
}

:deep(.el-dialog__body) {
  padding: 20px;
  max-height: calc(90vh - 120px);
  overflow-y: auto;
}

:deep(.el-dialog__footer) {
  padding: 12px 20px;
  border-top: 1px solid #f0f0f0;
}
</style>