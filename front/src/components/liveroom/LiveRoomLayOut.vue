<template>
  <div class="live-room-layout">
    <div class="left">
      <div class="left-top">
        <LiveHeader
          :host="host"
          :is-following="isFollowing"
          :is-joined="isJoined"
          @toggle-follow="toggleFollow"
          @toggle-join="toggleJoin"
          @go-circle="goCircle"
        />
      </div>

      <div class="left-mid">
        <LivePlayer ref="player" :flv-url="flvUrl" />
      </div>

      <div class="left-bottom">
        <GiftPanel
          :gifts="gifts"
          @send-gift="handleSendGift"
        />
      </div>
    </div>

    <div class="right">
      <!-- 排行榜 -->
            <div class="leaderboard-section">
                <div class="lb-tabs">
                    <span class="active">在线榜</span>
                </div>
                <div class="lb-list">
                    <div v-for="(item, idx) in currentLeaderboard" :key="idx" class="lb-item">
                        <div class="lb-rank" :class="'rank-'+(idx+1)">{{ idx+1 }}</div>
                        <img class="lb-avatar" :src="item.avatarUrl || '/assets/avatar.jpg'" alt="avatar" />
                        <div class="lb-name">
                            {{ item.username }}
                            <span v-if="item.fanLevel && item.fanLevel > 0" class="lb-fan">Lv{{ item.fanLevel }}</span>
                        </div>
                        <div class="lb-score">{{ item.totalAmount }}</div>
                    </div>
                     <div v-if="currentLeaderboard.length===0" class="lb-empty">暂无数据</div>
                </div>
            </div>

      <LiveChat
        ref="liveChat"
        :messages="messages"
        :current-user="currentUser"
        @send-message="handleSendMessage"
        @send-sc="handleSendSC"
      />
    </div>
  </div>
</template>

