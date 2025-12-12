package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorites", indexes = {
    @Index(name = "idx_user_content", columnList = "user_id,content_type,content_id"),
    @Index(name = "idx_user_created", columnList = "user_id,created_at")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "favorite_id")
    private Integer favoriteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 20)
    private ContentType contentType;
    
    @Column(name = "content_id", nullable = false)
    private Integer contentId;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    public enum ContentType {
        post,      // 帖子/视频
        asset      // 资产
    }
}
