package com.virtuallive.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 创建社区帖子DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostCreateDto {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    @NotNull(message = "作者ID不能为空")
    private Integer authorId;
    
    private String coverImageUrl; // 封面图
    private List<String> imageUrls; // 多图片
    private String videoUrl; // 视频URL
    private String category; // 分类
    private String tags; // 标签（逗号分隔）
    private Integer circleId; // 所属圈子ID（可选）
}
