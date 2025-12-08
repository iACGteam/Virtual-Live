

<template>
  <div class="post-card">
    <div class="post-header">
      <el-avatar :size="36" src="https://dummyimage.com/40x40/ccc/fff.png&text=A" />
      <div>
        <div class="author">{{ post.author }}</div>
        <div class="time">3 小时前</div>
      </div>
    </div>

    <div class="post-title">{{ post.title }}</div>

    <div class="post-footer">
      <div class="likes">
        <el-icon><Star /></el-icon>
        {{ post.likes }}
      </div>
      <div class="more" @click="goDetail()">阅读更多 ></div>
    </div>
  </div>
</template>

<script>
import { Star } from '@element-plus/icons-vue'

export default {
  props: {
    post: {
      type: Object,
      required: true
    }
  },
  methods: {
  goDetail() {
    this.$router.push(`detail`)
  }
}
}
</script>

<style scoped>
.post-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 18px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.15);
  cursor: pointer;
  transition: all 0.3s ease;
  color: #2d2d2d;
}
.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(255, 105, 180, 0.25);
  border-color: rgba(255, 105, 180, 0.4);
  background: rgba(255, 255, 255, 0.95);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author {
  font-size: 14px;
  font-weight: bold;
  color: #2d2d2d;
}

.time {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.6);
}

.post-title {
  margin: 12px 0;
  font-size: 18px;
  font-weight: bold;
  color: #2d2d2d;
}

.post-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.likes {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #ff69b4; 
}

.more {
  font-size: 14px;
  color: #ff69b4; 
  cursor: pointer;
  transition: color 0.2s;
}
.more:hover {
  text-decoration: underline;
  color: #9370db;
}


.el-avatar {
  border: 1px solid rgba(255, 105, 180, 0.3);
}
</style>

