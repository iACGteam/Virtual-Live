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
public class CircleMemberDto {
    private Integer memberId;
    private Integer userId;
    private String username;
    private String userAvatar;
    private LocalDateTime joinedAt;
    private Integer postCount;
}
