package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.model.MaintenanceRequest;
import com.iwfc.pattern.behavioural.MaintenanceObserver;
import com.iwfc.repository.Repository;

import java.util.ArrayList;
import java.util.List;

public class MaintenanceService {
    private final Repository<MaintenanceRequest> requestRepo;
    private final List<MaintenanceObserver> observers = new ArrayList<>();

    public MaintenanceService(Repository<MaintenanceRequest> requestRepo) {
        this.requestRepo = requestRepo;
    }

    public void attachObserver(MaintenanceObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(MaintenanceRequest request, String message) {
        for (MaintenanceObserver obs : observers) {
            obs.onStatusUpdate(request, message);
        }
    }

    public MaintenanceRequest reportIssue(String reqId, String equipmentId, String staffId,
                                          String desc, MaintenanceRequest.Urgency urgency)
            throws DuplicateDataException {
        if (requestRepo.existsById(reqId)) {
            throw new DuplicateDataException("Ticket ID '" + reqId + "' already exists.");
        }
        MaintenanceRequest req = new MaintenanceRequest(reqId, equipmentId, staffId, desc, urgency);
        requestRepo.save(req);
        notifyObservers(req, "New equipment fault ticket registered");
        return req;
    }

    public void assignRequest(String reqId, String adminId) {
        requestRepo.findById(reqId).ifPresent(req -> {
            req.setAssignedToAdminId(adminId);
            req.setStatus(MaintenanceRequest.Status.ASSIGNED);
            notifyObservers(req, "Ticket assigned to Admin ID: " + adminId);
        });
    }

    public void completeRequest(String reqId) {
        requestRepo.findById(reqId).ifPresent(req -> {
            req.setStatus(MaintenanceRequest.Status.COMPLETED);
            notifyObservers(req, "Maintenance work completed successfully");
        });
    }

    public List<MaintenanceRequest> getAllRequests() {
        return requestRepo.findAll();
    }
}
