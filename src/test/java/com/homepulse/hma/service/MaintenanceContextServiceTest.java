package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceContextServiceTest {

    @Test
    void buildContextSummaryIncludesRecentLogsAndUserHistory() {
        MaintenanceContextService service = new MaintenanceContextService("The user prefers morning reminders and is currently busy with home projects.");

        String context = service.buildContextSummary(
                java.util.List.of(
                        new MaintenanceLog("HVAC", "replaced filter", LocalDate.now().minusDays(95), "Filter appeared clogged"),
                        new MaintenanceLog("Fridge", "cleaned condenser coils", LocalDate.now().minusDays(10), "Routine maintenance")
                )
        );

        assertTrue(context.contains("Current date:"));
        assertTrue(context.contains("Recent logs:"));
        assertTrue(context.contains("User history/preferences:"));
        assertTrue(context.contains("The user prefers morning reminders"));
        assertTrue(context.contains("HVAC"));
        assertTrue(context.contains("Fridge"));
        assertTrue(context.contains("Candidate maintenance reminders:"));
    }

    @Test
    void buildContextSummaryHandlesEmptyLogsGracefully() {
        MaintenanceContextService service = new MaintenanceContextService("No preferences recorded.");

        String context = service.buildContextSummary(java.util.List.of());

        assertTrue(context.contains("No maintenance logs are available."));
        assertTrue(context.contains("No candidate maintenance items found."));
    }

    @Test
    void buildContextSummaryHandlesNullLogsGracefully() {
        MaintenanceContextService service = new MaintenanceContextService("No preferences recorded.");

        String context = service.buildContextSummary(null);

        assertTrue(context.contains("No maintenance logs are available."));
        assertTrue(context.contains("No candidate maintenance items found."));
    }

    @Test
    void buildContextSummaryIncludesNonRecurringOldLogAsCandidate() {
        MaintenanceContextService service = new MaintenanceContextService("Routine review.");

        String context = service.buildContextSummary(
                java.util.List.of(new MaintenanceLog("Garage door", "painted wall", LocalDate.now().minusDays(130), "Annual refresh"))
        );

        assertTrue(context.contains("Candidate maintenance reminders:"));
        assertTrue(context.contains("Garage door painted wall"));
        assertTrue(context.contains("130 days ago"));
    }

    @Test
    void buildContextSummarySkipsRecentRecurringTasks() {
        MaintenanceContextService service = new MaintenanceContextService("Routine review.");

        String context = service.buildContextSummary(
                java.util.List.of(new MaintenanceLog("HVAC", "replaced filter", LocalDate.now().minusDays(20), "Quick check"))
        );

        assertTrue(context.contains("Candidate maintenance reminders:"));
        assertFalse(context.contains("HVAC replaced filter"));
    }
}
