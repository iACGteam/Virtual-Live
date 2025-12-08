<template>
  <div class="live-chat" ref="chatContainer" @mousemove="onMouseMove">
    <!-- 顶部悬浮触发区与排行榜 -->
    <div class="chat-top" ref="chatTop">
      <div class="room-title">
        聊天区
        <span class="online">在线 {{ fakeOnline }}</span>
      </div>

      <!-- 礼物榜 -->
      <div
        class="gift-leaderboard"
        v-show="showLeaderboard"
        @mouseenter="keepLeaderboard = true"
        @mouseleave="keepLeaderboard = false"
      >
        <div class="lb-title">送礼排行榜</div>
        <ol>
          <li v-for="(u, idx) in leaderboard" :key="u.username">
            <span class="rank">{{ idx + 1 }}</span>
            <span class="name">{{ u.username }}</span>
            <span class="amount">{{ u.amount }} 礼物值</span>
          </li>
        </ol>
      </div>
    </div>

    <!-- 固定显示的 SC 弹幕区域（持久 10 秒） -->
    <div class="sc-pinned" v-if="scPinned.length > 0">
      <div
        class="sc-item"
        v-for="p in scPinned"
        :key="p.id"
        :style="bubbleStyle(p)"
      >
        <div class="meta">
          <span class="username">{{ p.username }}</span>
          <span class="sc-badge">SC</span>
        </div>
        <div class="content">{{ p.content }}</div>
        <div class="sc-amount">¥{{ p.scAmount }}</div>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="messages" ref="messagesList">
      <div
        v-for="msg in messages"
        :key="msg.id"
        class="msg"
        :class="msg.type"
      >
        <div class="avatar" :style="{ background: avatarColor(msg.username) }">
          {{ avatarInitial(msg.username) }}
        </div>

        <div class="bubble" :style="bubbleStyle(msg)">
          <div class="meta">
            <span class="username">{{ msg.username }}</span>

            <span v-if="msg.type === 'sc'" class="sc-badge">SC</span>
            <span v-if="msg.type === 'gift'" class="gift-badge">🎁</span>
          </div>

          <div class="content">{{ msg.content }}</div>

          <div v-if="msg.type === 'sc' && msg.scAmount" class="sc-amount">
            ¥{{ msg.scAmount }}
          </div>
        </div>
      </div>
    </div>

    <!-- SC 勾选开关 -->
    <div class="sc-toggle">
      <input type="checkbox" v-model="sendAsSC" id="scCheck" />
      <label for="scCheck">花费50发送SC弹幕</label>
    </div>

    <!-- 发送区 -->
    <div class="send-box">
      <input
        v-model="input"
        @keydown.enter.prevent="onSend"
        placeholder="按 Enter 发送"
      />
      <button @click="onSend">发送</button>
    </div>
  </div>
</template>

<script>
export default {
  name: "LiveChat",
  props: {
    messages: Array,
    currentUser: Object,
  },
  emits: ["send-message", "send-sc"],
  data() {
    return {
      input: "",
      sendAsSC: false, // ✔ 新增：勾选 SC 弹幕
      scPinned: [], // ✔ 固定显示的 SC 弹幕
      fakeOnline: 8523,

      showLeaderboard: false,
      keepLeaderboard: false,
      leaderboard: [
        { username: "土豪B", amount: 5200 },
        { username: "大佬SC", amount: 3000 },
        { username: "小萌新", amount: 900 },
      ],
    };
  },

  mounted() {
    this.scrollToBottom();
  },
  watch: {
    messages() {
      this.$nextTick(() => this.scrollToBottom());
    },
    keepLeaderboard(v) {
      if (v) this.showLeaderboard = true;
    },
  },

  methods: {
    /** 发送消息 */
    onSend() {
      const text = this.input.trim();
      if (!text) return;

      // --------------- SC 模式发送 ------------------
      if (this.sendAsSC) {
        const amount = 50; // 可做成表单，这里写一个默认值
        const msg = {
          username: this.currentUser.username,
          content: text,
          type: "sc",
          color: "#ff9f9f",
          scAmount: amount,
          id: Date.now(),
        };

        // 发送给父组件
        this.$emit("send-sc", msg);

        // ✔ 添加到固定展示区
        this.addPinnedSC(msg);

        this.input = "";
        return;
      }

      // --------------- 普通弹幕 ------------------
      this.$emit("send-message", {
        username: this.currentUser.username,
        content: text,
        type: "text",
        color: "#fff",
      });

      this.input = "";
    },

    /** 固定 SC 弹幕展示 10 秒 */
    addPinnedSC(msg) {
      this.scPinned.push(msg);

      setTimeout(() => {
        this.scPinned = this.scPinned.filter((m) => m.id !== msg.id);
      }, 10000);
    },

    avatarInitial(name) {
      return name ? name.slice(0, 1).toUpperCase() : "?";
    },
    avatarColor(name) {
      const str = name || "u";
      let h = 0;
      for (let i = 0; i < str.length; i++) h = (h << 5) - h + str.charCodeAt(i);
      return `hsl(${Math.abs(h) % 360} 70% 55%)`;
    },

    bubbleStyle(msg) {
      if (msg.type === "sc") {
        return {
          background:
            "linear-gradient(90deg, rgba(255,123,123,0.12), rgba(255,187,123,0.08))",
          border: "1px solid rgba(255,123,123,0.25)",
        };
      }
      if (msg.type === "gift") {
        return {
          background: "rgba(255,209,102,0.06)",
          border: "1px solid rgba(255,209,102,0.14)",
        };
      }
      return {
        background: "rgba(255,255,255,0.03)",
        border: "1px solid rgba(255,255,255,0.03)",
      };
    },

    scrollToBottom() {
      const el = this.$refs.messagesList;
      if (el) el.scrollTop = el.scrollHeight;
    },

    onMouseMove(e) {
      const rect = this.$refs.chatContainer.getBoundingClientRect();
      const y = e.clientY - rect.top;
      if (y <= 60) {
        this.showLeaderboard = true;
      } else if (!this.keepLeaderboard) {
        this.showLeaderboard = false;
      }
    },
  },
};
</script>

