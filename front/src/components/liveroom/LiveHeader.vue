<template>
  <div class="live-header">
    <!-- 左侧：头像 + 基础信息 -->
    <div class="left-info">
      <div class="avatar" :style="{ backgroundImage: host.avatar ? 'url(' + host.avatar + ')' : '' }">
        <div v-if="!host.avatar" class="initial">{{ hostInitial }}</div>
      </div>

      <div class="meta">
        <div class="room-name">{{ host.name }}</div>

        <div class="sub">
          <span class="status" :class="{ live: host.isLive }">
            <span class="dot"></span>
            {{ host.isLive ? "直播中" : "未开播" }}
          </span>

          <span class="fans">{{ hostFansFormatted }} 粉丝</span>
        </div>

        <div class="tags" v-if="host.tags?.length">
          <span class="tag" v-for="tag in host.tags" :key="tag">{{ tag }}</span>
        </div>
      </div>
    </div>

    <!-- 右侧按钮 -->
    <div class="right-actions">
      <button class="follow-btn" @click="$emit('toggle-follow')">
        {{ isFollowing ? "已关注" : "关注" }}
      </button>
    </div>
  </div>
</template>


<script>
export default {
  name: "LiveHeader",
  props: {
    host: {
      type: Object,
      required: true,
    },
    isFollowing: {
      type: Boolean,
      default: false,
    },
  },
  computed: {
    hostInitial() {
      return this.host && this.host.name ? this.host.name.slice(0, 1).toUpperCase() : "A";
    },
    hostFansFormatted() {
      if (!this.host || !this.host.fans) return "0";
      const n = this.host.fans;
      if (n >= 10000) return (n / 10000).toFixed(1) + "万";
      return n.toString();
    },
  },
};
</script>

<style scoped>
.live-header {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 16px;
  background: #fcf5ff;
  backdrop-filter: blur(8px);
  border-radius: 12px;
  box-sizing: border-box;
    border: 1px, solid, black;
}

/* 左侧区域 */
.left-info {
  display: flex;
  align-items: center;
  gap: 12px;

}

/* 头像 */
.avatar {
  width: 48px;
  height: 48px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: #444;
}

.initial {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

/* 文本信息 */
.meta {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.room-name {
  font-size: 16px;
  font-weight: 700;
}

.sub {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
}

.status {
  display: flex;
  align-items: center;
  gap: 4px;
  opacity: 0.9;
}

.status .dot {
  width: 8px;
  height: 8px;
  background: gray;
  border-radius: 50%;
}

/* 开播状态 */
.status.live .dot {
  background: #ff4d4f;
}

/* 标签 */
.tags {
  display: flex;
  gap: 6px;
  margin-top: 2px;
}

.tag {
  padding: 2px 6px;
  background: rgba(255,255,255,0.1);
  border-radius: 6px;
  font-size: 12px;
  color: black;
}

/* 右侧关注按钮 */
.right-actions .follow-btn {
  padding: 6px 14px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  font-weight: 700;
  color: #fff;
  background: linear-gradient(90deg, #8b5cf6, #06b6d4);
}
</style>

