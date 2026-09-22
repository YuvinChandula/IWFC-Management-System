package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.model.Equipment;
import com.iwfc.model.EquipmentStatus;
import com.iwfc.pattern.creational.EquipmentFactory;
import com.iwfc.repository.Repository;

import java.util.List;


public class EquipmentService {
    // Injected generic repository for Equipment
    private final Repository<Equipment> equipmentRepo;

    public EquipmentService(Repository<Equipment> equipmentRepo) {
        this.equipmentRepo = equipmentRepo;
    }

    /**
     * Registers new equipment into inventory using the Creational Factory Pattern.
     * Throws DuplicateDataException if ID already exists.
     */
    public Equipment registerEquipment(String type, String id, String name, String location)
            throws DuplicateDataException {
        if (equipmentRepo.existsById(id)) {
            throw new DuplicateDataException("Equipment with ID '" + id + "' already exists in inventory.");
        }
        // Instantiate using Factory Pattern
        Equipment equipment = EquipmentFactory.createEquipment(type, id, name, location);
        equipmentRepo.save(equipment);
        return equipment;
    }

    /**
     * Logs hours used and evaluates preventative maintenance thresholds.
     */
    public void logUsage(String id, double hours) {
        equipmentRepo.findById(id).ifPresent(eq -> {
            eq.addUsage(hours);
            // Assessment Requirement: Trigger preventative maintenance alert when threshold reached
            if (eq.requiresPreventativeMaintenance()) {
                System.out.println("⚠️ [MAINTENANCE ALERT] Equipment " + id + " (" + eq.getName() + ") reached " 
                        + eq.getUsageHours() + " hours. Preventative maintenance required!");
            }
        });
    }

    /**
     * Updates equipment status (e.g., OPERATIONAL -> FAULTY -> UNDER_MAINTENANCE).
     */
    public void setStatus(String id, EquipmentStatus status) {
        equipmentRepo.findById(id).ifPresent(eq -> eq.setStatus(status));
    }

    public List<Equipment> getAllEquipment() {
        return equipmentRepo.findAll();
    }

    public Equipment getEquipment(String id) {
        return equipmentRepo.findById(id).orElse(null);
    }
}