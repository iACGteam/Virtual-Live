package com.virtuallive.backend.model.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name; // nickname/username
    private String signature; // introduction
    private String avatar; // avatar url
    private String password;
    private String confirmPassword;
}
