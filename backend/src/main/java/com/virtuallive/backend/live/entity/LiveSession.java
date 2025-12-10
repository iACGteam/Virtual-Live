package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
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
    private Integer durationMinutes;
    private Integer viewerCount;
    private Integer peakViewers;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalGiftsRevenue = BigDecimal.ZERO;
}