package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import com.virtuallive.backend.model.entity.User;
import com.virtuallive.backend.repository.UserFollowRepository;
import com.virtuallive.backend.repository.UserRepository;
import com.virtuallive.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Live 模块正式用户服务实现
 *
 * 功能：
 * 1. 从前端传来的 JWT 中解析出用户名（subject）
 * 2. 再通过主项目的 UserRepository 查询用户信息
 *
 * 约定：
 * - 前端需要在请求头里带上：
 *   Authorization: Bearer <token>
 * - 这个 token 就是 AuthController 登录/注册时返回的 token
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    @Override
    public UserInfoDTO getUserByToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("getUserByToken 调用时 token 为空");
            return null;
        }

        try {
            // 支持既传裸 token，又传 "Bearer xxx" 两种形式
            String rawToken = token;
            if (rawToken.toLowerCase().startsWith("bearer ")) {
                rawToken = rawToken.substring(7).trim();
            }

            // 1. 从 JWT 中解析用户名（登录时是以 username 作为 subject）
            String username = jwtUtil.extractUsername(rawToken);
            if (username == null || username.isBlank()) {
                log.warn("JWT 中未解析出用户名, token={}", token);
                return null;
            }

            // 2. 根据用户名查用户
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在: " + username));

            // 3. 封装成 Live 模块用的 UserInfoDTO
            long fanCount = userFollowRepository.countByFollowing(user);
            return new UserInfoDTO(
                    user.getUserId().longValue(),
                    user.getUsername(),
                    user.getAvatarUrl(),
                    (int) fanCount
            );
        } catch (Exception e) {
            log.warn("解析 token 获取用户失败, token={}, error={}", token, e.getMessage());
            return null;
        }
    }

    @Override
    public UserInfoDTO getUserById(Integer userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    long fanCount = userFollowRepository.countByFollowing(user);
                    return new UserInfoDTO(
                            user.getUserId().longValue(),
                            user.getUsername(),
                            user.getAvatarUrl(),
                            (int) fanCount
                    );
                })
                .orElse(null);
    }
}