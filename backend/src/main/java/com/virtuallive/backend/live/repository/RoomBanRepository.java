package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.RoomBan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface RoomBanRepository extends JpaRepository<RoomBan, Integer> {
    // 查询用户在某房间是否还在禁言期内
    Optional<RoomBan> findFirstByRoomIdAndUserIdAndEndTimeAfter(Integer roomId, Integer userId, LocalDateTime now);
}