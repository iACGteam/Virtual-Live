package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "daily_checkins", 
    uniqueConstraints = @UniqueConstraint(name = "unique_user_circle_date", columnNames = {"user_id", "circle_id", "checkin_date"}),
    indexes = {
        @Index(name = "idx_user_circle", columnList = "user_id,circle_id"),
        @Index(name = "idx_checkin_date", columnList = "checkin_date")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyCheckin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkin_id")
    private Integer checkinId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id", nullable = false)
    private Circle circle;
    
    @Column(name = "checkin_date", nullable = false)
    private LocalDate checkinDate;
    
    @Builder.Default
    @Column(name = "continuous_days")
    private Integer continuousDays = 1;  // 连续签到天数
    
    @Builder.Default
    @Column(name = "total_days")
    private Integer totalDays = 1;  // 总签到天数
    
    @Builder.Default
    @Column(name = "reward_points")
    private Integer rewardPoints = 0;  // 获得积分
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
