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
        <img :src="com.avatar || defaultAvatar" class="avatar" />
      </div>
      <div class="info">
        <h2 class="name">
          {{ com.name }}
          <span class="follow-btn" :class="{ followed }" @click="toggleFollow">
            {{ followed ? '已关注 ✓' : '关注' }}
          </span>
        </h2>

        <div class="number">
          <p class="count">{{ com.count }} 粉丝</p>
          <p class="count">{{ com.count }} 作品量</p>
          <p class="count">已经连续签到{{ com.days }}天</p>
        </div>

        <p class="intro">介绍</p>

      </div>

      <div class="actions">
        <el-button class="join-btn" :type="joined ? 'success' : ''" @click="toggleJoin">
          <span v-if="joined">已加入 ✓</span>
          <span v-else>加入</span>
        </el-button>

        <el-button class="sign-btn" :class="{ signed: signed }
          " :disabled="!joined || signed" @click="handleSign">
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
            <div class="card-images" v-if="card.images && card.images.length">
              <img v-for="(img, index) in card.images" :key="index" :src="img" class="post-image" />
            </div>

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
              <input type="text" v-model="card.newComment"
                :placeholder="card.replyTo ? '回复：' + card.replyTo : '发表你的评论...'"
                @keyup.enter="submitComment(card.id)" />
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

  <!-- 固定发布按钮 -->
  <button class="post-btn" @click="showPostBox = true">
    ＋
  </button>

  <!-- 发布泡泡框 -->
  <transition name="fade-slide">
    <div v-if="showPostBox" class="post-bubble">
      <h3 class="post-title">发布新帖子</h3>

      <input v-model="newPost.title" placeholder="输入标题" class="post-input" />

      <textarea v-model="newPost.content" placeholder="写点什么吧..." class="post-textarea"></textarea>

      <!-- 图片上传 -->
      <div class="post-upload">
        <label class="upload-btn">
          📷 上传图片
          <input type="file" accept="image/*" multiple @change="handleImageUpload" />
        </label>

        <div class="preview-box">
          <img v-for="(img, index) in newPost.images" :key="index" :src="img" class="preview-img"
            @click="removeImage(index)" />
        </div>
      </div>


      <div class="post-actions">
        <button class="cancel" @click="showPostBox = false">取消</button>
        <button class="publish" @click="publishPost">发布</button>
      </div>
    </div>
  </transition>
</template>

<script>
export default {
  data() {
    return {
      defaultAvatar: require("@/assets/avatar.jpg"),
      followed: false,
      showPostBox: false,
      newPost: {
        title: "",
        content: "",
        images: []
      },
      com: {
        id: 1,
        name: "官方社团",
        count: 23000,
        days: 5
      },
      cards: [
        {
          id: 1,
          title: "哈哈哈",
          excerpt: '又是解禁的一张🥹是收藏集的阿萨Aza，也是带薪画上了 ​。',
          avatar: require('@/assets/community/aza2.jpg'),
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
          ],
          images: [require('@/assets/community/aza2.jpg'),],
        },
        {
          id: 2,
          title: "𝗛𝗮𝗽𝗽𝘆 𝟲𝘁𝗵 𝗔𝗻𝗻𝗶𝘃𝗲𝗿𝘀𝗮𝗿𝘆! ​",
          excerpt: '',
          avatar: require('@/assets/avatar.jpg'),
          likes: 3,
          liked: true,
          showComments: false,
          newComment: "",
          replyTo: null,
          highlight: false,
          comments: [],
          images: [require('@/assets/community/aza4.jpg')],
        }
      ],
      joined: false,
      signed: false,
      toast: {
        show: false,
        msg: ''
      },
      navList: ['最新', '最热'],
      activeTab: '最新',

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
    },
    toggleFollow() {
      this.followed = !this.followed;
      // this.showToast(this.followed ? "已关注该博主" : "已取消关注");
    },

    publishPost() {
      if (!this.newPost.title.trim()) {
        this.showToast("标题不能为空");
        return;
      }

      const newCard = {
        id: Date.now(),
        title: this.newPost.title,
        excerpt: this.newPost.content.substring(0, 30),
        avatar: require('@/assets/avatar.jpg'),
        likes: 0,
        liked: false,
        showComments: false,
        newComment: "",
        replyTo: null,
        highlight: true,
        comments: [],
        images: [...this.newPost.images]
      };

      this.cards.unshift(newCard);

      // 清空输入框
      this.newPost.title = "";
      this.newPost.content = "";
      this.newPost.images = [];

      this.showPostBox = false;

      this.showToast("发布成功");

      setTimeout(() => (newCard.highlight = false), 800);
    }
    ,
    handleImageUpload(e) {
      const files = Array.from(e.target.files);

      if (this.newPost.images.length + files.length > 2) {
        this.showToast("最多只能选择 2 张图片");
        return;
      }

      files.forEach(file => {
        const reader = new FileReader();
        reader.onload = (event) => {
          this.newPost.images.push(event.target.result);
        };
        reader.readAsDataURL(file);
      });

      // 清空 input，否则无法重复选择同一张图片
      e.target.value = null;
    },

    removeImage(index) {
      this.newPost.images.splice(index, 1);
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
    },
    // comName() {
    //   return this.$route.query.name
    // },
    // comAvatar() {
    //   return this.$route.query.avatar
    // }
  },
  mounted() {
    const q = this.$route.query;

    if (q.name) this.com.name = q.name;
    if (q.avatar) this.com.avatar = q.avatar;
    if (q.id) this.com.id = q.id;
  }

}
</script>