<script>
import LiveChat from "./LiveChat.vue";
import LivePlayer from "./LivePlayer.vue";
import GiftPanel from "./GiftPanel.vue";
import LiveHeader from "./LiveHeader.vue";
import { getLiveRoomInfo, getUserProfile } from "@/utils/api";
import { getAuthToken, getCurrentUserId } from "@/utils/auth";
import { ElMessage, ElMessageBox } from "element-plus";

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
  name: "LiveRoomLayout",
  components: {
    LiveChat,
    LivePlayer,
    GiftPanel,
    LiveHeader,
  },
  data() {
    return {
      roomId: null,
      flvUrl: "",
      stompClient: null,
      isConnected: false,
      
      currentUser: {
        id: 0,
        username: "游客",
        avatarColor: "#6c5ce7",
        fanLevel: 0,
        walletBalance: 0,
      },

      host: {
        id: 0,
        name: "加载中...",
        avatar: "",
        fans: 0,
        tags: [],
        intro: "",
        isLive: false,
      },

      isFollowing: false,
      isJoined: false,

      gifts: [
        { id: 1, name: "小花花", price: 1, img: "🌹" },
        { id: 2, name: "你真好看", price: 10, img: "😍" },
        { id: 3, name: "盛典门票", price: 1, img: "🎫" }, // 1电池
        { id: 4, name: "人气票", price: 1, img: "🔥" },
        { id: 5, name: "圣诞盲盒", price: 250, img: "🎁" },
        { id: 6, name: "星愿水晶球", price: 1000, img: "🔮" },
        { id: 7, name: "粉丝团灯牌", price: 1, img: "💡" },
        { id: 8, name: "大航海", price: 1980, img: "⚓", isSC: true }, // 模拟大额
      ],

    messages: [],
      nextMessageId: 1,
    // 页面卸载标记（用于区分刷新/关闭与路由离开）
    isPageUnloading: false,

      // 排行榜
      lbTab: 'online',
      leaderboardData: [], // 当前榜单数据
    };
  },
  computed: {
      currentLeaderboard() {
          // 暂时只用一个数据源演示，实际应根据 lbTab 请求不同接口
          return this.leaderboardData;
      }
  },
  async created() {
      this.roomId = this.$route.query.roomId;
      if (!this.roomId) {
          ElMessage.error("房间号不存在");
          return;
      }
      
      await this.loadCurrentUser();
      await this.loadRoomInfo();
      // 观众界面仅展示实时弹幕，不恢复历史，也不从 sessionStorage 恢复
      // 如果需要查看历史仅在主播端或管理端启用
      await this.initWebSocket();
      this.fetchLeaderboard();
  },
  mounted() {
      // 监听页面将要卸载（刷新/关闭）事件，标记为页面卸载（refresh/close）
      this._beforeUnloadHandler = () => {
          this.isPageUnloading = true;
      };
      window.addEventListener('beforeunload', this._beforeUnloadHandler);
  },
  beforeRouteLeave(to, from, next) {
      try {
          const inRoomKey = `vlive:room:${this.roomId}:inRoom`;
          const msgKey = `vlive:room:${this.roomId}:messages`;
          if (!this.isPageUnloading) {
              sessionStorage.removeItem(msgKey);
              sessionStorage.removeItem(inRoomKey);
          }
      } catch (e) { }
      next();
  },
  beforeUnmount() {
      // disconnect websocket
      if (this.stompClient) {
          this.stompClient.disconnect();
      }
      // 区分刷新/关闭与路由离开：如果不是页面卸载（即路由离开），清除本房间 sessionStorage 的消息和 inRoom 标记
      try {
          const inRoomKey = `vlive:room:${this.roomId}:inRoom`;
          const msgKey = `vlive:room:${this.roomId}:messages`;
          if (!this.isPageUnloading) {
              // 正常路由离开 -> 清理
              sessionStorage.removeItem(msgKey);
              sessionStorage.removeItem(inRoomKey);
          } else {
              // 页面刷新/关闭 -> 保留 sessionStorage（刷新时会恢复），但如果是关闭标签页，sessionStorage 会随标签清空
          }
      } catch (e) {
          console.warn('storage操作失败', e);
      }

      // 移除 beforeunload 监听器
      if (this._beforeUnloadHandler) {
          window.removeEventListener('beforeunload', this._beforeUnloadHandler);
          this._beforeUnloadHandler = null;
      }
  },
  methods: {
    restoreMessagesFromStorage() {
        try {
            const roomKey = this.$route.query.roomId;
            const inRoomKey = `vlive:room:${roomKey}:inRoom`;
            const key = `vlive:room:${roomKey}:messages`;

            const wasInRoom = sessionStorage.getItem(inRoomKey);
            if (wasInRoom) {
                // 说明是刷新 -> 恢复消息
                const raw = sessionStorage.getItem(key);
                if (raw) {
                    const arr = JSON.parse(raw);
                    if (Array.isArray(arr) && arr.length > 0) {
                        this.messages = arr;
                    }
                }
            } else {
                // 不是刷新（新的进入） -> 确保清空之前的消息并设置 inRoom 标记
                sessionStorage.removeItem(key);
                sessionStorage.setItem(inRoomKey, '1');
                this.messages = [];
            }
        } catch (e) {
            console.warn('恢复消息失败', e);
        }
    },
    async loadCurrentUser() {
        const uid = getCurrentUserId();
        if (uid) {
            try {
                const token = getAuthToken();
                // 1. Get Basic Profile
                const profile = await getUserProfile(uid);
                
                // 2. Get Wallet Balance
                let balance = 0;
                try {
                    const meRes = await fetch("/api/v1/live/user/me", {
                        headers: { "Authorization": "Bearer " + token }
                    });
                    const meJson = await meRes.json();
                    if (meJson.code === 0 || meJson.code === 200) {
                        balance = parseFloat(meJson.data.balance) || 0;
                    }
                } catch (e) {
                    console.error("Fetch wallet failed", e);
                }

                this.currentUser = {
                    id: uid,
                    username: profile.username,
                    avatarColor: "#ff69b4",
                    fanLevel: 0,
                    walletBalance: balance
                };
                            // 立即从后端获取此用户在该房间对应主播处的粉丝等级（如果已登录）
                            try {
                                const token = getAuthToken();
                                if (token) {
                                    const res = await fetch(`/api/v1/live/rooms/${this.roomId}/fan-level`, {
                                        headers: { "Authorization": "Bearer " + token }
                                    });
                                    const json = await res.json();
                                    if (json && (json.code === 0 || json.code === 200)) {
                                        this.currentUser.fanLevel = parseInt(json.data) || 0;
                                    }
                                }
                            } catch (e) {
                                console.warn('获取粉丝等级失败', e);
                            }
            } catch (e) {
                console.error(e);
            }
        }
    },
    async loadRoomInfo() {
        try {
            const info = await getLiveRoomInfo(this.roomId);
            // 获取主播详细信息（包括粉丝数）
            let fansCount = 0;
            try {
                const userRes = await fetch(`/api/v1/live/user/${info.creatorId}`);
                const userJson = await userRes.json();
                if (userJson.code === 0 || userJson.code === 200) {
                    fansCount = userJson.data.followers || 0;
                }
            } catch (e) {
                console.error("获取主播粉丝数失败", e);
            }

            // 检查是否关注
            if (this.currentUser.id) {
                this.checkFollowStatus(info.creatorId);
                this.checkCircleStatus(info.creatorId);
            }

            this.host = {
                id: info.creatorId || 0,
                name: info.creatorName || "主播",
                avatar: info.creatorAvatar || "",
                fans: fansCount,
                tags: [info.category || "直播"],
                intro: info.description || "暂无简介",
                isLive: info.live,
                title: info.title,
                circleId: null // 将在 checkCircleStatus 中更新
            };
            if (info.live && info.playUrlFlv) {
                this.flvUrl = info.playUrlFlv;
            }
        } catch (e) {
            ElMessage.error("获取直播间信息失败");
        }
    },
    async initWebSocket() {
        try {
            await Promise.all([
                loadScript("https://cdnjs.cloudflare.com/ajax/libs/sockjs-client/1.6.1/sockjs.min.js"),
                loadScript("https://cdnjs.cloudflare.com/ajax/libs/stomp.js/2.3.3/stomp.min.js")
            ]);
            
            const socket = new window.SockJS("http://localhost:8081/ws-live");
            this.stompClient = window.Stomp.over(socket);
            this.stompClient.debug = null; // disable debug logs
            
            this.stompClient.connect({}, (frame) => {
                console.log("Connected: " + frame);
                this.isConnected = true;
                
                // 订阅弹幕
                this.stompClient.subscribe(`/topic/danmaku/${this.roomId}`, (message) => {
                    const body = JSON.parse(message.body);
                    this.handleIncomingMessage(body);
                });

                // 订阅房间控制消息（例如：房主结束房间，通知客户端清理在线榜和弹幕）
                this.stompClient.subscribe(`/topic/room-control/${this.roomId}`, (message) => {
                    let body = null;
                    try { body = JSON.parse(message.body); } catch (e) { body = message.body; }
                    if (body && (body.action === 'ROOM_ENDED' || body.action === 'ROOM_STARTED')) {
                        // 无论是结束还是新开，观众端需要清空本场实时在线榜与本地弹幕
                        this.messages = [];
                        this.leaderboardData = [];
                        try {
                            sessionStorage.removeItem(`vlive:room:${this.roomId}:messages`);
                            sessionStorage.removeItem(`vlive:room:${this.roomId}:inRoom`);
                        } catch (e) { }
                        // 强制切换到在线榜并立即向后端拉取最新在线榜（通常应为空）以确保数据刷新
                        try {
                            this.lbTab = 'online';
                            this.fetchLeaderboard('ONLINE');
                        } catch (e) { }
                        try {
                            if (body.action === 'ROOM_ENDED') ElMessage.info('主播已结束直播，聊天与在线榜已清空');
                            else ElMessage.info('主播已开启新场次，在线榜与聊天已刷新');
                        } catch (e) { }
                    }
                });

                // 订阅错误消息
                if (this.currentUser.id) {
                    this.stompClient.subscribe(`/topic/errors/${this.currentUser.id}`, (message) => {
                        ElMessage.error(message.body);
                    });
                }
                
                // 连接成功后获取历史弹幕：仅在当前用户是主播时才获取历史（观众只看实时弹幕）
                if (this.currentUser && this.host && this.currentUser.id === this.host.id) {
                    this.fetchHistory();
                }
            }, (error) => {
                console.error("STOMP error", error);
                this.isConnected = false;
            });
        } catch (e) {
            console.error("WebSocket init failed", e);
        }
    },
    async fetchHistory() {
        // 观众端不应获取历史弹幕，直接返回
        if (!(this.currentUser && this.host && this.currentUser.id === this.host.id)) return;
        try {
            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/danmaku/history`);
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                const list = json.data || [];
                // 如果本地已有存储的消息（例如refresh场景），则不要覆盖它
                const storageKey = `vlive:room:${this.roomId}:messages`;
                const stored = (() => {
                    try { return JSON.parse(sessionStorage.getItem(storageKey) || 'null'); } catch (e) { return null }
                })();
                if (Array.isArray(stored) && stored.length > 0) {
                    // 仅主播端会使用 sessionStorage 恢复历史（观众不恢复）
                    this.messages = stored;
                    return;
                }

                list.forEach(msg => {
                    // 适配历史消息格式
                    const localMsg = {
                        id: msg.danmakuId || this.nextMessageId++,
                        username: msg.senderName || "匿名",
                        content: msg.content,
                        avatar: msg.senderAvatar || msg.senderAvatarUrl || "",
                        type: msg.type === 'CHAT' ? 'normal' : (msg.type === 'GIFT' ? 'gift' : 'sc'),
                        color: msg.color || '#fff',
                        fanLevel: msg.fanLevel || 0,
                        isAnchor: (this.host.id && msg.senderId == this.host.id) // 注意：历史消息可能没有 senderId，需要后端返回
                    };
                    this.messages.push(localMsg);
                    // 仅在主播端持久化历史消息到 sessionStorage（观众不持久化）
                    try {
                        if (this.currentUser && this.host && this.currentUser.id === this.host.id) {
                            const key = `vlive:room:${this.roomId}:messages`;
                            sessionStorage.setItem(key, JSON.stringify(this.messages));
                        }
                    } catch (e) { }
                });
            }
        } catch (e) {
            console.error("Fetch history failed", e);
        }
    },
    handleIncomingMessage(msg) {
        const localMsg = {
            id: msg.id || this.nextMessageId++,
            username: msg.senderName || msg.user,
            avatar: msg.senderAvatar || msg.avatar || '',
            content: msg.content,
            type: msg.type === 'CHAT' ? 'normal' : (msg.type === 'GIFT' ? 'gift' : 'sc'),
            color: msg.color || (msg.type === 'GIFT' ? '#ffd166' : '#fff'),
            fanLevel: (typeof msg.fanLevel !== 'undefined' && msg.fanLevel !== null)
                ? msg.fanLevel
                : (this.isFollowing && (msg.senderName || msg.user) === this.currentUser.username
                    ? (this.currentUser ? (this.currentUser.fanLevel || 0) : 0)
                    : 0),
            isAnchor: (msg.senderId && this.host.id && msg.senderId == this.host.id),
            userId: msg.senderId
        };
        // 如果这是当前用户自己的消息并且携带粉丝等级，更新本地 currentUser.fanLevel
        try {
            if (msg.senderId && this.currentUser && msg.senderId === this.currentUser.id && (msg.fanLevel || msg.fanLevel === 0)) {
                this.currentUser.fanLevel = msg.fanLevel;
            }
        } catch (e) { }
        
            if (msg.type === 'GIFT') {
                // 后端使用 giftCount 字段，避免使用不存在的 count 导致 "xundefined"
                localMsg.content = `送出了 ${msg.giftName} x${msg.giftCount || 1}`;
            }
        
        this.messages.push(localMsg);

        // 仅主播端将消息持久化到 sessionStorage；观众只显示实时消息
        try {
            if (this.currentUser && this.host && this.currentUser.id === this.host.id) {
                const key = `vlive:room:${this.roomId}:messages`;
                sessionStorage.setItem(key, JSON.stringify(this.messages));
            }
        } catch (e) { }

        if (this.$refs.player && this.$refs.player.shoot) {
            if (msg.type === 'CHAT' || msg.type === 'SC') {
                const shootColor = localMsg.isAnchor ? '#ff69b4' : (localMsg.color || '#fff');
                this.$refs.player.shoot(localMsg.content, shootColor);
            } else if (msg.type === 'GIFT') {
                const giftColor = localMsg.isAnchor ? '#ff69b4' : '#ff4081';
                this.$refs.player.shoot(`${localMsg.username} ${localMsg.content}`, giftColor);
            }
        }
        
        if (msg.type === 'GIFT' || msg.type === 'SC') {
            this.fetchLeaderboard();
        }

        // 如果是 SC，仅在主播（房主）端显示置顶区域；观众端不置顶
        if (localMsg.type === 'sc') {
            if (this.currentUser && this.host && this.currentUser.id === this.host.id) {
                if (this.$refs.liveChat && this.$refs.liveChat.addPinnedSC) {
                    this.$refs.liveChat.addPinnedSC(localMsg);
                }
            }
        }
    },
    async fetchLeaderboard(forcedType) {
        try {
            const token = getAuthToken();
            const headers = token ? { "Authorization": "Bearer " + token } : {};
            
            let type = 'SESSION';
            if (forcedType) {
                type = forcedType;
            } else {
                if (this.lbTab === 'day') type = 'DAY';
                else if (this.lbTab === 'week') type = 'WEEK';
                else if (this.lbTab === 'month') type = 'MONTH';
                else if (this.lbTab === 'online') type = 'ONLINE';
            }

            const res = await fetch(`/api/v1/live/rooms/${this.roomId}/stats/leaderboard?type=${type}`, { headers });
            if (res.ok) {
                const json = await res.json();
                if (json.code === 0 || json.code === 200) {
                    // 确保 avatarUrl 字段存在（后端应返回），前端可直接使用
                    this.leaderboardData = json.data || [];
                }
            }
        } catch (e) {
            console.error(e);
        }
    },
    async checkFollowStatus(targetId) {
        try {
            const token = getAuthToken();
            const res = await fetch(`/api/v1/follow/check?followerId=${this.currentUser.id}&followingId=${targetId}`, {
                headers: { "Authorization": "Bearer " + token }
            });
            const json = await res.json();
            if (json.code === 0 || json.code === 200) {
                this.isFollowing = json.data;
            }
        } catch (e) {
            console.error(e);
        }
    },
    async checkCircleStatus(creatorId) {
        try {
            const token = getAuthToken();
            // 1. 获取主播的圈子
            const res = await fetch(`/api/v1/circles/creator/${creatorId}`, {
                headers: { "Authorization": "Bearer " + token }
            });
            const json = await res.json();
            if ((json.code === 0 || json.code === 200) && json.data) {
                this.host.circleId = json.data.id;
                
                // 2. 检查是否加入
                const checkRes = await fetch(`/api/v1/circles/${json.data.id}/check-member?userId=${this.currentUser.id}`, {
                    headers: { "Authorization": "Bearer " + token }
                });
                const checkJson = await checkRes.json();
                if (checkJson.code === 0 || checkJson.code === 200) {
                    this.isJoined = checkJson.data;
                }
            }
        } catch (e) {
            console.error(e);
        }
    },
    async toggleFollow() {
      if (!this.currentUser.id) {
          ElMessage.warning("请先登录");
          return;
      }
      try {
          const token = getAuthToken();
          const res = await fetch(`/api/v1/follow/${this.host.id}`, {
              method: 'POST',
              headers: { 
                  "Authorization": "Bearer " + token,
                  "Content-Type": "application/json"
              },
              body: JSON.stringify({ userId: this.currentUser.id })
          });
          const json = await res.json();
          if (json.code === 0 || json.code === 200) {
              this.isFollowing = json.data.isFollowing;
              this.host.fans = json.data.followerCount;
              ElMessage.success(this.isFollowing ? "关注成功" : "已取消关注");
          }
      } catch (e) {
          ElMessage.error("操作失败");
      }
    },
    async toggleJoin() {
      if (!this.currentUser.id) {
          ElMessage.warning("请先登录");
          return;
      }
      if (!this.isFollowing) {
        ElMessage.warning('请先关注主播');
        return;
      }
      if (!this.host.circleId) {
          ElMessage.warning("主播尚未创建圈子");
          return;
      }
      
      try {
          const token = getAuthToken();
          const res = await fetch(`/api/v1/circles/${this.host.circleId}/join`, {
              method: 'POST',
              headers: { 
                  "Authorization": "Bearer " + token,
                  "Content-Type": "application/json"
              },
              body: JSON.stringify({ userId: this.currentUser.id })
          });
          const json = await res.json();
          if (json.code === 0 || json.code === 200) {
              this.isJoined = true;
              ElMessage.success("加入圈子成功");
          } else {
              ElMessage.error(json.message || "加入失败");
          }
      } catch (e) {
          ElMessage.error("操作失败");
      }
    },
    goCircle() {
       if (this.host.circleId) {
           this.$router.push({ path: '/com-detail', query: { id: this.host.circleId } });
       }
    },
    handleSendMessage(payload) {
        if (!this.isConnected) {
            ElMessage.error("未连接到直播间");
            return;
        }
        const token = getAuthToken();
        const headers = token ? { 'Authorization': 'Bearer ' + token } : {};
        
        const body = {
            type: "CHAT",
            roomId: this.roomId,
            content: payload.content,
            color: payload.color
        };
        this.stompClient.send("/app/send-danmaku", headers, JSON.stringify(body));
    },
    handleSendSC(payload) {
        if (!this.isConnected) {
            ElMessage.error("未连接到直播间");
            return;
        }
        const price = (payload.scAmount !== undefined && payload.scAmount !== null) ? payload.scAmount : payload.price;

        // 余额检查
        if (this.currentUser.walletBalance < price) {
            ElMessageBox.confirm(
                `余额不足 (当前: ¥${this.currentUser.walletBalance})，是否前往充值？`,
                '提示',
                {
                    confirmButtonText: '去充值',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                this.$router.push('/wallet'); // 假设充值页面路由
            }).catch(() => {});
            return;
        }

        const token = getAuthToken();
        const headers = token ? { 'Authorization': 'Bearer ' + token } : {};

        const body = {
            type: "SC",
            roomId: this.roomId,
            content: payload.content,
            giftPrice: price,
            giftCount: 1
        };
        this.stompClient.send("/app/send-danmaku", headers, JSON.stringify(body));

        // 乐观更新余额 (可选，或者等待后端推送余额更新)
        this.currentUser.walletBalance -= price;
    },
    handleSendGift(gift) {
        if (!this.isConnected) {
            ElMessage.error("未连接到直播间");
            return;
        }

        // 余额检查
        if (this.currentUser.walletBalance < gift.price) {
            ElMessageBox.confirm(
                `余额不足 (当前: ¥${this.currentUser.walletBalance})，是否前往充值？`,
                '提示',
                {
                    confirmButtonText: '去充值',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                this.$router.push('/wallet');
            }).catch(() => {});
            return;
        }

        const token = getAuthToken();
        const headers = token ? { 'Authorization': 'Bearer ' + token } : {};

        const body = {
            type: "GIFT",
            roomId: this.roomId,
            giftName: gift.name,
            giftCount: 1,
            giftPrice: gift.price
        };
        this.stompClient.send("/app/send-danmaku", headers, JSON.stringify(body));
        
        // 乐观更新余额
        this.currentUser.walletBalance -= gift.price;
    },
  },
};
</script>

<style scoped>
/* 整体布局：左 80% 右 20% */
.live-room-layout {
  display: flex;
  height: 100vh;
  background: #1a1a1a; /* 深色背景 */
  color: #eee;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

/* 左侧占 80% */
.left {
    flex: 1;
    display: flex;
    flex-direction: column;
    border-right: 1px solid #333;
}
.left-top {
    height: 80px;
    background: #222;
}
.left-mid {
    flex: 1;
    background: #000;
    position: relative;
}
.left-bottom {
    height: 100px; /* 礼物栏高度 */
    background: #222;
}

.right {
    width: 320px;
    display: flex;
    flex-direction: column;
    background: #222;
}

/* 排行榜样式 */
.leaderboard-section {
    height: 200px;
    border-bottom: 1px solid #333;
    display: flex;
    flex-direction: column;
}
.lb-tabs {
    display: flex;
    background: #2a2a2a;
}
.lb-tabs span {
    flex: 1;
    text-align: center;
    padding: 8px 0;
    font-size: 12px;
    cursor: pointer;
    color: #888;
}
.lb-tabs span.active {
    color: #ff69b4;
    border-bottom: 2px solid #ff69b4;
}
.lb-list {
    flex: 1;
    overflow-y: auto;
    padding: 10px;
}
.lb-item {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
    font-size: 13px;
}
.lb-avatar {
    width: 36px;
    height: 36px;
    border-radius: 50%;
    object-fit: cover;
    margin-right: 8px;
}
.lb-rank {
    width: 20px;
    height: 20px;
    text-align: center;
    line-height: 20px;
    margin-right: 8px;
    background: #444;
    border-radius: 4px;
    font-size: 12px;
}
.rank-1 { background: #ffd700; color: #000; }
.rank-2 { background: #c0c0c0; color: #000; }
.rank-3 { background: #cd7f32; color: #000; }

.lb-name {
    flex: 1;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    color: #ccc;
}
.lb-score {
    color: #ff69b4;
}
.lb-empty {
    text-align: center;
    color: #666;
    margin-top: 20px;
    font-size: 12px;
}
</style>
