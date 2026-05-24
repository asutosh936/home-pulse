package com.homepulse.hma.service;

import com.homepulse.hma.model.Notification;
import com.homepulse.hma.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Tool(description = "Send a proactive home maintenance reminder. Use this tool when the AI decides a reminder is necessary.")
    public String sendNotification(String message) {
        log.info("Proactive notification: {}", message);
        Notification notification = new Notification(message, LocalDateTime.now());
        notificationRepository.save(notification);
        return "Notification sent: " + message;
    }

    public List<NotificationDto> getNotifications() {
        return notificationRepository.findAll().stream()
                .map(notification -> new NotificationDto(
                        notification.getId(),
                        notification.getMessage(),
                        notification.getCreatedAt()))
                .collect(Collectors.toList());
    }
}
