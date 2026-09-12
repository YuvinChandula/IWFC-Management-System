package com.iwfc.model;

/**
 * Entity: Equipment
 * 
 * Purpose:
 * Represents gym equipment with usage tracking for preventative maintenance.
 * 
 * OOP Principles Demonstrated:
 * - Encapsulation: State is protected; status and hours change only via defined methods.
 */
public class Equipment {
    private final String id;                         // Unique machine ID (e.g., "EQ-TM01")
    private final String name;                       // Brand/Model name (e.g., "ProRunner X2")
    private final String type;                       // Category (e.g., "Treadmill", "Spin Bike")
    private EquipmentStatus status;                  // Operational state
    private String location;                         // Physical room/zone (e.g., "Cardio Zone")
    private double usageHours;                       // Cumulative hours used in sessions
    private final double maintenanceThresholdHours;  // Limit before preventative maintenance alert

    public Equipment(String id, String name, String type, String location, double maintenanceThresholdHours) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.location = location;
        this.status = EquipmentStatus.OPERATIONAL; // Defaults to operational on registration
        this.usageHours = 0.0;                     // Initial cumulative usage is 0
        this.maintenanceThresholdHours = maintenanceThresholdHours;
    }

    // --- Getters and Setters ---
    public String getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public double getUsageHours() { return usageHours; }
    public double getMaintenanceThresholdHours() { return maintenanceThresholdHours; }

    /**
     * Adds hours from a completed fitness session to cumulative usage.
     */
    public void addUsage(double hours) {
        if (hours > 0) {
            this.usageHours += hours;
        }
    }

    /**
     * Checks if cumulative usage has exceeded the preventative threshold.
     * Assessment Requirement: "The system must track cumulative usage hours to trigger preventative maintenance alerts."
     */
    public boolean requiresPreventativeMaintenance() {
        return this.usageHours >= this.maintenanceThresholdHours && this.status == EquipmentStatus.OPERATIONAL;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Status: %s | Loc: %s | Usage: %.1f/%.1fh",
                id, name, type, status, location, usageHours, maintenanceThresholdHours);
    }
}