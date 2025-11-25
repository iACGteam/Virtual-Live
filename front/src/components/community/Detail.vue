<template>
  <div class="detail-container">

    <div class="back-btn" @click="goBack">
      <el-icon><ArrowLeft /></el-icon>
      返回社区
    </div>


    <h1 class="title">{{ article.title }}</h1>


    <div class="meta">
      <div class="author">
        <img :src="article.avatar" alt="avatar" />
        <span>{{ article.author }}</span>
      </div>
      <span class="time">{{ article.time }}</span>
    </div>


    <div class="cover">
      <img :src="article.cover" alt="cover" />
    </div>


    <div class="content" v-html="article.content"></div>

 
    <div class="actions">
      <div class="like" @click="toggleLike">
        <el-icon :color="liked ? '#ff4d4f' : '#aaa'">
          <HeartFilled v-if="liked"/>
          <Heart v-else/>
        </el-icon>
        <span>{{ liked ? '已点赞' : '点赞' }}</span>
      </div>
    </div>


<div class="comment-section">

  <h2 class="comment-title">评论区</h2>


  <div class="comment-input-box">
    <img class="avatar" src="https://picsum.photos/50?random=10" />
    <el-input
      v-model="newComment"
      type="textarea"
      :rows="3"
      placeholder="发表你的评论..."
      class="comment-input"
    />
    <el-button type="primary" class="comment-btn" @click="submitComment">
      发送
    </el-button>
  </div>


  <div class="comment-list">
    <div class="comment-item" v-for="(c, index) in comments" :key="index">
      <div class="avatar-box">
        <img :src="c.avatar" />
      </div>

      <div class="comment-content">
        <div class="comment-header">
          <span class="name">{{ c.name }}</span>
          <span class="time">{{ c.time }}</span>
        </div>

        <div class="text">{{ c.text }}</div>

        <div class="comment-actions">
          <div class="action-btn" @click="toggleCommentLike(index)">
            <el-icon :color="c.liked ? '#ff4d4f' : '#aaa'">
              <HeartFilled v-if="c.liked"/>
              <Heart v-else/>
            </el-icon>
            {{ c.liked ? '已赞' : '点赞' }}
          </div>

          <div class="action-btn">
            回复
          </div>
        </div>

      </div>
    </div>
  </div>

</div>

  </div>
</template>

<script>
import { ArrowLeft, Heart, HeartFilled } from '@element-plus/icons-vue'

export default {
  name: 'CommunityDetail',

  components: {
    ArrowLeft,
    Heart,
    HeartFilled
  },

  data() {
    return {
      liked: false,

      newComment: "",
    comments: [
      {
        name: "用户A",
        avatar: "https://picsum.photos/50?random=4",
        time: "2 小时前",
        text: "写得太好了！期待下一篇！",
        liked: false
      },
      {
        name: "CoderX",
        avatar: "https://picsum.photos/50?random=5",
        time: "5 小时前",
        text: "内容很有帮助，感谢分享~",
        liked: true
      }
    ],

      article: {
        title: "虚拟直播平台开发心得：如何构建一个高质量社区",
        author: "MeraDicycle",
        avatar: "https://picsum.photos/60?random=2",
        time: "2025-11-25",
        cover: "https://picsum.photos/900/400?random=3",
        content: `
          <p>虚拟直播指利用虚拟主播进行直播的行为，分为完全虚拟IP形象（由AI或“中之人”驱动）
            与基于真人形象的数字分身两类形态。 
            2022年起该行业快速发展，快手2023年虚拟主播直播场次超30万场，京东定制刘强东数字人形象用于图书直播间，2
            024年“6·18”期间批量启用董明珠等品牌总裁数字人直播，商家成本为真人直播十分之一。</p>

        `
      }
    }
  },

  methods: {
    goBack() {
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        this.$router.push('/community')
      }
    },
    toggleLike() {
      this.liked = !this.liked
    },
    submitComment() {
    if (!this.newComment.trim()) {
      this.$message.warning("评论不能为空");
      return;
    }

    this.comments.unshift({
      name: "我",
      avatar: "https://picsum.photos/50?random=20",
      time: "刚刚",
      text: this.newComment,
      liked: false
    });

    this.newComment = "";
  },

  toggleCommentLike(index) {
    this.comments[index].liked = !this.comments[index].liked;
  }
  }

  
}
</script>

<style scoped>

/* 评论区 */
.comment-section {
  margin-top: 40px;
  padding-top: 20px;
  border-top: 1px solid #333;
}


.comment-title {
  font-size: 22px;
  margin-bottom: 20px;
  color: #fff;
}


.comment-input-box {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}

.comment-input-box .avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
}

.comment-input {
  flex: 1;
}

.comment-btn {
  height: 40px;
}


.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  background: #1b1b1b;
  padding: 16px;
  border-radius: 10px;
}

.comment-item .avatar-box img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  gap: 10px;
  margin-bottom: 6px;
}

.comment-header .name {
  font-weight: bold;
  color: #fff;
}

.comment-header .time {
  font-size: 13px;
  color: #888;
}

.text {
  color: #ddd;
  margin-bottom: 10px;
  line-height: 1.6;
}

/* 评论操作按钮 */
.comment-actions {
  display: flex;
  gap: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 14px;
  color: #aaa;
}

.action-btn:hover {
  color: #fff;
}





.detail-container {
  padding: 20px;
  color: #eee;
  max-width: 900px;
  margin: auto;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  color: #ccc;
  margin-bottom: 15px;
  font-size: 15px;
}
.back-btn:hover {
  color: #fff;
}

.title {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 10px;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #aaa;
  margin-bottom: 20px;
}

.author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
}

.cover img {
  width: 100%;
  border-radius: 12px;
  margin-bottom: 25px;
}

.content {
  line-height: 1.8;
  font-size: 17px;
}

.actions {
  margin-top: 30px;
  display: flex;
  gap: 20px;
}

.like {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  color: #aaa;
}
.like:hover {
  color: #fff;
}
</style>
