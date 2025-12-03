package com.virtuallive.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 社区帖子DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostDto {
    private Integer id;
    private String title;
    private String content;
    private String coverImageUrl;
    private String videoUrl;
    private List<String> imageUrls; // 多图片支持
    private Integer duration; // 视频时长
    private String category;
    private String tags;
    private Integer likes;
    private Integer views;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 作者信息
    private Integer authorId;
    private String authorName;
    private String authorAvatar;
    
    // 圈子信息（如果属于某个圈子）
    private Integer circleId;
    private String circleName;
}
