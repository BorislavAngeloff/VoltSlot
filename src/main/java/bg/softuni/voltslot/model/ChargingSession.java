package bg.softuni.voltslot.model;

import bg.softuni.voltslot.model.enums.BookingStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "charging_sessions")
public class ChargingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private LocalDateTime slotStartTime;

    @Column(nullable = false)
    private int durationMinutes;

    @Column(nullable = false)
    private int batteryPercentage = 20;

    @Column(nullable = false)
    private BigDecimal totalPaidEur;

    @Column(nullable = false)
    private BigDecimal penaltyEur = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.RESERVED;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne(optional = false)
    private ChargingStation station;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public LocalDateTime getSlotStartTime() { return slotStartTime; }
    public void setSlotStartTime(LocalDateTime slotStartTime) { this.slotStartTime = slotStartTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
    public int getBatteryPercentage() { return batteryPercentage; }
    public void setBatteryPercentage(int batteryPercentage) { this.batteryPercentage = batteryPercentage; }
    public BigDecimal getTotalPaidEur() { return totalPaidEur; }
    public void setTotalPaidEur(BigDecimal totalPaidEur) { this.totalPaidEur = totalPaidEur; }
    public BigDecimal getPenaltyEur() { return penaltyEur; }
    public void setPenaltyEur(BigDecimal penaltyEur) { this.penaltyEur = penaltyEur; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public ChargingStation getStation() { return station; }
    public void setStation(ChargingStation station) { this.station = station; }
}