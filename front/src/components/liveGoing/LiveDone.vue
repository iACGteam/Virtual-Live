<template>
  <div class="live-container">
    <!-- 顶部栏 -->
    <div class="top-bar">
      <div class="top-left">
        <div v-if="!isLiveMode" class="page-title">直播设置</div>
        <div v-else class="page-title-live">直播中</div>
        
        <!-- 粉丝数显示 -->
        <div class="fan-count-display" v-if="userProfile.followers !== null">
            <span class="fan-label">粉丝数:</span>
            <span class="fan-value">{{ userProfile.followers }}</span>
        </div>
      </div>
      <div class="top-right">
        <el-button v-if="!isLiveMode" type="danger" text @click="handleDeregister" style="margin-right: 10px">注销直播间</el-button>
        <el-button v-if="isLiveMode" type="primary" plain @click="openSettings" style="margin-right: 10px">修改设置</el-button>
        <el-button type="danger" plain @click="handleExit">退出直播间</el-button>
      </div>
    </div>

    <!-- 主内容区 -->
    <div class="main-content">
      
      <!-- 模式 A: 准备中 (未开播) -->
      <div v-if="!isLiveMode" class="prepare-mode-container">
        <div class="prepare-content">
            <!-- 1. 显眼的直播信息设置 -->
            <div class="info-card">
              <div class="cover-section" @click="openSettings">
                <img v-if="form.coverUrl" :src="form.coverUrl" class="cover-img" />
                <div v-else class="cover-placeholder">
                  <el-icon><PictureFilled /></el-icon>
                  <span>上传封面</span>
                </div>
                <div class="cover-overlay">
                  <el-icon><Edit /></el-icon> 修改信息
                </div>
              </div>
              <div class="info-details">
                <h2 class="live-title-large" @click="openSettings">
                  {{ form.title || '点击设置直播标题' }} <el-icon class="edit-icon"><Edit /></el-icon>
                </h2>
                <div class="live-meta-large">
                  <span class="meta-tag">{{ form.category || '选择分区' }}</span>
                  <span class="meta-tag">{{ form.permission === 'public' ? '公开' : '私密' }}</span>
                </div>
              </div>
            </div>

            <!-- 2. 推流配置 -->
            <div class="stream-config-card">
              <h3 class="card-title">推流配置</h3>
              <div class="config-row">
                <div class="config-item">
                  <label>服务器</label>
                  <div class="input-with-copy">
                    <el-input v-model="obs.url" readonly />
                    <el-button @click="copyToClipboard(obs.url)">复制</el-button>
                  </div>
                </div>
                <div class="config-item">
                  <label>推流码</label>
                  <div class="input-with-copy">
                    <el-input v-model="obs.code" type="password" show-password readonly />
                    <el-button @click="copyToClipboard(obs.code)">复制</el-button>
                  </div>
                </div>
              </div>
              <p class="config-hint">请将以上信息填入 OBS (设置 -> 直播)</p>
            </div>

            <!-- 3. 巨大的开始按钮 -->
            <div class="action-area">
              <button class="start-live-btn" @click="startLive">
                <span class="btn-icon">🚀</span> 开启直播 / 进入直播间
              </button>
              <p class="action-hint">在 OBS 点击“开始推流”后，点击上方按钮进入直播互动界面</p>
            </div>
        </div>
      </div>

      <!-- 模式 B: 直播中 (新界面) -->
      <div v-else class="live-mode-layout">
        
        <!-- 左侧：直播画面与弹幕层 -->
        <div class="video-area">
            <div class="video-wrapper">
                <video id="videoElement" controls autoplay muted class="video-player"></video>
                
                <!-- 弹幕列表覆盖层 (可拖拽) -->
                <div 
                    class="danmaku-overlay-list"
                    :style="{ top: overlayPos.top + 'px', left: overlayPos.left + 'px' }"
                    @mousedown="startDrag"
                >
                    <div class="overlay-header">
                        <span>实时弹幕</span>
                        <el-icon><Rank /></el-icon>
                    </div>
                    <div class="overlay-content" ref="overlayList">
                        <div v-for="dm in visibleDanmakuList" :key="dm.id" :class="['overlay-item', { 'overlay-anchor': dm.isAnchor }]">
                          <span class="overlay-user">{{ dm.user }}</span>
                          <span v-if="dm.isAnchor" class="anchor-badge-mini">主播</span>
                          <span v-if="dm.fanLevel > 0" class="fan-badge">Lv{{ dm.fanLevel }}</span>
                          <span class="overlay-text">: {{ dm.text }}</span>
                        </div>
                    </div>
                </div>

                <div v-if="!isPlaying" class="video-placeholder">
                    <div class="loading-spinner"></div>
                    <p>等待推流信号...</p>
                    <el-button type="primary" size="small" @click="reloadPlayer">刷新播放器</el-button>
                </div>
            </div>

            <!-- 底部控制栏 -->
            <div class="video-controls-bar">
                <div class="left-controls">
                </div>
                <div class="center-status">
                  <span class="live-dot"></span> 直播中
                </div>
                <div class="right-controls">
                    <el-button type="danger" size="small" @click="stopLive">结束直播</el-button>
                </div>
            </div>
        </div>

        <!-- 右侧：仪表盘 -->
        <div class="dashboard-sidebar">
            <el-tabs v-model="activeTab" class="dashboard-tabs" stretch>
                
                <!-- Tab 1: 互动消息流 -->
                <el-tab-pane label="互动" name="interaction">
                    <div class="message-stream-container">
                        <div class="message-list" ref="msgList">
                            <div v-for="msg in messageList" :key="msg.id" :class="['msg-card', 'msg-type-' + msg.type]">
                                
                                <!-- 普通弹幕 -->
                                <template v-if="msg.type === 'CHAT'">
                                  <div class="msg-header">
                                    <span class="user-name">{{ msg.user }}</span>
                                    <span class="user-id" v-if="msg.userId">#{{ msg.userId }}</span>
                                    <span v-if="msg.isAnchor" class="anchor-badge">主播</span>
                                    <span v-if="msg.fanLevel > 0" class="fan-badge">Lv{{ msg.fanLevel }}</span>
                                    <span class="time">{{ formatTime(msg.timestamp) }}</span>
                                    <div class="msg-actions">
                                      <el-tooltip content="禁言该用户" placement="top">
                                        <el-icon class="action-icon" @click="quickMute(msg.userId, msg.user)"><Lock /></el-icon>
                                      </el-tooltip>
                                      <el-tooltip content="删除此条" placement="top">
                                        <el-icon class="action-icon delete" @click="deleteDanmaku(msg.id)"><Delete /></el-icon>
                                      </el-tooltip>
                                    </div>
                                  </div>
                                  <div class="msg-content">{{ msg.content }}</div>
                                </template>

                                <!-- 礼物消息 -->
                                <template v-else-if="msg.type === 'GIFT'">
                                  <div class="gift-content">
                                    <span class="user-name">{{ msg.user }}</span>
                                    <span class="user-id" v-if="msg.userId">#{{ msg.userId }}</span>
                                    <span class="gift-action">送出了</span>
                                    <span class="gift-name">{{ msg.giftName }}</span>
                                    <span class="gift-count">x{{ msg.count }}</span>
                                  </div>
                                </template>

                                <!-- SC (醒目留言) -->
                                <template v-else-if="msg.type === 'SC'">
                                    <div class="sc-header">
                                        <span class="user-name">{{ msg.user }}</span>
                                        <span class="sc-price">￥{{ msg.price }}</span>
                                    </div>
                                    <div class="sc-body">
                                        {{ msg.content }}
                                    </div>
                                </template>

                                <!-- 系统消息 -->
                                <template v-else-if="msg.type === 'SYSTEM'">
                                    <div class="system-content">
                                        <el-icon><Bell /></el-icon> {{ msg.content }}
                                    </div>
                                </template>

                            </div>
                        </div>
                        
                        <!-- 发送框 -->
                        <div class="chat-input-area">
                            <el-input 
                                v-model="chatInput" 
                                placeholder="和观众聊聊..." 
                                @keyup.enter="sendChat"
                            >
                                <template #append>
                                    <el-button @click="sendChat">发送</el-button>
                                </template>
                            </el-input>
                        </div>
                    </div>
                </el-tab-pane>

                <!-- Tab 2: 房管权限 -->
                <el-tab-pane label="管理" name="admin">
                    <div class="admin-panel">
                        <div class="panel-section">
                            <h4><el-icon><Lock /></el-icon> 禁言管理</h4>
                            <el-form label-position="top" size="small">
                                <el-form-item label="用户ID">
                                    <el-input v-model="adminForm.muteUserId" placeholder="输入用户ID" />
                                </el-form-item>
                                <el-form-item label="时长(秒)">
                                    <el-input-number v-model="adminForm.muteDuration" :min="60" :step="60" style="width: 100%" />
                                </el-form-item>
                                <div class="form-actions">
                                    <el-button type="danger" @click="handleMute">🚫 禁言</el-button>
                                    <el-button type="success" @click="handleUnmute">🟢 解禁</el-button>
                                </div>
                            </el-form>
                        </div>

                            <!-- 弹幕管理已移除 -->
                    </div>
                </el-tab-pane>

                <!-- 数据面板已移除 -->
            </el-tabs>
        </div>

      </div>
    </div>

    <!-- 开播信息弹窗 (保持不变) -->
    <el-dialog
      v-model="settingsVisible"
      title="编辑直播间信息"
      width="520px"
      :close-on-click-modal="false"
    >
      <div class="dialog-content">
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
          <el-input v-model="form.title" placeholder="请输入直播标题" />
        </div>
        
        <div class="form-group">
          <label class="required">分区</label>
          <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
            <el-option label="虚拟gamer" value="虚拟gamer" />
            <el-option label="虚拟singer" value="虚拟singer" />
            <el-option label="虚拟声优" value="虚拟声优" />
            <el-option label="虚拟男V" value="虚拟男V" />
          </el-select>
        </div>

        <div class="form-group">
            <label>简介</label>
            <el-input type="textarea" v-model="form.announcement" />
        </div>
        
        <div class="form-group">
          <label>观看权限</label>
          <el-select v-model="form.permission" placeholder="请选择" style="width: 100%">
            <el-option label="公开" value="public" />
            <el-option label="私密" value="private" />
          </el-select>
        </div>
      </div>
      
      <template #footer>
        <el-button @click="closeSettings">取消</el-button>
        <el-button type="primary" @click="saveSettings">保存并更新</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, nextTick } from "vue";
