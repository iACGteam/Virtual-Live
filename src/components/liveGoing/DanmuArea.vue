<template>
  <div class="danmu-wrapper">
    <div class="danmu-header">
      <div class="count">弹幕 · {{ visibleCount }}</div>
      <div class="controls">
        <button class="clear" @click="clearDanmu">清空</button>
      </div>
    </div>

    <!-- 弹幕显示区 -->
    <div class="danmu-area" ref="danmuArea" tabindex="0" @scroll.passive="onScroll">
      <transition-group name="danmu-move" tag="div">
        <div
          v-for="(item, index) in danmuList"
          :key="item.id"
          class="danmu-item"
          :style="{ animationDelay: (index % 6) * 0.06 + 's' }"
        >
          <span class="username">{{ item.user }}：</span>
          <span class="content">{{ item.text }}</span>
        </div>
      </transition-group>
    </div>

    <!-- 输入发送区 -->
    <div class="danmu-input-box">
      <input
        v-model="inputText"
        type="text"
        class="danmu-input"
        placeholder="发送弹幕...（按 Enter 发送）"
        @keyup.enter="sendDanmu"
        :disabled="muted"
      />
      <button class="send-btn" @click="sendDanmu" :disabled="muted">发送</button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, nextTick } from 'vue'

const inputText = ref('')
const muted = ref(false)
const danmuArea = ref(null)

// 使用简单的 id 生成器
let idSeed = 1

const danmuList = reactive([
  { id: idSeed++, user: '小明', text: '哇主播今天好帅！' },
  { id: idSeed++, user: '阿香', text: '直播效果太棒了～' },
  { id: idSeed++, user: '铁粉001', text: '来了来了！' }
])

const MAX_DANMU = 120

const visibleCount = computed(() => danmuList.length)

function scrollToBottom() {
  nextTick(() => {
    const box = danmuArea.value
    if (!box) return
    // 仅在接近底部时自动滚动，避免打断用户手动查看弹幕
    const nearBottom = box.scrollTop + box.clientHeight + 80 >= box.scrollHeight
    if (nearBottom) box.scrollTop = box.scrollHeight
  })
}

function sendDanmu() {
  const text = inputText.value.trim()
  if (!text) return

  // 简单防刷：限制重复发送相同内容（短时间内）
  const last = danmuList[danmuList.length - 1]
  if (last && last.text === text) {
    inputText.value = ''
    return
  }

  danmuList.push({ id: idSeed++, user: '你', text })

  // 控制最大长度
  while (danmuList.length > MAX_DANMU) {
    danmuList.shift()
  }

  inputText.value = ''
  scrollToBottom()
}

function clearDanmu() {
  danmuList.splice(0, danmuList.length)
}

function onScroll() {
  // 这里可以添加：用户手动滚动时暂停自动滚动逻辑（已通过 scrollToBottom 的判断实现）
}
</script>

<style scoped>
.danmu-wrapper {
  display: flex;
  flex-direction: column;
  height: 100%;
  background: #ffffff; /* 保持白色底 */
  padding: 12px;
  border-radius: 10px;
}

.danmu-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.count {
  font-size: 13px;
  color: #374151;
  font-weight: 600;
}

.controls .clear {
  background: transparent;
  border: 1px solid rgba(219,39,119,0.12);
  padding: 4px 8px;
  border-radius: 8px;
  color: #c0266a;
  cursor: pointer;
}

.danmu-area {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  border-radius: 8px;
  border: 1px solid rgba(219,39,119,0.06);
  background: linear-gradient(180deg, #ffffff 0%, #fffafb 100%);
}

.danmu-item {
  display: inline-block;
  margin-bottom: 8px;
  font-size: 14px;
  line-height: 1.4;
  padding: 6px 10px;
  border-radius: 18px;
  background: rgba(219,39,119,0.04);
  color: #4b5563;
  margin-right: 8px;
  animation: floatIn 0.36s ease-out both;
}

.username {
  color: #9f1239; /* 深粉色，醒目但不刺眼 */
  font-weight: 700;
  margin-right: 6px;
}

@keyframes floatIn {
  from { transform: translateY(6px); opacity: 0 }
  to { transform: translateY(0); opacity: 1 }
}

.danmu-move-enter-active, .danmu-move-leave-active {
  transition: all 0.25s ease;
}

.danmu-input-box {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.danmu-input {
  flex: 1;
  padding: 10px 12px;
  border-radius: 10px;
  border: 1px solid rgba(17,24,39,0.06);
  outline: none;
  height: 40px;
  background: #fff;
  box-shadow: 0 6px 18px rgba(16,24,40,0.04);
  color: #111827;
}

.send-btn {
  padding: 9px 16px;
  border-radius: 10px;
  border: none;
  background: linear-gradient(90deg,#ffdce6,#ffc7dd);
  color: #5b1030;
  font-weight: 700;
  cursor: pointer;
}

.send-btn:disabled, .danmu-input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>


