package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.R;
import com.virtuallive.backend.model.dto.UpdateProfileRequest;
import com.virtuallive.backend.model.dto.UserProfileDto;
import com.virtuallive.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    @GetMapping("/{userId}/profile")
    public R<UserProfileDto> getUserProfile(@PathVariable Integer userId) {
        try {
            UserProfileDto profile = userService.getUserProfile(userId);
            return R.ok(profile);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PutMapping("/{userId}/profile")
    public R<UserProfileDto> updateProfile(
            @PathVariable Integer userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            UserProfileDto profile = userService.updateProfile(userId, request);
            return R.ok("更新成功", profile);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
