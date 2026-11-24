package bg.softuni.voltslot.web.dto;

import bg.softuni.voltslot.model.enums.ChargerType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class StationDto {

    @NotBlank(message = "Името на станцията е задължително!")
    @Size(min = 3, max = 50, message = "Името трябва да е между 3 и 50 символа!")
    private String name;

    @NotBlank(message = "Локацията е задължителна!")
    private String location;

    @NotNull(message = "Изберете тип зарядно!")
    private ChargerType chargerType;

    @NotNull(message = "Цената е задължителна!")
    @DecimalMin(value = "0.05", message = "Минималната цена е 0.05 EUR/kWh!")
    private BigDecimal pricePerKwh;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public ChargerType getChargerType() { return chargerType; }
    public void setChargerType(ChargerType chargerType) { this.chargerType = chargerType; }
    public BigDecimal getPricePerKwh() { return pricePerKwh; }
    public void setPricePerKwh(BigDecimal pricePerKwh) { this.pricePerKwh = pricePerKwh; }
}