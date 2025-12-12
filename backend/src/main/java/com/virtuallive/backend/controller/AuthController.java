package com.virtuallive.backend.controller;

import com.virtuallive.backend.model.dto.*;
import com.virtuallive.backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    public R<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return R.ok("登录成功", response);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
    
    @PostMapping("/register")
    public R<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return R.ok("注册成功", response);
        } catch (Exception e) {
            return R.error(e.getMessage());
        }
    }
}
