<template>
  <el-scrollbar height="100%">
    <div
      v-for="item in giftList"
      :key="item.id"
      class="avatar-item"
    >
      <img :src="item.img" class="avatar-img" />
      <p class="avatar-name">{{ item.name }}</p>
    </div>
  </el-scrollbar>
</template>

<script setup>
import { ref } from "vue";

import cat from "@/assets/gift/cat.jpeg";

const giftList = ref([
  { id: 1, name: "可爱小狐娘", img: cat, type: "2d" },
  { id: 1, name: "可爱小狐娘", img: cat, type: "2d" },
  { id: 1, name: "可爱小狐娘", img: cat, type: "2d" },
  { id: 1, name: "可爱小狐娘", img: cat, type: "2d" },
  { id: 1, name: "可爱小狐娘", img: cat, type: "2d" }
]);
</script>

<style scoped>
.avatar-item {
  width: 100%;
  height: 80px;
  margin: 10px 0;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 8px;
  transition: all 0.2s ease;
  cursor: pointer;
  border: 1px solid rgba(255, 105, 180, 0.2);
}

.avatar-item:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.4);
  transform: translateX(4px);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.2);
}

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 10px;
  border: 2px solid rgba(255, 105, 180, 0.3);
}

.avatar-name {
  font-size: 14px;
  color: #2d2d2d;
  font-weight: 500;
}
</style>
