package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import com.virtuallive.backend.live.service.impl.InteractionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Slf4j
@Controller
public class DanmakuController {

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private IUserService userService;
    @Autowired private InteractionServiceImpl interactionService;

    @MessageMapping("/send-danmaku")
    public void sendDanmaku(@Payload DanmakuMessage message, StompHeaderAccessor headerAccessor) {
        try {
            // 1. 鉴权
            String token = headerAccessor.getFirstNativeHeader("token");
            UserInfoDTO user = userService.getUserByToken(token);
            if (user == null || user.getUserId() == 0) {
                log.warn("未授权的弹幕请求");
                return;
            }

            // 2. 检查是否被禁言
            // 修正：如果是 SC (Super Chat) 或 礼物 (GIFT)，允许被禁言用户发送
            boolean isSC = "SC".equalsIgnoreCase(message.getType());
            boolean isGift = "GIFT".equalsIgnoreCase(message.getType());
            boolean isPaidInteraction = isSC || isGift;

            if (!isPaidInteraction && interactionService.isUserMuted(message.getRoomId(), user.getUserId().intValue())) {
                log.warn("用户[{}] 被禁言，消息丢弃", user.getUsername());
                // 可选：发送一条私有消息告诉用户被禁言了
                return;
            }

            // 3. 填充信息
            message.setSenderName(user.getUsername());
            message.setSenderAvatar(user.getAvatarUrl());

            // 4. 业务处理
            if (isPaidInteraction) {
                try {
                    // processGift 内部如果为 SC 会自动调用 saveDanmaku 并填充 id 到 message
                    interactionService.processGift(message, user);

                    // 构建广播文案
                    if (!isSC) {
                        message.setContent("送出了 " + message.getGiftName() + " x" + message.getGiftCount());
                    }
                    broadcast(message);

                } catch (Exception e) {
                    log.error("礼物/SC发送失败: " + e.getMessage());
                }
            } else {
                // 普通弹幕
                message.setContent(HtmlUtils.htmlEscape(message.getContent()));
                message.setType("CHAT");

                // 保存并获取 ID，返回给前端
                Integer danmakuId = interactionService.saveDanmaku(message, user);
                message.setDanmakuId(danmakuId);

                broadcast(message);
            }

        } catch (Exception e) {
            log.error("弹幕处理异常", e);
        }
    }

    private void broadcast(DanmakuMessage message) {
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }
}