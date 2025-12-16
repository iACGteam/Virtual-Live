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
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import { getCurrentUserId } from '@/utils/auth'
import { getComments, addComment, replyComment, deleteComment as apiDeleteComment, likeComment } from '@/utils/api'

const route = useRoute()
const videoId = computed(() => parseInt(route.query.id || 0))

// 后端评论列表（分页）
const comments = ref([])
const page = ref(0)
const size = ref(20)
const sortOrder = ref('time') // 后端支持 time/hot

const totalComments = computed(() => {
  return comments.value.reduce((sum, c) => sum + 1 + (c.replies?.length || 0), 0)
})

// 加载评论
async function loadComments() {
  if (!videoId.value) return
  const pageData = await getComments(videoId.value, page.value, size.value, sortOrder.value)
  const list = Array.isArray(pageData?.content) ? pageData.content : []
  // 统一前端需要的结构
  comments.value = list.map(c => ({
    id: c.id,
    user: c.authorName || `用户${c.authorId}`,
    content: c.content,
    time: c.createdAt,
    likes: c.likes || 0,
    liked: false,
    replies: []
  }))
}

onMounted(async () => {
  await loadComments()
})

// 点赞评论
async function toggleLike(item) {
  try {
    const uid = getCurrentUserId()
    if (!uid) throw new Error('未登录')
    await likeComment(item.id)
    item.liked = !item.liked
    item.likes = Math.max(0, (item.likes || 0) + (item.liked ? 1 : -1))
  } catch (e) {
    console.warn('点赞失败', e)
  }
}

// 新增评论
const newComment = ref("")
const canPostComment = computed(() => newComment.value.trim().length > 0)
async function postComment() {
  if (!canPostComment.value) return
  try {
    const uid = getCurrentUserId()
    if (!uid || !videoId.value) throw new Error('未登录或视频ID缺失')
    await addComment(videoId.value, uid, newComment.value.trim())
    newComment.value = ''
    await loadComments()
    nextTick(() => updateSidebarHeight())
  } catch (e) {
    console.warn('发表评论失败', e)
  }
}

// 回复框开关
const replyingTo = ref(null)
const replyText = ref("")
function toggleReplyBox(comment) {
  if (replyingTo.value === comment.id) {
    replyingTo.value = null
  } else {
    replyingTo.value = comment.id
    replyText.value = ''
  }
}

// 提交回复
async function submitReply(comment) {
  if (!replyText.value.trim()) return
  try {
    const uid = getCurrentUserId()
    if (!uid || !videoId.value) throw new Error('未登录或视频ID缺失')
    await replyComment(comment.id, videoId.value, uid, replyText.value.trim())
    replyText.value = ''
    replyingTo.value = null
    await loadComments()
  } catch (e) {
    console.warn('回复失败', e)
  }
}

// 删除评论
async function deleteComment(comment) {
  try {
    const uid = getCurrentUserId()
    if (!uid) throw new Error('未登录')
    await apiDeleteComment(comment.id, uid)
    await loadComments()
  } catch (e) {
    console.warn('删除失败', e)
  }
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