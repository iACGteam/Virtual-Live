// API 基础配置
const BASE_URL = 'http://localhost:8081/api/v1'

// 通用请求方法
async function request(url, options = {}) {
  const token = localStorage.getItem('vlive-auth-token')
  
  const defaultOptions = {
    headers: {
      'Content-Type': 'application/json',
      ...(token && { 'Authorization': `Bearer ${token}` })
    }
  }
  
  const mergedOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...options.headers
    }
  }
  
  try {
    const response = await fetch(`${BASE_URL}${url}`, mergedOptions)
    const data = await response.json()
    
    if (data.code !== 0) {
      throw new Error(data.message || '请求失败')
    }
    
    return data.data
  } catch (error) {
    console.error('API Error:', error)
    throw error
  }
}

// ==================== 视频相关 API ====================

// 获取视频列表
export async function getVideos(page = 0, size = 10, sort = 'time') {
  return request(`/videos?page=${page}&size=${size}&sort=${sort}`)
}

// 获取视频详情
export async function getVideoById(id) {
  return request(`/videos/${id}`)
}

// 获取分类视频
export async function getVideosByCategory(category) {
  return request(`/videos/category/${category}`)
}

// ==================== 评论相关 API ====================

// 获取视频评论
export async function getComments(videoId, page = 0, size = 20, sort = 'time') {
  return request(`/comments/video/${videoId}?page=${page}&size=${size}&sort=${sort}`)
}

// 发表评论
export async function addComment(videoId, userId, content) {
  return request(`/comments/video/${videoId}`, {
    method: 'POST',
    body: JSON.stringify({ userId, content })
  })
}

// 点赞评论
export async function likeComment(commentId) {
  return request(`/comments/${commentId}/like`, {
    method: 'POST'
  })
}

// ==================== 主页相关 API ====================

// 获取推荐内容
export async function getRecommendations() {
  return request('/videos?page=0&size=20&sort=popular')
}

// 获取热门内容
export async function getPopularVideos() {
  return request('/videos?page=0&size=10&sort=popular')
}

export default {
  getVideos,
  getVideoById,
  getVideosByCategory,
  getComments,
  addComment,
  likeComment,
  getRecommendations,
  getPopularVideos
}
