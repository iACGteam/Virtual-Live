package com.virtuallive.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadDto {
    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    @NotBlank(message = "视频URL不能为空")
    private String videoUrl;

    private String coverImageUrl; // 可选，如果不提供则自动提取

    private String category;

    private String tags;

    private Integer authorId;
    
    private Integer circleId; // 所属圈子ID，可选
}

