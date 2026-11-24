package bg.softuni.voltslot.web.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class BookingDto {

    @NotNull(message = "Изберете зарядна станция!")
    private UUID stationId;

    @NotBlank(message = "Регистрационният номер е задължителен!")
    @Pattern(regexp = "^[A-Za-zА-Яа-я0-9]{4,10}$", message = "Невалиден регистрационен номер (напр. CB1234AB)!")
    private String licensePlate;

    @NotNull(message = "Изберете начален час на слота!")
    private LocalDateTime slotStartTime;

    @Min(value = 15, message = "Минималното време е 15 минути!")
    @Max(value = 240, message = "Максималното време е 240 минути!")
    private int durationMinutes = 30;

    public UUID getStationId() { return stationId; }
    public void setStationId(UUID stationId) { this.stationId = stationId; }
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public LocalDateTime getSlotStartTime() { return slotStartTime; }
    public void setSlotStartTime(LocalDateTime slotStartTime) { this.slotStartTime = slotStartTime; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }
}