package com.virtuallive.backend.live.service.impl;

import com.virtuallive.backend.live.entity.LiveRoom;
import com.virtuallive.backend.live.entity.LiveSession;
import com.virtuallive.backend.live.repository.LiveRoomRepository;
import com.virtuallive.backend.live.repository.LiveSessionRepository;
import com.virtuallive.backend.live.service.LiveStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LiveStreamServiceImpl implements LiveStreamService {

    @Autowired
    private LiveRoomRepository liveRoomRepository;
    @Autowired
    private LiveSessionRepository liveSessionRepository;

    @Override
    @Transactional
    public boolean startLive(String streamKey) {
        // 1. 校验 Stream Key
        Optional<LiveRoom> roomOpt = liveRoomRepository.findByStreamKey(streamKey);
        if (roomOpt.isEmpty()) {
            log.warn("推流鉴权失败：无效的 StreamKey -> {}", streamKey);
            return false;
        }

        LiveRoom room = roomOpt.get();
        // 2. 修改直播间状态
        room.setIsLive(true);
        liveRoomRepository.save(room);

        // 3. 创建新的场次记录
        LiveSession session = new LiveSession();
        session.setRoomId(room.getRoomId());
        session.setStartTime(LocalDateTime.now());
        session.setViewerCount(0);
        liveSessionRepository.save(session);

        log.info("直播开始：房间 [{}]", room.getRoomTitle());
        return true;
    }

    @Override
    @Transactional
    public void stopLive(String streamKey) {
        Optional<LiveRoom> roomOpt = liveRoomRepository.findByStreamKey(streamKey);
        if (roomOpt.isPresent()) {
            LiveRoom room = roomOpt.get();
            // 1. 修改直播间状态
            room.setIsLive(false);
            liveRoomRepository.save(room);

            // 2. 结算当前场次
            Optional<LiveSession> sessionOpt = liveSessionRepository.findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(room.getRoomId());
            if (sessionOpt.isPresent()) {
                LiveSession session = sessionOpt.get();
                session.setEndTime(LocalDateTime.now());
                liveSessionRepository.save(session);
            }
            log.info("直播结束：房间 [{}]", room.getRoomTitle());
        }
    }




}