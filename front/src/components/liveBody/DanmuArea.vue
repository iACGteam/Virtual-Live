<template>
  <div class="danmu-wrapper">

    <!-- 弹幕显示区 -->
    <div class="danmu-area" ref="danmuArea">
      <div 
        v-for="(item, index) in danmuList" 
        :key="index"
        class="danmu-item"
      >
        <span class="username">{{ item.user }}：</span>
        <span class="content">{{ item.text }}</span>
      </div>
    </div>

    <!-- 输入发送区 -->
    <div class="danmu-input-box">
      <input 
        v-model="inputText" 
        type="text" 
        class="danmu-input"
        placeholder="发送弹幕..."
        @keyup.enter="sendDanmu"
      />
      <button class="send-btn" @click="sendDanmu">发送</button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      inputText: "",
      danmuList: [
        { user: "小明", text: "哇主播今天好帅！" },
        { user: "阿香", text: "直播效果太棒了～" },
        { user: "铁粉001", text: "来了来了！" },
      ]
    }
  },

  methods: {
    sendDanmu() {
      if (!this.inputText.trim()) return;

      // 添加到弹幕区
      this.danmuList.push({
        user: "你",      // 后续可以改成真实用户昵称
        text: this.inputText
      });

      this.inputText = "";

      this.$nextTick(() => {
        // 让滚动条自动滚到最底部
        const box = this.$refs.danmuArea;
        box.scrollTop = box.scrollHeight;
      });
    }
  }
}
</script>

<style scoped>
.danmu-wrapper {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 弹幕展示区 */
.danmu-area {
  flex: 1;
  background-color: #1c1c1c;
  color: #fff;
  padding: 10px;
  border-radius: 8px;
  /* overflow-y: auto; */
  overflow: hidden;
  outline: none;
}

.danmu-item {
  margin-bottom: 6px;
  font-size: 14px;
  line-height: 1.4;
}

.username {
  color: #4da3ff;
  font-weight: bold;
}

/* 输入区 */
.danmu-input-box {
  display: flex;
  /* margin-top: 10px; */
  background-color: #1c1c1c;
}

.danmu-input {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: none;
  outline: none;
  background: #2a2a2a;
  color: #fff;
}

.send-btn {
  margin-left: 10px;
  padding: 8px 18px;
  border: none;
  border-radius: 6px;
  background-color: #409EFF;
  color: #fff;
  cursor: pointer;
}

.send-btn:hover {
  background-color: #66b1ff;
}
</style>
