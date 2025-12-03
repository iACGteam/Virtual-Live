<template>
  <div class="circle-detail">
    <div class="header-right">
      <button class="back-btn" @click="goBack">
        <span class="back-icon">←</span>
        <span>返回</span>
      </button>
    </div>

    <header>
      <div class="touxiang">
        <img src="@/assets/avatar.jpg" class="avatar" />
      </div>
      <div class="info">
        <h2 class="name">{{ com.name }}</h2>
        <div class="number">
          <p class="count">{{ com.count }} 粉丝</p>
          <p class="count">{{ com.count }} 作品量</p>
          <p class="count">已经连续签到{{com.days}}天</p>
        </div>

        <p class="intro">介绍</p>

      </div>

      <div class="actions">
        <el-button class="join-btn" :type="joined ? 'success' : ''" @click="toggleJoin">
          <span v-if="joined">已加入 ✓</span>
          <span v-else>加入</span>
        </el-button>

        <el-button
          class="sign-btn"
          :class="{ signed: signed }
          "
          :disabled="!joined || signed"
          @click="handleSign"
        >
          <template v-if="!joined">加入并签到</template>
          <template v-else-if="!signed">点击签到</template>
          <template v-else>今日已签到 ✔</template>
        </el-button>

      </div>
    </header>

    <transition name="fade-slide">
      <div v-if="toast.show" class="toast">{{ toast.msg }}</div>
    </transition>

    <div class="nav-tabs">
      <div v-for="item in navList" :key="item" :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="activeTab = item">
        {{ item }}
      </div>
    </div>

    <div class="content-list">
      <div v-for="card in filteredCards" :key="card.id" class="card" :class="{ highlight: card.highlight }">

        <!-- 卡片内容 -->
        <div class="card-header">
          <div class="card-left">
            <p class="title">{{ card.title }}</p>
            <p class="excerpt">{{ card.excerpt }}</p>
          </div>
          <img :src="card.avatar" class="card-avatar" />
        </div>

        <!-- 底部按钮 -->
        <footer class="card-footer">
          <div class="comments" @click="toggleComments(card.id)">
            💬 查看 {{ card.comments.length }} 条评论
          </div>
          <div class="reply" @click="startReply(card.id, null)">↩ 回复</div>
          <div class="like" @click="toggleLike(card)">
            <span :class="['heart', card.liked ? 'liked' : '']">❤</span>
            <span class="likes-num">{{ card.likes }}</span>
          </div>
        </footer>

        <!-- 评论区（可展开） -->
        <transition name="expand">
          <div class="comment-box" v-if="card.showComments">
            <!-- 发布评论 -->
            <div class="comment-input">
              <input
                type="text"
                v-model="card.newComment"
                :placeholder="card.replyTo ? '回复：' + card.replyTo : '发表你的评论...'"
                @keyup.enter="submitComment(card.id)"
              />
              <button @click="submitComment(card.id)">发表</button>
            </div>


            <div class="comments-list">
              <div class="comment-item" v-for="(c, index) in card.comments" :key="index">

                <img :src="c.avatar" class="comment-avatar" />

                <div class="comment-main">
                  <div class="comment-user">{{ c.user }}</div>
                  <div class="comment-content">{{ c.text }}</div>

                  <div class="comment-actions">
                    <span class="comment-time">{{ c.time }}</span>
                    <span class="reply-btn" @click="startReply(card.id, c.user)">回复</span>
                  </div>
                </div>

              </div>
            </div>
          </div>
        </transition>

      </div>
    </div>

  </div>
</template>

