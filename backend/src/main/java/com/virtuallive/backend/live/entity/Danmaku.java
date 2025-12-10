package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "danmaku")
public class Danmaku {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer danmakuId;

    private Integer sessionId;
    private Integer userId; // 关联 users 表

    @Column(columnDefinition = "TEXT")
    private String content;

    private String color;

    private Boolean isDeleted = false;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}