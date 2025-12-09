<template>
  <div class="live-room-layout">
    <div class="left">
      <div class="left-top">
        <LiveHeader
          :host="host"
          :is-following="isFollowing"
          @toggle-follow="toggleFollow"
        />
      </div>

      <div class="left-mid">
        <LivePlayer />
      </div>

      <div class="left-bottom">
        <GiftPanel
          :gifts="gifts"
          @send-gift="handleSendGift"
        />
      </div>
    </div>

    <div class="right">
      <LiveChat
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
      // 当前用户示例
      currentUser: {
        id: 999,
        username: "我自己",
        avatarColor: "#6c5ce7",
      },

      // 主播信息（传给 LiveHeader）
      host: {
        id: 1,
        name: "主播小A",
        avatar: "", // 可以使用图片地址或留空使用头像占位
        fans: 12345,
        tags: ["娱乐", "唱见"],
        intro: "欢迎来到直播间！今晚有歌有礼物，陪聊不停~",
        isLive: true,
      },

      isFollowing: false,

      // 礼物样例（传给 GiftPanel）
      gifts: [
        { id: 1, name: "土豪金玫瑰", price: 66, img: "🌹" },
        { id: 2, name: "火箭", price: 520, img: "🚀" },
        { id: 3, name: "小心心", price: 10, img: "💖" },
        { id: 4, name: "超级SC", price: 999, img: "💎", isSC: true },
        { id: 5, name: "土豪金玫瑰", price: 66, img: "🌹" },
        { id: 6, name: "火箭", price: 520, img: "🚀" },
        { id: 7, name: "小心心", price: 10, img: "💖" },
        { id: 8, name: "土豪金玫瑰", price: 66, img: "🌹" },
        { id: 9, name: "火箭", price: 520, img: "🚀" },
        { id: 10, name: "小心心", price: 10, img: "💖" },
        { id: 11, name: "土豪金玫瑰", price: 66, img: "🌹" },
        { id: 12, name: "火箭", price: 520, img: "🚀" },
        { id: 13, name: "小心心", price: 10, img: "💖" },
      ],

      // 聊天消息数据（由父组件统一管理）
      messages: [
        {
          id: 1,
          username: "用户A",
          content: "大家好～",
          type: "normal",
          color: "#ffffff",
        },
        {
          id: 2,
          username: "土豪B",
          content: "送了个火箭！",
          type: "gift",
          color: "#ffd166",
        },
        {
          id: 3,
          username: "大佬SC",
          content: "支持一下，超级聊天！",
          type: "sc",
          color: "#ff7b7b",
          scAmount: 100,
        },
      ],
      nextMessageId: 4,
    };
  },
  methods: {
    toggleFollow() {
      this.isFollowing = !this.isFollowing;
      // 模拟粉丝数变化
      if (this.isFollowing) this.host.fans += 1;
      else this.host.fans -= 1;
    },
    handleSendMessage(payload) {
      // payload: { username, content, color, type? }
      const msg = {
        id: this.nextMessageId++,
        username: payload.username,
        content: payload.content,
        type: payload.type || "normal",
        color: payload.color || "#fff",
      };
      this.messages.push(msg);
    },
    handleSendSC(payload) {
      // payload: { username, content, color, scAmount }
      const msg = {
        id: this.nextMessageId++,
        username: payload.username,
        content: payload.content,
        type: "sc",
        color: payload.color || "#ffd1d1",
        scAmount: payload.scAmount || 0,
      };
      this.messages.push(msg);
    },
    handleSendGift(gift) {
      // gift: {id, name, price, img, isSC}
      const msg = {
        id: this.nextMessageId++,
        username: this.currentUser.username,
        content: `${this.currentUser.username} 送出 ${gift.name} ×1`,
        type: gift.isSC ? "sc" : "gift",
        color: gift.isSC ? "#ffb86b" : "#ffd166",
      };
      this.messages.push(msg);
    },
  },
};
</script>

<style scoped>
/* 整体布局：左 80% 右 20% */
.live-room-layout {
  display: flex;
  height: 100vh;
  background: #ffecf5; /* 主体浅灰白背景 */
  color: #333;
  font-family: "Helvetica Neue", Arial, sans-serif;
}

/* 左侧占 80% */
.left {
  width: 75%;
  display: flex;
  flex-direction: column;
  padding: 18px;
  gap: 12px;
  box-sizing: border-box;
  background: #fefbff;
  border-right: 1px solid #e6e6e6;
}

/* 右侧占 20% */
.right {
  width: 25%;
  padding: 18px;
  box-sizing: border-box;
  background: #fefbff;
  border-left: 1px solid #e5e5e5;
  overflow: hidden;
}

/* 左侧三部分高度分配：15% / 70% / 15% */
.left-top {
  height: 15%;
  min-height: 72px;
}

.left-mid {
  height: 70%;
  min-height: 420px;
  background: #f0f2f5; /* 原视频区改成浅背景 */
  border-radius: 12px;
  border: 1px solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.left-bottom {
  height: 15%;
  min-height: 72px;
  background: #ffffff;
}

/* 小屏幕保护 */
@media (max-width: 900px) {
  .live-room-layout {
    flex-direction: column;
  }
  .left,
  .right {
    width: 100%;
  }
  .right {
    height: 320px;
  }
}

</style>
