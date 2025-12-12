package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "likes", indexes = {
    @Index(name = "idx_user_content", columnList = "user_id,content_type,content_id"),
    @Index(name = "idx_content", columnList = "content_type,content_id")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Like {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Integer likeId;

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
        comment,   // 评论
        asset      // 资产
    }
}
