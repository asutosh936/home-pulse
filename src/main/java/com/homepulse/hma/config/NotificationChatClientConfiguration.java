package com.homepulse.hma.config;

import com.homepulse.hma.service.NotificationService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationChatClientConfiguration {

    @Bean
    public ChatClient maintenanceNotificationChatClient(ChatClient.Builder chatClientBuilder,
                                                        NotificationService notificationService) {
        return chatClientBuilder
                .defaultSystem("You are an intelligent home assistant. Review the provided maintenance logs and user context. Decide if a maintenance reminder is necessary. If you decide to send a reminder, use the 'sendNotification' tool to craft a personalized message.")
                .defaultTools(notificationService)
                .build();
    }
}
