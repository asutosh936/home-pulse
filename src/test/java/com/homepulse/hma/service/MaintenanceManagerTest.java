package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import com.homepulse.hma.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ai.chat.client.ChatClient;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class MaintenanceManagerTest {

    @Mock
    private MaintenanceRepository maintenanceRepository;

    @Mock
    private MaintenanceContextService contextService;

    @Mock
    private ChatClient notificationAgent;

    @Mock
    private ChatClient.ChatClientRequestSpec promptSpec;

    @Mock
    private ChatClient.CallResponseSpec responseSpec;

    private MaintenanceManager maintenanceManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        maintenanceManager = new MaintenanceManager(maintenanceRepository, contextService, notificationAgent);
    }

    @Test
    void runDailyMaintenanceReviewUsesChatClientPrompt() {
        List<MaintenanceLog> logs = List.of(
                new MaintenanceLog("HVAC filter", "replaced filter", LocalDate.now().minusDays(95), "Filter was dirty"),
                new MaintenanceLog("Fridge", "cleaned coils", LocalDate.now().minusDays(10), "Routine service")
        );

        when(maintenanceRepository.findAll()).thenReturn(logs);
        when(contextService.buildContextSummary(logs)).thenReturn("CURRENT_CONTEXT");
        when(notificationAgent.prompt()).thenReturn(promptSpec);
        when(promptSpec.user(anyString())).thenReturn(promptSpec);
        when(promptSpec.call()).thenReturn(responseSpec);
        when(responseSpec.content()).thenReturn("No reminder needed.");

        maintenanceManager.runDailyMaintenanceReview();

        verify(maintenanceRepository).findAll();
        verify(contextService).buildContextSummary(logs);
        verify(notificationAgent).prompt();
        verify(promptSpec).user("CURRENT_CONTEXT");
        verify(promptSpec).call();
        verify(responseSpec).content();
    }
}
