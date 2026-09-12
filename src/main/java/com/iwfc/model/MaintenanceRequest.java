package com.iwfc.model;

import java.time.LocalDateTime;

public class MaintenanceRequest {
    public enum Urgency { LOW, MEDIUM, HIGH }
    public enum Status { PENDING, ASSIGNED, COMPLETED }

    private final String requestId;
    private final String equipmentId;
    private final String reportedByStaffId;
    private final String description;
    private final Urgency urgency;
    private Status status;
    private String assignedToAdminId;
    private final LocalDateTime reportedAt;

    public MaintenanceRequest(String requestId, String equipmentId, String reportedByStaffId,
                              String description, Urgency urgency) {
        this.requestId = requestId;
        this.equipmentId = equipmentId;
        this.reportedByStaffId = reportedByStaffId;
        this.description = description;
        this.urgency = urgency;
        this.status = Status.PENDING;
        this.assignedToAdminId = null;
        this.reportedAt = LocalDateTime.now();
    }

    public String getRequestId() { return requestId; }
    public String getEquipmentId() { return equipmentId; }
    public String getReportedByStaffId() { return reportedByStaffId; }
    public String getDescription() { return description; }
    public Urgency getUrgency() { return urgency; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public String getAssignedToAdminId() { return assignedToAdminId; }
    public void setAssignedToAdminId(String assignedToAdminId) { this.assignedToAdminId = assignedToAdminId; }
    public LocalDateTime getReportedAt() { return reportedAt; }

    @Override
    public String toString() {
        return String.format("[%s] Equip: %s | Urgency: %s | Status: %s | Assigned: %s | Details: %s",
                requestId, equipmentId, urgency, status,
                (assignedToAdminId != null ? assignedToAdminId : "Unassigned"), description);
    }
}
