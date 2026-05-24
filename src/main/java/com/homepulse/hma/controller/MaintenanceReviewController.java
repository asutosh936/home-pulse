package com.homepulse.hma.controller;

import com.homepulse.hma.service.MaintenanceManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class MaintenanceReviewController {

    private final MaintenanceManager maintenanceManager;

    public MaintenanceReviewController(MaintenanceManager maintenanceManager) {
        this.maintenanceManager = maintenanceManager;
    }

    @PostMapping("/maintenance-review")
    public String triggerMaintenanceReview() {
        maintenanceManager.runDailyMaintenanceReview();
        return "Maintenance review executed";
    }
}
