<template>
  <div class="create-circle-page">
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
      <div class="page-content">
        <div class="content-card">
          <h2 class="page-title">创建新的圈子</h2>
          
          <div class="form-group">
            <label>圈子名称</label>
            <input v-model="form.name" type="text" placeholder="给圈子取个响亮的名字" maxlength="20" />
            <div class="field-hint">{{ form.name.length }}/20</div>
          </div>

          <div class="form-group">
            <label>圈子简介</label>
            <textarea v-model="form.description" placeholder="介绍一下这个圈子是关于什么的..." maxlength="200"></textarea>
            <div class="field-hint">{{ form.description.length }}/200</div>
          </div>

          <div class="form-group">
            <label>封面图片</label>
            <div 
              class="cover-upload-box" 
              @click="triggerCoverInput" 
              @dragover.prevent 
              @drop.prevent="handleCoverDrop"
            >
              <input 
                ref="coverInput" 
                type="file" 
                accept="image/*" 
                hidden 
                @change="handleCoverSelect" 
              />
              <div v-if="!coverPreview" class="cover-placeholder">
                <div class="upload-icon">🖼️</div>
                <p>点击或拖拽上传封面</p>
                <p class="upload-hint">支持 JPG/PNG，建议比例 1:1</p>
              </div>
              <img v-else :src="coverPreview" alt="封面预览" class="cover-preview" />
              <div v-if="coverPreview" class="reupload-mask">
                <span>更换图片</span>
              </div>
            </div>
          </div>

          <div class="actions">
            <button class="submit-btn" @click="handleSubmit" :disabled="!isValid">立即创建</button>
          </div>
        </div>
      </div>
    </main>
  </div>
</template>

<script>
import { createCircle, uploadImage } from '@/utils/api'
import { getCurrentUserId } from '@/utils/auth'

export default {
  name: 'CreateCircleView',
  data() {
    return {
      form: {
        name: '',
        description: '',
        coverUrl: ''
      },
      coverFile: null,
      coverPreview: '',
      isSubmitting: false
    }
  },
  computed: {
    isValid() {
      return this.form.name.trim().length > 0 && this.form.description.trim().length > 0 && !this.isSubmitting
    }
  },
  methods: {
    goBack() {
      this.$router.go(-1)
    },
    triggerCoverInput() {
      this.$refs.coverInput?.click()
    },
    handleCoverSelect(event) {
      const file = event.target.files?.[0]
      if (file) {
        this.processCover(file)
      }
    },
    handleCoverDrop(event) {
      const file = event.dataTransfer?.files?.[0]
      if (file) {
        this.processCover(file)
      }
    },
    processCover(file) {
      if (!file.type.startsWith('image/')) {
        alert('请上传图片文件')
        return
      }
      
      const maxSize = 5 * 1024 * 1024 // 5MB
      if (file.size > maxSize) {
        alert('图片大小不能超过 5MB')
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        this.coverPreview = e.target.result
        this.coverFile = file
      }
      reader.readAsDataURL(file)
    },
    async handleSubmit() {
      if (!this.isValid) return

      const userId = getCurrentUserId()
      if (!userId) {
        alert('请先登录')
        this.$router.push('/login')
        return
      }

      this.isSubmitting = true
      try {
        let finalCoverUrl = this.form.coverUrl || 'https://picsum.photos/200'

        // Upload cover if selected
        if (this.coverFile) {
          try {
            const uploadResult = await uploadImage(this.coverFile)
            if (uploadResult && typeof uploadResult === 'object') {
              finalCoverUrl = uploadResult.url
            } else if (typeof uploadResult === 'string') {
              finalCoverUrl = uploadResult
            }
          } catch (uploadErr) {
            console.error('封面上传失败:', uploadErr)
            alert('封面上传失败，将使用默认封面')
          }
        }

        const payload = {
          name: this.form.name,
          description: this.form.description,
          coverUrl: finalCoverUrl,
          creatorId: userId
        }

        await createCircle(payload)
        alert('圈子创建成功！')
        this.$router.push('/community')
      } catch (error) {
        console.error('创建圈子失败:', error)
        alert('创建失败，请稍后重试')
      } finally {
        this.isSubmitting = false
      }
    }
  }
}
</script>

<style scoped>
.create-circle-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  font-family: 'Segoe UI', sans-serif;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 32px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(255, 105, 180, 0.2);
  position: sticky;
  top: 0;
  z-index: 100;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 50%, #48d1cc 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-weight: 700;
  font-size: 1.1rem;
}

.brand-text h1 {
  font-size: 1.1rem;
  margin: 0;
  color: #2d2d2d;
}

.brand-text p {
  margin: 2px 0 0;
  color: rgba(45, 45, 45, 0.6);
  font-size: 0.8rem;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 20px;
  background: transparent;
  color: #ff69b4;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.back-btn:hover {
  background: rgba(255, 105, 180, 0.1);
}

.page-body {
  padding: 40px 20px;
  display: flex;
  justify-content: center;
}

.content-card {
  width: 100%;
  max-width: 600px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 24px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(255, 105, 180, 0.1);
}

.page-title {
  margin: 0 0 32px;
  font-size: 1.8rem;
  color: #2d2d2d;
  text-align: center;
}

.form-group {
  margin-bottom: 24px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #4a4a4a;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid rgba(255, 105, 180, 0.2);
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.5);
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #ff69b4;
  background: #fff;
  box-shadow: 0 0 0 4px rgba(255, 105, 180, 0.1);
}

.form-group textarea {
  height: 120px;
  resize: vertical;
}

.field-hint {
  text-align: right;
  font-size: 0.85rem;
  color: #999;
  margin-top: 4px;
}

.cover-upload-box {
  position: relative;
  width: 100%;
  height: 200px;
  border: 2px dashed rgba(255, 105, 180, 0.3);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.3s ease;
}

.cover-upload-box:hover {
  border-color: #ff69b4;
  background: rgba(255, 105, 180, 0.05);
}

.cover-placeholder {
  text-align: center;
  color: rgba(45, 45, 45, 0.6);
}

.upload-icon {
  font-size: 2rem;
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 0.8rem;
  color: rgba(45, 45, 45, 0.4);
  margin-top: 4px;
}

.cover-preview {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.reupload-mask {
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s;
}

.reupload-mask span {
  color: #fff;
  font-weight: 600;
  padding: 8px 16px;
  border: 1px solid #fff;
  border-radius: 20px;
}

.cover-upload-box:hover .reupload-mask {
  opacity: 1;
}

.actions {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.submit-btn {
  padding: 14px 48px;
  background: linear-gradient(135deg, #ff69b4 0%, #9370db 100%);
  color: #fff;
  border: none;
  border-radius: 24px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 15px rgba(255, 105, 180, 0.3);
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 105, 180, 0.4);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: #ccc;
  box-shadow: none;
}
</style>