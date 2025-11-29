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
public class CommentDto {
    private Long id;
    private String content;
    private Integer likes;
    private LocalDateTime createdAt;
    
    // 用户信息
    private Long userId;
    private String username;
    private String userAvatar;
    
    // 父评论ID（用于回复）
    private Long parentId;
}
