package com.virtuallive.backend.live.dto;

import lombok.Data;

@Data
public class DanmakuMessage {
    // 新增字段：弹幕ID，用于前端删除或引用
    private Integer danmakuId;

    private String type;      // 消息类型: CHAT, GIFT, LIKE, JOIN, SC
    private Integer roomId;   // 直播间ID
    private String content;   // 弹幕内容
    private String color;     // 弹幕颜色 (十六进制)

    // 发送者信息 (后端填充)
    private String senderName;
    private String senderAvatar;

    // 礼物特有字段
    private String giftName;
    private int giftCount;
    private double giftPrice;

    // SC 特有字段
    private int scDuration; // SC停留时间(秒)
}