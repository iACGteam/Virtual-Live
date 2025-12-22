package com.virtuallive.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoDto {
    private Integer id;
    private String title;
    private String content;
    private String coverImageUrl;
    private String videoUrl;
    private java.util.List<String> imageUrls; // 多图片支持
    private Integer duration; // 视频时长（秒）
    private String category;
    private String tags;
    private Integer likes;
    private Boolean isLiked; // 当前用户是否点赞
    private Integer views;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    
    // 作者信息
    private Integer authorId;
    private String authorName;
    private String authorAvatar;
    
    // 圈子信息
    private Integer circleId;
    private String circleName;
}