import {
  Plus, Edit, Lock, PictureFilled, Setting, Microphone, VideoCamera, Bell, Delete, Search, Rank
} from "@element-plus/icons-vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { getAuthToken } from "@/utils/auth";

// 动态加载脚本辅助函数
function loadScript(src) {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve();
      return;
    }
    const script = document.createElement('script');
    script.src = src;
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  });
}

export default {
  components: {
    Plus, Edit, Lock, PictureFilled, Setting, Microphone, VideoCamera, Bell, Delete, Search, Rank
  },
  data() {
    return {
      isLiveMode: false, // 是否进入直播模式
      settingsVisible: false,
      activeTab: 'interaction',
      
      // 播放器状态
      isPlaying: false,
      flvPlayer: null,
      stompClient: null,

      onlineCount: 0,
      
      form: {
        title: "",
        category: "",
        coverUrl: "",
        announcement: "",
        permission: "public",
      },
      obs: {
        url: "",
        code: "",
      },
      
      // 消息流
      messageList: [], // { id, type: 'CHAT'|'GIFT'|'SC'|'SYSTEM', user, userId, content, ... }
      visibleDanmakuList: [], // { id, user, text }
      chatInput: "",
      roomId: null,

      // 弹幕列表拖拽
      overlayPos: { top: 20, left: 20 },
      dragging: false,
      dragOffset: { x: 0, y: 0 },

      // 管理表单
      adminForm: {
          muteUserId: '',
          muteDuration: 60,
          deleteMsgId: ''
      },

      // 数据
      leaderboard: [],
      searchQuery: '',
      Search,
      
      // 用户信息
      userProfile: {
          followers: null
      }
    };
  },
  mounted() {
    this.fetchUserProfile();
    this.fetchStreamInfo();
  },
  methods: {
    async fetchUserProfile() {
        const token = getAuthToken();
        if (!token) return;
        
        try {
            const res = await fetch("/api/v1/live/user/me", {
                headers: {
                    "Authorization": "Bearer " + token
                }
            });
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                this.userProfile = json.data;
            }
        } catch (e) {
            console.error("获取用户信息失败", e);
        }
    },
    openSettings() {
      this.settingsVisible = true;
    },
    closeSettings() {
      this.settingsVisible = false;
    },
    handleExit() {
      this.$router.push({ path: '/profile' });
    },
    async handleDeregister() {
      try {
        await ElMessageBox.confirm(
          '确定要注销当前直播间吗？注销后下次开播将创建新的直播间。',
          '警告',
          {
            confirmButtonText: '确定注销',
            cancelButtonText: '取消',
            type: 'warning',
          }
        )
        
        const token = getAuthToken();
        const headers = { "Authorization": "Bearer " + token };
        
        const res = await fetch("/api/v1/live/rooms/my", {
            method: "DELETE",
            headers
        });
        
        const json = await res.json();
        if (json.code === 0 || json.code === 200) {
            ElMessage.success("直播间已注销");
            this.$router.push('/live');
        } else {
            ElMessage.error(json.message || "注销失败");
        }
      } catch (e) {
        if (e !== 'cancel') {
            console.error(e);
            ElMessage.error("操作失败");
        }
      }
    },
    async handleCoverChange(file) {
      // 1. 预览
      this.form.coverUrl = URL.createObjectURL(file.raw);
      
      // 2. 上传
      const formData = new FormData();
      formData.append("file", file.raw);
      
      const token = getAuthToken();
      const headers = {};
      if (token) {
         headers["Authorization"] = token.startsWith("Bearer ") ? token : "Bearer " + token;
      }
      
      try {
        const res = await fetch("/api/v1/upload/image", {
            method: "POST",
            headers,
            body: formData
        });
        const json = await res.json();
        if (json.code === 0 || json.code === 200) { 
             this.form.coverUrl = json.data.url;
             ElMessage.success("封面上传成功");
        } else {
             ElMessage.error("封面上传失败: " + (json.message || json.msg));
        }
      } catch (e) {
        console.error(e);
        ElMessage.error("上传出错");
      }
    },
    
    // 复制功能
    copyToClipboard(text) {
      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).then(() => {
          ElMessage.success("已复制");
        }).catch(() => this.fallbackCopy(text));
      } else {
        this.fallbackCopy(text);
      }
    },
    fallbackCopy(text) {
      const textArea = document.createElement("textarea");
      textArea.value = text;
      textArea.style.position = "fixed";
      textArea.style.left = "-9999px";
      document.body.appendChild(textArea);
      textArea.select();
      try {
        document.execCommand("copy");
        ElMessage.success("已复制");
      } catch (err) {
        ElMessage.error("复制失败");
      }
      document.body.removeChild(textArea);
    },

    // 保存设置
    async saveSettings() {
      if (!this.roomId) {
          ElMessage.error("未获取到房间ID");
          return;
      }
      const token = getAuthToken();
      const headers = {
        "Content-Type": "application/json",
        "Authorization": token.startsWith("Bearer ") ? token : "Bearer " + token
      };
      
      const body = {
          title: this.form.title,
          description: this.form.announcement,
          coverUrl: this.form.coverUrl,
          category: this.form.category
      };

      try {
          const res = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/update`, {
              method: "POST",
              headers,
              body: JSON.stringify(body)
          });
          if (res.ok) {
              ElMessage.success("更新成功");
              this.settingsVisible = false;
          } else {
              ElMessage.error("更新失败");
          }
      } catch (e) {
          ElMessage.error("网络错误");
      }
    },

    // 获取推流信息
    async fetchStreamInfo() {
      const token = getAuthToken();
      if (!token) return;
      
      const headers = {
        "Content-Type": "application/json",
        "Authorization": token.startsWith("Bearer ") ? token : "Bearer " + token
      };

      try {
        // 1. Get My Room
        let r1 = await fetch("/api/v1/live/rooms/my", { headers });
        if (!r1.ok) return;
        let myJson = await r1.json();
        
        // 兼容数据结构
        if (myJson.data) myJson = myJson.data;
        
        this.roomId = myJson.roomId || myJson.id;
        
        // 填充表单
        if (myJson.title) this.form.title = myJson.title;
        if (myJson.coverUrl) this.form.coverUrl = myJson.coverUrl;
        if (myJson.category) this.form.category = myJson.category;
        if (myJson.description) this.form.announcement = myJson.description;

        if (!this.roomId) return;

        // 2. Get Manager Info
        let r2 = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/info`, { headers });
        if (!r2.ok) return;
        let resJson = await r2.json();
        
        if (resJson.code === 0 || resJson.code === 200) {
            const info = resJson.data;
            this.obs.url = info.serverAddress || info.rtmpServer;
            this.obs.code = info.streamKey;
            ElMessage.success("推流信息已获取");
        } else {
            ElMessage.error(resJson.message || "获取推流信息失败");
        }
        
      } catch (e) {
        console.error(e);
        ElMessage.error("网络请求失败");
      }
    },

    // === 核心逻辑：开始直播 ===
    async startLive() {
      if (!this.obs.url || !this.obs.code) {
          ElMessage.warning("正在获取推流信息，请稍候...");
          await this.fetchStreamInfo();
          if (!this.obs.url) return;
      }
      
      // 通知后端更新直播状态
      try {
          const token = getAuthToken();
          const headers = { "Content-Type": "application/json", "Authorization": "Bearer " + token };
          const statusRes = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/status`, {
              method: 'POST',
              headers,
              body: JSON.stringify({ isLive: true })
          });
          if (!statusRes.ok) {
             console.error("Failed to update status, response:", statusRes.status);
             ElMessage.warning("直播状态同步失败，可能导致列表不显示");
          } else {
             console.log("Live status updated to TRUE for room", this.roomId);
          }
      } catch (e) {
          console.error("Failed to update live status", e);
          ElMessage.warning("网络异常，直播状态同步失败");
      }

      this.isLiveMode = true;
      ElMessage.success("进入直播间，正在连接服务...");

      // 加载依赖脚本
      try {
        await Promise.all([
            loadScript("https://unpkg.com/flv.js/dist/flv.min.js"),
            loadScript("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"),
            loadScript("https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js")
        ]);
        
        this.initWebSocket();
        this.initPlayer();
        this.fetchLeaderboard();
        
      } catch (e) {
          ElMessage.error("加载直播组件失败，请检查网络");
          console.error(e);
      }
    },

    async stopLive() {
      // 通知后端更新直播状态
      try {
          const token = getAuthToken();
          const headers = { "Content-Type": "application/json", "Authorization": "Bearer " + token };
          await fetch(`/api/v1/live/rooms/${this.roomId}/manager/status`, {
              method: 'POST',
              headers,
              body: JSON.stringify({ isLive: false })
          });
      } catch (e) {
          console.error("Failed to update live status", e);
      }

      this.isLiveMode = false;
      if (this.flvPlayer) {
          this.flvPlayer.destroy();
          this.flvPlayer = null;
      }
      if (this.stompClient) {
          this.stompClient.disconnect();
          this.stompClient = null;
      }
      this.isPlaying = false;
    },

    // 初始化播放器
    initPlayer() {
        if (!window.flvjs || !window.flvjs.isSupported()) return;
        
        const playHost = "http://localhost:8088"; 
        const app = "live";
        const streamKey = this.obs.code;
        const flvUrl = `${playHost}/${app}/${streamKey}.flv`;
        
        console.log("尝试播放:", flvUrl);

        const videoElement = document.getElementById("videoElement");
        if (!videoElement) return;

        this.flvPlayer = window.flvjs.createPlayer({
            type: 'flv',
            url: flvUrl,
            isLive: true,
            hasAudio: true,
        });
        
        this.flvPlayer.attachMediaElement(videoElement);
        this.flvPlayer.load();
        
        this.flvPlayer.play().then(() => {
            this.isPlaying = true;
        }).catch(e => {
            console.warn("自动播放失败或流未就绪", e);
            this.isPlaying = false;
        });
        
        this.flvPlayer.on(window.flvjs.Events.ERROR, (e) => {
            console.log("播放器错误", e);
            this.isPlaying = false;
        });
    },
    
    reloadPlayer() {
        if (this.flvPlayer) {
            this.flvPlayer.destroy();
        }
        this.initPlayer();
    },

    // 初始化 WebSocket
    initWebSocket() {
        if (!this.roomId) return;
        
        const socket = new window.SockJS("http://localhost:8081/ws-live");
        this.stompClient = window.Stomp.over(socket);
        this.stompClient.debug = null; 
        
        const token = getAuthToken();
        const headers = token ? { "Authorization": "Bearer " + token } : {};

        this.stompClient.connect(headers, (frame) => {
            console.log("WS Connected");
            
            // 订阅弹幕
            this.stompClient.subscribe(`/topic/danmaku/${this.roomId}`, (message) => {
                const body = JSON.parse(message.body);
                this.handleIncomingMessage(body);
            });

            // 订阅错误消息
            if (this.userProfile && this.userProfile.id) {
                this.stompClient.subscribe(`/topic/errors/${this.userProfile.id}`, (message) => {
                    ElMessage.error(message.body);
                });
            } else {
                 // Fallback or try to get ID from token if userProfile not ready
                 // For host, userProfile should be ready
            }
            
            // 获取历史弹幕
            this.fetchHistory();
            
        }, (err) => {
            console.error("WS Error", err);
            ElMessage.error("聊天室连接断开");
        });
    },

    async fetchHistory() {
        try {
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/danmaku/history`);
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                const list = json.data || [];
                list.forEach(msg => {
                    const localMsg = {
                        id: msg.danmakuId || Date.now() + Math.random(),
                        type: msg.type || 'CHAT',
                        user: msg.senderName || "匿名",
                        userId: msg.senderId,
                        content: msg.content,
                        timestamp: new Date(), // 历史消息时间暂用当前时间或解析 msg.createdAt
                      fanLevel: msg.fanLevel || 0,
                      isAnchor: !!msg.isAnchor
                    };
                    this.messageList.push(localMsg);
                    // 历史消息也加入覆盖层？通常不需要，或者只加最近几条
                });
                // 滚动到底部
                nextTick(() => {
                    const list = this.$refs.msgList;
                    if (list) list.scrollTop = list.scrollHeight;
                });
            }
        } catch (e) {
            console.error("Fetch history failed", e);
        }
    },

    handleIncomingMessage(msg) {
        // 1. 添加到消息流
        const newMsg = {
            id: msg.id || Date.now(),
            type: msg.type || 'CHAT',
            user: msg.senderName || "匿名",
            userId: msg.senderId,
            content: msg.content,
            timestamp: new Date(),
            // Gift specific
            giftName: msg.giftName,
            count: msg.giftCount,
            // SC specific
            price: msg.giftPrice,
        fanLevel: msg.fanLevel || 0,
        isAnchor: msg.isAnchor || false
        };
        
        this.messageList.push(newMsg);
        
        // 滚动到底部
        nextTick(() => {
            const list = this.$refs.msgList;
            if (list) list.scrollTop = list.scrollHeight;
        });

        // 2. 如果是普通弹幕，添加到覆盖层列表（并传递是否为主播）
        if (msg.type === 'CHAT') {
          this.addOverlayDanmaku(newMsg.user, msg.content, newMsg.fanLevel, newMsg.isAnchor);
        }
    },

    addOverlayDanmaku(user, text, fanLevel, isAnchor) {
        const item = {
            id: Date.now() + Math.random(),
            user: user,
            text: text,
            fanLevel: fanLevel || 0
        };
      item.isAnchor = !!isAnchor;
      this.visibleDanmakuList.push(item);
        
      // Keep only last 50
      if (this.visibleDanmakuList.length > 50) {
        this.visibleDanmakuList.shift();
      }
        
        // Scroll to bottom of overlay list
        nextTick(() => {
            const list = this.$refs.overlayList;
            if (list) list.scrollTop = list.scrollHeight;
        });
    },

    // === 拖拽逻辑 ===
    startDrag(e) {
        this.dragging = true;
        this.dragOffset.x = e.clientX - this.overlayPos.left;
        this.dragOffset.y = e.clientY - this.overlayPos.top;
        document.addEventListener('mousemove', this.onDrag);
        document.addEventListener('mouseup', this.stopDrag);
    },
    onDrag(e) {
        if (!this.dragging) return;
        
        let newLeft = e.clientX - this.dragOffset.x;
        let newTop = e.clientY - this.dragOffset.y;
        
        // 简单边界限制
        const wrapper = document.querySelector('.video-wrapper');
        if (wrapper) {
            const w = wrapper.clientWidth;
            const h = wrapper.clientHeight;
            // 假设弹幕框宽200，高300
            if (newLeft < 0) newLeft = 0;
            if (newTop < 0) newTop = 0;
            if (newLeft > w - 50) newLeft = w - 50;
            if (newTop > h - 50) newTop = h - 50;
        }
        
        this.overlayPos.left = newLeft;
        this.overlayPos.top = newTop;
    },
    stopDrag() {
        this.dragging = false;
        document.removeEventListener('mousemove', this.onDrag);
        document.removeEventListener('mouseup', this.stopDrag);
    },

    sendChat() {
        if (!this.chatInput.trim() || !this.stompClient) return;
        
        const payload = {
            type: "CHAT",
            roomId: this.roomId,
            content: this.chatInput
        };
        
        const token = getAuthToken();
        const headers = token ? { "Authorization": "Bearer " + token } : {};
        
        this.stompClient.send("/app/send-danmaku", headers, JSON.stringify(payload));
        this.chatInput = "";
    },

    formatTime(date) {
        return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    },

    // === 管理功能 ===
    quickMute(userId, userName) {
        this.adminForm.muteUserId = userId;
        this.activeTab = 'admin';
        ElMessage.info(`已选中用户 ${userName}，请确认禁言时长`);
    },
    
    async handleMute() {
        if (!this.adminForm.muteUserId) return;
        try {
            const token = getAuthToken();
            const headers = { "Content-Type": "application/json", "Authorization": "Bearer " + token };
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/mute`, {
                method: 'POST',
                headers,
                body: JSON.stringify({
                    userId: parseInt(this.adminForm.muteUserId),
                    durationSeconds: this.adminForm.muteDuration
                })
            });
            if (res.ok) ElMessage.success("禁言成功");
            else ElMessage.error("操作失败");
        } catch (e) {
            ElMessage.error("网络错误");
        }
    },

    async handleUnmute() {
        if (!this.adminForm.muteUserId) return;
        try {
            const token = getAuthToken();
            const headers = { "Content-Type": "application/json", "Authorization": "Bearer " + token };
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/unmute`, {
                method: 'POST',
                headers,
                body: JSON.stringify({
                    userId: parseInt(this.adminForm.muteUserId)
                })
            });
            if (res.ok) ElMessage.success("解禁成功");
            else ElMessage.error("操作失败");
        } catch (e) {
            ElMessage.error("网络错误");
        }
    },

    async deleteDanmaku(msgId) {
        if (!msgId) return;
        try {
            const token = getAuthToken();
            const headers = { "Authorization": "Bearer " + token };
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/manager/danmaku/${msgId}`, {
                method: 'DELETE',
                headers
            });
            if (res.ok) {
                ElMessage.success("删除成功");
                // 本地移除
                this.messageList = this.messageList.filter(m => m.id !== msgId);
            }
            else ElMessage.error("操作失败");
        } catch (e) {
            ElMessage.error("网络错误");
        }
    },
    
    async handleDeleteMsg() {
        await this.deleteDanmaku(this.adminForm.deleteMsgId);
    },

    // === 数据功能 ===
    async fetchLeaderboard() {
        try {
            const token = getAuthToken();
            const headers = { "Authorization": "Bearer " + token };
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/stats/leaderboard?type=SESSION`, { headers });
            if (res.ok) {
                const json = await res.json();
                if (json.code === 0 || json.code === 200) {
                    this.leaderboard = json.data;
                } else {
                    this.leaderboard = []; // 失败时清空，避免显示错误数据
                }
            }
        } catch (e) {
            console.error(e);
        }
    }
  },
  beforeUnmount() {
      this.stopLive();
  }
};
</script>

