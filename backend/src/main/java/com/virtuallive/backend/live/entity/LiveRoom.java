// src/main/java/com/virtuallive/backend/live/entity/LiveRoom.java
package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "live_rooms")
public class LiveRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    private Integer vtuberId;
    private String roomTitle;

    private Boolean isLive; // 对应数据库 tinyint(1)

    @Column(unique = true)
    private String streamKey; // 核心：推流密钥

    private LocalDateTime createdAt;
}