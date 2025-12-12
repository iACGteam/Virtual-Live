// src/main/java/com/virtuallive/backend/live/repository/LiveSessionRepository.java
package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.LiveSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LiveSessionRepository extends JpaRepository<LiveSession, Integer> {
    // 查找该房间最后一条“未结束”的直播记录
    Optional<LiveSession> findFirstByRoomIdAndEndTimeIsNullOrderByStartTimeDesc(Integer roomId);
}