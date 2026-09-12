package com.iwfc.pattern.behavioural;

import com.iwfc.model.MaintenanceRequest;

/**
 * Behavioural Design Pattern: Observer Interface
 * 
 * Purpose:
 * Defines the contract for any subscriber/listener wishing to receive automated
 * notifications when a maintenance ticket changes status
 * (e.g., PENDING -> ASSIGNED -> COMPLETED).
 */
public interface MaintenanceObserver {

    /**
     * Callback method triggered whenever a maintenance ticket status changes.
     *
     * @param request The affected maintenance ticket.
     * @param message Descriptive update message.
     */
    void onStatusUpdate(MaintenanceRequest request, String message);
}