package com.virtuallive.backend.live.controller;

import com.virtuallive.backend.live.dto.DanmakuMessage;
import com.virtuallive.backend.live.dto.UserInfoDTO;
import com.virtuallive.backend.live.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor; // 注意引入这个
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

@Controller
public class DanmakuController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IUserService userService; // 注入接口

    @MessageMapping("/send-danmaku")
    // 添加 StompHeaderAccessor 参数来获取 Header
    public void sendDanmaku(@Payload DanmakuMessage message, StompHeaderAccessor headerAccessor) {

        // 1. 从 WebSocket 消息头中获取 "token" (前端需要传)
        // 注意：getFirstNativeHeader 获取的是前端 stompClient.send headers 里的值
        String token = headerAccessor.getFirstNativeHeader("token");

        // 2. 调用 Mock 服务获取“真实”用户信息
        UserInfoDTO user = userService.getUserByToken(token);

        // 3. 使用查出来的用户信息覆盖消息内容
        message.setSenderName(user.getUsername());
        // 可以在 DanmakuMessage 里加一个 avatar 字段，把 user.getAvatarUrl() 塞进去

        // 4. 防 XSS 处理
        String safeContent = HtmlUtils.htmlEscape(message.getContent());
        message.setContent(safeContent);

        System.out.println("收到弹幕 [" + message.getRoomId() + "] " + user.getUsername() + ": " + safeContent);

        // 5. 广播
        String destination = "/topic/danmaku/" + message.getRoomId();
        messagingTemplate.convertAndSend(destination, message);
    }
}









//package com.virtuallive.backend.live.controller;
//
//import com.virtuallive.backend.live.dto.DanmakuMessage;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.util.HtmlUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//
//@Controller
//public class DanmakuController {
//
//    @Autowired
//    private SimpMessagingTemplate messagingTemplate;
//
//    /**
//     * 接收前端发送的弹幕
//     * 前端发送地址: /app/send-danmaku
//     */
//    @MessageMapping("/send-danmaku")
//    public void sendDanmaku(@Payload DanmakuMessage message) {
//        // 1. 安全转义：防止 XSS 攻击 (把 <script> 转成 &lt;script&gt;)
//        String safeContent = HtmlUtils.htmlEscape(message.getContent());
//        message.setContent(safeContent);
//
//        System.out.println("收到弹幕 [" + message.getRoomId() + "] " + message.getSenderName() + ": " + safeContent);
//
//        // 2. TODO: 这里可以调用 Service 层把弹幕存入数据库 (live_danmaku 表)
//
//        // 3. 广播给指定房间的所有订阅者
//        // 订阅地址: /topic/danmaku/{roomId}
//        String destination = "/topic/danmaku/" + message.getRoomId();
//        messagingTemplate.convertAndSend(destination, message);
//    }
//}
