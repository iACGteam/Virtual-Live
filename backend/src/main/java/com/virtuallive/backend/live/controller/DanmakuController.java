package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.entity.LiveRoom;
import com.virtuallive.backend.live.repository.LiveRoomRepository;
import com.virtuallive.backend.live.service.FanBadgeService;
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

import java.util.Optional;

@Slf4j
@Controller
public class DanmakuController {

    // WebSocket 消息模板
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private IUserService userService;
    @Autowired private InteractionServiceImpl interactionService;

    @Autowired private LiveRoomRepository liveRoomRepository;
    @Autowired private FanBadgeService fanBadgeService;

    @MessageMapping("/send-danmaku")
    public void sendDanmaku(@Payload DanmakuMessage message, StompHeaderAccessor headerAccessor) {
        try {
            // 1. 从 STOMP 头里拿 token（优先 Authorization: Bearer xxx，其次 token 兼容旧逻辑）
            String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
            String tokenHeader = headerAccessor.getFirstNativeHeader("token");

            String token = null;
            if (authHeader != null && !authHeader.isBlank()) {
                token = authHeader;
            } else if (tokenHeader != null && !tokenHeader.isBlank()) {
                // 兼容旧版：如果前端还在用 "token" 头，这里当作裸 token 处理
                token = tokenHeader;
            }

            UserInfoDTO user = userService.getUserByToken(token);
            if (user == null || user.getUserId() == 0) {
                log.warn("未授权的弹幕请求，tokenHeader={}, authHeader={}", tokenHeader, authHeader);
                return;
            }

            // 2. 判断消息类型（SC / GIFT 允许被禁言用户发送；普通弹幕不允许）
            boolean isSC = "SC".equalsIgnoreCase(message.getType());
            boolean isGift = "GIFT".equalsIgnoreCase(message.getType());
            boolean isPaidInteraction = isSC || isGift;

            if (!isPaidInteraction && interactionService.isUserMuted(message.getRoomId(), user.getUserId().intValue())) {
                log.warn("用户[{}] 被禁言，普通弹幕丢弃", user.getUsername());
                return;
            }

            // 3. 填充用户基础信息
            message.setSenderId(user.getUserId());
            message.setSenderName(user.getUsername());
            message.setSenderAvatar(user.getAvatarUrl());

            // 填充粉丝等级
            try {
                Integer vtuberId = findVtuberIdByRoomId(message.getRoomId());
                if (vtuberId != null) {
                    Integer level = fanBadgeService.getFanBadgeLevel(vtuberId, user.getUserId().intValue());
                    message.setFanLevel(level);
                }
            } catch (Exception e) {
                log.warn("获取粉丝等级失败", e);
            }

            if (isPaidInteraction) {
                // ===== 礼物 / SC 逻辑 =====
                try {
                    interactionService.processGift(message, user);

                    if (!isSC) {
                        message.setContent("送出了 " + message.getGiftName() + " x" + message.getGiftCount());
                    }

                    // 保存到弹幕表，以便历史记录可见
                    Integer danmakuId = interactionService.saveDanmaku(message, user);
                    message.setDanmakuId(danmakuId);

                    // 更新粉丝牌等级
                    try {
                        Integer vtuberId = findVtuberIdByRoomId(message.getRoomId());
                        Integer fanId = user.getUserId().intValue();
                        if (vtuberId != null && fanId != null) {
                            fanBadgeService.updateFanBadgeLevel(vtuberId, fanId);
                        } else {
                            log.warn("无法更新粉丝牌：vtuberId 或 fanId 为空, roomId={}, userId={}",
                                    message.getRoomId(), user.getUserId());
                        }
                    } catch (Exception e) {
                        log.error("更新粉丝牌失败（不影响礼物本身）：roomId={}, userId={}, error={}",
                                message.getRoomId(), user.getUserId(), e.getMessage(), e);
                    }

                    broadcast(message);
                } catch (Exception e) {
                    log.error("礼物/SC 发送失败: {}", e.getMessage(), e);
                }

            } else {
                // ===== 普通弹幕逻辑 =====
                message.setContent(HtmlUtils.htmlEscape(message.getContent()));
                message.setType("CHAT");

                Integer danmakuId = interactionService.saveDanmaku(message, user);
                message.setDanmakuId(danmakuId);

                broadcast(message);
            }

        } catch (Exception e) {
            log.error("弹幕处理异常", e);
            // 发送错误消息给用户
            try {
                String authHeader = headerAccessor.getFirstNativeHeader("Authorization");
                String tokenHeader = headerAccessor.getFirstNativeHeader("token");
                String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : tokenHeader;
                
                if (token != null) {
                     UserInfoDTO user = userService.getUserByToken(token);
                     if (user != null && user.getUserId() != 0) {
                        messagingTemplate.convertAndSend(
                            "/topic/errors/" + user.getUserId(), 
                            e.getMessage()
                        );
                     }
                }
            } catch (Exception ex) {
                log.error("发送错误消息失败", ex);
            }
        }
    }

    private void broadcast(DanmakuMessage message) {
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }

    private Integer findVtuberIdByRoomId(Integer roomId) {
        if (roomId == null) return null;
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);
        return roomOpt.map(LiveRoom::getVtuberId).orElse(null);
    }
}