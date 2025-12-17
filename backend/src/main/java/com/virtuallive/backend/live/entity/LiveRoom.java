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
    private String description;
    private String category;
    private String thumbnailUrl;

    private Boolean isLive; // tinyint(1)

    @Column(unique = true)
    private String streamKey;

    private String rtmpServer; // SRS 服务器地址

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}