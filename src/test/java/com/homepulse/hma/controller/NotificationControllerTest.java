package com.homepulse.hma.controller;

import com.homepulse.hma.service.NotificationDto;
import com.homepulse.hma.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @Test
    void testGetNotificationsReturnsList() throws Exception {
        when(notificationService.getNotifications()).thenReturn(
                List.of(new NotificationDto(1L, "Your HVAC filter is due.", LocalDateTime.of(2026, 5, 24, 9, 0)))
        );

        mockMvc.perform(get("/api/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"message\":\"Your HVAC filter is due.\",\"createdAt\":\"2026-05-24T09:00:00\"}]"));
    }
}
