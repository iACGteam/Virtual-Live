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
    private Long id;
    private String title;
    private String content;
    private String coverImageUrl;
    private String category;
    private String tags;
    private Integer likes;
    private Integer views;
    private Integer commentsCount;
    private LocalDateTime createdAt;
    
    // 作者信息
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}
