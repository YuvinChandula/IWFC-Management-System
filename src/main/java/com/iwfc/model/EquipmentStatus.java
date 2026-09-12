package com.iwfc.model;

/**
 * Enum: EquipmentStatus
 * 
 * Purpose:
 * Represents the lifecycle stages of gym machines and devices.
 * Used by Equipment Tracking and Maintenance workflows.
 */
public enum EquipmentStatus {
    OPERATIONAL,       // Machine is functional and available for sessions
    FAULTY,            // Machine has an issue reported by an instructor
    UNDER_MAINTENANCE, // Currently being serviced/repaired
    DEACTIVATED        // Decommissioned/retired from gym floor
}