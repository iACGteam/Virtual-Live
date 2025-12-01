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
}