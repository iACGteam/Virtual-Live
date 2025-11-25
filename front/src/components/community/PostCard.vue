

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
  background: #1c1c1c; /* 深色背景 */
  padding: 18px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
  cursor: pointer;
  transition: all 0.3s ease;
  color: #f0f0f0;
}
.post-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.5);
}

.post-header {
  display: flex;
  align-items: center;
  gap: 12px;
}

.author {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

.time {
  font-size: 12px;
  color: #aaa;
}

.post-title {
  margin: 12px 0;
  font-size: 18px;
  font-weight: bold;
  color: #e0e0e0;
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
  color: #ffd86b; 
}

.more {
  font-size: 14px;
  color: #66b1ff; 
  cursor: pointer;
  transition: color 0.2s;
}
.more:hover {
  text-decoration: underline;
  color: #409eff;
}


.el-avatar {
  border: 1px solid #444;
}
</style>

