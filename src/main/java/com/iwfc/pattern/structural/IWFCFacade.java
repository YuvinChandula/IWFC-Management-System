package com.iwfc.pattern.structural;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.exception.UnauthorizedAccessException;
import com.iwfc.model.*;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SessionService;

import java.time.LocalDateTime;
import java.util.List;

public class IWFCFacade {
    private final EquipmentService equipmentService;
    private final SessionService sessionService;
    private final MaintenanceService maintenanceService;

    public IWFCFacade(EquipmentService eqService, SessionService sessService, MaintenanceService maintService) {
        this.equipmentService = eqService;
        this.sessionService = sessService;
        this.maintenanceService = maintService;
    }

    private void checkRole(User user, UserRole... allowedRoles) throws UnauthorizedAccessException {
        for (UserRole allowed : allowedRoles) {
            if (user.getRole() == allowed) return;
        }
        throw new UnauthorizedAccessException("Access Denied: User '" + user.getName() 
                + "' with role " + user.getRole() + " is not authorized for this operation.");
    }

    public Equipment addEquipment(User user, String type, String id, String name, String loc)
            throws UnauthorizedAccessException, DuplicateDataException {
        checkRole(user, UserRole.ADMIN);
        return equipmentService.registerEquipment(type, id, name, loc);
    }

    public void assignMaintenance(User user, String reqId, String adminId) throws UnauthorizedAccessException {
        checkRole(user, UserRole.ADMIN);
        maintenanceService.assignRequest(reqId, adminId);
    }

    public Session scheduleSession(User user, String sessionId, String title, String studio,
                                   String equipId, LocalDateTime start, LocalDateTime end, int capacity)
            throws UnauthorizedAccessException, DuplicateDataException, InvalidBookingException {
        checkRole(user, UserRole.ADMIN, UserRole.INSTRUCTOR);
        return sessionService.scheduleSession(sessionId, title, user.getId(), studio, equipId, start, end, capacity);
    }

    public MaintenanceRequest reportFault(User user, String reqId, String eqId, String desc, MaintenanceRequest.Urgency urgency)
            throws UnauthorizedAccessException, DuplicateDataException {
        checkRole(user, UserRole.INSTRUCTOR, UserRole.ADMIN);
        return maintenanceService.reportIssue(reqId, eqId, user.getId(), desc, urgency);
    }

    public void bookSession(User user, String sessionId)
            throws UnauthorizedAccessException, InvalidBookingException {
        checkRole(user, UserRole.MEMBER, UserRole.ADMIN);
        sessionService.bookMember(sessionId, user.getId());
    }

    public List<Equipment> viewEquipment() { return equipmentService.getAllEquipment(); }
    public List<Session> viewSessions() { return sessionService.getAllSessions(); }
    public List<MaintenanceRequest> viewMaintenanceRequests() { return maintenanceService.getAllRequests(); }
}