<style scoped>
.live-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #f4f6f9;
  font-family: 'Segoe UI', sans-serif;
}

.top-bar {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid #eee;
  background: #fff;
  z-index: 10;
}

.page-title {
  font-weight: 600;
  font-size: 20px;
  color: #333;
}

.main-content {
  flex: 1;
  overflow: hidden;
  display: flex;
  justify-content: center;
}

/* 准备模式 */
.prepare-mode-container {
    width: 100%;
    display: flex;
    justify-content: center;
    padding: 40px;
    overflow-y: auto;
}

.prepare-content {
    width: 100%;
    max-width: 800px;
}

/* 复用之前的卡片样式 */
.info-card {
  display: flex;
  gap: 24px;
  margin-bottom: 40px;
  background: #fff;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
}

.cover-section {
  width: 240px;
  height: 135px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  cursor: pointer;
  background: #f0f0f0;
}

.cover-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  gap: 8px;
  color: #555;
  font-weight: bold;
}

.cover-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 6px;
  text-align: center;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}

.cover-section:hover .cover-overlay {
  opacity: 1;
}

/* 调整占位符内的图标大小，确保垂直居中 */
.cover-placeholder svg,
.cover-placeholder i,
.cover-placeholder .el-icon {
  width: 36px;
  height: 36px;
  display: block;
}

