package com.virtuallive.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 更新社区帖子DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostUpdateDto {
    private String title;
    private String content;
    private String coverImageUrl;
    private List<String> imageUrls;
    private String videoUrl;
    private String category;
    private String tags;
}
