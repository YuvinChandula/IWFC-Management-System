package com.iwfc;

import com.iwfc.model.*;
import com.iwfc.pattern.behavioural.StaffNotificationListener;
import com.iwfc.pattern.structural.IWFCFacade;
import com.iwfc.repository.Repository;
import com.iwfc.service.EquipmentService;
import com.iwfc.service.MaintenanceService;
import com.iwfc.service.SessionService;
import com.iwfc.ui.IWFCMainFrame;

import javax.swing.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // 1. Initialize Repositories (Generics & Collections)
        Repository<Equipment> eqRepo = new Repository<>(Equipment::getId);
        Repository<Session> sessRepo = new Repository<>(Session::getSessionId);
        Repository<MaintenanceRequest> maintRepo = new Repository<>(MaintenanceRequest::getRequestId);

        // 2. Initialize Business Services
        EquipmentService eqService = new EquipmentService(eqRepo);
        SessionService sessService = new SessionService(sessRepo);
        MaintenanceService maintService = new MaintenanceService(maintRepo);

        // 3. Attach Console Observer Listener
        maintService.attachObserver(new StaffNotificationListener("Duty Staff"));

        // 4. Initialize Structural Facade
        IWFCFacade facade = new IWFCFacade(eqService, sessService, maintService);

        // 5. Pre-populate Sample Data
        User admin = new User("U01", "Alice Admin", "alice@iwfc.com", UserRole.ADMIN);
        User instructor = new User("U02", "Bob Instructor", "bob@iwfc.com", UserRole.INSTRUCTOR);

        try {
            facade.addEquipment(admin, "TREADMILL", "EQ-TM01", "ProRunner X2", "Cardio Zone");
            facade.addEquipment(admin, "SPIN_BIKE", "EQ-SB01", "SpeedRider V4", "Studio A");
            facade.addEquipment(admin, "ROWER", "EQ-RW01", "HydroRow Concept 2", "Cardio Zone");

            LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0);
            facade.scheduleSession(instructor, "SESS-01", "HIIT Cardio Blast", "Studio A", "EQ-SB01",
                    now.plusHours(1), now.plusHours(2), 10);
            facade.scheduleSession(instructor, "SESS-02", "Morning Yoga Flow", "Studio B", null,
                    now.plusHours(3), now.plusHours(4), 15);
        } catch (Exception e) {
            System.err.println("Setup error: " + e.getMessage());
        }

        // 6. Launch Desktop Graphical User Interface (GUI)
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            IWFCMainFrame frame = new IWFCMainFrame(facade);
            maintService.attachObserver(frame); // GUI also observes live maintenance updates!
            frame.setVisible(true);
        });
    }
}