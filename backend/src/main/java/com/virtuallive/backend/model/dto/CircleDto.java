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
public class CircleDto {
    private Integer id;
    private String name;
    private String description;
    private String avatarUrl;
    private String coverImageUrl;
    private Integer memberCount;
    private Integer postCount;
    private String category;
    private Boolean isOfficial;
    private LocalDateTime createdAt;
}
