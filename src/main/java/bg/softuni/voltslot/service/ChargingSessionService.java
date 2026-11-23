package bg.softuni.voltslot.service;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.ChargingSession;
import bg.softuni.voltslot.model.ChargingStation;
import bg.softuni.voltslot.model.User;
import bg.softuni.voltslot.model.enums.BookingStatus;
import bg.softuni.voltslot.repository.ChargingSessionRepository;
import bg.softuni.voltslot.repository.ChargingStationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ChargingSessionService {

    private static final BigDecimal PENALTY_AMOUNT_EUR = new BigDecimal("5.00");

    private final ChargingSessionRepository sessionRepository;
    private final ChargingStationRepository stationRepository;

    public ChargingSessionService(ChargingSessionRepository sessionRepository,
                                  ChargingStationRepository stationRepository) {
        this.sessionRepository = sessionRepository;
        this.stationRepository = stationRepository;
    }

    public void bookSlot(User user, UUID stationId, String licensePlate, LocalDateTime startTime, int durationMinutes) {
        ChargingStation station = stationRepository.findById(stationId)
                .orElseThrow(() -> new DomainRuleViolationException("Станцията не съществува!"));

        if (!station.isAvailable()) {
            throw new DomainRuleViolationException("Избраната зарядна станция е заета!");
        }

        ChargingSession session = new ChargingSession();
        session.setUser(user);
        session.setStation(station);
        session.setLicensePlate(licensePlate.toUpperCase().trim());
        session.setSlotStartTime(startTime);
        session.setDurationMinutes(durationMinutes);
        session.setBatteryPercentage(20);
        session.setTotalPaidEur(station.getPricePerKwh().multiply(BigDecimal.valueOf(durationMinutes * 0.5)));

        station.setAvailable(false);
        stationRepository.save(station);
        sessionRepository.save(session);
    }

    public void verifyPlateAndStartCharging(UUID sessionId, String detectedPlate) {
        ChargingSession session = getSession(sessionId);
        if (!session.getLicensePlate().equalsIgnoreCase(detectedPlate.trim())) {
            throw new DomainRuleViolationException("ANPR отказ! Камерата засече " + detectedPlate + ", а слотът е резервиран за " + session.getLicensePlate());
        }
        session.setStatus(BookingStatus.ACTIVE_CHARGING);
        session.setBatteryPercentage(55);
        sessionRepository.save(session);
    }

    public void applyNoShowPenalty(UUID sessionId) {
        ChargingSession session = getSession(sessionId);
        session.setPenaltyEur(session.getPenaltyEur().add(PENALTY_AMOUNT_EUR));
        session.setStatus(BookingStatus.PENALIZED_NO_SHOW);
        releaseStation(session.getStation());
        sessionRepository.save(session);
    }

    public void finishCharging(UUID sessionId, boolean overstayedMoreThan15Min) {
        ChargingSession session = getSession(sessionId);
        session.setBatteryPercentage(100);
        if (overstayedMoreThan15Min) {
            session.setPenaltyEur(session.getPenaltyEur().add(PENALTY_AMOUNT_EUR));
            session.setStatus(BookingStatus.PENALIZED_OVERSTAY);
        } else {
            session.setStatus(BookingStatus.COMPLETED);
        }
        releaseStation(session.getStation());
        sessionRepository.save(session);
    }

    public List<ChargingSession> getUserSessions(UUID userId) {
        return sessionRepository.findAllByUserId(userId);
    }

    private ChargingSession getSession(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new DomainRuleViolationException("Сесията не е намерена!"));
    }

    private void releaseStation(ChargingStation station) {
        station.setAvailable(true);
        stationRepository.save(station);
    }
}