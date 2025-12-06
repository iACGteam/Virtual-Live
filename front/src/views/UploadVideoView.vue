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
          返回
        </button>
      </div>
    </header>

    <main class="page-body">
      <div class="page-content" :class="{ 'with-info': hasVideoUploaded }">
        <div class="content">
          <div class="upload-container">
            <div
              v-if="!hasVideoUploaded"
              class="upload-area"
              :class="{ 'drag-over': isDragOver }"
              @click="triggerFileInput"
              @dragover.prevent="isDragOver = true"
              @dragleave.prevent="isDragOver = false"
              @drop.prevent="handleDrop"
            >
              <input ref="videoInput" type="file" accept="video/*" hidden @change="handleFileSelect" />
              <div class="upload-box">
                <div class="upload-icon">⬆️</div>
                <p class="upload-text">点击或拖拽上传视频</p>
                <p class="upload-subtext">支持 MP4/MOV/AVI，大小上限 500MB</p>
              </div>
            </div>

            <div v-if="hasVideoUploaded" class="right-column">
              <div class="info-panel">
                <h3 class="info-title">视频信息</h3>
                <div class="field">
                  <label>标题</label>
                  <input v-model="form.title" type="text" placeholder="给视频取个标题吧" maxlength="30" />
                  <div class="field-hint">{{ form.title.length }}/30</div>
                </div>
                <div class="field">
                  <label>简介</label>
                  <textarea v-model="form.desc" placeholder="简单介绍一下视频内容" maxlength="200"></textarea>
                  <div class="field-hint">{{ form.desc.length }}/200</div>
                </div>
                <div class="field">
                  <label>谁可以看</label>
                  <div class="pill-group">
                    <button
                      type="button"
                      :class="['pill', { active: form.visibility === 'public' }]"
                      @click="form.visibility = 'public'"
                    >
                      公开
                    </button>
                    <button
                      type="button"
                      :class="['pill', { active: form.visibility === 'private' }]"
                      @click="form.visibility = 'private'"
                    >
                      私密
                    </button>
                  </div>
                </div>
              </div>

              <div class="info-panel">
                <p class="section-title">上传视频封面</p>
                <div class="cover-upload-box" @click="triggerCoverInput" @dragover.prevent @drop.prevent="handleCoverDrop">
                  <input ref="coverInput" type="file" accept="image/*" hidden @change="handleCoverSelect" />
                  <div v-if="!coverPreview" class="cover-placeholder">点击或拖拽图片上传</div>
                  <img v-else :src="coverPreview" alt="cover preview" class="cover-preview" />
                </div>
              </div>

              <div class="info-panel">
                <p class="section-title">选择标签</p>
                <div class="tag-list">
                  <button
                    v-for="option in topicOptions"
                    :key="option.key"
                    :class="['tag', { active: selectedTopic === option.key }]"
                    @click="selectTopic(option.key)"
                  >
                    {{ option.label }}
                  </button>
                </div>
                <p class="tag-hint">标签将决定视频展示分类</p>
              </div>

              <div class="info-panel" v-if="videoPreview">
                <p class="section-title">视频预览</p>
                <video controls :src="videoPreview" class="preview-player"></video>
              </div>

              <div class="action-buttons">
                <button class="btn primary" @click="saveVideo">上传视频</button>
                <button class="btn ghost" @click="resetForm">重新选择</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