.info-details {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.live-title-large {
  font-size: 24px;
  margin: 0 0 16px 0;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #333;
  font-weight: bold;
}

.live-title-large:hover {
  color: #ff69b4;
}

.live-meta-large {
  display: flex;
  gap: 10px;
}

.meta-tag {
  background: #f5f5f5;
  padding: 4px 12px;
  border-radius: 100px;
  font-size: 14px;
  color: #666;
}

.stream-config-card {
  background: #f9f9f9;
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 40px;
  border: 1px solid #eee;
}

.card-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 18px;
  color: #333;
  font-weight: bold;
}

.config-row {
  display: flex;
  gap: 20px;
}

.config-item {
  flex: 1;
}

.config-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
  color: #333;
}

.input-with-copy {
  display: flex;
  gap: 10px;
}

.config-hint {
  margin-top: 16px;
  color: #888;
  font-size: 13px;
}

.action-area {
  text-align: center;
}

.start-live-btn {
  background: linear-gradient(135deg, #ff69b4, #ff4081);
  color: #fff;
  border: none;
  padding: 16px 48px;
  font-size: 20px;
  border-radius: 50px;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(255, 64, 129, 0.3);
  transition: transform 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 10px;
}

.start-live-btn:hover {
  transform: scale(1.05);
}

.action-hint {
  margin-top: 16px;
  color: #666;
}

/* === 直播模式布局 === */
.live-mode-layout {
    display: flex;
    width: 100%;
    height: 100%;
}

.video-area {
    flex: 1;
    background: #000;
    display: flex;
    flex-direction: column;
    position: relative;
}

.video-wrapper {
    flex: 1;
    position: relative;
    overflow: hidden;
    display: flex;
    align-items: center;
    justify-content: center;
}

.video-player {
    width: 100%;
    height: 100%;
    max-height: 100%;
}

.danmaku-layer {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none; /* 允许点击穿透 */
    overflow: hidden;
    z-index: 10;
}

.flying-item {
    position: absolute;
    right: -100%; /* 初始在右侧屏幕外 */
    white-space: nowrap;
    font-size: 24px;
    font-weight: bold;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.8);
    animation-name: fly;
    animation-timing-function: linear;
}

