package com.virtuallive.backend.live.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 1. 启用简单的内存消息代理
        // 客户端订阅的路径前缀（后端推给前端的消息）
        config.enableSimpleBroker("/topic");

        // 2. 客户端发送消息的路径前缀（前端发给后端的消息）
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 3. 注册 WebSocket 连接点
        // 前端连接地址：http://localhost:8080/ws-live
        registry.addEndpoint("/ws-live")
                .setAllowedOriginPatterns("*") // 允许跨域连接
                .withSockJS(); // 启用 SockJS 回退机制（保证兼容性）
    }
}