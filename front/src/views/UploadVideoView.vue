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
          <span>发布视频</span>
        </button>
      </aside>

      <main class="content">
        <div class="upload-container">
          <div 
            class="upload-area"
            :class="{ 'drag-over': isDragOver }"
            @drop="handleDrop"
            @dragover.prevent="isDragOver = true"
            @dragleave="isDragOver = false"
            @click="triggerFileInput"
          >
            <div class="upload-icon">☁️</div>
            <p class="upload-text">点击上传或直接将视频文件拖入此区域</p>
            <p class="upload-note">为了更好的观看体验和平台安全，平台将对上传的视频预审。超过40秒的视频建议上传横版视频</p>
            <input
              ref="fileInput"
              type="file"
              accept="video/*"
              style="display: none"
              @change="handleFileSelect"
            >
          </div>
        </div>
      </main>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UploadVideoView',
  data() {
    return {
      isDragOver: false,
      selectedFile: null
    }
  },
  methods: {
    goBack() {
      // 如果有历史记录，返回上一页；否则返回首页
      if (window.history.length > 1) {
        this.$router.go(-1)
      } else {
        this.$router.push({ path: '/' })
      }
    },
    triggerFileInput() {
      this.$refs.fileInput?.click()
    },
    handleFileSelect(event) {
      const file = event.target.files?.[0]
      if (file) {
        this.selectedFile = file
        this.processFile(file)
      }
    },
    handleDrop(event) {
      event.preventDefault()
      this.isDragOver = false
      const file = event.dataTransfer?.files?.[0]
      if (file && file.type.startsWith('video/')) {
        this.selectedFile = file
        this.processFile(file)
      }
    },
    processFile(file) {
      console.log('Selected file:', file.name, file.size, file.type)
      
      // 读取用户信息（用于获取用户名）
      let userName = 'zk3zy' // 默认用户名
      try {
        const profileData = localStorage.getItem('profileUser')
        if (profileData) {
          const parsed = JSON.parse(profileData)
          if (parsed.name) {
            userName = parsed.name
          }
        }
      } catch (err) {
        console.warn('读取用户信息失败', err)
      }
      
      // 生成视频ID（使用时间戳）
      const videoId = Date.now()
      
      // 生成视频时长（模拟，实际应该从视频文件读取）
      const duration = this.formatDuration(Math.floor(Math.random() * 300 + 10)) // 10-310秒
      
      // 生成随机渐变色作为缩略图
      const gradients = [
        'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
        'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
        'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)',
        'linear-gradient(135deg, #fa709a 0%, #fee140 100%)',
        'linear-gradient(135deg, #30cfd0 0%, #330867 100%)',
        'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)',
        'linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%)',
        'linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%)',
        'linear-gradient(135deg, #ff6cab 0%, #7366ff 100%)'
      ]
      const thumbnailColor = gradients[Math.floor(Math.random() * gradients.length)]
      
      // 创建视频对象
      const videoData = {
        id: videoId,
        title: file.name.replace(/\.[^/.]+$/, ''), // 移除文件扩展名作为标题
        creator: userName,
        duration: duration,
        views: '0次观看',
        tags: ['我的作品'],
        thumbnailColor: thumbnailColor,
        uploadTime: new Date().toISOString(),
        fileName: file.name,
        fileSize: file.size,
        fileType: file.type
      }
      
      // 保存到localStorage
      this.saveVideoToWorks(videoData)
      
      // 触发自定义事件，通知其他页面更新
      window.dispatchEvent(new Event('userWorksUpdated'))
      
      // 显示成功提示
      alert('视频上传成功！已添加到我的作品')
      
      // 可选：跳转到个人页面查看作品
      // this.$router.push({ path: '/profile', query: { tab: 'works' } })
    },
    formatDuration(seconds) {
      const mins = Math.floor(seconds / 60)
      const secs = seconds % 60
      return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    saveVideoToWorks(videoData) {
      try {
        // 从localStorage读取现有作品
        const existingWorks = this.getUserWorks()
        
        // 添加新作品到列表开头
        existingWorks.unshift(videoData)
        
        // 保存回localStorage
        localStorage.setItem('userWorks', JSON.stringify(existingWorks))
        
        console.log('视频已保存到作品:', videoData)
      } catch (err) {
        console.error('保存作品失败', err)
        alert('保存作品失败，请重试')
      }
    },
    getUserWorks() {
      try {
        const works = localStorage.getItem('userWorks')
        if (works) {
          return JSON.parse(works)
        }
      } catch (err) {
        console.warn('读取作品失败', err)
      }
      return []
    }
  }
}
</script>

<style scoped>
.upload-page {
  min-height: 100vh;
  background: #0f1016;
  color: #fff;
  font-family: 'Segoe UI', 'PingFang SC', 'Microsoft Yahei', sans-serif;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 48px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
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
  background: linear-gradient(135deg, #6b73ff 0%, #000dff 100%);
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
  color: rgba(255, 255, 255, 0.6);
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
  background: rgba(255, 255, 255, 0.08);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.12);
  border-color: rgba(255, 255, 255, 0.2);
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
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 255, 255, 0.08);
  display: flex;
  flex-direction: column;
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #ff6cab 0%, #7366ff 100%);
  border: none;
  border-radius: 14px;
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.publish-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(115, 102, 255, 0.3);
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

.upload-container {
  width: 100%;
  max-width: 800px;
}

.upload-area {
  background: rgba(255, 255, 255, 0.04);
  border: 2px dashed rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  padding: 80px 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.upload-area:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(255, 255, 255, 0.3);
}

.upload-area.drag-over {
  background: rgba(115, 102, 255, 0.1);
  border-color: #7366ff;
  transform: scale(1.02);
}

.upload-icon {
  font-size: 64px;
  margin-bottom: 24px;
}

.upload-text {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.9);
  margin: 0 0 16px;
}

.upload-note {
  font-size: 0.9rem;
  color: rgba(255, 255, 255, 0.6);
  margin: 0;
  line-height: 1.6;
}
</style>

