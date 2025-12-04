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

// ==================== 圈子相关 API ====================

// 获取圈子列表
export async function getCircles(page = 0, size = 10, sort = 'new') {
  return request(`/circles?page=${page}&size=${size}&sort=${sort}`)
}

// 搜索圈子
export async function searchCircles(keyword, page = 0, size = 10) {
  return request(`/circles/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
}

// 获取分类圈子
export async function getCirclesByCategory(category, page = 0, size = 10) {
  return request(`/circles/category/${category}?page=${page}&size=${size}`)
}

// 获取官方圈子
export async function getOfficialCircles(page = 0, size = 10) {
  return request(`/circles/official?page=${page}&size=${size}`)
}

// 获取圈子详情
export async function getCircleById(id) {
  return request(`/circles/${id}`)
}

// ==================== 社区帖子相关 API ====================

// 获取社区帖子列表
export async function getCommunityPosts(page = 0, size = 20, sort = 'time', circleId = null) {
  const params = new URLSearchParams({ page, size, sort })
  if (circleId) params.append('circleId', circleId)
  return request(`/community/posts?${params}`)
}

// 全局搜索帖子
export async function searchCommunityPosts(keyword, page = 0, size = 20) {
  return request(`/community/posts/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
}

// 获取帖子详情
export async function getCommunityPostById(id) {
  return request(`/community/posts/${id}`)
}

// 获取用户的帖子列表
export async function getUserCommunityPosts(userId, page = 0, size = 20) {
  return request(`/community/posts/user/${userId}?page=${page}&size=${size}`)
}

// 获取热门帖子
export async function getTrendingPosts(limit = 10) {
  return request(`/community/posts/trending?limit=${limit}`)
}

// 创建帖子
export async function createCommunityPost(postData) {
  return request('/community/posts', {
    method: 'POST',
    body: JSON.stringify(postData)
  })
}

// 更新帖子
export async function updateCommunityPost(id, postData) {
  return request(`/community/posts/${id}`, {
    method: 'PUT',
    body: JSON.stringify(postData)
  })
}

// 删除帖子
export async function deleteCommunityPost(id, userId) {
  return request(`/community/posts/${id}?userId=${userId}`, {
    method: 'DELETE'
  })
}

// 点赞帖子
export async function likeCommunityPost(id) {
  return request(`/community/posts/${id}/like`, {
    method: 'POST'
  })
}

// ==================== 文件上传相关 API ====================

// 上传单个图片
export async function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  const token = localStorage.getItem('vlive-auth-token')
  const response = await fetch(`${BASE_URL}/upload/image`, {
    method: 'POST',
    headers: {
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    body: formData
  })
  
  const data = await response.json()
  if (data.code !== 0) {
    throw new Error(data.message || '上传失败')
  }
  
  return data.data
}

// 上传多个图片
export async function uploadImages(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  
  const token = localStorage.getItem('vlive-auth-token')
  const response = await fetch(`${BASE_URL}/upload/images`, {
    method: 'POST',
    headers: {
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    body: formData
  })
  
  const data = await response.json()
  if (data.code !== 0) {
    throw new Error(data.message || '上传失败')
  }
  
  return data.data
}

// 上传视频
export async function uploadVideo(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  const token = localStorage.getItem('vlive-auth-token')
  const response = await fetch(`${BASE_URL}/upload/video`, {
    method: 'POST',
    headers: {
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    body: formData
  })
  
  const data = await response.json()
  if (data.code !== 0) {
    throw new Error(data.message || '上传失败')
  }
  
  return data.data
}

// 上传音频
export async function uploadAudio(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  const token = localStorage.getItem('vlive-auth-token')
  const response = await fetch(`${BASE_URL}/upload/audio`, {
    method: 'POST',
    headers: {
      ...(token && { 'Authorization': `Bearer ${token}` })
    },
    body: formData
  })
  
  const data = await response.json()
  if (data.code !== 0) {
    throw new Error(data.message || '上传失败')
  }
  
  return data.data
}

export default {
  getVideos,
  getVideoById,
  getVideosByCategory,
  getComments,
  addComment,
  likeComment,
  getRecommendations,
  getPopularVideos,
  getCircles,
  searchCircles,
  getCirclesByCategory,
  getOfficialCircles,
  getCircleById,
  // 社区帖子相关
  getCommunityPosts,
  searchCommunityPosts,
  getCommunityPostById,
  getUserCommunityPosts,
  getTrendingPosts,
  createCommunityPost,
  updateCommunityPost,
  deleteCommunityPost,
  likeCommunityPost,
  // 文件上传
  uploadImage,
  uploadImages,
  uploadVideo,
  uploadAudio
}
