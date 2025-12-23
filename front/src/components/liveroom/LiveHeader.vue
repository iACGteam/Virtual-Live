<template>
  <div class="live-header">
    <!-- 左侧：头像 + 基础信息 -->
    <div class="left-info">
      <div class="avatar" :style="{ backgroundImage: host.avatar ? 'url(' + host.avatar + ')' : '' }">
        <div v-if="!host.avatar" class="initial">{{ hostInitial }}</div>
      </div>

      <div class="meta">
        <div class="room-title" :title="host.title">{{ host.title || '直播间' }}</div>
        <div class="host-info">
            <span class="host-name">{{ host.name }}</span>
            <span class="status" :class="{ live: host.isLive }">
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
      <button class="follow-btn" :class="{ active: isFollowing }" @click="$emit('toggle-follow')">
        {{ isFollowing ? "已关注" : "关注" }}
      </button>
      <button v-if="host.circleId" class="circle-btn" @click="onJoinClick">
        {{ isJoined ? "进入圈子" : "加入圈子" }}
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
    isJoined: {
      type: Boolean,
      default: false,
    },
  },
  methods: {
    onJoinClick() {
      if (this.isJoined) {
        this.$emit('go-circle');
      } else {
        this.$emit('toggle-join');
      }
    }
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
  padding: 0 20px;
  background: #222;
  color: #fff;
}

/* 左侧区域 */
.left-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.avatar {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: #444;
  background-size: cover;
  background-position: center;
  position: relative;
  border: 2px solid #333;
  flex-shrink: 0;
}

.initial {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #aaa;
  font-weight: bold;
}

.meta {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
}

.room-title {
    font-size: 18px;
    font-weight: bold;
    color: #fff;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
    max-width: 400px;
}

.host-info {
    display: flex;
    align-items: center;
    gap: 10px;
    font-size: 13px;
    color: #aaa;
}

.host-name {
    color: #ddd;
    font-weight: 500;
}

.status {
    padding: 1px 6px;
    border-radius: 4px;
    background: #444;
    font-size: 12px;
}
.status.live {
    background: #ff4081;
    color: #fff;
}

.tags {
    display: flex;
    gap: 6px;
}
.tag {
    font-size: 12px;
    background: #333;
    padding: 2px 6px;
    border-radius: 4px;
    color: #888;
}

/* 右侧按钮 */
.right-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.follow-btn, .circle-btn {
  padding: 6px 16px;
  border-radius: 20px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s;
}

.follow-btn {
    background: #ff4081;
    color: #fff;
}
.follow-btn.active {
    background: #444;
    color: #aaa;
}

.circle-btn {
    background: #333;
    color: #ddd;
    border: 1px solid #555;
}
.circle-btn:hover {
    background: #444;
}
</style>
