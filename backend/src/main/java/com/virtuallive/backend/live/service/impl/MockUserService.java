package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class MockUserService implements IUserService {

    @Override
    public UserInfoDTO getUserByToken(String token) {
        // === 模拟逻辑 ===

        // 1. 如果没传 Token，视为游客
        if (token == null || token.isEmpty()) {
            return new UserInfoDTO(0L, "游客_" + (int)(Math.random()*1000), null);
        }

        // 2. 模拟管理员
        if ("admin-token-123".equals(token)) {
            return new UserInfoDTO(1L, "超管大大", "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix");
        }

        // 3. 模拟普通用户 (假设 token 是 "user-token-xxx")
        if (token.startsWith("user-token")) {
            return new UserInfoDTO(88L, "活跃观众", "https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka");
        }

        // 4. Token 无效的情况
        return new UserInfoDTO(0L, "非法用户", null);
    }
}