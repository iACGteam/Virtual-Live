package com.virtuallive.backend.live.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardItemDTO {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private BigDecimal totalAmount;
    private Integer rank;
}