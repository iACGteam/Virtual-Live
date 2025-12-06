package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_follows", 
    uniqueConstraints = @UniqueConstraint(name = "unique_follow", columnNames = {"follower_id", "following_id"}),
    indexes = {
        @Index(name = "idx_follower", columnList = "follower_id"),
        @Index(name = "idx_following", columnList = "following_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserFollow {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "follow_id")
    private Integer followId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;  // 粉丝（关注者）
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "following_id", nullable = false)
    private User following; // 被关注人
    
    @CreationTimestamp
    @Column(name = "follow_date", nullable = false, updatable = false)
    private LocalDateTime followDate;
    
    @Builder.Default
    @Column(name = "is_blocked", nullable = false)
    private Boolean isBlocked = false;
}
