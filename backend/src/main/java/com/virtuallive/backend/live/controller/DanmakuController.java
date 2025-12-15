package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.entity.LiveRoom;
import com.virtuallive.backend.live.repository.LiveRoomRepository;
import com.virtuallive.backend.live.service.FanBadgeService;   // ★ 新增：粉丝牌服务
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

    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private IUserService userService;
    @Autowired private InteractionServiceImpl interactionService;

    // ★ 新增：用于从 roomId 查主播 vtuberId
    @Autowired private LiveRoomRepository liveRoomRepository;

    // ★ 新增：粉丝牌服务（用来更新粉丝牌等级）
    @Autowired private FanBadgeService fanBadgeService;

    @MessageMapping("/send-danmaku")
    public void sendDanmaku(@Payload DanmakuMessage message, StompHeaderAccessor headerAccessor) {
        try {
            // 1. 鉴权（从 STOMP 头里拿 token）
            String token = headerAccessor.getFirstNativeHeader("token");
            UserInfoDTO user = userService.getUserByToken(token);
            if (user == null || user.getUserId() == 0) {
                log.warn("未授权的弹幕请求，token={}", token);
                return;
            }

            // 2. 判断消息类型（SC / GIFT 允许被禁言用户发送；普通弹幕不允许）
            boolean isSC = "SC".equalsIgnoreCase(message.getType());
            boolean isGift = "GIFT".equalsIgnoreCase(message.getType());
            boolean isPaidInteraction = isSC || isGift;

            if (!isPaidInteraction && interactionService.isUserMuted(message.getRoomId(), user.getUserId().intValue())) {
                log.warn("用户[{}] 被禁言，普通弹幕丢弃", user.getUsername());
                // 这里可以考虑发一条私信提示用户被禁言了
                return;
            }

            // 3. 填充用户基础信息（昵称、头像）
            message.setSenderName(user.getUsername());
            message.setSenderAvatar(user.getAvatarUrl());

            // 4. 根据类型分别处理
            if (isPaidInteraction) {
                // ===================== 礼物 / SC 逻辑 =====================
                try {
                    // 4.1 先让业务层完成扣费、写 gift_donations、更新 session 收益等
                    //     ★ 注意：processGift 内部如果是 SC，会自动调用 saveDanmaku 并设置 danmakuId
                    interactionService.processGift(message, user);

                    // 4.2 如果是普通礼物（非 SC），这里统一设置一个展示文案
                    if (!isSC) {
                        // 示例文案：送出了 火箭 x1
                        message.setContent("送出了 " + message.getGiftName() + " x" + message.getGiftCount());
                    }
                    // SC 的文案已经在 processGift 里进行了包装（前缀 [SC xx s]）

                    // ===================== ★ 新增：更新粉丝牌等级 =====================
                    try {
                        // 1. 找到当前房间对应的主播 vtuberId
                        Integer vtuberId = findVtuberIdByRoomId(message.getRoomId());
                        // 2. 当前送礼粉丝的 userId
                        Integer fanId = user.getUserId().intValue();

                        if (vtuberId != null && fanId != null) {
                            // 3. 调用粉丝牌服务，根据累计打赏金额更新 fan_badges 表
                            fanBadgeService.updateFanBadgeLevel(vtuberId, fanId);
                        } else {
                            log.warn("无法更新粉丝牌：vtuberId 或 fanId 为空, roomId={}, userId={}",
                                    message.getRoomId(), user.getUserId());
                        }
                    } catch (Exception e) {
                        // 为了保证直播 / 送礼主流程不受影响，这里只打日志，不抛出
                        log.error("更新粉丝牌失败（不影响礼物本身）：roomId={}, userId={}, error={}",
                                message.getRoomId(), user.getUserId(), e.getMessage(), e);
                    }
                    // ===================== ★ 新增部分结束 =====================

                    // 4.3 广播消息给房间内所有观众（礼物 / SC）
                    broadcast(message);

                } catch (Exception e) {
                    // 如果礼物/SC 发送失败（余额不足等），这里会捕获异常
                    log.error("礼物/SC 发送失败: {}", e.getMessage(), e);
                    // 可选：可以考虑给当前用户发一条私有消息提示失败原因
                }

            } else {
                // ===================== 普通弹幕逻辑 =====================
                // 4.4 防 XSS：转义 HTML
                message.setContent(HtmlUtils.htmlEscape(message.getContent()));
                message.setType("CHAT");

                // 4.5 保存弹幕到 danmaku 表，拿到自增 ID 返回给前端
                Integer danmakuId = interactionService.saveDanmaku(message, user);
                message.setDanmakuId(danmakuId);

                // 4.6 广播普通弹幕
                broadcast(message);
            }

        } catch (Exception e) {
            log.error("弹幕处理异常", e);
        }
    }

    /**
     * 封装广播逻辑：
     * 把消息推送到 /topic/danmaku/{roomId}
     */
    private void broadcast(DanmakuMessage message) {
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }

    /**
     * ★ 新增辅助方法：
     * 根据 roomId 从 live_rooms 表找到对应的 vtuberId（主播用户ID）
     */
    private Integer findVtuberIdByRoomId(Integer roomId) {
        if (roomId == null) return null;
        Optional<LiveRoom> roomOpt = liveRoomRepository.findById(roomId);
        return roomOpt.map(LiveRoom::getVtuberId).orElse(null);
    }
}