<style scoped>
.live-chat {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fcf5ff;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  border: 1px solid #e5e7eb;
}

/* 固定 SC 显示区 */
.sc-pinned {
  padding: 8px 12px;
  border-bottom: 1px solid #e5e7eb;
  background: #fff9f9;
}
.sc-item {
  padding: 8px 10px;
  margin-bottom: 6px;
  border-radius: 10px;
  font-size: 14px;
  border: 1px solid rgba(255, 123, 123, 0.25);
}
.sc-item .username {
  font-weight: 700;
  color: #222;
}
.sc-item .sc-amount {
  margin-top: 4px;
  color: #e55;
  font-weight: bold;
}

.room-title {
  font-weight: 600;
  color: #222;
}

/* 礼物榜 */
.gift-leaderboard {
  position: absolute;
  left: 12px;
  top: 56px;
  width: 220px;
  background: white;
  border-radius: 10px;
  padding: 10px;
  border: 1px solid #e5e7eb;
  box-shadow: 0 8px 20px rgba(0,0,0,0.06);
  z-index: 20;
}


/* 单条消息 */
.msg {
  display: flex;
  gap: 8px;
}

/* SC 勾选按钮区域 */
.sc-toggle {
  padding: 6px 12px;
  background: #fefbff;
  border-top: 1px solid #e5e7eb;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 6px;
}


.send-box input {
  flex: 1;
  height: 40px;
  border-radius: 8px;
  border: 1px solid #d1d5db;
  padding: 8px 12px;
}

.live-chat {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #fbf5ff;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  border: 1px solid #e5e7eb;
}

/* 顶部区域 */
.chat-top {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 8px 12px;
  position: relative;
  user-select: none;
  border-bottom: 1px solid #e5e7eb;
  background: #fefbff;
}

.room-title {
  font-weight: 600;
  color: #222;
  letter-spacing: 0.4px;
}
.room-title .online {
  margin-left: 10px;
  font-size: 12px;
  color: #888;
}

/* 排行榜浮层 */
.gift-leaderboard {
  position: absolute;
  left: 12px;
  top: 56px;
  width: 220px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 10px;
  padding: 10px;
  box-shadow: 0 8px 20px rgba(0,0,0,0.06);
  z-index: 30;
  transition: transform 0.18s ease, opacity 0.18s ease;
}

.lb-title {
  font-size: 13px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #222;
}

.gift-leaderboard ol {
  padding-left: 8px;
  margin: 0;
}

.gift-leaderboard li {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 2px;
  font-size: 13px;
  color: #333;
}
.gift-leaderboard .rank {
  width: 28px;
  text-align: center;
  font-weight: 700;
  color: #444;
}
.gift-leaderboard .name {
  flex: 1;
  margin-left: 6px;
  color: #333;
}
.gift-leaderboard .amount {
  font-size: 12px;
  color: #777;
}

/* 消息列表区 */
.messages {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  background: #fefbff;
}

/* 单条消息 */
.msg {
  display: flex;
  gap: 8px;
  align-items: flex-start;
}

/* Avatar */
.avatar {
  min-width: 36px;
  min-height: 36px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  color: #fff;
  font-size: 14px;
  box-shadow: 0 2px 4px rgba(0,0,0,0.08);
}

/* 气泡 */
.bubble {
  padding: 8px 10px;
  border-radius: 10px;
  max-width: 100%;
  color: #222;
  font-size: 14px;
  line-height: 1.25;
  border: 1px solid #e5e7eb;
  background: #f7f7f7;
  box-shadow: 0 3px 10px rgba(0,0,0,0.05);
  position: relative;
}

/* 元信息行 */
.meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.meta .username {
  font-weight: 700;
  color: #222;
}

/* 徽章 */
.sc-badge,
.gift-badge {
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 999px;
  background: #e5e7eb;
  color: #444;
  font-weight: 700;
}

/* SC 动画 */
.msg.sc .bubble {
  border-radius: 12px;
  animation: pop 0.22s ease;
}
@keyframes pop {
  from {
    transform: scale(0.98);
    opacity: 0.85;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}

/* 发送框 */
.send-box {
  height: 56px;
  display: flex;
  gap: 8px;
  align-items: center;
  padding: 8px;
  border-top: 1px solid #e5e7eb;
  background: #fefbff;
}

.send-box input {
  flex: 1;
  height: 40px;
  border-radius: 8px;
  padding: 8px 12px;
  background: #ffffff;
  border: 1px solid #d1d5db;
  color: #222;
  outline: none;
}
.send-box input::placeholder {
  color: #999;
}

.send-box button {
  height: 40px;
  padding: 0 14px;
  border-radius: 8px;
  background: #4f46e5;
  border: none;
  color: #fff;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.15s ease;
}
.send-box button:hover {
  background: #4338ca;
}

</style>
