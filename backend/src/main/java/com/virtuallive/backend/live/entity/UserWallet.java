package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_wallet")
public class UserWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer walletId;

    @Column(unique = true, nullable = false)
    private Integer userId;

    @Column(precision = 12, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalEarned = BigDecimal.ZERO;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalSpent = BigDecimal.ZERO;

    private LocalDateTime updatedAt;

    @PreUpdate
    @PrePersist
    public void updateTimestamp() {
        updatedAt = LocalDateTime.now();
    }
}