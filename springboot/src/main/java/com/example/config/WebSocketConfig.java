package com.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // ✅ 建议统一 WebSocket 连接端点
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // ✅ 启用简单内存消息代理，支持 /topic 订阅
        registry.enableSimpleBroker("/topic");

        // ✅ 客户端发往服务器消息时的前缀
        registry.setApplicationDestinationPrefixes("/app");
    }
}