export default {
  name: 'UploadVideoView',
  data() {
    return {
      isDragOver: false,
      selectedFile: null,
      videoPreview: '',
      coverFile: null,
      coverPreview: '',
      hasVideoUploaded: false,
      videoDuration: null,
      form: {
        title: '',
        desc: '',
        visibility: 'public'
      },
      defaultThumbnail: 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8Xw8AAn8B9nkRVKkAAAAASUVORK5CYII=',
      selectedTopic: '',
      topicOptions: [
        { key: 'vsinger', label: '虚拟singer' },
        { key: 'vgamer', label: '虚拟gamer' },
        { key: 'vseiyuu', label: '虚拟声优' },
        { key: 'vmale', label: '虚拟男V' }
      ]
    }
  },
  computed: {
    topicTagMap() {
      return {
        vsinger: 'Music',
        vgamer: 'Esports',
        vseiyuu: 'Podcast',
        vmale: 'Stage'
      }
    },
    topicLabelMap() {
      return this.topicOptions.reduce((acc, cur) => {
        acc[cur.key] = cur.label
        return acc
      }, {})
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
      this.$refs.videoInput?.click()
    },
    triggerCoverInput() {
      this.$refs.coverInput?.click()
    },
    handleFileSelect(event) {
      const file = event.target.files?.[0]
      if (file) {
        this.setSelectedVideo(file)
      }
    },
    handleDrop(event) {
      event.preventDefault()
      this.isDragOver = false
      const file = event.dataTransfer?.files?.[0]
      if (file) {
        this.setSelectedVideo(file)
      }
    },
    setSelectedVideo(file) {
      if (!file.type.startsWith('video/')) {
        alert('请选择视频文件')
        return
      }

      const maxSize = 500 * 1024 * 1024 // 500MB
      if (file.size > maxSize) {
        alert('视频文件过大（超过500MB），请选择更小的文件')
        return
      }

      if (this.videoPreview) {
        URL.revokeObjectURL(this.videoPreview)
      }

      this.selectedFile = file
      this.hasVideoUploaded = true
      this.videoPreview = URL.createObjectURL(file)
      // 同时保存视频的blob URL，供后续查看时使用
      this.$data.selectedVideoUrl = this.videoPreview
      this.extractVideoDuration(file)

      if (!this.form.title) {
        this.form.title = file.name.replace(/\.[^/.]+$/, '')
      }
    },
    handleCoverSelect(event) {
      const file = event.target.files?.[0]
      if (file) {
        this.processCover(file)
      }
    },
    handleCoverDrop(event) {
      event.preventDefault()
      const file = event.dataTransfer?.files?.[0]
      if (file) {
        this.processCover(file)
      }
    },
    processCover(file) {
      if (!file.type.startsWith('image/')) {
        alert('请上传图片作为封面')
        return
      }

      const maxSize = 2 * 1024 * 1024 // 2MB
      if (file.size > maxSize) {
        alert('封面图片过大（超过2MB），请选择较小的图片')
        return
      }
      const reader = new FileReader()
      reader.onload = (e) => {
        this.coverFile = file
        this.coverPreview = e.target.result
        // 同时保存到sessionStorage用于临时存储
        this.coverDataUrl = e.target.result
      }
      reader.onerror = () => {
        alert('读取封面失败，请重试')
      }
      reader.readAsDataURL(file)
    },
    selectTopic(topicKey) {
      this.selectedTopic = topicKey
    },
    saveVideo() {
      if (!this.selectedFile) {
        alert('请先选择要上传的视频')
        return
      }
      this.processFile(this.selectedFile)
    },
    async processFile(file) {
      const maxSize = 500 * 1024 * 1024 // 500MB
      if (file.size > maxSize) {
        alert('视频文件过大（超过500MB），请选择更小的文件')
        return
      }
      
      // 保存视频的blob URL到sessionStorage，供后续查看时使用
      const videoUrl = URL.createObjectURL(file)
      const videoId = Date.now()
      sessionStorage.setItem(`videoBlob_${videoId}`, videoUrl)
      
      // 保存封面到sessionStorage（如果有的话）
      let coverKey = null
      if (this.coverDataUrl) {
        coverKey = `coverBlob_${videoId}`
        sessionStorage.setItem(coverKey, this.coverDataUrl)
      }
      
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
      
      const duration = this.videoDuration
        ? this.formatDuration(Math.round(this.videoDuration))
        : this.formatDuration(Math.floor(Math.random() * 300 + 10)) // 10-310秒兜底
      const primaryTag = this.topicLabelMap[this.selectedTopic] || (this.selectedTopic || '未分类')
      const title = (this.form.title && this.form.title.trim()) || file.name.replace(/\.[^/.]+$/, '')
      const desc = (this.form.desc && this.form.desc.trim()) || ''
      const visibility = this.form.visibility === 'private' ? 'private' : 'public'
      const isPrivate = visibility === 'private'
      
      // 不再将视频内联到localStorage，避免超出浏览器存储配额（10MB左右就可能失败）
      let inlineVideoData = ''

      const videoData = {
        id: videoId,
        title,
        creator: userName,
        duration: duration,
        views: '0次观看',
        tags: [primaryTag],
        thumbnail: this.coverPreview || this.defaultThumbnail,
        coverKey: coverKey,
        desc,
        visibility,
        isPrivate,
        uploadTime: new Date().toISOString(),
        fileName: file.name,
        fileSize: file.size,
        fileType: file.type,
        videoUrl: videoUrl,
        videoData: inlineVideoData // 不再内联，避免localStorage超限；刷新后大文件需重传
      }

      // 保存到localStorage
      this.saveVideoToWorks(videoData)

      // 触发自定义事件，通知其他页面更新
      window.dispatchEvent(new Event('userWorksUpdated'))

      // 显示成功提示
      alert('视频上传成功！已添加到我的作品')
      this.resetForm()
    },
    extractVideoDuration(file) {
      return new Promise((resolve) => {
        const url = URL.createObjectURL(file)
        const video = document.createElement('video')
        video.preload = 'metadata'
        video.onloadedmetadata = () => {
          URL.revokeObjectURL(url)
          this.videoDuration = video.duration || null
          resolve(this.videoDuration)
        }
        video.onerror = () => {
          URL.revokeObjectURL(url)
          this.videoDuration = null
          resolve(null)
        }
        video.src = url
      })
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
    },
    resetForm() {
      if (this.videoPreview) {
        URL.revokeObjectURL(this.videoPreview)
      }

      this.isDragOver = false
      this.selectedFile = null
      this.videoPreview = ''
      this.coverFile = null
      this.coverPreview = ''
      this.hasVideoUploaded = false
      this.videoDuration = null
      this.form.title = ''
      this.form.desc = ''
      this.form.visibility = 'public'
      this.selectedTopic = ''

      if (this.$refs.videoInput) {
        this.$refs.videoInput.value = ''
      }
      if (this.$refs.coverInput) {
        this.$refs.coverInput.value = ''
      }
    }
  }
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
  position: relative;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 48px;
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
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

.brand-text h1 {
  color: #2d2d2d;
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
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 10px;
  color: #2d2d2d;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.back-btn:hover {
  background: rgba(255, 105, 180, 0.1);
  border-color: rgba(255, 105, 180, 0.5);
  color: #ff69b4;
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
  display: flex;
  flex: 1;
  justify-content: center;
  align-items: flex-start;
}

.sidebar {
  padding: 32px 24px;
  border-right: 1px solid rgba(255, 105, 180, 0.2);
  display: flex;
  flex-direction: column;
  background: rgba(255, 255, 255, 0.6);
  backdrop-filter: blur(10px);
}

.publish-btn {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 20px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
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
  padding: 48px 24px;
  overflow-y: auto;
  display: flex;
  align-items: flex-start;
  justify-content: center;
  width: 100%;
}

.upload-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.right-column {
  width: 100%;
}

.info-panel {
  background: rgba(255, 255, 255, 0.9);
  border: 1px solid rgba(255, 105, 180, 0.25);
  border-radius: 16px;
  padding: 16px;
  box-shadow: 0 4px 20px rgba(255, 105, 180, 0.15);
  margin-bottom: 16px;
}

.info-title {
  font-weight: 700;
  color: #2d2d2d;
  margin-bottom: 12px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 12px;
}

.field label {
  font-weight: 600;
  color: #2d2d2d;
}

.field input,
.field textarea {
  width: 100%;
  border-radius: 12px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  padding: 12px;
  color: #2d2d2d;
  font-size: 14px;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.field input:focus,
.field textarea:focus {
  border-color: #ff69b4;
  outline: none;
  box-shadow: 0 0 0 3px rgba(255, 105, 180, 0.15);
}

.field textarea {
  resize: vertical;
  min-height: 90px;
}

.field-hint {
  font-size: 12px;
  color: rgba(45, 45, 45, 0.55);
  text-align: right;
}

.cover-upload-box {
  position: relative;
  border: 1px dashed rgba(255, 105, 180, 0.35);
  border-radius: 12px;
  width: 100%;
  aspect-ratio: 16 / 9;
  min-height: 220px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: border 0.2s ease, box-shadow 0.2s ease;
}

.cover-upload-box:hover {
  border-color: rgba(255, 105, 180, 0.5);
  box-shadow: 0 8px 24px rgba(255, 105, 180, 0.2);
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  position: absolute;
  inset: 0;
}

.cover-placeholder {
  color: rgba(45, 45, 45, 0.6);
  font-weight: 600;
}

.upload-area {
  margin-top: 16px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border: 2px dashed rgba(255, 105, 180, 0.3);
  border-radius: 20px;
  padding: 140px 80px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 4px 20px rgba(255, 105, 180, 0.15);
}

.upload-area.success {
  cursor: default;
  border-style: solid;
  border-color: rgba(255, 105, 180, 0.4);
}

.upload-area:hover {
  background: rgba(255, 255, 255, 0.95);
  border-color: rgba(255, 105, 180, 0.5);
  box-shadow: 0 8px 30px rgba(255, 105, 180, 0.25);
}

.upload-area.drag-over {
  background: rgba(255, 105, 180, 0.1);
  border-color: #ff69b4;
  transform: scale(1.02);
  box-shadow: 0 12px 40px rgba(255, 105, 180, 0.3);
}

.upload-icon {
  font-size: 64px;
  margin-bottom: 24px;
}

.upload-text {
  font-size: 1.1rem;
  color: #2d2d2d;
  margin: 0 0 16px;
  font-weight: 600;
}

.upload-subtext {
  font-size: 0.95rem;
  color: rgba(45, 45, 45, 0.65);
  margin: 0 0 12px;
}

.upload-note {
  font-size: 0.9rem;
  color: rgba(45, 45, 45, 0.7);
  margin: 0;
  line-height: 1.6;
}

.section-title {
  font-weight: 700;
  color: #2d2d2d;
  margin: 0 0 10px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag {
  padding: 10px 14px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 12px;
}
.tag.active {
  background: #ff69b4;
  color: #fff;
  border-color: #ff69b4;
  box-shadow: 0 6px 18px rgba(255, 105, 180, 0.35);
}

.tag-hint {
  font-size: 0.85rem;
  color: rgba(45, 45, 45, 0.6);
}

.pill-group {
  display: inline-flex;
  gap: 8px;
}

.pill {
  padding: 10px 14px;
  border-radius: 999px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  background: rgba(255, 255, 255, 0.8);
  color: #2d2d2d;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pill:hover {
  border-color: rgba(255, 105, 180, 0.6);
  box-shadow: 0 4px 14px rgba(255, 105, 180, 0.2);
}

.pill.active {
  background: #ff69b4;
  color: #fff;
  border-color: #ff69b4;
  box-shadow: 0 6px 18px rgba(255, 105, 180, 0.35);
}

.preview-player {
  width: 100%;
  border-radius: 12px;
  background: #000;
}

.action-buttons {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 12px;
}

.btn {
  padding: 12px 16px;
  border-radius: 12px;
  border: none;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn.primary {
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  color: #fff;
  box-shadow: 0 6px 18px rgba(255, 105, 180, 0.3);
}

.btn.primary:hover {
  box-shadow: 0 8px 22px rgba(255, 105, 180, 0.35);
  transform: translateY(-1px);
}

.btn.ghost {
  background: rgba(255, 255, 255, 0.9);
  color: #ff69b4;
  border: 1px solid rgba(255, 105, 180, 0.4);
}

.btn.ghost:hover {
  background: rgba(255, 105, 180, 0.05);
  border-color: rgba(255, 105, 180, 0.6);
}
</style>