@keyframes fly {
    from { transform: translateX(0); right: -20%; }
    to { transform: translateX(-150vw); right: 100%; }
}

.video-placeholder {
    position: absolute;
    color: #fff;
    text-align: center;
    z-index: 5;
}

.video-controls-bar {
    height: 50px;
    background: #1a1a1a;
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 20px;
    color: #fff;
}

.left-controls {
    display: flex;
    gap: 20px;
}

.control-item {
    display: flex;
    align-items: center;
    gap: 6px;
    cursor: pointer;
    color: #aaa;
    font-size: 14px;
}
.control-item.active {
    color: #fff;
}

.center-status {
    font-size: 14px;
    color: #ff69b4;
    display: flex;
    align-items: center;
    gap: 8px;
}

.live-dot {
    width: 8px;
    height: 8px;
    background: #ff69b4;
    border-radius: 50%;
    animation: pulse 2s infinite;
}

/* 右侧仪表盘 */
.dashboard-sidebar {
    width: 380px;
    background: #fff;
    border-left: 1px solid #eee;
    display: flex;
    flex-direction: column;
}

.dashboard-tabs {
    height: 100%;
    display: flex;
    flex-direction: column;
}

:deep(.el-tabs__content) {
    flex: 1;
    overflow: hidden;
    display: flex;
    flex-direction: column;
}

