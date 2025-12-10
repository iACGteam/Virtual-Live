package com.virtuallive.backend.live.service;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;

public interface InteractionService {
    void saveDanmaku(DanmakuMessage message, UserInfoDTO user);
    void processGift(DanmakuMessage message, UserInfoDTO user);
}