package bg.softuni.voltslot.repository;

import bg.softuni.voltslot.model.ChargingStation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ChargingStationRepository extends JpaRepository<ChargingStation, UUID> {
}
