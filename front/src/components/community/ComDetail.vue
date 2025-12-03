<template>
  <div class="circle-detail">

    Community Detail Body
    <header>
      <div class="touxiang">
        <img src="@/assets/avatar.jpg" class="avatar" />
      </div>
      <div class="info">
        <h2 class="name">{{ com.name }}</h2>
        <div class="number">
          <p class="count">{{ com.count }} 粉丝</p>
          <p class="count">{{ com.count }} 作品量</p>
        </div>

        <p class="intro">介绍</p>
        
      </div>
      <el-button class="join-btn" :type="joined ? 'success' : ''" @click="toggleJoin">
        {{ joined ? '已加入' : '加入' }}
      </el-button>
      <el-button class="join-btn" :type="joined ? 'success' : ''" @click="toggleJoin">
        {{ joined ? '签到' : '加入并签到' }}
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
        <div class="card-header">
          <div class="info">
            <p class="title">{{ card.title }}</p>
            <!-- <p class="content">{{ card.content }} </p> -->
          </div> 
          <img :src="card.avatar" class="avatar">      
          <footer>
            <div class="comments">查看。条评论</div>
            <div class="reply">回复</div>
            <div class="like">爱心</div>
          </footer>
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
        count: 23000
      },
      cards:[
        {
          id: 1, 
          avatar:'@/assets/avatar.jpg',
          title: '标题',
          content: '内容预览'
        },
        {
          id: 2, 
          avatar:'@/assets/avatar.jpg',
          title: '标题',
          content: '内容预览'
        },
      ],
      joined: false,
      days: 3,
      signedToday: false,
      navList: ['最新','最热']
    }
  },
  methods: {
    toggleJoin() {
      this.joined = !this.joined;
    },
    signToday() {
      this.signedToday = true;
      this.days += 1;
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
  flex: 1; /* 占满剩余空间 */
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
  align-self: flex-start; /* 按钮靠左 */
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

</style>
