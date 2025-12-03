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
      <el-button class="join-btn" :type="joined ? 'success' : ''" @click="toggleJoin">
        {{ joined ? '已加入' : '加入' }}
      </el-button>
      <el-button class="sign-btn" :type="signed ? 'success' : ''" @click="handleSign">
        {{ joined ? (signed ? '今日已签到' : '未签到') : '加入并签到' }}
      </el-button>
    </header>

    <div class="nav-tabs">
      <div v-for="item in navList" :key="item" :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="activeTab = item">
        {{ item }}
      </div>
    </div>

    <div class="content-list">
      <div v-for="card in cards" :key="card.id" class="card">

        <!-- 卡片内容 -->
        <div class="card-header">
          <div class="info">
            <p class="title">{{ card.title }}</p>
          </div>
          <img :src="card.avatar" class="avatar">
        </div>

        <!-- 底部按钮 -->
        <footer class="card-footer">
          <div class="comments" @click="toggleComments(card.id)">
            查看 {{ card.comments.length }} 条评论
          </div>
          <div class="reply" @click="startReply(card.id, null)">回复</div>
          <div class="like" @click="toggleLike(card)">
            ❤️ {{ card.likes }}
          </div>
        </footer>

        <!-- 评论区（可展开） -->
        <div class="comment-box" v-if="card.showComments">
          <!-- 发布评论 -->
          <div class="comment-input">
            <input type="text" v-model="card.newComment"
              :placeholder="card.replyTo ? '回复：' + card.replyTo : '发表你的评论...'" />
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

      </div>
    </div>

  </div>
</template>


<script>
import { ref } from "vue";
export default {
  components: {

  },
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
          avatar: '@/assets/avatar.jpg',
          likes: 0,
          liked: false,
          showComments: false,
          newComment: "",
          replyTo: null,
          comments: [
            {
              user: "用户A",
              text: "写得不错！",
              avatar: "@/assets/avatar.jpg",
              time: "2小时前"
            },
            {
              user: "用户B",
              text: "很有帮助！",
              avatar: "@/assets/avatar.jpg",
              time: "1天前"
            }
          ]
        },
        {
          id: 2,
          title: "标题2",
          avatar: '@/assets/avatar.jpg',
          likes: 3,
          liked: true,
          showComments: false,
          newComment: "",
          replyTo: null,
          comments: []
        }
      ],
      joined: false,
      signed: false,
      days: 3,
      signedToday: false,
      navList: ['最新', '最热'],
      activeTab: '最新'
    }
  },
  methods: {
    toggleJoin() {
      this.joined = !this.joined;
    },
    signToday() {
      this.signedToday = true;
      this.days += 1;
    },
    handleSign() {
      if (!this.joined) {
        // 未加入 → 自动加入 + 签到
        this.joined = true
        this.signed = true
      } else {
        // 已加入 → 执行签到
        this.signed = true
      }
    },
    // 展开/收起评论
    toggleComments(id) {
      this.cards = this.cards.map(card =>
        card.id === id ? { ...card, showComments: !card.showComments } : card
      );
    },

    // 点赞 ❤️
    toggleLike(card) {
      card.liked = !card.liked;
      card.likes += card.liked ? 1 : -1;
    },

    // 进入回复模式
    startReply(cardId, user) {
      const card = this.cards.find(c => c.id === cardId);
      card.replyTo = user; // null 表示不是回复
      card.showComments = true; // 自动展开评论区
    },

    // 发表评论
    submitComment(cardId) {
      const card = this.cards.find(c => c.id === cardId);
      if (!card.newComment.trim()) return;

      const text = card.replyTo ? `回复 ${card.replyTo}：${card.newComment}` : card.newComment;

      card.comments.push({
        user: "我",
        text
      });

      // 清空输入
      card.newComment = "";
      card.replyTo = null;
    },
    goBack() {
      // 如果有历史记录，返回上一页；否则返回首页
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        this.$router.push({ path: '/' })
      }
    },
  },
  computed: {
    filteredCards() {
      if (this.activeTab === '最新') {
        // 最新：按时间倒序
        return this.cards.sort((a, b) => b.time - a.time);
      }
      if (this.activeTab === '最热') {
        // 最热：按点赞排序
        return this.cards.sort((a, b) => b.likes - a.likes);
      }
      return this.cards;
    }
  }


}


</script>

<style scoped>
header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  /* backgrou; */
  border-bottom: 1px solid #333;
}


/* 头像外层 */
.touxiang {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  margin: 10px;
}

/* 头像 */
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 信息区 */
.info {
  padding-left: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
  /* 占满剩余空间 */
}

/* 名称 */
.info .name {
  font-size: 20px;
  font-weight: 700;
  margin: 0;
  color: #fff;
}

/* 粉丝数 */
.info .count {
  font-size: 14px;
  color: #aaa;
  margin-top: 4px;
}

/* 按钮 */
.join-btn {
  margin-top: 10px;
  align-self: flex-start;
  /* 按钮靠左 */
  border-radius: 10px;
  font-weight: 600;
  padding: 6px 18px;
}

.sign-btn {
  margin-top: 10px;
  align-self: flex-start;
  /* 按钮靠左 */
  border-radius: 10px;
  font-weight: 600;
  padding: 6px 18px;
}

.number {
  display: flex;
  gap: 10px;
}


.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 18px;
  margin-bottom: 20px;
  margin-top: 8px;
}

.tab-item {
  cursor: pointer;
  color: #aaa;
  transition: all 0.2s;
}

.tab-item.active {
  color: #fff;
  font-weight: bold;
  border-bottom: 2px solid #8b5cf6;
}

.tab-item:hover {
  transform: translateY(-2px);
}



.card {
  padding: 15px;
  border-bottom: 1px solid #333;
}

.card-header {
  display: flex;
  flex-direction: column;
}

.card-footer {
  display: flex;
  gap: 20px;
  margin-top: 10px;
  color: #aaa;
  cursor: pointer;
}

.card-footer div:hover {
  color: #fff;
}

.comment-box {
  margin-top: 15px;
  background: #1e1e1e;
  padding: 10px;
  border-radius: 8px;
}

.comment-input {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.comment-input input {
  flex: 1;
  padding: 10px;
  background: #2c2c2c;
  border: none;
  color: #fff;
  border-radius: 8px;
  height: 1.4rem;
  outline: none;
  font-size: 1rem;

}

.comment-input button {
  padding: 12px 24px;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  background-color: #2c2c2c;
  color: gray;
  transition: all, 0.3s;

}

.comment-input button:hover {
  color: white;
}


.comment-item {
  display: flex;
  gap: 10px;
  padding: 12px 0;
  border-bottom: 1px solid #2a2a2a;
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
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.comment-content {
  color: #ddd;
  font-size: 14px;
  margin: 4px 0;
  line-height: 1.4;
}

.comment-actions {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #888;
}

.reply-btn {
  font-size: 12px;
  color: #aaa;
  cursor: pointer;
}

.reply-btn:hover {
  color: #fff;
}



.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.7rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
  transform: translateX(-2px);
}

.back-btn:active {
  transform: translateX(0);
}

.back-icon {
  font-size: 1.2rem;
  font-weight: 600;
}
</style>
