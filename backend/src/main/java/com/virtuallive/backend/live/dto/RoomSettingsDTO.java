package com.virtuallive.backend.live.dto;

import lombok.Data;

@Data
public class RoomSettingsDTO {
    private String title;
    private String description;
    private String coverUrl;
    private String category;
}