<script>
export default {
  data() {
    return {
      com: {
        id: 1,
        name: "官方社团",
        count: 23000,
        days: 5
      },
      cards: [
        {
          id: 1,
          title: "标题1",
          excerpt: '这是第一条帖子的摘要，支持多行显示，简洁好看。',
          avatar: require('@/assets/avatar.jpg'),
          likes: 0,
          liked: false,
          showComments: false,
          newComment: "",
          replyTo: null,
          highlight: false,
          comments: [
            {
              user: "用户A",
              text: "写得不错！",
              avatar: require('@/assets/avatar.jpg'),
              time: "2小时前"
            },
            {
              user: "用户B",
              text: "很有帮助！",
              avatar: require('@/assets/avatar.jpg'),
              time: "1天前"
            }
          ]
        },
        {
          id: 2,
          title: "标题2",
          excerpt: '第二条内容的简短描述，配合卡片布局看起来更整齐。',
          avatar: require('@/assets/avatar.jpg'),
          likes: 3,
          liked: true,
          showComments: false,
          newComment: "",
          replyTo: null,
          highlight: false,
          comments: []
        }
      ],
      joined: false,
      signed: false,
      toast: {
        show: false,
        msg: ''
      },
      navList: ['最新', '最热'],
      activeTab: '最新'
    }
  },
  methods: {
    toggleJoin() {
      this.joined = !this.joined;
      if (this.joined && !this.signed) {
        // Optionally keep signed false until user explicitly signs
      }
    },
    handleSign() {
      if (!this.joined) {
        // 自动加入并签到
        this.joined = true;
      }

      if (this.signed) {
        this.showToast('你今天已经签到过了');
        return;
      }

      // 执行签到逻辑
      this.signed = true;
      this.com.days += 1;
      // 给第一个卡片一个简短高亮反馈
      this.cards[0].highlight = true;
      setTimeout(() => (this.cards[0].highlight = false), 800);

      this.showToast('签到成功！连续签到+' + 1);
    },
    showToast(msg = '', ms = 1200) {
      this.toast.msg = msg;
      this.toast.show = true;
      setTimeout(() => (this.toast.show = false), ms);
    },
    toggleComments(id) {
      this.cards = this.cards.map(card =>
        card.id === id ? { ...card, showComments: !card.showComments } : card
      );
    },
    toggleLike(card) {
      // 小动画：先切换 liked，再调整数字
      card.liked = !card.liked;
      card.likes += card.liked ? 1 : -1;
      // 触发 highlight 动画
      card.highlight = true;
      setTimeout(() => (card.highlight = false), 400);
    },
    startReply(cardId, user) {
      const card = this.cards.find(c => c.id === cardId);
      card.replyTo = user; // null 表示不是回复
      card.showComments = true; // 自动展开评论区
      // 将焦点放到输入框（下一次可以用 $nextTick 获取元素并 focus）
    },
    submitComment(cardId) {
      const card = this.cards.find(c => c.id === cardId);
      if (!card.newComment || !card.newComment.trim()) return;

      const text = card.replyTo ? `回复 ${card.replyTo}：${card.newComment}` : card.newComment;

      card.comments.push({
        user: "我",
        text,
        avatar: require('@/assets/avatar.jpg'),
        time: '刚刚'
      });

      // 清空输入
      card.newComment = "";
      card.replyTo = null;

      this.showToast('评论已发布', 900);
    },
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1);
      } else {
        this.$router.push({ path: '/' });
      }
    }
  },
  computed: {
    filteredCards() {
      // 返回新的数组副本，避免 sort 改变原数组顺序
      const list = this.cards.slice();
      if (this.activeTab === '最新') {
        // 假设卡片没有时间字段，用 id 倒序代表最新
        return list.sort((a, b) => b.id - a.id);
      }
      if (this.activeTab === '最热') {
        return list.sort((a, b) => b.likes - a.likes);
      }
      return list;
    }
  }
}
</script>

<style scoped>
/* 整体页面背景保持你原有的设置（白底），主题用淡粉色强调 */
.circle-detail {
  background: #ffffff; /* 保持白色背景 */
  padding: 18px;
  border-radius: 12px;
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid rgba(0,0,0,0.06);
  background: transparent; /* 尊重你原来的 background */
}

/* 头像外层 */
.touxiang {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  margin: 8px;
  box-shadow: 0 6px 18px rgba(0,0,0,0.06);
}

