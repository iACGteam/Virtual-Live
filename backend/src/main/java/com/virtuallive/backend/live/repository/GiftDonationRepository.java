package com.virtuallive.backend.live.repository;

import com.virtuallive.backend.live.dto.LeaderboardItemDTO;
import com.virtuallive.backend.live.entity.GiftDonation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GiftDonationRepository extends JpaRepository<GiftDonation, Integer> {

    /**
     * 统计某房间在指定时间范围内的打赏榜单
     * 修正点：将 User 的包路径修改为 com.virtuallive.backend.model.entity.User
     */

    // 1. 根据时间范围统计（例如：日榜、月榜）
    @Query("SELECT new com.virtuallive.backend.live.dto.LeaderboardItemDTO(d.senderId, u.username, u.avatarUrl, SUM(d.totalValue), 0) " +
            "FROM GiftDonation d " +
            "JOIN com.virtuallive.backend.model.entity.User u ON d.senderId = u.userId " +
            "WHERE d.donationTime BETWEEN :startTime AND :endTime " +
            "GROUP BY d.senderId, u.username, u.avatarUrl " +
            "ORDER BY SUM(d.totalValue) DESC")
    List<LeaderboardItemDTO> findLeaderboardByTimeRange(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);

    // 2. 根据 Session ID 统计（本次直播榜）
    @Query("SELECT new com.virtuallive.backend.live.dto.LeaderboardItemDTO(d.senderId, u.username, u.avatarUrl, SUM(d.totalValue), 0) " +
            "FROM GiftDonation d " +
            "JOIN com.virtuallive.backend.model.entity.User u ON d.senderId = u.userId " +
            "WHERE d.sessionId = :sessionId " +
            "GROUP BY d.senderId, u.username, u.avatarUrl " +
            "ORDER BY SUM(d.totalValue) DESC")
    List<LeaderboardItemDTO> findLeaderboardBySession(@Param("sessionId") Integer sessionId);

    // 3. 根据房间ID和时间范围统计（此方法是你报错日志中具体指出的那个）
    @Query("SELECT new com.virtuallive.backend.live.dto.LeaderboardItemDTO(d.senderId, u.username, u.avatarUrl, SUM(d.totalValue), 0) " +
            "FROM GiftDonation d " +
            "JOIN com.virtuallive.backend.model.entity.User u ON d.senderId = u.userId " +
            "JOIN com.virtuallive.backend.live.entity.LiveSession ls ON d.sessionId = ls.sessionId " +
            "WHERE ls.roomId = :roomId AND d.donationTime >= :startTime " +
            "GROUP BY d.senderId, u.username, u.avatarUrl " +
            "ORDER BY SUM(d.totalValue) DESC")
    List<LeaderboardItemDTO> findLeaderboardByRoomAndTime(@Param("roomId") Integer roomId, @Param("startTime") LocalDateTime startTime);
}