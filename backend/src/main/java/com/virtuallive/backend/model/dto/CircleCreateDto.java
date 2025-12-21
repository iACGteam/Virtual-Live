package com.virtuallive.backend.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CircleCreateDto {
    @NotBlank(message = "圈子名称不能为空")
    private String name;
    
    private String description;
    
    private String coverImageUrl;
    
    private String avatarUrl;
    
    private String category;
    
    private Integer creatorId;
}
