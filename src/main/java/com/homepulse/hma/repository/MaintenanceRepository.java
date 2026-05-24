package com.homepulse.hma.repository;

import com.homepulse.hma.model.MaintenanceLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceLog, Long> {

    /**
     * Finds a maintenance log matching the exact item, action, and date to prevent duplicates.
     */
    Optional<MaintenanceLog> findByItemNameAndActionAndDate(String itemName, String action, LocalDate date);
}
