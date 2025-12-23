<template>
  <div class="gift-panel">
    <div class="gift-list">
      <!-- 显示前7个礼物 -->
      <div
        v-for="gift in displayGifts"
        :key="gift.id"
        class="gift-item"
        @click="sendGift(gift)"
      >
        <div class="gift-img">{{ gift.img }}</div>
        <div class="gift-info">
          <div class="gift-name">{{ gift.name }}</div>
          <div class="gift-price">¥{{ gift.price }}</div>
        </div>
      </div>

      <!-- 更多按钮（超过 7 个礼物时显示） -->
      <div v-if="gifts.length > 6" class="more-btn" @click="toggleMore">
        更多
      </div>
    </div>

    <!-- 泡泡框：所有礼物网格展示 -->
    <div class="more-popup" v-if="showMore">
      <div class="popup-grid">
        <div
          class="popup-item"
          v-for="gift in gifts"
          :key="'all-' + gift.id"
          @click="sendGiftAndClose(gift)"
        >
          <div class="pi-img">{{ gift.img }}</div>
          <div class="pi-name">{{ gift.name }}</div>
          <div class="pi-price">¥{{ gift.price }}</div>
        </div>
      </div>

      <div class="popup-close" @click="toggleMore">关闭</div>
    </div>
  </div>
</template>

<script>
export default {
  name: "GiftPanel",
  props: {
    gifts: Array,
  },
  emits: ["send-gift"],
  data() {
    return {
      showMore: false,
    };
  },
  computed: {
    displayGifts() {
      return this.gifts.slice(0, 6); // 只展示前7个
    },
  },
  methods: {
    sendGift(gift) {
      this.$emit("send-gift", gift);
    },
    sendGiftAndClose(gift) {
      this.$emit("send-gift", gift);
      this.showMore = false;
    },
    toggleMore() {
      this.showMore = !this.showMore;
    },
  },
};
</script>

<style scoped>
.gift-panel {
  background: #222;
  border-radius: 12px;
  padding: 10px;
  border: 1px solid #333;
  display: flex;
  flex-direction: column;
  gap: 10px;
  color: #fff;
}

/* 横向礼物列表 */
.gift-list {
  display: flex;
  gap: 8px;
  align-items: center;
  overflow-x: hidden; /* ❗隐藏滚动条 */
  padding-bottom: 6px;
}

/* 礼物 item */
.gift-item {
  min-width: 120px;
  background: #333;
  border: 1px solid #444;
  border-radius: 8px;
  padding: 8px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.gift-item:hover {
  background: #444;
  border-color: #ff4081;
}

.gift-img {
  font-size: 24px;
}

.gift-info {
  display: flex;
  flex-direction: column;
}

.gift-name {
  font-size: 13px;
  font-weight: bold;
  color: #ddd;
}

.gift-price {
  font-size: 12px;
  color: #aaa;
}

/* 更多按钮 */
.more-btn {
  min-width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #333;
  border-radius: 8px;
  cursor: pointer;
  font-size: 12px;
  color: #aaa;
  border: 1px solid #444;
}
.more-btn:hover {
  background: #444;
  color: #fff;
}

/* 泡泡框 */
.more-popup {
  position: absolute;
  bottom: 110px; /* 位于礼物栏上方 */
  left: 20px;
  right: 340px; /* 避开右侧聊天栏 */
  background: #2a2a2a;
  border: 1px solid #444;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.5);
  z-index: 100;
}

.popup-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 12px;
}

.popup-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}
.popup-item:hover {
  background: #333;
}

.pi-img {
  font-size: 32px;
  margin-bottom: 4px;
}
.pi-name {
  font-size: 13px;
  color: #ddd;
}
.pi-price {
  font-size: 12px;
  color: #888;
}

.popup-close {
  margin-top: 12px;
  text-align: center;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}
.popup-close:hover {
  color: #aaa;
}
</style>
