<template>
  <div class="header-right">
    <!-- 消息 -->
    <el-dropdown trigger="click">
      <div class="notification-bell">
        <el-badge :value="messageCount" :max="99">
          <el-icon size="20"><Bell /></el-icon>
        </el-badge>
      </div>

      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item>
            <div class="message-item">
              <el-avatar :size="30" src="@/assets/avatar1.jpg" />
              <div class="message-content">
                <p>用户A评论了你的帖子</p>
                <span>2分钟前</span>
              </div>
            </div>
          </el-dropdown-item>

          <el-dropdown-divider />

          <el-dropdown-item>
            <div class="view-all">查看全部消息</div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <!-- 用户头像 -->
    <el-dropdown @command="handleUserCommand">
      <div class="user-avatar">
        <el-avatar :size="36" :src="userInfo.avatar" />
        <span class="username">{{ userInfo.name }}</span>
        <el-icon><ArrowDown /></el-icon>
      </div>

      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item command="profile">
            <el-icon><User /></el-icon> 个人资料
          </el-dropdown-item>
          <el-dropdown-item command="settings">
            <el-icon><Setting /></el-icon> 账号设置
          </el-dropdown-item>
          <el-dropdown-divider />
          <el-dropdown-item command="logout">
            <el-icon><SwitchButton /></el-icon> 退出登录
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { Bell, User, Setting, SwitchButton, ArrowDown } from '@element-plus/icons-vue'

const router = useRouter()
const messageCount = ref(5)

const userInfo = reactive({
  name: '用户名',
  avatar: '@/assets/user-avatar.jpg'
})

const handleUserCommand = (cmd) => {
  if (cmd === 'profile') router.push('/profile')
  if (cmd === 'settings') router.push('/settings')
  if (cmd === 'logout') console.log('退出登录')
}
</script>

<style scoped>
.header-right {
  display: flex;
  align-items: center;
  gap: 20px;

}

.user-avatar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.user-avatar:hover {
  background-color: #f5f7fa;
}

.username {
  font-size: 14px;
  color: #606266;
}


</style>
