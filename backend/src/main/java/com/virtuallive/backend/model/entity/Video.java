package com.virtuallive.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "community_posts")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Integer postId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circle_id")
    private Circle circle;
    
    @Column(name = "title", nullable = false, length = 200)
    private String title;
    
    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;
    
    @Column(name = "category", length = 100)
    private String category;
    
    @Column(name = "tags", length = 200)
    private String tags;
    
    @Column(name = "cover_image_url", length = 500)
    private String coverImageUrl;
    
    @Column(name = "video_url", length = 500)
    private String videoUrl;
    
    @Column(name = "duration")
    private Integer duration; // 视频时长，单位：秒

    @Builder.Default
    @Column(name = "likes")
    private Integer likes = 0;
    
    @Builder.Default
    @Column(name = "comments_count")
    private Integer commentsCount = 0;
    
    @Builder.Default
    @Column(name = "views")
    private Integer views = 0;
    
    @Builder.Default
    @Column(name = "is_deleted")
    private Boolean isDeleted = false;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
