package com.virtuallive.backend.live.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LeaderboardItemDTO {
    private Integer userId;
    private String username;
    private String avatarUrl;
    private BigDecimal totalAmount;
    private Integer rank;
    // 新增：粉丝等级（由后端填充）
    private Integer fanLevel = 0;

    // 保持与现有 JPQL 查询一致的构造函数签名：
    public LeaderboardItemDTO(Integer userId, String username, String avatarUrl, BigDecimal totalAmount, Integer rank) {
        this.userId = userId;
        this.username = username;
        this.avatarUrl = avatarUrl;
        this.totalAmount = totalAmount;
        this.rank = rank;
    }

    public LeaderboardItemDTO() {
    }
}