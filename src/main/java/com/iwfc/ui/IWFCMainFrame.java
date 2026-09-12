package com.iwfc.ui;

import com.iwfc.model.*;
import com.iwfc.pattern.behavioural.MaintenanceObserver;
import com.iwfc.pattern.structural.IWFCFacade;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Graphical User Interface: IWFCMainFrame
 * 
 * Provides an interactive desktop dashboard for the Intelligent Wellness & Fitness Center.
 * Demonstrates:
 * 1. Role switching (Admin, Instructor, Member).
 * 2. Real-time updates via Observer Pattern.
 * 3. Interaction through the IWFCFacade.
 */
public class IWFCMainFrame extends JFrame implements MaintenanceObserver {
    private final IWFCFacade facade;
    
    // Available demo users
    private final User adminUser = new User("U01", "Alice Admin", "alice@iwfc.com", UserRole.ADMIN);
    private final User instructorUser = new User("U02", "Bob Instructor", "bob@iwfc.com", UserRole.INSTRUCTOR);
    private final User memberUser = new User("U03", "Charlie Member", "charlie@gmail.com", UserRole.MEMBER);
    
    private User currentUser = adminUser; // Current active actor

    // UI Table Models
    private DefaultTableModel equipmentTableModel;
    private DefaultTableModel sessionTableModel;
    private DefaultTableModel maintenanceTableModel;
    private DefaultListModel<String> notificationListModel;

    // UI Labels
    private JLabel currentUserLabel;

