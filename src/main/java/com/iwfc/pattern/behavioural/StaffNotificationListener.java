package com.iwfc.pattern.behavioural;

import com.iwfc.model.MaintenanceRequest;

public class StaffNotificationListener implements MaintenanceObserver {
    private final String recipientName;

    public StaffNotificationListener(String recipientName) {
        this.recipientName = recipientName;
    }
    @Override
    public void onStatusUpdate(MaintenanceRequest request, String message) {
        System.out.println(String.format(">> [STAFF NOTIFICATION - %s]: Ticket #%s (Equip ID: %s) -> %s [Status: %s]",
                recipientName.toUpperCase(), request.getRequestId(), request.getEquipmentId(), message, request.getStatus()));
    }
}
