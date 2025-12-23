<template>
  <div class="live-player">
    <video id="videoElement" controls autoplay width="100%" height="100%"></video>
    <div v-if="!flvUrl" class="player-placeholder">
      <div class="studio">OFFLINE</div>
      <div class="placeholder-text">主播暂未开播</div>
    </div>
    
    <!-- 弹幕层 -->
    <div class="danmaku-layer" ref="danmakuLayer">
        <div v-for="dm in flyingDanmakuList" :key="dm.id" 
             class="flying-item"
             :style="{ top: dm.top + '%', color: dm.color, animationDuration: dm.duration + 's' }">
            {{ dm.text }}
        </div>
    </div>
  </div>
</template>

<script>
function loadScript(src) {
  return new Promise((resolve, reject) => {
    if (document.querySelector(`script[src="${src}"]`)) {
      resolve();
      return;
    }
    const script = document.createElement('script');
    script.src = src;
    script.onload = resolve;
    script.onerror = reject;
    document.head.appendChild(script);
  });
}

export default {
  name: "LivePlayer",
  props: {
    flvUrl: {
      type: String,
      default: ""
    }
  },
  data() {
    return {
      flvPlayer: null,
      flyingDanmakuList: [],
      nextDmId: 1
    };
  },
  watch: {
    flvUrl(newVal) {
      if (newVal) {
        this.initPlayer();
      }
    }
  },
  async mounted() {
    await loadScript("https://unpkg.com/flv.js/dist/flv.min.js");
    if (this.flvUrl) {
      this.initPlayer();
    }
  },
  beforeUnmount() {
    if (this.flvPlayer) {
      this.flvPlayer.destroy();
      this.flvPlayer = null;
    }
  },
  methods: {
    initPlayer() {
      if (this.flvPlayer) {
        this.flvPlayer.destroy();
      }
      
      if (window.flvjs && window.flvjs.isSupported()) {
        const videoElement = document.getElementById('videoElement');
        this.flvPlayer = window.flvjs.createPlayer({
          type: 'flv',
          url: this.flvUrl,
          isLive: true,
          hasAudio: true,
        });
        this.flvPlayer.attachMediaElement(videoElement);
        this.flvPlayer.load();
        this.flvPlayer.play().catch(e => {
            console.warn("Autoplay blocked", e);
        });
      }
    },
    shoot(text, color = '#fff') {
        const id = this.nextDmId++;
        const top = Math.floor(Math.random() * 80) + 5; // 5% - 85%
        const duration = Math.floor(Math.random() * 5) + 8; // 8-12s
        
        this.flyingDanmakuList.push({ id, text, color, top, duration });
        
        // 自动清理
        setTimeout(() => {
            this.flyingDanmakuList = this.flyingDanmakuList.filter(d => d.id !== id);
        }, duration * 1000);
    }
  }
};
</script>

<style scoped>
.live-player {
  height: 100%;
  width: 100%;
  background: #000;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

video {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.danmaku-layer {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    pointer-events: none;
    z-index: 20;
    overflow: hidden;
}

.flying-item {
    position: absolute;
    right: -100%;
    white-space: nowrap;
    font-size: 24px;
    font-weight: bold;
    text-shadow: 1px 1px 2px rgba(0,0,0,0.8);
    animation-name: fly;
    animation-timing-function: linear;
}

@keyframes fly {
    from { transform: translateX(0); right: -20%; }
    to { transform: translateX(-150vw); right: 100%; }
}

.player-placeholder {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #fff;
  background: #222;
  z-index: 10;
}

.studio {
  background: #666;
  padding: 4px 8px;
  border-radius: 4px;
  margin-bottom: 10px;
  font-weight: bold;
}

.placeholder-text {
  font-size: 18px;
  color: rgba(255,255,255,0.8);
}
</style>
