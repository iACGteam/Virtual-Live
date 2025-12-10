package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "room_bans")
public class RoomBan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer banId;

    private Integer roomId;
    private Integer userId;

    private LocalDateTime endTime; // 禁言结束时间
    private String reason;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}