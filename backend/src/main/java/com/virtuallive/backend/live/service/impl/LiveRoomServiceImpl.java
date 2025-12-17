package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.entity.LiveRoom;
import com.virtuallive.backend.live.repository.LiveRoomRepository;
import com.virtuallive.backend.live.service.LiveRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class LiveRoomServiceImpl implements LiveRoomService {

    private final LiveRoomRepository liveRoomRepository;

    @Override
    @Transactional
    public LiveRoom getOrCreateRoomForVtuber(Integer vtuberId) {
        // 1. 先查是否已有房间
        return liveRoomRepository.findFirstByVtuberId(vtuberId)
                .orElseGet(() -> {
                    // 2. 如果没有，就创建一个
                    LiveRoom room = new LiveRoom();
                    room.setVtuberId(vtuberId);
                    room.setRoomTitle("主播 " + vtuberId + " 的直播间");
                    room.setDescription("欢迎来到直播间");
                    room.setCategory("General");
                    room.setIsLive(false);
                    room.setStreamKey("room_" + vtuberId + "_" + UUID.randomUUID().toString().substring(0,8));
                    // rtmpServer 可以先用你的默认配置，也可以留 null
                    room.setRtmpServer("rtmp://localhost/live");

                    LiveRoom saved = liveRoomRepository.save(room);
                    log.info("为主播 {} 创建直播间，roomId={}", vtuberId, saved.getRoomId());
                    return saved;
                });
    }
}