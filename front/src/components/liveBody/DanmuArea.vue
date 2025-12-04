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
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  color: #2d2d2d;
  padding: 10px;
  border-radius: 8px;
  /* overflow-y: auto; */
  overflow: hidden;
  outline: none;
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.danmu-item {
  margin-bottom: 6px;
  font-size: 14px;
  line-height: 1.4;
}

.username {
  color: #ff69b4;
  font-weight: bold;
}

/* 输入区 */
.danmu-input-box {
  display: flex;
  /* margin-top: 10px; */
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  padding: 8px;
  border-radius: 8px;
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.danmu-input {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  outline: none;
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
}

.danmu-input::placeholder {
  color: rgba(45, 45, 45, 0.5);
}

.send-btn {
  margin-left: 10px;
  padding: 8px 18px;
  border: none;
  border-radius: 6px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  color: #fff;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 500;
}

.send-btn:hover {
  background: linear-gradient(135deg, #ff7bc4 0%, #a085e8 50%, #5ee0d5 100%);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.3);
  transform: translateY(-1px);
}
</style>
