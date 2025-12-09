<template>
  <div class="upload-page">
    <header class="page-header">
      <div class="brand">
        <div class="logo">VL</div>
        <div class="brand-text">
          <h1>VirtuaLive</h1>
          <p>虚拟互动中心</p>
        </div>
      </div>
      <div class="header-right">
        <button class="back-btn" @click="goBack">
          <span class="back-icon">←</span>
          <span>返回</span>
        </button>
      </div>
    </header>

    <div class="page-content">
      <aside class="sidebar">
        <button class="publish-btn">
          <span class="btn-icon">📹</span>
          <span>开直播</span>
        </button>
      </aside>

      <div>
        <Content></Content>
      </div>
    </div>
  </div>
</template>

<script>
import Content from './Content.vue';
export default {
  name: 'GoLive',
  components: {
        Content
    },
  data() {
    return {
      isDragOver: false,
      selectedFile: null
    }
  },
    methods: {
        goBack() {

            if (window.history.length > 1) {
                this.$router.go(-1)
            } else {
                this.$router.push({ path: '/' })
            }
        },
        GoingLiveManage() {
          this.$router.push("/live-manage");
        }
    },
}
    
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft Yahei', sans-serif;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 48px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 48px;
  height: 48px;
  border-radius: 14px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  letter-spacing: 1px;
}

.brand-text h1 {
  font-size: 1.2rem;
  margin: 0;
}

.brand-text p {
  margin: 4px 0 0;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.85rem;
}

.header-right {
  display: flex;
  align-items: center;
  justify-content: flex-end;
}


.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(255, 255, 255, 0.85);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 10px;
  color: #2d2d2d;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 105, 180, 0.06);
  border-color: rgba(255, 105, 180, 0.6);
  transform: translateX(-2px);
}

.back-btn:active {
  transform: translateX(0);
}

.back-icon {
  font-size: 1.2rem;
  font-weight: 600;
}

.page-content {
  display: grid;
  grid-template-columns: 260px 1fr;
  flex: 1;
  margin: 16px 24px 24px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 18px;
  box-shadow: 0 10px 30px rgba(255, 105, 180, 0.15);
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 105, 180, 0.2);
  display: flex;
  flex-direction: column;
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: #ffffff;
  border: 1px solid rgba(255, 105, 180, 0.4);
  border-radius: 14px;
  color: #ff69b4;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 18px rgba(255, 105, 180, 0.25);
}

.publish-btn:active {
  transform: translateY(0);
}

.btn-icon {
  font-size: 1.2rem;
}

.content {
  padding: 48px;
  overflow-y: auto;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>

