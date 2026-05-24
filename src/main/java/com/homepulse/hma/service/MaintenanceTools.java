package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import com.homepulse.hma.repository.MaintenanceRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MaintenanceTools {

    private final MaintenanceRepository maintenanceRepository;

    public MaintenanceTools(MaintenanceRepository maintenanceRepository) {
        this.maintenanceRepository = maintenanceRepository;
    }

    @Tool(description = "Log a new home maintenance task. Use this tool when the user says they performed maintenance on an item.")
    public String logMaintenance(
            @ToolParam(description = "The name of the item or appliance that was maintained, e.g. 'Dyson vacuum' or 'AC unit'") String item,
            @ToolParam(description = "The action performed on the item, e.g. 'replaced HEPA filter' or 'cleaned coils'") String action,
            @ToolParam(description = "Any additional details, notes, or observations. Use empty string or null if not provided") String notes) {
        
        LocalDate date = LocalDate.now();
        MaintenanceLog log = new MaintenanceLog(item, action, date, notes);
        MaintenanceLog savedLog = maintenanceRepository.save(log);
        
        return "Successfully logged maintenance: " + savedLog.toString();
    }

    @Tool(description = "List recent home maintenance tasks. Use this tool when the user asks what tasks have been completed or logs to retrieve.")
    public List<MaintenanceLog> listRecentMaintenance(
            @ToolParam(description = "The maximum number of recent logs to retrieve") int limit) {
        
        return maintenanceRepository.findAll().stream()
                .sorted((a, b) -> b.getId().compareTo(a.getId()))
                .limit(limit)
                .toList();
    }
}
