<template>
  <div class="view-wrap">
    <LiveRoomLayout />
  </div>
</template>

<script>
import LiveRoomLayout from "@/components/liveroom/LiveRoomLayOut.vue";
export default {
  name: "LiveRoomView",
  components: { LiveRoomLayout },
};
</script>

<style scoped>
.view-wrap {
  min-height: 100vh;
  background: linear-gradient(180deg,#f8fbff,#ffffff);
  padding: 0;
}
</style>
