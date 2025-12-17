package com.virtuallive.backend.live.service;

import com.virtuallive.backend.live.entity.LiveRoom;

public interface LiveRoomService {
    /**
     * 为给定主播（vtuberId）获取或创建一个直播间
     */
    LiveRoom getOrCreateRoomForVtuber(Integer vtuberId);
}