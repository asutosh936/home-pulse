package com.homepulse.hma.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_log")
public class MaintenanceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;
    private String action;
    private LocalDate date;
    private String notes;

    public MaintenanceLog() {
    }

    public MaintenanceLog(String itemName, String action, LocalDate date, String notes) {
        this.itemName = itemName;
        this.action = action;
        this.date = date;
        this.notes = notes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "MaintenanceLog{" +
                "id=" + id +
                ", itemName='" + itemName + '\'' +
                ", action='" + action + '\'' +
                ", date=" + date +
                ", notes='" + notes + '\'' +
                '}';
    }
}
