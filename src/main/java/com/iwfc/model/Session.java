package com.iwfc.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Session {
    private final String sessionId;            // Unique session code (e.g., "SESS-01")
    private final String title;                // Class title (e.g., "Morning HIIT")
    private final String instructorId;         // Assigned instructor ID
    private final String studioRoom;           // Assigned room (e.g., "Studio A")
    private final String equipmentId;          // Assigned equipment ID (optional, can be null)
    private final LocalDateTime startTime;     // Session start timestamp
    private final LocalDateTime endTime;       // Session end timestamp
    private final int capacity;                // Max member slots
    private final List<String> bookedMemberIds;// List holding registered member IDs

    public Session(String sessionId, String title, String instructorId, String studioRoom,
                   String equipmentId, LocalDateTime startTime, LocalDateTime endTime, int capacity) {
        this.sessionId = sessionId;
        this.title = title;
        this.instructorId = instructorId;
        this.studioRoom = studioRoom;
        this.equipmentId = equipmentId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.bookedMemberIds = new ArrayList<>(); // Initial empty attendee list
    }

    // --- Getters ---
    public String getSessionId() { return sessionId; }
    public String getTitle() { return title; }
    public String getInstructorId() { return instructorId; }
    public String getStudioRoom() { return studioRoom; }
    public String getEquipmentId() { return equipmentId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getCapacity() { return capacity; }

    /**
     * Returns an unmodifiable view of the booked member list to protect internal state.
     */
    public List<String> getBookedMemberIds() {
        return Collections.unmodifiableList(bookedMemberIds);
    }

    /**
     * Checks if class capacity has reached its maximum.
     */
    public boolean isFull() {
        return bookedMemberIds.size() >= capacity;
    }

    /**
     * Books a member if space is available and the member hasn't booked yet.
     */
    public boolean addMember(String memberId) {
        if (!isFull() && !bookedMemberIds.contains(memberId)) {
            return bookedMemberIds.add(memberId);
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Room: %s | Equip: %s | Time: %s to %s | Booked: %d/%d",
                sessionId, title, studioRoom, (equipmentId != null ? equipmentId : "None"),
                startTime.toLocalTime(), endTime.toLocalTime(), bookedMemberIds.size(), capacity);
    }
}