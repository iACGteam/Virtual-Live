package com.virtuallive.backend.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Integer id;
    private String username;
    private String email;
    private String avatarUrl;
    private String introduction;
    private String userType;
    
    // Stats
    private Long followersCount;
    private Long followingCount;
    private Long circlesCount;
    private Long likesCount;
}
