<template>
  <div class="home-page">
    <aside class="sidebar">
      <div class="brand">
        <div class="logo">VL</div>
        <div class="brand-text">
          <h1>VirtuaLive</h1>
          <p>虚拟互动中心</p>
        </div>
      </div>

      <nav class="nav-links">
        <button
          v-for="link in navLinks"
          :key="link.key"
          :class="['nav-link', { active: activeNav === link.key }]"
          @click="handleNavClick(link)"
        >
          <span class="icon">{{ link.icon }}</span>
          <span>{{ link.label }}</span>
        </button>
      </nav>
    </aside>

    <main class="content">
      <ComDetailPage></ComDetailPage>
    </main>
  </div>
</template>

<script>
import ComDetailPage from './ComDetailPage.vue';

export default {
  components: { 
    ComDetailPage,
  },
  name: "LiveView",
  data() {
    return {
      activeNav: "live",
      navLinks: [
        { key: "discover", label: "发现内容", icon: "✨" },
        { key: "live", label: "直播", icon: "📡" },
        { key: "community", label: "社区", icon: "💬" },
        { key: "my", label: "我的", icon: "" }
      ]
    };
  },
  methods: {
    handleNavClick(link) {
      this.activeNav = link.key;

      if (link.key === "my") {
        this.$router.push("/profile");
        return;
      }

      const routeMap = {
        discover: "/",
        live: "/live",
        community: "/community"
      };

      const target = routeMap[link.key] || "/";

      if (this.$route.path !== target) {
        this.$router.push(target);
      }
    }
  },
  mounted() {
    const pathMap = {
      "/": "discover",
      "/live": "live",
      "/community": "community",
      "/profile": "my"
    };
    this.activeNav = pathMap[this.$route.path] || "discover";
  }
};
</script>

<style scoped>


.home-page {
  display: grid;
  grid-template-columns: 260px 1fr;
  height: 100vh;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  color: #2d2d2d;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft Yahei', sans-serif;
  position: relative;
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 105, 180, 0.2);
  display: flex;
  flex-direction: column;
  gap: 32px;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
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
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.4);
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

.nav-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: transparent;
  border: none;
  color: rgba(45, 45, 45, 0.75);
  font-size: 0.95rem;
  text-align: left;
  border-radius: 12px;
  cursor: pointer;
  transition: background 0.2s ease, color 0.2s ease;
}

.nav-link .icon {
  font-size: 1.1rem;
}

.nav-link.active,
.nav-link:hover {
  background: linear-gradient(135deg, rgba(255, 105, 180, 0.15) 0%, rgba(147, 112, 219, 0.15) 100%);
  color: #ff69b4;
  border: 1px solid rgba(255, 105, 180, 0.3);
}
</style>
