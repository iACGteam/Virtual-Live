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

// 回复评论
export async function replyComment(parentCommentId, videoId, userId, content) {
  return request(`/comments/${parentCommentId}/reply`, {
    method: 'POST',
    body: JSON.stringify({ videoId, userId, content })
  })
}

// 删除评论
export async function deleteComment(commentId, userId) {
  return request(`/comments/${commentId}?userId=${userId}`, {
    method: 'DELETE'
  })
}

// 获取评论的回复列表
export async function getCommentReplies(commentId, page = 0, size = 10) {
  return request(`/comments/${commentId}/replies?page=${page}&size=${size}`)
}

// 获取评论总数
export async function getCommentCount(videoId) {
  return request(`/comments/video/${videoId}/count`)
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

// 获取圈子内的帖子列表
export async function getCirclePosts(circleId, page = 0, size = 20, sort = 'new') {
  return request(`/circles/${circleId}/posts?page=${page}&size=${size}&sort=${sort}`)
}

// 加入圈子
export async function joinCircle(circleId, userId) {
  return request(`/circles/${circleId}/join`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 退出圈子
export async function leaveCircle(circleId, userId) {
  return request(`/circles/${circleId}/leave`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查是否是圈子成员
export async function checkCircleMember(circleId, userId) {
  return request(`/circles/${circleId}/check-member?userId=${userId}`)
}

// 获取圈子成员列表
export async function getCircleMembers(circleId, page = 0, size = 20) {
  return request(`/circles/${circleId}/members?page=${page}&size=${size}`)
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

// ==================== 点赞相关 API ====================

// 点赞/取消点赞视频
export async function toggleVideoLike(videoId, userId) {
  return request(`/likes/video/${videoId}`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 点赞/取消点赞评论
export async function toggleCommentLike(commentId, userId) {
  return request(`/likes/comment/${commentId}`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查是否已点赞
export async function checkLike(userId, contentId, contentType) {
  return request(`/likes/check?userId=${userId}&contentId=${contentId}&contentType=${contentType}`)
}

// 获取点赞数
export async function getLikeCount(contentId, contentType) {
  return request(`/likes/count?contentId=${contentId}&contentType=${contentType}`)
}

// ==================== 收藏相关 API ====================

// 收藏/取消收藏视频
export async function toggleVideoFavorite(videoId, userId) {
  return request(`/favorites/video/${videoId}`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查是否已收藏
export async function checkFavorite(userId, contentId, contentType) {
  return request(`/favorites/check?userId=${userId}&contentId=${contentId}&contentType=${contentType}`)
}

// 获取收藏数
export async function getFavoriteCount(contentId, contentType) {
  return request(`/favorites/count?contentId=${contentId}&contentType=${contentType}`)
}

// 获取用户收藏列表
export async function getUserFavorites(userId, contentType = 'post', page = 0, size = 20) {
  return request(`/favorites/user/${userId}?contentType=${contentType}&page=${page}&size=${size}`)
}

// ==================== 关注相关 API ====================

// 关注/取消关注用户
export async function toggleFollow(followingId, userId) {
  return request(`/follow/${followingId}`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查是否已关注
export async function checkFollow(followerId, followingId) {
  return request(`/follow/check?followerId=${followerId}&followingId=${followingId}`)
}

// 获取粉丝列表
export async function getFollowers(userId, page = 0, size = 20) {
  return request(`/follow/followers/${userId}?page=${page}&size=${size}`)
}

// 获取关注列表
export async function getFollowing(userId, page = 0, size = 20) {
  return request(`/follow/following/${userId}?page=${page}&size=${size}`)
}

// 获取粉丝数和关注数
export async function getFollowCount(userId) {
  return request(`/follow/count/${userId}`)
}

// ==================== 圈子成员相关 API ====================

// 加入/退出圈子
export async function toggleCircleMembership(circleId, userId) {
  return request(`/circles/${circleId}/join`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查是否是圈子成员
export async function checkCircleMembership(circleId, userId) {
  return request(`/circles/${circleId}/check?userId=${userId}`)
}

// 获取圈子成员列表
export async function getCircleMembers(circleId, page = 0, size = 20) {
  return request(`/circles/${circleId}/members?page=${page}&size=${size}`)
}

// 获取用户加入的圈子列表
export async function getUserCircles(userId, page = 0, size = 20) {
  return request(`/circles/user/${userId}/joined?page=${page}&size=${size}`)
}

// ==================== 签到相关 API ====================

// 签到
export async function checkin(circleId, userId) {
  return request(`/checkin/circle/${circleId}`, {
    method: 'POST',
    body: JSON.stringify({ userId })
  })
}

// 检查今天是否已签到
export async function checkTodayCheckin(circleId, userId) {
  return request(`/checkin/circle/${circleId}/check?userId=${userId}`)
}

// 获取签到信息
export async function getCheckinInfo(circleId, userId) {
  return request(`/checkin/circle/${circleId}/info?userId=${userId}`)
}

// ==================== 搜索相关 API ====================

// 全局搜索
export async function globalSearch(keyword, page = 0, size = 10) {
  return request(`/search?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`)
}

// ==================== 活动中心相关 API ====================

// 获取圈子活动动态
export async function getCircleActivity(circleId, page = 0, size = 10) {
  return request(`/activity/circle/${circleId}?page=${page}&size=${size}`)
}

// 获取用户动态
export async function getUserFeed(userId, page = 0, size = 20) {
  return request(`/activity/user/${userId}/feed?page=${page}&size=${size}`)
}

// 获取热门动态
export async function getTrending() {
  return request('/activity/trending')
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
  replyComment,
  deleteComment,
  getCommentReplies,
  getCommentCount,
  getRecommendations,
  getPopularVideos,
  getCircles,
  searchCircles,
  getCirclesByCategory,
  getOfficialCircles,
  getCircleById,
  getCirclePosts,
  joinCircle,
  leaveCircle,
  checkCircleMember,
  getCircleMembers,
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
  // 点赞相关
  toggleVideoLike,
  toggleCommentLike,
  checkLike,
  getLikeCount,
  // 收藏相关
  toggleVideoFavorite,
  checkFavorite,
  getFavoriteCount,
  getUserFavorites,
  // 关注相关
  toggleFollow,
  checkFollow,
  getFollowers,
  getFollowing,
  getFollowCount,
  // 圈子成员相关
  toggleCircleMembership,
  checkCircleMembership,
  getCircleMembers,
  getUserCircles,
  // 签到相关
  checkin,
  checkTodayCheckin,
  getCheckinInfo,
  // 搜索相关
  globalSearch,
  // 活动中心相关
  getCircleActivity,
  getUserFeed,
  getTrending,
  // 文件上传
  uploadImage,
  uploadImages,
  uploadVideo,
  uploadAudio
}