<style scoped>
.circle-detail {
  background: #fefbff;
  padding: 18px;
  border-radius: 12px;
}

header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.06);
  background: transparent;
  /* 尊重你原来的 background */
}

/* 头像外层 */
.touxiang {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  margin: 8px;
  box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
}

/* 头像 */
.avatar,
.card-avatar {
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
  color: #111827;
  /* 深色文本，保持可读 */
}

.info .count {
  font-size: 13px;
  color: #6b7280;
  margin-top: 6px;
}

.join-btn,
.sign-btn {
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
  background: linear-gradient(90deg, #ffd9e6, #ffeef6);
  /* 淡粉色系 */
  border: 1px solid rgba(255, 182, 193, 0.6);
  color: #6b2146;
}

.sign-btn.signed {
  background: linear-gradient(90deg, #ffb6d5, #ff8fbf);
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
  color: #b83280;
  /* 淡粉偏紫色高亮 */
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
  border: 1px solid rgba(17, 24, 39, 0.04);
}

.card.highlight {
  transform: translateY(-6px);
  box-shadow: 0 16px 36px rgba(183, 92, 145, 0.12);
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: 0 12px 30px rgba(17, 24, 39, 0.06);
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
  background: linear-gradient(135deg, #fff7fb 0%, #fff0f6 100%);
  /* 浅粉渐变 */
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
  border: 1px solid rgba(17, 24, 39, 0.06);
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
  background: linear-gradient(90deg, #ffc6dd, #ffb3cf);
  color: #4b1030;
}

.comment-item {
  display: flex;
  gap: 10px;
  padding: 8px 0;
  border-bottom: 1px solid rgba(17, 24, 39, 0.03);
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
  background: linear-gradient(90deg, #ffd6e7, #ffbcd1);
  color: #3b1020;
  border-radius: 20px;
  box-shadow: 0 8px 24px rgba(183, 92, 145, 0.12);
}

/* transitions */
.fade-slide-enter-active,
.fade-slide-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.fade-slide-enter-from,
.fade-slide-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

.expand-enter-active,
.expand-leave-active {
  transition: all 0.25s ease;
}

.expand-enter-from {
  height: 0;
  opacity: 0;
}

.expand-enter-to {
  height: auto;
  opacity: 1;
}

/* 底部固定发布按钮 */
.post-btn {
  position: fixed;
  bottom: 26px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 60px;
  background: linear-gradient(120deg, #ff9acb, #ff6fae);
  color: white;
  border: none;
  border-radius: 50%;
  font-size: 32px;
  line-height: 60px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 10px 30px rgba(255, 75, 150, 0.35);
  z-index: 999;
  transition: 0.15s;
}

.post-btn:hover {
  transform: translateX(-50%) scale(1.08);
}

/* 发布泡泡框 */
.post-bubble {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: 85%;
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 18px 42px rgba(0, 0, 0, 0.12);
  z-index: 999;
}

.post-title {
  margin: 0 0 12px 0;
  font-size: 18px;
  font-weight: 700;
  color: #b83280;
}

.post-input {
  width: 99%;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.1);
  margin-bottom: 10px;
}

.post-textarea {
  width: 99%;
  height: 90px;
  padding: 10px;
  border-radius: 10px;
  border: 1px solid rgba(17, 24, 39, 0.1);
  resize: none;
}

.post-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 12px;
}

.post-actions .cancel {
  padding: 8px 14px;
  border-radius: 10px;
  background: #eee;
  border: none;
  cursor: pointer;
}

.post-actions .publish {
  padding: 8px 14px;
  border-radius: 10px;
  background: #ff8cbc;
  color: white;
  border: none;
  cursor: pointer;
}

/* 博主关注按钮 */
.follow-btn {
  margin-left: 8px;
  padding: 3px 8px;
  font-size: 12px;
  border-radius: 8px;
  background: #ffe0f0;
  color: #b83280;
  cursor: pointer;
  transition: 0.2s;
}

.follow-btn.followed {
  background: #ff8cbc;
  color: white;
}

.follow-btn:hover {
  transform: scale(1.08);
}

/* 图片上传框 */
.post-upload {
  margin-top: 10px;
}

.upload-btn {
  display: inline-block;
  background: #ffe0f0;
  padding: 6px 10px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 13px;
  color: #b83280;
  margin-bottom: 10px;
}

.upload-btn input {
  display: none;
}

/* 上传预览 */
.preview-box {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
}

.preview-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  cursor: pointer;
  border: 1px solid #ffd6e7;
}

.preview-img:hover {
  transform: scale(1.05);
}

/* 作品内图片展示 */
.card-images {
  display: flex;
  gap: 6px;
  margin-top: 8px;
  flex-wrap: wrap;
}

.post-image {
  width: 150px;
  height: 200px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid rgba(255, 182, 193, 0.3);
}
</style>
