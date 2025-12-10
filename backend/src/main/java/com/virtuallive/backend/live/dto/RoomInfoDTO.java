package com.virtuallive.backend.live.dto;

import lombok.Data;

@Data
public class RoomInfoDTO {
    private Integer roomId;
    private String title;
    private String description;
    private String coverUrl;
    private boolean isLive;
    private String playUrlHls; // HLS 播放地址
    private String playUrlFlv; // HTTP-FLV 播放地址
    private Integer viewerCount;
}