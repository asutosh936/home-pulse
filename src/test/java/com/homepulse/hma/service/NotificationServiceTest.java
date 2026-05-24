package com.homepulse.hma.service;

import com.homepulse.hma.model.Notification;
import com.homepulse.hma.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new NotificationService(notificationRepository);
    }

    @Test
    void sendNotificationStoresAndReturnsMessage() {
        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        when(notificationRepository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        String result = service.sendNotification("It's time to replace your HVAC filter.");

        assertNotNull(result);
        assertTrue(result.startsWith("Notification sent:"));

        Notification saved = captor.getValue();
        assertEquals("It's time to replace your HVAC filter.", saved.getMessage());
        assertNotNull(saved.getCreatedAt());
        assertTrue(saved.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }
}
