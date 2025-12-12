package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "fan_circles")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Circle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "circle_id")
    private Integer circleId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", length = 500)
    private String description;
    
    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;
    
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    @Builder.Default
    @Column(name = "member_count")
    private Integer memberCount = 0;
    
    @Builder.Default
    @Column(name = "post_count")
    private Integer postCount = 0;
    
    @Column(name = "category", length = 50)
    private String category;
    
    @Builder.Default
    @Column(name = "is_official")
    private Boolean isOfficial = false;
    
    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
