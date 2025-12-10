package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import com.virtuallive.backend.live.service.impl.InteractionServiceImpl; // 注意这里用 Impl 是为了方便调用 mute 检查，实际应用建议提取接口方法
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
                return; // 实际可以发送 error 消息回给客户端
            }

            // 2. 检查是否被禁言
            if (interactionService.isUserMuted(message.getRoomId(), user.getUserId().intValue())) {
                log.warn("用户[{}] 被禁言，消息丢弃", user.getUsername());
                // 可选：发送一条私有消息告诉用户被禁言了
                return;
            }

            // 3. 填充信息
            message.setSenderName(user.getUsername());
            message.setSenderAvatar(user.getAvatarUrl());

            // 4. 业务处理
            if ("GIFT".equalsIgnoreCase(message.getType()) || "SC".equalsIgnoreCase(message.getType())) {
                try {
                    interactionService.processGift(message, user);

                    // 构建广播文案
                    if ("SC".equalsIgnoreCase(message.getType())) {
                        // SC 内容已经在 processGift 里拼装好了
                    } else {
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
                interactionService.saveDanmaku(message, user);
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