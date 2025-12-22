package com.virtuallive.backend.model.dto;

import lombok.Data;

@Data
public class DanmakuDto {
    private Integer id;
    private String text;
    private String color;
    private Float time; // video time in seconds
    private Integer userId;
    private String username;
    private String avatarUrl;
}
