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
  background: #1c1c1c;
  border-radius: 8px;
  display: flex;
  align-items: center;
  padding: 8px;
  transition: 0.2s;
  cursor: pointer;
}

.avatar-item:hover {
  background: var(--el-color-primary-light-7);
}

.avatar-img {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 10px;
}

.avatar-name {
  font-size: 14px;
  color: white;
}
</style>
