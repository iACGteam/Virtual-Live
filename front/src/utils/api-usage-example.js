// 在Vue组件中使用新API的示例

import { 
  toggleVideoLike, 
  toggleVideoFavorite, 
  toggleFollow,
  toggleCircleMembership,
  checkin,
  getCheckinInfo
} from '@/utils/api'
import { getCurrentUserId } from '@/utils/auth'

export default {
  data() {
    return {
      // 当前登录用户ID
      currentUserId: getCurrentUserId() || 1, // 如果未登录，默认为1（临时方案）
      
      // 视频相关状态
      isLiked: false,
      isFavorited: false,
      likeCount: 0,
      
      // 关注相关
      isFollowing: false,
      
      // 圈子相关
      isMember: false,
      hasCheckedIn: false,
      continuousDays: 0
    }
  },
  
  methods: {
    // ========== 点赞功能 ==========
    async handleLike(videoId) {
      try {
        const result = await toggleVideoLike(videoId, this.currentUserId)
        if (result.code === 200) {
          this.isLiked = result.data.isLiked
          this.likeCount = result.data.likeCount
          this.$message.success(this.isLiked ? '点赞成功' : '取消点赞')
        }
      } catch (error) {
        console.error('点赞失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    // ========== 收藏功能 ==========
    async handleFavorite(videoId) {
      try {
        const result = await toggleVideoFavorite(videoId, this.currentUserId)
        if (result.code === 200) {
          this.isFavorited = result.data.isFavorited
          this.$message.success(this.isFavorited ? '收藏成功' : '取消收藏')
        }
      } catch (error) {
        console.error('收藏失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    // ========== 关注功能 ==========
    async handleFollow(authorId) {
      try {
        const result = await toggleFollow(authorId, this.currentUserId)
        if (result.code === 200) {
          this.isFollowing = result.data.isFollowing
          this.$message.success(this.isFollowing ? '关注成功' : '取消关注')
        }
      } catch (error) {
        console.error('关注失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    // ========== 加入圈子 ==========
    async handleJoinCircle(circleId) {
      try {
        const result = await toggleCircleMembership(circleId, this.currentUserId)
        if (result.code === 200) {
          this.isMember = result.data.isMember
          this.$message.success(this.isMember ? '加入成功' : '已退出圈子')
        }
      } catch (error) {
        console.error('加入圈子失败:', error)
        this.$message.error('操作失败，请重试')
      }
    },
    
    // ========== 签到功能 ==========
    async handleCheckin(circleId) {
      try {
        const result = await checkin(circleId, this.currentUserId)
        if (result.code === 200) {
          this.hasCheckedIn = true
          this.continuousDays = result.data.continuousDays
          this.$message.success(`签到成功！连续签到${result.data.continuousDays}天，获得${result.data.rewardPoints}积分`)
        }
      } catch (error) {
        console.error('签到失败:', error)
        this.$message.error(error.message || '签到失败，请重试')
      }
    },
    
    // ========== 加载签到信息 ==========
    async loadCheckinInfo(circleId) {
      try {
        const result = await getCheckinInfo(circleId, this.currentUserId)
        if (result.code === 200) {
          this.hasCheckedIn = result.data.hasCheckedInToday
          this.continuousDays = result.data.continuousDays
        }
      } catch (error) {
        console.error('加载签到信息失败:', error)
      }
    }
  },
  
  mounted() {
    // 组件挂载时加载初始数据
    // 例如：加载签到信息
    // this.loadCheckinInfo(this.circleId)
  }
}
