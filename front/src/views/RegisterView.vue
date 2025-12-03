<template>
  <div class="auth-page register">
    <div class="grid glow"></div>
    <div class="auth-card">
      <div class="card-header">
        <h1>创建账号</h1>
      </div>

      <form class="auth-form" @submit.prevent="handleSubmit">
        <label>
          <span>用户名</span>
          <input
            v-model.trim="form.nickname"
            type="text"
            placeholder="不超过 16 个字符"
            maxlength="16"
            required
          >
        </label>

        <label>
          <span>邮箱</span>
          <input
            v-model.trim="form.email"
            type="email"
            placeholder="player@cosmos.tv"
            required
          >
        </label>

        <label>
          <span>密码</span>
          <input
            v-model="form.password"
            type="password"
            placeholder="至少 6 位，含数字"
            minlength="6"
            required
          >
        </label>

        <label>
          <span>确认密码</span>
          <input
            v-model="form.confirmPassword"
            type="password"
            placeholder="再次输入密码"
            minlength="6"
            required
          >
        </label>

        <label class="agreement">
          <input v-model="agree" type="checkbox" required>
          <span>我已阅读并同意《用户协议》和《隐私条款》</span>
        </label>

        <button class="primary" type="submit">
          <span v-if="!loading">注册并激活账号</span>
          <span v-else>正在创建账号…</span>
        </button>

        <p v-if="message" class="message">{{ message }}</p>
      </form>

      <div class="card-footer">
        已有账号？
        <router-link to="/login">返回登录</router-link>
      </div>
    </div>
  </div>
</template>

<script>
import { addMockUser, isMockEmailTaken, isMockUsernameTaken } from '@/data/mockUsers'

export default {
  name: 'RegisterView',
  data() {
    return {
      form: {
        nickname: '',
        email: '',
        password: '',
        confirmPassword: '',
        category: ''
      },
      agree: false,
      message: '',
      loading: false
    }
  },
  methods: {
    handleSubmit() {
      this.message = ''

      if (this.form.password !== this.form.confirmPassword) {
        this.message = '两次输入的密码不一致'
        return
      }

      if (!this.agree) {
        this.message = '请先勾选并同意相关条款'
        return
      }

      const nickname = this.form.nickname.trim()
      const email = this.form.email.trim().toLowerCase()
      const password = this.form.password

      if (isMockUsernameTaken(nickname)) {
        this.message = '该用户名已被占用，请尝试其他昵称'
        return
      }

      if (isMockEmailTaken(email)) {
        this.message = '该邮箱已注册，请直接登录'
        return
      }

      this.loading = true
      setTimeout(() => {
        addMockUser({
          username: nickname,
          email,
          password,
          name: nickname
        })

        this.loading = false
        this.message = '注册成功（模拟），正在跳转至登录页…'
        const redirect = this.$route.query.redirect
        const redirectQuery = typeof redirect === 'string' ? { redirect } : undefined
        setTimeout(() => {
          this.$router.replace({ name: 'login', query: redirectQuery }).catch(() => {})
        }, 800)
      }, 1200)
    }
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px 20px;
  background: linear-gradient(135deg, #fef7ff 0%, #f5f0ff 50%, #fff0f8 100%);
  position: relative;
  overflow: hidden;
}

.grid {
  position: absolute;
  width: 140%;
  height: 120%;
  top: -10%;
  left: -20%;
  background-image:
    linear-gradient(90deg, rgba(255, 255, 255, 0.04) 1px, transparent 0),
    linear-gradient(0deg, rgba(255, 255, 255, 0.04) 1px, transparent 0);
  background-size: 80px 80px;
  opacity: 0.9;
  transform: rotate(-6deg);
}

.glow::after {
  content: '';
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 20% 20%, rgba(124, 92, 255, 0.45), transparent 55%),
    radial-gradient(circle at 80% 60%, rgba(70, 208, 255, 0.35), transparent 50%);
}

.auth-card {
  width: 100%;
  max-width: 520px;
  background: rgba(9, 11, 22, 0.95);
  border-radius: 32px;
  padding: 40px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 30px 50px rgba(0, 0, 0, 0.6);
  position: relative;
  z-index: 1;
}

.card-header h1 {
  margin: 10px 0;
  font-size: 34px;
}

.badge {
  display: inline-flex;
  padding: 4px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.08);
  color: #9ec5ff;
  font-size: 12px;
  letter-spacing: 0.1em;
}

.subtitle {
  color: rgba(255, 255, 255, 0.75);
}

.auth-form {
  margin-top: 26px;
  display: flex;
  flex-direction: column;
  gap: 18px;
}

label span {
  display: block;
  margin-bottom: 6px;
  font-weight: 600;
  letter-spacing: 0.02em;
}

input,
select {
  width: 100%;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(6, 8, 19, 0.9);
  padding: 16px;
  color: #fff;
  font-size: 15px;
  transition: border 0.2s ease, transform 0.2s ease;
}

input:focus,
select:focus {
  border-color: #7c9dff;
  outline: none;
  transform: translateY(-1px);
}

select {
  appearance: none;
  background-image: linear-gradient(45deg, transparent 50%, #869eff 50%),
    linear-gradient(135deg, #869eff 50%, transparent 50%);
  background-position: calc(100% - 24px) calc(50% - 3px),
    calc(100% - 16px) calc(50% - 3px);
  background-size: 8px 8px;
  background-repeat: no-repeat;
}

.agreement {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
}

.agreement input {
  width: auto;
}

.primary {
  width: 100%;
  padding: 17px;
  border-radius: 22px;
  border: none;
  background: linear-gradient(120deg, #ff7bc4, #7f8dff);
  color: #fff;
  font-weight: 600;
  font-size: 16px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 25px 35px rgba(142, 130, 255, 0.3);
}

.message {
  text-align: center;
  color: #8bc4ff;
  font-size: 14px;
}

.card-footer {
  margin-top: 24px;
  text-align: center;
  color: rgba(255, 255, 255, 0.7);
}

.card-footer a {
  color: #9fc3ff;
  font-weight: 600;
  margin-left: 4px;
}

@media (max-width: 540px) {
  .auth-card {
    padding: 28px 24px;
  }
}
</style>

