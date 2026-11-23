package bg.softuni.voltslot.service;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.ChargingStation;
import bg.softuni.voltslot.model.enums.ChargerType;
import bg.softuni.voltslot.repository.ChargingStationRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ChargingStationService {

    private final ChargingStationRepository stationRepository;

    public ChargingStationService(ChargingStationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @PostConstruct
    public void initSampleStations() {
        if (stationRepository.count() == 0) {
            create("Sofia Ring Hypercharger #1", "София, Околовръстен път 214", ChargerType.ULTRA_FAST_350KW, new BigDecimal("0.45"));
            create("Plovdiv Trakia Hub #2", "Пловдив, бул. Цариградско шосе", ChargerType.DC_FAST_150KW, new BigDecimal("0.38"));
            create("Varna Sea Garden AC #3", "Варна, Морска гара", ChargerType.AC_22KW, new BigDecimal("0.25"));
        }
    }

    public void create(String name, String location, ChargerType type, BigDecimal pricePerKwh) {
        ChargingStation station = new ChargingStation();
        station.setName(name);
        station.setLocation(location);
        station.setChargerType(type);
        station.setPricePerKwh(pricePerKwh);
        station.setAvailable(true);
        stationRepository.save(station);
    }

    public List<ChargingStation> findAll() {
        return stationRepository.findAll();
    }

    public ChargingStation findById(UUID id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new DomainRuleViolationException("Станцията не е намерена!"));
    }

    public void update(UUID id, String name, String location, ChargerType type, BigDecimal pricePerKwh) {
        ChargingStation station = findById(id);
        station.setName(name);
        station.setLocation(location);
        station.setChargerType(type);
        station.setPricePerKwh(pricePerKwh);
        stationRepository.save(station);
    }

    public void delete(UUID id) {
        ChargingStation station = findById(id);
        if (!station.isAvailable()) {
            throw new DomainRuleViolationException("Не може да изтриете станция с активна резервация!");
        }
        stationRepository.delete(station);
    }
}