/* 头像 */
.avatar, .card-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区 */
.info {
  padding-left: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.info .name {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #111827; /* 深色文本，保持可读 */
}

.info .count {
  font-size: 13px;
  color: #6b7280;
  margin-top: 6px;
}

.join-btn, .sign-btn {
  margin-top: 10px;
  align-self: flex-start;
  border-radius: 12px;
  font-weight: 600;
  padding: 8px 18px;
}

.actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.sign-btn {
  background: linear-gradient(90deg,#ffd9e6,#ffeef6); /* 淡粉色系 */
  border: 1px solid rgba(255, 182, 193, 0.6);
  color: #6b2146;
}

.sign-btn.signed {
  background: linear-gradient(90deg,#ffb6d5,#ff8fbf);
  color: #fff;
}

.sign-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.number {
  display: flex;
  gap: 14px;
}

.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 16px;
  margin-bottom: 14px;
  margin-top: 12px;
}

.tab-item {
  cursor: pointer;
  color: rgba(45, 45, 45, 0.8);
  transition: all 0.15s;
  padding-bottom: 6px;
}

.tab-item.active {
  color: #b83280; /* 淡粉偏紫色高亮 */
  font-weight: 700;
  border-bottom: 2px solid #ffd0e6;
}

.card {
  padding: 14px;
  border-radius: 12px;
  background: #fff;
  margin-bottom: 12px;
  box-shadow: 0 6px 18px rgba(17, 24, 39, 0.03);
  transition: transform 0.18s ease, box-shadow 0.18s ease;
  border: 1px solid rgba(17,24,39,0.04);
}

.card.highlight {
  transform: translateY(-6px);
  box-shadow: 0 16px 36px rgba(183, 92, 145, 0.12);
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(17,24,39,0.06);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-left {
  flex: 1;
  padding-right: 12px;
}

.title {
  font-size: 16px;
  font-weight: 700;
  margin: 0 0 6px 0;
  color: #111827;
}

.excerpt {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.card-avatar {
  width: 56px;
  height: 56px;
  border-radius: 10px;
  object-fit: cover;
}

.card-footer {
  display: flex;
  gap: 20px;
  margin-top: 12px;
  color: #6b7280;
  align-items: center;
}

.card-footer div:hover {
  color: #111827;
}

.heart {
  display: inline-block;
  transform-origin: center;
  transition: transform 0.18s ease, color 0.18s ease;
  margin-right: 6px;
}

.heart.liked {
  transform: scale(1.18);
  color: #ff6b9a;
}

.likes-num {
  font-weight: 600;
}

.comment-box {
  margin-top: 12px;
  background: linear-gradient(135deg, #fff7fb 0%, #fff0f6 100%); /* 浅粉渐变 */
  padding: 12px;
  border-radius: 10px;
  border: 1px solid rgba(255, 182, 193, 0.3);
}

.comment-input {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.comment-input input {
  flex: 1;
  padding: 10px;
  background: #fff;
  border: 1px solid rgba(17,24,39,0.06);
  color: #111827;
  border-radius: 8px;
  height: 36px;
  outline: none;
}

.comment-input button {
  padding: 8px 14px;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  background: linear-gradient(90deg,#ffc6dd,#ffb3cf);
  color: #4b1030;
}

.comment-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(17,24,39,0.03);
}

.comment-avatar {
  width: 38px;
  height: 38px;
  border-radius: 50%;
  object-fit: cover;
}

.comment-main {
  flex: 1;
}

.comment-user {
  font-size: 13px;
  font-weight: 700;
  color: #111827;
}

.comment-content {
  color: #4b5563;
  font-size: 14px;
  margin: 4px 0;
  line-height: 1.4;
}

.comment-actions {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #6b7280;
}

.reply-btn {
  font-size: 12px;
  color: #6b7280;
  cursor: pointer;
}

.reply-btn:hover {
  color: #111827;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  background: #111827;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.18s ease;
}

.back-btn:hover {
  transform: translateX(-4px);
}

.back-icon {
  font-size: 1.1rem;
  font-weight: 600;
}

/* toast */
.toast {
  position: fixed;
  left: 50%;
  transform: translateX(-50%);
  bottom: 22px;
  padding: 10px 18px;
  background: linear-gradient(90deg,#ffd6e7,#ffbcd1);
  color: #3b1020;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(183,92,145,0.12);
}

/* transitions */
.fade-slide-enter-active, .fade-slide-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}
.fade-slide-enter-from, .fade-slide-leave-to {
  opacity: 0; transform: translateY(8px);
}

.expand-enter-active, .expand-leave-active {
  transition: all 0.25s ease;
}
.expand-enter-from { height: 0; opacity: 0; }
.expand-enter-to { height: auto; opacity: 1; }

</style>
