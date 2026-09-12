package com.iwfc;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.Equipment;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.model.Session;
import com.iwfc.model.User;
import com.iwfc.model.UserRole;
import com.iwfc.pattern.structural.IWFCFacade;
import com.iwfc.repository.Repository;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomExceptionTest {
    private IWFCFacade facade;
    private User member;
    private User admin;

    @BeforeEach
    void setUp() {
        EquipmentService eqService = new EquipmentService(new Repository<>(Equipment::getId));
        SessionService sessService = new SessionService(new Repository<>(Session::getSessionId));
        MaintenanceService maintService = new MaintenanceService(new Repository<>(MaintenanceRequest::getRequestId));
        facade = new IWFCFacade(eqService, sessService, maintService);

        member = new User("U01", "Member John", "john@mail.com", UserRole.MEMBER);
        admin = new User("U02", "Admin Sarah", "sarah@iwfc.com", UserRole.ADMIN);
    }

    @Test
    void testUnauthorizedAccessThrowsException() {
        assertThrows(UnauthorizedAccessException.class, () ->
                facade.addEquipment(member, "TREADMILL", "EQ01", "Treadmill X", "Zone A")
        );
    }

    @Test
    void testDuplicateDataThrowsException() throws Exception {
        facade.addEquipment(admin, "TREADMILL", "EQ01", "Treadmill X", "Zone A");

        assertThrows(DuplicateDataException.class, () ->
                facade.addEquipment(admin, "SPIN_BIKE", "EQ01", "Spin Bike Y", "Zone B")
        );
    }
}
