// src/main/java/com/virtuallive/backend/live/repository/LiveRoomRepository.java
package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.entity.LiveRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LiveRoomRepository extends JpaRepository<LiveRoom, Integer> {
    // 自动生成 SQL: select * from live_rooms where stream_key = ?
    Optional<LiveRoom> findByStreamKey(String streamKey);


}