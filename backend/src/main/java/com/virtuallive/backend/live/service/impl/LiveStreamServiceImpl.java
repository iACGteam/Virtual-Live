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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Collections;

@Slf4j
@Service
public class LiveStreamServiceImpl implements LiveStreamService {

    @Autowired
    private LiveRoomRepository liveRoomRepository;
    @Autowired
    private LiveSessionRepository liveSessionRepository;
    @Autowired
    private com.virtuallive.backend.live.repository.DanmakuRepository danmakuRepository;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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

        // 广播房间开始控制消息，通知前端清理（确保旧数据不会残留于观众端）
        try {
            String dest = "/topic/room-control/" + room.getRoomId();
            messagingTemplate.convertAndSend(dest, Collections.singletonMap("action", "ROOM_STARTED"));
        } catch (Exception ex) {
            log.warn("发送房间开始通知失败", ex);
        }

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
                // 清除该场次的弹幕数据（物理删除），确保下一次进入不会看到本场历史弹幕
                try {
                    Integer sid = session.getSessionId();
                    if (sid != null) {
                        danmakuRepository.deleteBySessionId(sid);
                    }
                } catch (Exception ex) {
                    log.error("清除弹幕失败", ex);
                }

                // 广播房间结束控制消息，通知前端清空在线榜与弹幕显示
                try {
                    String dest = "/topic/room-control/" + room.getRoomId();
                    messagingTemplate.convertAndSend(dest, Collections.singletonMap("action", "ROOM_ENDED"));
                } catch (Exception ex) {
                    log.warn("发送房间结束通知失败", ex);
                }
            }
            log.info("直播结束：房间 [{}]", room.getRoomTitle());
        }
    }




}