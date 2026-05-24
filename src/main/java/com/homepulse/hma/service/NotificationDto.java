package com.homepulse.hma.service;

import java.time.LocalDateTime;

public record NotificationDto(Long id, String message, LocalDateTime createdAt) {
}
