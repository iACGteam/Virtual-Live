package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class MockUserService implements IUserService {

    @Override
    public UserInfoDTO getUserByToken(String token) {
        // === 针对数据库真实数据的适配 ===

        // 1. 模拟管理员 (假设数据库里没有ID为1的用户，这里可能会报错，建议你也改成88，或者去数据库加个ID为1的用户)
        if ("admin-token-123".equals(token)) {
            // 如果你数据库里没有 ID=1 的用户，这里也会报错！
            // 为了安全起见，我们在测试阶段统一返回 88
            return new UserInfoDTO(88L, "测试用户88", "https://api.dicebear.com/7.x/avataaars/svg?seed=Felix");
        }

        // 2. 【核心修改】无论有没有Token，无论Token对不对，统统返回数据库里存在的那个 ID (88)
        // 这样就能保证外键约束永远通过
        return new UserInfoDTO(88L, "TestUser", "https://api.dicebear.com/7.x/avataaars/svg?seed=Aneka");
    }
}