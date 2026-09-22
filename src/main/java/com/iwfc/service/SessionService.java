package com.iwfc.service;

import com.iwfc.exception.DuplicateDataException;
import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.Session;
import com.iwfc.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;


public class SessionService {
    private final Repository<Session> sessionRepo;

    public SessionService(Repository<Session> sessionRepo) {
        this.sessionRepo = sessionRepo;
    }

    /**
     * Schedules a fitness session after validating time slots and conflicts.
     */
    public Session scheduleSession(String sessionId, String title, String instructorId,
                                  String studioRoom, String equipmentId,
                                  LocalDateTime start, LocalDateTime end, int capacity)
            throws DuplicateDataException, InvalidBookingException {

        // 1. Check for Duplicate Session ID
        if (sessionRepo.existsById(sessionId)) {
            throw new DuplicateDataException("Session with ID '" + sessionId + "' already exists.");
        }

        // 2. Validate Chronological Order
        if (start.isAfter(end) || start.isEqual(end)) {
            throw new InvalidBookingException("Session start time must be strictly before end time.");
        }

        // 3. Operating Hours Constraint: 06:00 AM to 22:00 PM
        if (start.getHour() < 6 || end.getHour() > 22 || (end.getHour() == 22 && end.getMinute() > 0)) {
            throw new InvalidBookingException("Session falls outside gym operating hours (06:00 - 22:00).");
        }

        // 4. Strict Double-Booking Validation (Room & Equipment overlap)
        for (Session existing : sessionRepo.findAll()) {
            // Condition for time overlap between two intervals: (StartA < EndB) and (EndA > StartB)
            boolean timeOverlap = start.isBefore(existing.getEndTime()) && end.isAfter(existing.getStartTime());
            if (timeOverlap) {
                if (existing.getStudioRoom().equalsIgnoreCase(studioRoom)) {
                    throw new InvalidBookingException("Double Booking Conflict: Studio Room '" 
                            + studioRoom + "' is already occupied by session '" + existing.getTitle() + "'.");
                }
                if (equipmentId != null && equipmentId.equalsIgnoreCase(existing.getEquipmentId())) {
                    throw new InvalidBookingException("Double Booking Conflict: Equipment '" 
                            + equipmentId + "' is already reserved for session '" + existing.getTitle() + "'.");
                }
            }
        }

        // Save valid session
        Session session = new Session(sessionId, title, instructorId, studioRoom, equipmentId, start, end, capacity);
        sessionRepo.save(session);
        return session;
    }

    /**
     * Books a member into a session, checking for capacity and double bookings.
     */
    public void bookMember(String sessionId, String memberId) throws InvalidBookingException {
        Session session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new InvalidBookingException("Session ID '" + sessionId + "' not found."));

        if (session.isFull()) {
            throw new InvalidBookingException("Session '" + session.getTitle() + "' is fully booked.");
        }

        if (session.getBookedMemberIds().contains(memberId)) {
            throw new InvalidBookingException("Member '" + memberId + "' is already booked into this session.");
        }

        session.addMember(memberId);
    }

    public List<Session> getAllSessions() {
        return sessionRepo.findAll();
    }
}