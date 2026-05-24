package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import com.homepulse.hma.repository.MaintenanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.ai.anthropic.api-key=dummy-key")
class MaintenanceToolsTest {

    @Autowired
    private MaintenanceRepository maintenanceRepository;

    @Autowired
    private MaintenanceTools maintenanceTools;

    @BeforeEach
    void setUp() {
        maintenanceRepository.deleteAll();
    }

    @Test
    void testLogMaintenance() {
        String result = maintenanceTools.logMaintenance("Dyson vacuum", "replaced HEPA filter", "Filter was dirty");
        assertNotNull(result);
        assertTrue(result.contains("Successfully logged maintenance"));

        List<MaintenanceLog> logs = maintenanceRepository.findAll();
        assertEquals(1, logs.size());
        MaintenanceLog log = logs.get(0);
        assertEquals("Dyson vacuum", log.getItemName());
        assertEquals("replaced HEPA filter", log.getAction());
        assertEquals(LocalDate.now(), log.getDate());
        assertEquals("Filter was dirty", log.getNotes());
    }

    @Test
    void testListRecentMaintenance() {
        maintenanceTools.logMaintenance("Item 1", "Action 1", "Notes 1");
        maintenanceTools.logMaintenance("Item 2", "Action 2", "Notes 2");
        maintenanceTools.logMaintenance("Item 3", "Action 3", "Notes 3");

        List<MaintenanceLog> recent = maintenanceTools.listRecentMaintenance(2);
        assertEquals(2, recent.size());
        assertEquals("Item 3", recent.get(0).getItemName());
        assertEquals("Item 2", recent.get(1).getItemName());
    }
}