:deep(.el-tab-pane) {
    height: 100%;
    display: flex;
    flex-direction: column;
}

/* 消息流 */
.message-stream-container {
    flex: 1;
    display: flex;
    flex-direction: column;
    height: 100%;
    background: #f9f9f9;
}

.message-list {
    flex: 1;
    overflow-y: auto;
    padding: 12px;
}

.msg-card {
    margin-bottom: 10px;
    padding: 8px 12px;
    border-radius: 8px;
    background: #fff;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    font-size: 14px;
}

.msg-type-CHAT {
    border-left: 3px solid #eee;
}

.msg-header {
    display: flex;
    align-items: center;
    margin-bottom: 4px;
    font-size: 12px;
    color: #999;
}

.user-name {
    color: #666;
    font-weight: bold;
    margin-right: 8px;
}

.msg-actions {
    margin-left: auto;
    display: none; /* 默认隐藏，hover显示 */
    gap: 8px;
}

.msg-card:hover .msg-actions {
    display: flex;
}

.action-icon {
    cursor: pointer;
    font-size: 14px;
}
.action-icon:hover { color: #ff69b4; }
.action-icon.delete:hover { color: #f56c6c; }

.msg-content {
    color: #333;
    line-height: 1.4;
    word-break: break-all;
}

/* 礼物样式 */
.msg-type-GIFT {
    background: linear-gradient(to right, #fff0f5, #fff);
    border-left: 3px solid #ff69b4;
}
.gift-name {
    color: #ff69b4;
    font-weight: bold;
}

/* SC 样式 */
.msg-type-SC {
    background: #ff4081;
    color: #fff;
    padding: 0;
    overflow: hidden;
}
.sc-header {
    padding: 8px 12px;
    background: rgba(0,0,0,0.1);
    display: flex;
    justify-content: space-between;
    font-weight: bold;
}
.sc-body {
    padding: 10px 12px;
    font-size: 15px;
}
.msg-type-SC .user-name { color: #fff; }

/* 系统消息 */
.msg-type-SYSTEM {
    background: #f0f9eb;
    color: #67c23a;
    text-align: center;
    font-size: 12px;
}

.chat-input-area {
    padding: 12px;
    background: #fff;
    border-top: 1px solid #eee;
}

/* 管理面板 */
.admin-panel {
    padding: 20px;
    overflow-y: auto;
}
.panel-section {
    background: #fff;
    padding: 20px;
    border-radius: 8px;
    border: 1px solid #eee;
    margin-bottom: 20px;
}
.panel-section h4 {
    margin-top: 0;
    margin-bottom: 16px;
    display: flex;
    align-items: center;
    gap: 8px;
    color: #333;
}
.form-actions {
    display: flex;
    gap: 10px;
    margin-top: 10px;
}

/* 数据面板 */
.data-panel {
    padding: 20px;
    overflow-y: auto;
}
.panel-header {
    font-weight: bold;
    font-size: 16px;
    margin-bottom: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.rank-item {
    display: flex;
    align-items: center;
    padding: 10px 0;
    border-bottom: 1px solid #f5f5f5;
}
.rank-num {
    width: 24px;
    height: 24px;
    background: #eee;
    border-radius: 50%;
    text-align: center;
    line-height: 24px;
    font-size: 12px;
    margin-right: 12px;
    color: #666;
}
.rank-1 { background: #ffd700; color: #fff; }
.rank-2 { background: #c0c0c0; color: #fff; }
.rank-3 { background: #cd7f32; color: #fff; }

.rank-user {
    flex: 1;
    font-weight: 500;
}
.rank-score {
    color: #ff69b4;
    font-weight: bold;
}
.empty-tip {
    text-align: center;
    color: #999;
    padding: 20px;
}

/* 弹窗样式 */
.cover-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 180px;
}
.cover-uploader:hover {
  border-color: #ff69b4;
}
.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 确保 el-upload 的文件输入覆盖整个封面区域，避免只有左上角可点的问题 */
.cover-uploader .el-upload__input {
  position: absolute !important;
  top: 0;
  left: 0;
  width: 100% !important;
  height: 100% !important;
  opacity: 0;
  cursor: pointer;
}

/* 强制去除容器内边距并让占位符内容完全居中 */
.cover-uploader {
  padding: 0 !important;
  display: flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.cover-uploader .cover-placeholder {
  width: 100% !important;
  height: 100% !important;
  display: flex !important;
  flex-direction: column !important;
  align-items: center !important;
  justify-content: center !important;
  text-align: center !important;
  gap: 8px !important;
  padding: 0 !important;
  margin: 0 !important;
  box-sizing: border-box !important;
}

.cover-uploader .cover-placeholder .el-icon,
.cover-uploader .cover-placeholder svg,
.cover-uploader .cover-placeholder i {
  font-size: 36px !important;
  width: 36px !important;
  height: 36px !important;
  line-height: 36px !important;
}

.cover-uploader .cover-placeholder span,
.cover-uploader .cover-placeholder p {
  margin: 0 !important;
  padding: 0 !important;
}
.form-group {
  margin-bottom: 16px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
}

.page-title-live {
  font-size: 20px;
  font-weight: bold;
  color: #ff69b4;
  font-family: 'Microsoft YaHei', sans-serif;
}

/* 弹幕列表覆盖层 */
.danmaku-overlay-list {
    position: absolute;
    width: 240px;
    height: 300px;
    background: rgba(0, 0, 0, 0); /* 透明背景 */
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    z-index: 20;
    cursor: move;
    /* backdrop-filter: blur(4px);  移除模糊效果，避免遮挡 */
    border: none; /* 移除边框 */
    overflow: hidden;
}

.overlay-header {
    height: 32px;
    background: rgba(0, 0, 0, 0.1); /* 标题栏稍微有点背景以便拖拽 */
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 0 10px;
    color: #fff;
    font-size: 12px;
    user-select: none;
    border-radius: 8px 8px 0 0;
}

.overlay-content {
    flex: 1;
    overflow-y: auto;
    padding: 8px;
    display: flex;
    flex-direction: column;
    gap: 4px;
}

/* 隐藏滚动条但保留功能 */
.overlay-content::-webkit-scrollbar {
    width: 4px;
}
.overlay-content::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 2px;
}

.overlay-item {
    font-size: 13px;
    line-height: 1.4;
    color: #fff;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.8); /* 增强文字阴影，确保在透明背景下可见 */
    word-break: break-all;
    padding: 2px 4px;
    background: rgba(0,0,0,0.2); /* 给每条弹幕加一点点背景，增加可读性 */
    border-radius: 4px;
    margin-bottom: 2px;
}

/* 增强：礼物消息整体使用粉色，避免和背景重叠 */
.msg-type-GIFT .gift-content {
  color: #ff69b4;
  font-weight: 700;
}

.user-id {
  color: #ccc;
  margin-left: 6px;
  font-size: 12px;
}

/* 弹幕覆盖层的主播小徽章 */
.overlay-item .anchor-badge-mini {
  display: inline-block;
  margin-left: 6px;
  padding: 0 6px;
  background: linear-gradient(90deg, #ff69b4, #ff4d9a);
  color: #fff;
  border-radius: 6px;
  font-size: 11px;
  font-weight: 800;
  box-shadow: 0 3px 8px rgba(255,105,180,0.16);
}

/* 限制弹幕覆盖层高度，显示更多条目 */
.overlay-content {
  max-height: 320px;
}

.overlay-user {
    color: #ffd700;
    margin-right: 4px;
    font-weight: bold;
}

.fan-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  min-width: 20px;
  height: 16px;
  border-radius: 3px;
  background: linear-gradient(135deg, #ffe8b8, #ffd166);
  color: #8a4b00;
  font-weight: 700;
  font-size: 10px;
  border: 1px solid rgba(255, 209, 102, 0.5);
  margin: 0 4px;
  line-height: 1;
  vertical-align: middle;
}

.fan-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  min-width: 20px;
  height: 16px;
  border-radius: 3px;
  background: linear-gradient(135deg, #ffe8b8, #ffd166);
  color: #8a4b00;
  font-weight: 700;
  font-size: 10px;
  border: 1px solid rgba(255, 209, 102, 0.5);
  margin: 0 4px;
  line-height: 1;
  vertical-align: middle;
}

/* 当 overlay 项来自主播时，用粉色高亮整条 */
.overlay-item.overlay-anchor {
    background: linear-gradient(90deg, rgba(255,105,180,0.08), rgba(255,105,180,0.03));
    border: 1px solid rgba(255,105,180,0.18);
    box-shadow: 0 6px 18px rgba(255,105,180,0.06);
    color: #ffdbe9;
}
</style>
