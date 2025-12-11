<template>
    <div class="comments-section" ref="commentsRef">

        <div class="comment-input-box">
            <textarea v-model="newComment" placeholder="发表你的看法…"></textarea>
            <button :disabled="!canPostComment" @click="postComment">发表评论</button>
        </div>

        <div class="comments-header">
            <h3>评论</h3>
            <select v-model="sortOrder">
                <option value="time">按时间</option>
                <option value="hot">按热度</option>
            </select>
        </div>

        <ul class="comments-list">
            <li v-for="comment in sortedComments" :key="comment.id">
                <div class="comment-item">
                    <strong>{{ comment.user }}:</strong> {{ comment.content }}
                    <div class="comment-actions">
                        <span class="like-btn" :class="{ liked: comment.liked }" @click="toggleLike(comment)">
                            ❤️ {{ comment.likes }}
                        </span>
                        <span @click="toggleReplyBox(comment)">💬 回复</span>
                    </div>
                    <div v-if="replyingTo === comment.id" class="reply-box">
                        <textarea v-model="replyText" placeholder="回复内容…"></textarea>
                        <button @click="submitReply(comment)">发送</button>
                    </div>
                    <ul class="reply-list" v-if="comment.replies.length > 0">
                        <li v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                            <strong>{{ reply.user }}:</strong> {{ reply.content }}
                        </li>
                    </ul>
                </div>
            </li>
        </ul>
    </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 评论数据示例
const comments = ref([
  { id: 1, user: 'Alice', content: '太棒了！', time: '2025-11-23T17:00:00', hot: 10 },
  { id: 2, user: 'Bob', content: '非常喜欢！', time: '2025-11-23T17:05:00', hot: 15 },
  { id: 3, user: 'Charlie', content: '学习了', time: '2025-11-23T17:10:00', hot: 8 }
])

// 评论增强
comments.value = comments.value.map(c => ({
  ...c,
  likes: c.hot || 0,
  liked: false,
  replies: []
}))

// 选择排序方式
const sortOrder = ref('time')

const sortedComments = computed(() => {
  return [...comments.value].sort((a, b) => {
    if (sortOrder.value === 'time') {
      return new Date(b.time) - new Date(a.time)
    } else if (sortOrder.value === 'hot') {
      return b.hot - a.hot
    }
    return 0
  })
})

const totalComments = computed(() => {
  return comments.value.reduce((sum, c) => sum + 1 + (c.replies?.length || 0), 0)
})

// 点赞功能（视频或评论通用）
const toggleLike = (item) => {
  if (!item.liked) {
    item.likes++
    item.liked = true
  } else {
    item.likes--
    item.liked = false
  }
}

// 发布评论
const newComment = ref("")
const canPostComment = computed(() => newComment.value.trim().length > 0)
const replyingTo = ref(null)
const replyText = ref("")

const postComment = () => {
  if (!canPostComment.value) return

  comments.value.unshift({
    id: Date.now(),
    user: "You",
    content: newComment.value,
    time: new Date().toISOString(),
    likes: 0,
    replies: []
  })

  newComment.value = ""
  nextTick(() => updateSidebarHeight())
}

// 回复框开关
const toggleReplyBox = (comment) => {
  if (replyingTo.value === comment.id) {
    replyingTo.value = null
  } else {
    replyingTo.value = comment.id
    replyText.value = ""
  }
}

// 回复提交
const submitReply = (comment) => {
  if (!replyText.value.trim()) return

  comment.replies.push({
    id: Date.now(),
    user: "You",
    content: replyText.value,
    time: new Date().toISOString()
  })

  replyText.value = ""
  replyingTo.value = null
}

</script>

<style scoped>


/* ================== 评论区样式 ================== */

.comments-section {
  /* width: 95%; */
  /* max-width: 800px; */
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  padding: 20px;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.2);
  box-shadow: 0 4px 16px rgba(255, 105, 180, 0.15);
  margin-top: 12px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comments-header h3 {
  margin: 0;
  color: #2d2d2d;
}

.comments-header select {
  padding: 4px 8px;
  border-radius: 6px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  cursor: pointer;
}

/* 评论输入框 */
.comment-input-box {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
}

.comment-input-box textarea {
  flex: 1;
  height: 80px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.9);
}

.comment-input-box button {
  align-self: flex-end;
  margin-top: 8px;
  padding: 6px 12px;
  border-radius: 8px;
  background: #ff69b4;
  color: white;
  cursor: pointer;
  border: none;
}

.comment-input-box button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

/* 评论显示 */
.comments-list li {
  margin-bottom: 10px;
  padding: 6px 0;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  color: rgba(45, 45, 45, 0.8);
}

.comments-list li strong {
  color: black;
}

/* 评论动作 */
.comment-actions {
  display: flex;
  gap: 12px;
  margin: 4px 0 8px;
  color: gray;
}

.comment-actions span {
  cursor: pointer;
}

/* 回复框 */
.reply-box {
  margin: 8px 0 10px 20px;
  display: flex;
  flex-direction: column;
}

.reply-box textarea {
  width: 90%;
  height: 60px;
  padding: 8px;
  border-radius: 8px;
  border: 1px solid rgba(255, 105, 180, 0.3);
}

.reply-box button {
  align-self: flex-start;
  margin-top: 6px;
  padding: 4px 10px;
  border-radius: 6px;
  border: none;
  background: #ff69b4;
  color: white;
}

/* 回复列表 */
.reply-list {
  margin-left: 20px;
  margin-top: 8px;
  padding-left: 15px;
  border-left: 2px solid rgba(255, 105, 180, 0.3);
}

.reply-item {
  margin-bottom: 6px;
  color: rgba(45, 45, 45, 0.8);
}

/* 评论点赞按钮 */
.like-btn {
  cursor: pointer;
  transition: all 0.2s;
  color: rgba(45, 45, 45, 0.6);
}

.like-btn.liked {
  color: #ff4d88;
  transform: scale(1.2);
}

.like-btn:hover {
  color: #ff69b4;
}

</style>