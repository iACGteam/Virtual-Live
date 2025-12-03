<template>
  <div class="gift-wrapper">
    <div class="gift-scroll">
      <div
        class="gift-item"
        v-for="item in giftList"
        :key="item.id"
        @click="selectGift(item)"
      >
        <img :src="item.icon" class="gift-icon" />
        <div class="gift-name">{{ item.name }}</div>
        <div class="gift-price">{{ item.price }} coins</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

import car from "@/assets/gift/bsj.png";
import firework from "@/assets/gift/lh.png";
import glasses from "@/assets/gift/mj.png";
import balloon from "@/assets/gift/rqq.png";

const giftList = ref([
  { id: 1, name: "跑车", price: 5, icon: car },
  { id: 2, name: "礼花", price: 12, icon: firework },
  { id: 3, name: "墨镜", price: 3, icon: glasses },
  { id: 4, name: "热气球", price: 1999, icon: balloon }
]);

const selectGift = (item) => {
  console.log("用户选择了礼物:", item);
  // 后续加入 “发送礼物” 的逻辑
};
</script>

<style scoped>

.gift-wrapper {
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  display: flex;
  justify-content: center;
}


.gift-scroll {
  width: 100%;
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 一行三个 */
  gap: 10px;
  padding: 10px;
  overflow-y: auto;
}

.gift-scroll::-webkit-scrollbar {
  display: none;
}


.gift-item {
  background: rgba(255, 255, 255, 0.6);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 10px;
  padding: 8px 4px;
  text-align: center;
  transition: all 0.2s ease;
  cursor: pointer;
  user-select: none;
}

.gift-item:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.4);
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(255, 105, 180, 0.2);
}


.gift-icon {
  width: 40px;
  height: 40px;
  object-fit: contain;
}

.gift-name {
  margin-top: 5px;
  font-size: 12px;
  color: #2d2d2d;
}


.gift-price {
  margin-top: 2px;
  font-size: 11px;
  color: #ff69b4;
  font-weight: 600;
}
</style>
