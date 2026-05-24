package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import com.homepulse.hma.repository.MaintenanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

@Component
public class MaintenanceManager {

    private static final Logger log = LoggerFactory.getLogger(MaintenanceManager.class);

    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceContextService contextService;
    private final ChatClient notificationAgent;

    public MaintenanceManager(MaintenanceRepository maintenanceRepository,
                              MaintenanceContextService contextService,
                              ChatClient notificationAgent) {
        this.maintenanceRepository = maintenanceRepository;
        this.contextService = contextService;
        this.notificationAgent = notificationAgent;
    }

    @Scheduled(cron = "${hma.notification.cron:0 0 9 * * ?}")
    public void runDailyMaintenanceReview() {
        List<MaintenanceLog> logs = maintenanceRepository.findAll();
        log.info("Starting daily proactive maintenance review. Retrieved {} maintenance logs.", logs.size());

        String context = contextService.buildContextSummary(logs);
        log.debug("Maintenance review context:\n{}", context);

        String decision = notificationAgent.prompt()
                .user(context)
                .call()
                .content();

        log.info("Maintenance review complete. Agent decision: {}", decision);
    }
}
