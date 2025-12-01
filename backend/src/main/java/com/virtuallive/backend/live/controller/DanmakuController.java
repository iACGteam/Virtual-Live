package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class DanmakuController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 接收前端发送的弹幕
     * 前端发送地址: /app/send-danmaku
     */
    @MessageMapping("/send-danmaku")
    public void sendDanmaku(@Payload DanmakuMessage message) {
        // 1. 安全转义：防止 XSS 攻击 (把 <script> 转成 &lt;script&gt;)
        String safeContent = HtmlUtils.htmlEscape(message.getContent());
        message.setContent(safeContent);

        System.out.println("收到弹幕 [" + message.getRoomId() + "] " + message.getSenderName() + ": " + safeContent);

        // 2. TODO: 这里可以调用 Service 层把弹幕存入数据库 (live_danmaku 表)

        // 3. 广播给指定房间的所有订阅者
        // 订阅地址: /topic/danmaku/{roomId}
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }
}