    public IWFCMainFrame(IWFCFacade facade) {
        this.facade = facade;
        
        setTitle("Intelligent Wellness & Fitness Center (IWFC) Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center on screen

        initUI();
        refreshAllTables();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // 1. TOP HEADER PANEL (User/Role Switcher)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(33, 37, 41));
        topPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel titleLabel = new JLabel("🏋️ IWFC Center Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rolePanel.setOpaque(false);

        currentUserLabel = new JLabel("Current Actor: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        currentUserLabel.setForeground(new Color(255, 193, 7));
        currentUserLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JComboBox<String> userComboBox = new JComboBox<>(new String[]{
                "Alice Admin (ADMIN)",
                "Bob Instructor (INSTRUCTOR)",
                "Charlie Member (MEMBER)"
        });
        userComboBox.addActionListener(e -> {
            int selected = userComboBox.getSelectedIndex();
            currentUser = switch (selected) {
                case 1 -> instructorUser;
                case 2 -> memberUser;
                default -> adminUser;
            };
            currentUserLabel.setText("Current Actor: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        });

        rolePanel.add(currentUserLabel);
        rolePanel.add(new JLabel("  Switch: "));
        rolePanel.add(userComboBox);

        topPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(rolePanel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // 2. MAIN TABBED PANE
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("🏋️ Equipment Inventory", createEquipmentPanel());
        tabbedPane.addTab("📅 Session Scheduling", createSessionPanel());
        tabbedPane.addTab("🔧 Maintenance Log", createMaintenancePanel());
        tabbedPane.addTab("🔔 Live Notifications", createNotificationPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    // ==========================================
    // TAB 1: EQUIPMENT INVENTORY
    // ==========================================
    private JPanel createEquipmentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        equipmentTableModel = new DefaultTableModel(
                new String[]{"ID", "Name", "Category", "Location", "Status", "Usage Hours", "Threshold"}, 0);
        JTable table = new JTable(equipmentTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("➕ Add New Equipment (Admin)");
        JButton refreshBtn = new JButton("🔄 Refresh");

        addBtn.addActionListener(e -> showAddEquipmentDialog());
        refreshBtn.addActionListener(e -> refreshEquipmentTable());

        buttonPanel.add(addBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showAddEquipmentDialog() {
        JTextField idField = new JTextField("EQ-0" + (facade.viewEquipment().size() + 1));
        JTextField nameField = new JTextField();
        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"TREADMILL", "SPIN_BIKE", "ROWER"});
        JTextField locField = new JTextField("Cardio Zone");

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Equipment ID:"));
        form.add(idField);
        form.add(new JLabel("Brand / Name:"));
        form.add(nameField);
        form.add(new JLabel("Type / Category:"));
        form.add(typeCombo);
        form.add(new JLabel("Gym Location:"));
        form.add(locField);

        int result = JOptionPane.showConfirmDialog(this, form, "Add Equipment", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                facade.addEquipment(currentUser, (String) typeCombo.getSelectedItem(),
                        idField.getText().trim(), nameField.getText().trim(), locField.getText().trim());
                JOptionPane.showMessageDialog(this, "Equipment registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshEquipmentTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 2: SESSION SCHEDULING & BOOKINGS
    // ==========================================
    private JPanel createSessionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        sessionTableModel = new DefaultTableModel(
                new String[]{"Session ID", "Title", "Studio", "Equipment", "Start Time", "End Time", "Booked Slots"}, 0);
        JTable table = new JTable(sessionTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton scheduleBtn = new JButton("➕ Schedule Class (Staff)");
        JButton bookBtn = new JButton("🎟️ Book Selected Class (Member)");
        JButton refreshBtn = new JButton("🔄 Refresh");

        scheduleBtn.addActionListener(e -> showScheduleSessionDialog());
        bookBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a session from the table first.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String sessionId = (String) sessionTableModel.getValueAt(selectedRow, 0);
            try {
                facade.bookSession(currentUser, sessionId);
                JOptionPane.showMessageDialog(this, "Booking confirmed for " + currentUser.getName() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshSessionTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Booking Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        refreshBtn.addActionListener(e -> refreshSessionTable());

        buttonPanel.add(scheduleBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showScheduleSessionDialog() {
        JTextField idField = new JTextField("SESS-0" + (facade.viewSessions().size() + 1));
        JTextField titleField = new JTextField("Power Spin & Cardio");
        JTextField studioField = new JTextField("Studio A");
        JTextField equipIdField = new JTextField("EQ-SB01");
        JTextField capacityField = new JTextField("10");

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.add(new JLabel("Session ID:"));
        form.add(idField);
        form.add(new JLabel("Class Title:"));
        form.add(titleField);
        form.add(new JLabel("Studio Room:"));
        form.add(studioField);
        form.add(new JLabel("Assigned Equipment ID:"));
        form.add(equipIdField);
        form.add(new JLabel("Capacity:"));
        form.add(capacityField);

        int result = JOptionPane.showConfirmDialog(this, form, "Schedule Fitness Class", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                LocalDateTime start = LocalDateTime.now().plusHours(2).withMinute(0).withSecond(0);
                LocalDateTime end = start.plusHours(1);
                int cap = Integer.parseInt(capacityField.getText().trim());

                facade.scheduleSession(currentUser, idField.getText().trim(), titleField.getText().trim(),
                        studioField.getText().trim(), equipIdField.getText().trim().isEmpty() ? null : equipIdField.getText().trim(),
                        start, end, cap);

                JOptionPane.showMessageDialog(this, "Session scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshSessionTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Scheduling Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 3: MAINTENANCE LOG
    // ==========================================
    private JPanel createMaintenancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        maintenanceTableModel = new DefaultTableModel(
                new String[]{"Ticket ID", "Equip ID", "Reporter", "Urgency", "Status", "Assigned Admin", "Description"}, 0);
        JTable table = new JTable(maintenanceTableModel);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton reportBtn = new JButton("🚨 Report Equipment Fault (Staff)");
        JButton assignBtn = new JButton("🛠️ Assign Ticket (Admin)");
        JButton refreshBtn = new JButton("🔄 Refresh");

        reportBtn.addActionListener(e -> showReportFaultDialog());
        assignBtn.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a maintenance ticket first.", "Info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String ticketId = (String) maintenanceTableModel.getValueAt(selectedRow, 0);
            try {
                facade.assignMaintenance(currentUser, ticketId, adminUser.getId());
                JOptionPane.showMessageDialog(this, "Ticket assigned to Admin " + adminUser.getName(), "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshMaintenanceTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Authorization Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        refreshBtn.addActionListener(e -> refreshMaintenanceTable());

        buttonPanel.add(reportBtn);
        buttonPanel.add(assignBtn);
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void showReportFaultDialog() {
        JTextField idField = new JTextField("TICKET-0" + (facade.viewMaintenanceRequests().size() + 1));
        JTextField equipIdField = new JTextField("EQ-SB01");
        JComboBox<MaintenanceRequest.Urgency> urgencyCombo = new JComboBox<>(MaintenanceRequest.Urgency.values());
        JTextField descField = new JTextField("Resistance knob calibration error");

        JPanel form = new JPanel(new GridLayout(4, 2, 5, 5));
        form.add(new JLabel("Ticket ID:"));
        form.add(idField);
        form.add(new JLabel("Equipment ID:"));
        form.add(equipIdField);
        form.add(new JLabel("Urgency:"));
        form.add(urgencyCombo);
        form.add(new JLabel("Description:"));
        form.add(descField);

        int result = JOptionPane.showConfirmDialog(this, form, "Report Equipment Fault", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                facade.reportFault(currentUser, idField.getText().trim(), equipIdField.getText().trim(),
                        descField.getText().trim(), (MaintenanceRequest.Urgency) urgencyCombo.getSelectedItem());
                JOptionPane.showMessageDialog(this, "Fault ticket logged!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshMaintenanceTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ==========================================
    // TAB 4: LIVE OBSERVER NOTIFICATION PANEL
    // ==========================================
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        notificationListModel = new DefaultListModel<>();
        JList<String> notificationList = new JList<>(notificationListModel);
        notificationList.setFont(new Font("Consolas", Font.PLAIN, 13));
        panel.add(new JScrollPane(notificationList), BorderLayout.CENTER);

        JButton clearBtn = new JButton("🗑️ Clear Notifications");
        clearBtn.addActionListener(e -> notificationListModel.clear());
        panel.add(clearBtn, BorderLayout.SOUTH);

        return panel;
    }

    // --- Observer Pattern Implementation ---
    @Override
    public void onStatusUpdate(MaintenanceRequest request, String message) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = String.format("[%s] 🔔 Ticket #%s (Equip: %s) -> %s [Status: %s]",
                time, request.getRequestId(), request.getEquipmentId(), message, request.getStatus());
        SwingUtilities.invokeLater(() -> {
            if (notificationListModel != null) {
                notificationListModel.addElement(entry);
            }
        });
    }

    // --- Helper Refresh Methods ---
    private void refreshAllTables() {
        refreshEquipmentTable();
        refreshSessionTable();
        refreshMaintenanceTable();
    }

    private void refreshEquipmentTable() {
        equipmentTableModel.setRowCount(0);
        for (Equipment eq : facade.viewEquipment()) {
            equipmentTableModel.addRow(new Object[]{
                    eq.getId(), eq.getName(), eq.getType(), eq.getLocation(),
                    eq.getStatus(), eq.getUsageHours() + "h", eq.getMaintenanceThresholdHours() + "h"
            });
        }
    }

    private void refreshSessionTable() {
        sessionTableModel.setRowCount(0);
        for (Session s : facade.viewSessions()) {
            sessionTableModel.addRow(new Object[]{
                    s.getSessionId(), s.getTitle(), s.getStudioRoom(),
                    (s.getEquipmentId() != null ? s.getEquipmentId() : "None"),
                    s.getStartTime().toLocalTime(), s.getEndTime().toLocalTime(),
                    s.getBookedMemberIds().size() + "/" + s.getCapacity()
            });
        }
    }

    private void refreshMaintenanceTable() {
        maintenanceTableModel.setRowCount(0);
        for (MaintenanceRequest r : facade.viewMaintenanceRequests()) {
            maintenanceTableModel.addRow(new Object[]{
                    r.getRequestId(), r.getEquipmentId(), r.getReportedByStaffId(),
                    r.getUrgency(), r.getStatus(),
                    (r.getAssignedToAdminId() != null ? r.getAssignedToAdminId() : "Unassigned"),
                    r.getDescription()
            });
        }
    }
}