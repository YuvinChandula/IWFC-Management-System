package com.iwfc;

import com.iwfc.exception.InvalidBookingException;
import com.iwfc.model.Session;
import com.iwfc.repository.Repository;
import com.iwfc.service.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTest {
    private SessionService sessionService;

    @BeforeEach
    void setUp() {
        sessionService = new SessionService(new Repository<>(Session::getSessionId));
    }

    @Test
    void testScheduleAndBookSessionSuccess() throws Exception {
        LocalDateTime start = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(1);

        Session session = sessionService.scheduleSession("S01", "Yoga", "INS01", "Studio 1", null, start, end, 5);
        assertNotNull(session, "Session object should be created.");

        sessionService.bookMember("S01", "MEM01");
        assertEquals(1, session.getBookedMemberIds().size(), "Booked members count should be 1.");
    }

    @Test
    void testDoubleBookingStudioRoomThrowsException() throws Exception {
        LocalDateTime start = LocalDateTime.now().withHour(10).withMinute(0);
        LocalDateTime end = start.plusHours(1);

        sessionService.scheduleSession("S01", "Yoga", "INS01", "Studio 1", null, start, end, 5);

        assertThrows(InvalidBookingException.class, () ->
                sessionService.scheduleSession("S02", "Pilates", "INS02", "Studio 1", null, start, end, 5),
                "Should throw InvalidBookingException for overlapping studio room booking."
        );
    }
}
