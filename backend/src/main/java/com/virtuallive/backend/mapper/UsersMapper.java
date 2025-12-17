package com.virtuallive.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.virtuallive.entity.Users;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UsersMapper extends BaseMapper<Users> {
    // 根据用户名或邮箱查询用户
    Users selectByUsernameOrEmail(@Param("username") String username, @Param("email") String email);
    
    // 根据用户名查询用户
    Users selectByUsername(@Param("username") String username);
    
    // 根据邮箱查询用户
    Users selectByEmail(@Param("email") String email);
    
    // 获取关注列表
    List<Users> selectFollowingList(@Param("followerId") Long followerId);
    
    // 获取粉丝列表
    List<Users> selectFollowerList(@Param("followingId") Long followingId);
}