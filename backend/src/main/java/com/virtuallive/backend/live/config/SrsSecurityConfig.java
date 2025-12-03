package com.virtuallive.backend.live.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SrsSecurityConfig {

    @Bean
    @Order(1) // 优先级设为 1，确保最先匹配这个规则
    public SecurityFilterChain srsFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 定义这个安全链只负责 SRS 回调接口
//                .securityMatcher("/api/live/callback/**")
                // 修改这里：同时匹配 callback 和 websocket 端点
                .securityMatcher("/api/live/callback/**", "/ws-live/**")
                // 2. 禁用 CSRF (服务器间通信不需要 CSRF)
                .csrf(AbstractHttpConfigurer::disable)
                // 3. 允许所有请求直接访问 (无需登录)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }
}
