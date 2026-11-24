package bg.softuni.voltslot;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.ChargingSession;
import bg.softuni.voltslot.model.ChargingStation;
import bg.softuni.voltslot.model.User;
import bg.softuni.voltslot.model.enums.BookingStatus;
import bg.softuni.voltslot.model.enums.ChargerType;
import bg.softuni.voltslot.repository.ChargingSessionRepository;
import bg.softuni.voltslot.repository.ChargingStationRepository;
import bg.softuni.voltslot.repository.UserRepository;
import bg.softuni.voltslot.service.ChargingSessionService;
import bg.softuni.voltslot.service.ChargingStationService;
import bg.softuni.voltslot.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ChargingSessionServiceIT {

    @Autowired
    private ChargingSessionService sessionService;

    @Autowired
    private ChargingStationService stationService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChargingStationRepository stationRepository;

    @Autowired
    private ChargingSessionRepository sessionRepository;

    @Test
    void bookVerifyAnprAndFinishWithOverstayPenalty_ShouldWorkEndToEnd() {
        userService.register("test_ev_driver", "Pass123!");
        User user = userRepository.findByUsername("test_ev_driver").orElseThrow();

        stationService.create("Test Hub #99", "София, Център", ChargerType.DC_FAST_150KW, new BigDecimal("0.40"));
        ChargingStation station = stationRepository.findAll().stream()
                .filter(s -> s.getName().equals("Test Hub #99"))
                .findFirst()
                .orElseThrow();

        sessionService.bookSlot(user, station.getId(), "CB9999TT", LocalDateTime.now().plusHours(1), 40);

        List<ChargingSession> sessions = sessionService.getUserSessions(user.getId());
        assertEquals(1, sessions.size());
        ChargingSession session = sessions.get(0);
        assertEquals(BookingStatus.RESERVED, session.getStatus());
        assertFalse(stationRepository.findById(station.getId()).orElseThrow().isAvailable());

        assertThrows(DomainRuleViolationException.class,
                () -> sessionService.verifyPlateAndStartCharging(session.getId(), "PB1111AA"));

        sessionService.verifyPlateAndStartCharging(session.getId(), "cb9999tt");
        ChargingSession activeSession = sessionRepository.findById(session.getId()).orElseThrow();
        assertEquals(BookingStatus.ACTIVE_CHARGING, activeSession.getStatus());
        assertEquals(55, activeSession.getBatteryPercentage());

        sessionService.finishCharging(session.getId(), true);
        ChargingSession finishedSession = sessionRepository.findById(session.getId()).orElseThrow();
        assertEquals(BookingStatus.PENALIZED_OVERSTAY, finishedSession.getStatus());
        assertEquals(100, finishedSession.getBatteryPercentage());
        assertEquals(new BigDecimal("5.00"), finishedSession.getPenaltyEur());
        assertTrue(stationRepository.findById(station.getId()).orElseThrow().isAvailable());
    }
}