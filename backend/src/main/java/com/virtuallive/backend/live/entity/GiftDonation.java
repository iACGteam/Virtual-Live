package com.virtuallive.backend.live.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "gift_donations")
public class GiftDonation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donationId;

    private Integer sessionId;
    private Integer senderId;

    private String giftType; // 'GIFT'
    private String giftName;

    @Column(precision = 12, scale = 2)
    private BigDecimal giftValue; // 单价

    private Integer quantity;

    @Column(precision = 12, scale = 2)
    private BigDecimal totalValue; // 总价

    private LocalDateTime donationTime;

    @PrePersist
    protected void onCreate() {
        donationTime = LocalDateTime.now();
    }
}