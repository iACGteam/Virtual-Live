package com.virtuallive.backend.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanmakuMessage {
    private Integer roomId;    // 房间号（用于区分不同直播间）
    private String senderName; // 发送者昵称
    private String content;    // 弹幕内容
    private String color;      // 弹幕颜色 (可选, 比如 #FFFFFF)

    // 新增字段
    private String type;       // 消息类型: "CHAT" (普通弹幕) 或 "GIFT" (礼物)
    private String giftName;   // 礼物名称 (如 "rocket", "flower")
    private Integer giftCount; // 礼物数量
}