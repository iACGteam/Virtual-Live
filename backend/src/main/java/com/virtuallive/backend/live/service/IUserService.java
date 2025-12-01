package com.virtuallive.backend.live.service;

import com.virtuallive.backend.live.dto.UserInfoDTO;

/**
 * 用户服务接口
 * 目前由 Mock 实现，未来替换为组员的真实 Service
 */
public interface IUserService {
    /**
     * 根据 Token 获取用户信息
     * @param token 前端传来的 Token 字符串
     * @return 用户简要信息
     */
    UserInfoDTO getUserByToken(String token);
}