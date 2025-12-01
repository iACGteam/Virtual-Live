<template>
  <div class="community-page">
    <!-- 搜索栏 -->
    <el-input
      v-model="search"
      placeholder="搜索你感兴趣的主播或圈子"
      class="search-bar"
      prefix-icon="Search"
      clearable
    />

    <!-- 最新 / 最热 -->
    <div class="nav-tabs">
      <div 
        v-for="item in navList"
        :key="item"
        :class="['tab-item', activeTab === item ? 'active' : '']"
        @click="activeTab = item"
      >
        {{ item }}
      </div>
    </div>

    <!-- 粉丝圈列表 -->
    <div class="circle-list">
      <div v-if="loading" class="loading-container">
        <p>加载中...</p>
      </div>
      
      <div v-else-if="circles.length === 0" class="empty-container">
        <p>暂无圈子数据</p>
      </div>
      
      <div 
        v-else
        v-for="circle in circles"
        :key="circle.id"
        class="circle-card"
      >
        <div class="circle-header">
          <img :src="circle.avatarUrl" class="avatar" @error="(e) => e.target.src = '/mock/default.jpg'">
          <div class="info">
            <p class="name">{{ circle.name }}</p>
            <p class="count">{{ formatCount(circle.memberCount) }} 粉丝已加入</p>
          </div>

          <el-button class="join-btn" size="small">加入</el-button>
        </div>

        <!-- 图片预览行 -->
        <div class="circle-photos" v-if="circle.photos && circle.photos.length > 0">
          <img 
            v-for="(img, idx) in circle.photos"
            :key="idx"
            :src="img"
            class="photo"
          >
        </div>

        <div class="circle-desc" v-if="circle.description">
          {{ circle.description }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { getCircles, searchCircles } from '@/utils/api'

const search = ref('')
const activeTab = ref('最新')
const navList = ['最新','最热']
const loading = ref(false)

// 圈子数据
const circles = ref([])

// 默认数据（当API失败时使用）
const defaultCircles = [
  {
    id: 1,
    name: "薛之谦的圈子",
    avatarUrl: "/mock/xzq.jpg",
    memberCount: 7248,
    photos: [
      "/mock/s1.jpg",
      "/mock/s2.jpg",
      "/mock/s3.jpg"
    ],
    description: "抖音也有超话了哈哈哈哈"
  },
  {
    id: 2,
    name: "黄子弘凡的圈子",
    avatarUrl: "/mock/hz1.jpg",
    memberCount: 18000,
    photos: [
      "/mock/h1.jpg",
      "/mock/h2.jpg",
      "/mock/h3.jpg"
    ],
    description: "各位蟹黄堡，大家好"
  }
]

// 加载圈子列表
const loadCircles = async () => {
  loading.value = true
  try {
    const sort = activeTab.value === '最热' ? 'hot' : 'new'
    
    let response
    if (search.value.trim()) {
      // 如果有搜索关键词,使用搜索API
      response = await searchCircles(search.value, 0, 20)
    } else {
      // 否则获取全部圈子
      response = await getCircles(0, 20, sort)
    }
    
    circles.value = (response.content || []).map(circle => ({
      id: circle.id,
      name: circle.name,
      avatarUrl: circle.avatarUrl || '/mock/default.jpg',
      memberCount: circle.memberCount || 0,
      photos: [], // 后端暂未实现圈子图片功能
      description: circle.description || ''
    }))
  } catch (error) {
    console.error('加载圈子失败:', error)
    // API失败时使用默认数据
    circles.value = defaultCircles
  } finally {
    loading.value = false
  }
}

// 监听tab切换
watch(activeTab, () => {
  loadCircles()
})

// 监听搜索(防抖)
let searchTimeout = null
watch(search, (newVal) => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    loadCircles()
  }, 500)
})

// 格式化数字
const formatCount = (count) => {
  if (count >= 10000) {
    return (count / 10000).toFixed(1) + '万'
  }
  return count.toString()
}

// 组件挂载时加载数据
onMounted(() => {
  loadCircles()
})
</script>

<style scoped>
.community-page {
  padding: 20px;
  color: #fff;
  width: 80%;
}

.search-bar {
  margin-bottom: 20px;
  width: 90%;
}

.nav-tabs {
  display: flex;
  gap: 20px;
  font-size: 18px;
  margin-bottom: 20px;
}

.tab-item {
  cursor: pointer;
  color: #aaa;
}

.tab-item.active {
  color: #fff;
  font-weight: bold;
  border-bottom: 2px solid #8b5cf6;
}

.circle-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 90%;
}

.circle-card {
  background: #1e1e1e;
  border-radius: 16px;
  padding: 16px;
}

.circle-header {
  display: flex;
  align-items: center;
  margin-bottom: 14px;
}

.avatar {
  width: 58px;
  height: 58px;
  border-radius: 50%;
  margin-right: 12px;
}

.info .name {
  font-size: 18px;
  font-weight: 600;
}

.info .count {
  color: #ccc;
  font-size: 14px;
}

.join-btn {
  margin-left: auto;
  border-radius: 15px;
}

.circle-photos {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.photo {
  width: 33%;
  border-radius: 12px;
  object-fit: cover;
  height: 120px;
}

.circle-desc {
  color: #ccc;
  font-size: 14px;
  padding-left: 4px;
}

.loading-container, .empty-container {
  text-align: center;
  padding: 60px 20px;
  color: #888;
  font-size: 16px;
}
</style>
