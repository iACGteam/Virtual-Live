package com.virtuallive.backend.live.service;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;

public interface InteractionService {
    // 修改返回值 void -> Integer，返回生成的弹幕ID
    Integer saveDanmaku(DanmakuMessage message, UserInfoDTO user);
    void processGift(DanmakuMessage message, UserInfoDTO user);

    // 新增解除禁言接口
    void unmuteUser(Integer roomId, Integer userId);
}