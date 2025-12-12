package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "circle_members", 
    uniqueConstraints = @UniqueConstraint(name = "unique_circle_member", columnNames = {"circle_id", "user_id"}),
    indexes = {
        @Index(name = "idx_circle", columnList = "circle_id"),
        @Index(name = "idx_user", columnList = "user_id"),
        @Index(name = "idx_joined_at", columnList = "joined_at")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CircleMember {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Integer memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id", nullable = false)
    private Circle circle;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "joined_at", nullable = false, updatable = false)
    private LocalDateTime joinedAt;
    
    @Builder.Default
    @Column(name = "post_count")
    private Integer postCount = 0;  // 用户在该圈子的发帖数
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
