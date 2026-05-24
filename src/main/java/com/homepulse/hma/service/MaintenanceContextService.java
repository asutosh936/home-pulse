package com.homepulse.hma.service;

import com.homepulse.hma.model.MaintenanceLog;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class MaintenanceContextService {

    private final String userHistory;

    public MaintenanceContextService(@Value("${hma.user-history:No user preferences have been recorded yet.}") String userHistory) {
        this.userHistory = userHistory;
    }

    public String buildContextSummary(List<MaintenanceLog> logs) {
        String currentDate = LocalDate.now().toString();
        String recentLogs = buildRecentLogSummary(logs);
        String candidates = buildCandidateSummary(logs);

        return String.join("\n",
                "Current date: " + currentDate,
                "",
                "Recent logs:",
                recentLogs,
                "",
                "User history/preferences:",
                userHistory,
                "",
                "Candidate maintenance reminders:",
                candidates,
                "",
                "Review the context above and decide whether a proactive reminder should be sent.");
    }

    private String buildRecentLogSummary(List<MaintenanceLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return "No maintenance logs are available.";
        }

        return logs.stream()
                .sorted(Comparator.comparing(MaintenanceLog::getDate).reversed())
                .limit(10)
                .map(this::formatLogEntry)
                .collect(Collectors.joining("\n"));
    }

    private String buildCandidateSummary(List<MaintenanceLog> logs) {
        if (logs == null || logs.isEmpty()) {
            return "No candidate maintenance items found."
                    + " If the user added maintenance entries, the assistant should still decide whether a reminder is necessary based on future schedules.";
        }

        return logs.stream()
                .map(this::toCandidateDescription)
                .filter(Predicate.not(String::isBlank))
                .distinct()
                .limit(10)
                .collect(Collectors.joining("\n"));
    }

    private String toCandidateDescription(MaintenanceLog log) {
        long daysSince = ChronoUnit.DAYS.between(log.getDate(), LocalDate.now());
        boolean isLikelyRecurring = isLikelyRecurringTask(log.getAction());

        if (isLikelyRecurring && daysSince >= 60) {
            return String.format("- %s %s (last recorded %d days ago).", log.getItemName(), log.getAction(), daysSince);
        }

        if (!isLikelyRecurring && daysSince >= 120) {
            return String.format("- %s %s (last recorded %d days ago).", log.getItemName(), log.getAction(), daysSince);
        }

        return "";
    }

    private boolean isLikelyRecurringTask(String action) {
        if (action == null) {
            return false;
        }
        String normalized = action.toLowerCase();
        return normalized.contains("filter")
                || normalized.contains("battery")
                || normalized.contains("change")
                || normalized.contains("replace")
                || normalized.contains("clean")
                || normalized.contains("service");
    }

    private String formatLogEntry(MaintenanceLog log) {
        String notes = log.getNotes() != null && !log.getNotes().isBlank() ? " Notes: " + log.getNotes() : "";
        return String.format("- %s | %s | %s%s", log.getDate(), log.getItemName(), log.getAction(), notes);
    }
}
