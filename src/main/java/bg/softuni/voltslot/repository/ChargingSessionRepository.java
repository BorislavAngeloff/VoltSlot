package bg.softuni.voltslot.repository;

import bg.softuni.voltslot.model.ChargingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ChargingSessionRepository extends JpaRepository<ChargingSession, UUID> {
    List<ChargingSession> findAllByUserId(UUID userId);
}