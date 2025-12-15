//package com.virtuallive.backend.live.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//@EnableWebSecurity
//public class SrsSecurityConfig {
//
//    @Bean
//    @Order(1) // 优先级设为 1，确保最先匹配这个规则
//    public SecurityFilterChain srsFilterChain(HttpSecurity http) throws Exception {
//        http
//                // 1. 定义这个安全链只负责 SRS 回调接口
////                .securityMatcher("/api/live/callback/**")
//                // 修改这里：同时匹配 callback 和 websocket 端点
//                .securityMatcher("/api/live/callback/**", "/ws-live/**", "/api/live/rooms/**")
//                // 2. 禁用 CSRF (服务器间通信不需要 CSRF)
//                .csrf(AbstractHttpConfigurer::disable)
//                // 3. 允许所有请求直接访问 (无需登录)
//                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        return http.build();
//    }
//}


package com.virtuallive.backend.live.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SrsSecurityConfig {

    @Bean
    @Order(1) // 优先匹配 SRS & Live 相关接口
    public SecurityFilterChain srsFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. 只负责 SRS 回调、WebSocket 端点、直播房间接口
                .securityMatcher("/api/live/callback/**", "/ws-live/**", "/api/live/rooms/**")
                // 2. 启用 CORS（非常重要）
                .cors(cors -> cors.configurationSource(liveCorsConfigurationSource()))
                // 3. 禁用 CSRF
                .csrf(AbstractHttpConfigurer::disable)
                // 4. 暂时全部放行
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Live/SRS 专用 CORS 配置
     * 开发环境直接放开所有来源，带上凭证
     */
    @Bean
    public CorsConfigurationSource liveCorsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // ★ 使用 allowedOriginPatterns("*") + allowCredentials(true)
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 只针对 /api/live/** 和 /ws-live/** 也可以，这里用 /** 简化
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}