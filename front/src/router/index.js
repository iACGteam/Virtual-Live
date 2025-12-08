import { createRouter, createWebHashHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import LiveView from '../views/LiveView.vue'
import ProfileView from '../views/ProfileView.vue'
import LoginView from '../views/LoginView.vue'
import RegisterView from '../views/RegisterView.vue'
import UploadVideoView from '../views/UploadVideoView.vue'
import { getAuthToken } from '@/utils/auth'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView,
    meta: { requiresAuth: true }
  },
  {
    path: '/live',
    name: 'live',
    component: LiveView,
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'profile',
    component: ProfileView,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'register',
    component: RegisterView,
    meta: { requiresAuth: false }
  },
  {
    path: '/upload-video',
    name: 'upload-video',
    component: UploadVideoView,
    meta: { requiresAuth: true }
  },

  //直播间
  {
    path: '/live-room',
    name: 'LiveRoom',
    component: () => import('../views/LiveRoomView.vue')
  },
  {
    path: '/community',
    name: 'Community',
    component: () => import('../views/CommunityView.vue')
  },
  
  //看视频
  {
    path: '/video',
    name: 'Video',
    component: () => import('../views/Video.vue')
  },
  //开直播
  {
    path: '/live-going',
    name: 'LiveGoing',
    component: () => import('../views/LiveGoingView.vue')
  },
  {
    path: '/live-manage',
    name: 'Manage',
    component: () => import('../views/LiveManage.vue')
  },
  {
    path: '/com-detail',
    name: 'CommunityDetail',
    component: () => import('../components/community/ComDetailNav.vue')
  },
  {
    path: "/new-live",
    name: "NewLiveRoom",
    component: () => import('../views/LiveRoomView.vue')
  },

]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = getAuthToken()
  const requiresAuth = to.meta?.requiresAuth !== false
  const redirectQuery = to.query?.redirect
  const redirectPath = typeof redirectQuery === 'string' ? redirectQuery : '/'

  if (requiresAuth && !token) {
    next({
      name: 'login',
      query: { redirect: to.fullPath }
    })
    return
  }

  if (!requiresAuth && token) {
    next(redirectPath)
    return
  }

  next()
})

export default router
