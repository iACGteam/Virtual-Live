// src/main/java/com/virtuallive/backend/live/entity/LiveSession.java
package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "live_sessions")
public class LiveSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sessionId;

    private Integer roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer viewerCount;
}