package com.virtuallive.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtuallive.entity.UserFollows;
import org.apache.ibatis.annotations.Param;

public interface UserFollowsMapper extends BaseMapper<UserFollows> {
    // 获取关注数
    Integer getFollowingCount(@Param("userId") Long userId);
    
    // 获取粉丝数
    Integer getFollowerCount(@Param("userId") Long userId);
    
    // 关注用户
    Integer followUser(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    // 取消关注
    Integer unfollowUser(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
    
    // 检查是否已关注
    Boolean isFollowing(@Param("followerId") Long followerId, @Param("followingId") Long followingId);
}
