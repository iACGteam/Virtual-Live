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
  background: #fcf5ff;
  border-radius: 12px;
  padding: 10px;
  border: 1px solid #e5e7eb;
  display: flex;
  flex-direction: column;
  gap: 10px;
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
  background: #f9fafb;
  border: 1px solid #e5e7eb;
  padding: 8px;
  border-radius: 10px;
  display: flex;
  gap: 8px;
  cursor: pointer;
  transition: 0.12s ease;
}

.gift-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0,0,0,0.08);
}

.gift-img {
  font-size: 28px;
  width: 44px;
  height: 44px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  border: 1px solid #e5e7eb;
}

/* 更多按钮 */
.more-btn {
  min-width: 60px;
  padding: 6px 12px;
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #333;
  display: flex;
  align-items: center;
  justify-content: center;
}

.more-btn:hover {
  background: #f3f4f6;
}

/* 泡泡框 */
.more-popup {
  position: absolute;
  bottom: 70px;
  right: 20px;
  width: 320px;
  max-height: 320px;
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 12px;
  box-shadow: 0 12px 30px rgba(0,0,0,0.15);
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
  z-index: 200;
}

/* 网格布局 */
.popup-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  overflow-y: auto;
}

/* 隐藏滚动条 */
.popup-grid::-webkit-scrollbar {
  display: none;
}

.popup-item {
  background: #f9fafb;
  border-radius: 10px;
  padding: 8px;
  border: 1px solid #e5e7eb;
  cursor: pointer;
  text-align: center;
}

.popup-item:hover {
  background: #f3f4f6;
}

.pi-img {
  font-size: 32px;
}

.pi-name {
  font-size: 12px;
  margin-top: 4px;
  font-weight: 600;
}

.pi-price {
  font-size: 12px;
  color: #666;
}

/* 关闭按钮 */
.popup-close {
  padding: 6px 0;
  text-align: center;
  background: #f3f4f6;
  border-radius: 8px;
  cursor: pointer;
}

.popup-close:hover {
  background: #e5e7eb;
}

.hint {
  font-size: 12px;
  color: #999;
}
</style>
