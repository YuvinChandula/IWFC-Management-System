package com.iwfc.pattern.behavioural;

import com.iwfc.model.MaintenanceRequest;


public interface MaintenanceObserver {

    /**
     * Callback method triggered whenever a maintenance ticket status changes.
     *
     * @param request The affected maintenance ticket.
     * @param message Descriptive update message.
     */
    void onStatusUpdate(MaintenanceRequest request, String message);
}