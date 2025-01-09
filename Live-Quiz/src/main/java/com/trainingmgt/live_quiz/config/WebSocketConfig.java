package com.trainingmgt.live_quiz.config;

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
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(
                "http://localhost:4200",
                "https://training-sys-kahoot-private.vercel.app",
                "https://training-management.amalitech-dev.net",
                "https://dev.duu3qoluscyig.amplifyapp.com",
                "https://octopus-arriving-monthly.ngrok-free.app",
                        "http://127.0.0.1:5500"
        )
                .withSockJS();
    }
}
