package com.iwfc;

import com.iwfc.model.MaintenanceRequest;
import com.iwfc.pattern.behavioural.StaffNotificationListener;
import com.iwfc.repository.Repository;
import com.iwfc.service.MaintenanceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaintenanceServiceTest {
    private MaintenanceService maintenanceService;

    @BeforeEach
    void setUp() {
        maintenanceService = new MaintenanceService(new Repository<>(MaintenanceRequest::getRequestId));
        maintenanceService.attachObserver(new StaffNotificationListener("Test Listener"));
    }

    @Test
    void testMaintenanceLifecycleTransitions() throws Exception {
        MaintenanceRequest req = maintenanceService.reportIssue("REQ01", "EQ01", "INS01", "Belt loose", MaintenanceRequest.Urgency.HIGH);
        assertEquals(MaintenanceRequest.Status.PENDING, req.getStatus(), "Initial status must be PENDING");

        maintenanceService.assignRequest("REQ01", "ADM01");
        assertEquals(MaintenanceRequest.Status.ASSIGNED, req.getStatus(), "Status after assignment must be ASSIGNED");

        maintenanceService.completeRequest("REQ01");
        assertEquals(MaintenanceRequest.Status.COMPLETED, req.getStatus(), "Status after resolution must be COMPLETED");
    }
}
