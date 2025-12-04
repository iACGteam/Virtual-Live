<template>
  <div class="auth-page">
    <div class="neon-orbit"></div>
    <div class="auth-card">
      <div class="card-header">

        <h1>欢迎回来</h1>
        <p class="subtitle">登录以同步观影历史、观看进度和个性推荐。</p>
      </div>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label>
          <span>用户名</span>
          <input
            v-model.trim="form.username"
            type="text"
            placeholder="请输入用户名"
            required
          >
        </label>

        <label>
          <span>密码</span>
          <input
            v-model="form.password"
            type="password"
            placeholder="请输入密码"
            minlength="6"
            required
          >
        </label>

        <div class="form-meta">
          <label class="remember">
            <input v-model="remember" type="checkbox">
            <span>保持登录</span>
          </label>
          <button class="link" type="button">忘记密码？</button>
        </div>

        <button class="primary" type="submit">
          <span v-if="!loading">立即登录</span>
          <span v-else>正在读取登录态…</span>
        </button>

        <p v-if="message" class="message">{{ message }}</p>
      </form>

      <div class="card-footer">
        还没有账号？
        <router-link to="/register">前往注册</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { getAuthToken, setAuthToken, setCurrentUser } from '@/utils/auth'
import { findMockUser } from '@/data/mockUsers'

export default {
  name: 'LoginView',
  data() {
    return {
      form: {
        username: '',
        password: ''
      },
      remember: true,
      message: '',
      loading: false
    }
  },
  created() {
    if (getAuthToken()) {
      this.redirectAfterLogin()
    }
  },
  methods: {
    redirectAfterLogin() {
      const redirect = this.$route.query.redirect
      const target = typeof redirect === 'string' ? redirect : '/'
      this.$router.replace(target)
    },
    handleSubmit() {
      this.message = ''

      if (!this.form.username || !this.form.password) {
        this.message = '请填写用户名和密码'
        return
      }

      this.loading = true
      setTimeout(() => {
        const user = findMockUser(this.form.username, this.form.password)

        if (!user) {
          this.loading = false
          this.message = '账号或密码不正确'
          return
        }

        const mockToken = `mock-token-${Date.now()}`
        setAuthToken(mockToken, this.remember)
        setCurrentUser(user.username, this.remember)
        this.message = `欢迎回来，${user.name}，正在跳转…`
        this.loading = false
        setTimeout(() => this.redirectAfterLogin(), 200)
      }, 1000)
    }
  }
}
</script>

<style scoped>
.auth-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  overflow: hidden;
}

.neon-orbit {
  position: absolute;
  width: 520px;
  height: 520px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(255, 105, 180, 0.15), transparent 60%);
  filter: blur(10px);
  animation: pulse 6s ease-in-out infinite;
}

.auth-card {
  width: 100%;
  max-width: 420px;
  background: rgba(255, 255, 255, 0.95);
  border: 1px solid rgba(255, 105, 180, 0.2);
  border-radius: 30px;
  padding: 36px;
  box-shadow: 0 20px 60px rgba(255, 105, 180, 0.2);
  backdrop-filter: blur(30px);
  position: relative;
  z-index: 1;
}

.card-header h1 {
  margin: 8px 0;
  font-size: 32px;
}

.badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  border-radius: 999px;
  background: rgba(255, 105, 180, 0.15);
  color: #ff69b4;
  font-size: 12px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.subtitle {
  color: rgba(45, 45, 45, 0.7);
  margin: 0;
}

.auth-form {
  margin-top: 28px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

label span {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
}

input[type='text'],
input[type='email'],
input[type='password'] {
  width: 100%;
  border: 1px solid rgba(255, 105, 180, 0.3);
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.9);
  color: #2d2d2d;
  padding: 14px 16px;
  font-size: 15px;
  transition: border 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;
}

input:focus {
  border-color: #ff69b4;
  outline: none;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(255, 105, 180, 0.2);
}

.form-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
}

.remember {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(45, 45, 45, 0.8);
}

.link {
  border: none;
  background: none;
  color: #ff69b4;
  cursor: pointer;
  transition: color 0.2s ease;
}

.link:hover {
  color: #9370db;
}

.primary {
  width: 100%;
  padding: 16px;
  border-radius: 18px;
  border: none;
  background: linear-gradient(120deg, #546dff, #9e7aff);
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 20px 30px rgba(84, 109, 255, 0.25);
}

.message {
  text-align: center;
  color: #ff69b4;
  font-size: 14px;
}

.card-footer {
  margin-top: 24px;
  text-align: center;
  color: rgba(45, 45, 45, 0.7);
}

.card-footer a {
  color: #ff69b4;
  font-weight: 600;
  margin-left: 4px;
  transition: color 0.2s ease;
}

.card-footer a:hover {
  color: #9370db;
}

@keyframes pulse {
  0% { transform: scale(0.9); opacity: 0.7; }
  50% { transform: scale(1.1); opacity: 1; }
  100% { transform: scale(0.9); opacity: 0.7; }
}
</style>

