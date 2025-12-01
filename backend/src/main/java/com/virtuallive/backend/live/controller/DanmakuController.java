package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class DanmakuController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private IUserService userService;

    @MessageMapping("/send-danmaku")
    public void sendDanmaku(@Payload DanmakuMessage message, StompHeaderAccessor headerAccessor) {
        // 1. 鉴权 (Mock)
        String token = headerAccessor.getFirstNativeHeader("token");
        UserInfoDTO user = userService.getUserByToken(token);

        // 2. 填充用户信息
        message.setSenderName(user.getUsername());
//        message.setSenderAvatar(user.getAvatarUrl());

        // 3. 判断消息类型
        if ("GIFT".equalsIgnoreCase(message.getType())) {
            handleGiftMessage(message, user);
        } else {
            handleChatMessage(message);
        }

        // 4. 广播消息
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }

    private void handleChatMessage(DanmakuMessage message) {
        // 防 XSS
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setType("CHAT");
    }

    private void handleGiftMessage(DanmakuMessage message, UserInfoDTO user) {
        // === 模拟扣款逻辑 ===
        // 真实场景：userService.deductBalance(user.getUserId(), giftPrice);
        System.out.println("💰 [Mock扣款] 用户 " + user.getUsername() + " 送出了 " + message.getGiftCount() + " 个 " + message.getGiftName());

        // === 模拟入库逻辑 ===
        // 真实场景：giftRepository.save(...);
        System.out.println("📝 [Mock记录] 礼物记录已保存到数据库 (模拟)");

        // 设置特殊的提示文案
        message.setContent("送出了 " + message.getGiftName() + " x" + message.getGiftCount());
    }
}