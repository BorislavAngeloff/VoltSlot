package bg.softuni.voltslot.web;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.ChargingStation;
import bg.softuni.voltslot.model.enums.ChargerType;
import bg.softuni.voltslot.service.ChargingStationService;
import bg.softuni.voltslot.web.dto.StationDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/stations")
public class StationController {

    private final ChargingStationService stationService;

    public StationController(ChargingStationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public String listAndCreateForm(Model model, HttpSession session) {
        if (session.getAttribute("user_id") == null) return "redirect:/login";
        model.addAttribute("stations", stationService.findAll());
        model.addAttribute("stationDto", new StationDto());
        model.addAttribute("types", ChargerType.values());
        return "stations";
    }

    @PostMapping("/add")
    public String createStation(@Valid @ModelAttribute("stationDto") StationDto stationDto,
                                BindingResult bindingResult, HttpSession session, Model model) {
        if (!isAdmin(session)) throw new DomainRuleViolationException("Само администратор може да добавя станции!");
        if (bindingResult.hasErrors()) {
            model.addAttribute("stations", stationService.findAll());
            model.addAttribute("types", ChargerType.values());
            return "stations";
        }
        stationService.create(stationDto.getName(), stationDto.getLocation(), stationDto.getChargerType(), stationDto.getPricePerKwh());
        return "redirect:/stations";

    }
    @PostMapping("/toggle/{id}")
    public String toggleStationStatus(@PathVariable UUID id, HttpSession session) {
        if (!isAdmin(session)) throw new DomainRuleViolationException("Само администратор може да променя статуса!");
        stationService.toggleStatus(id);
        return "redirect:/stations";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, HttpSession session, Model model) {
        if (!isAdmin(session)) throw new DomainRuleViolationException("Само администратор може да редактира станции!");
        ChargingStation station = stationService.findById(id);
        StationDto dto = new StationDto();
        dto.setName(station.getName());
        dto.setLocation(station.getLocation());
        dto.setChargerType(station.getChargerType());
        dto.setPricePerKwh(station.getPricePerKwh());

        model.addAttribute("stationId", id);
        model.addAttribute("stationDto", dto);
        model.addAttribute("types", ChargerType.values());
        return "station-edit";
    }

    @PostMapping("/edit/{id}")
    public String updateStation(@PathVariable UUID id, @Valid @ModelAttribute("stationDto") StationDto stationDto,
                                BindingResult bindingResult, HttpSession session, Model model) {
        if (!isAdmin(session)) throw new DomainRuleViolationException("Само администратор може да редактира станции!");
        if (bindingResult.hasErrors()) {
            model.addAttribute("stationId", id);
            model.addAttribute("types", ChargerType.values());
            return "station-edit";
        }
        stationService.update(id, stationDto.getName(), stationDto.getLocation(), stationDto.getChargerType(), stationDto.getPricePerKwh());
        return "redirect:/stations";
    }

    @PostMapping("/delete/{id}")
    public String deleteStation(@PathVariable UUID id, HttpSession session) {
        if (!isAdmin(session)) throw new DomainRuleViolationException("Само администратор може да изтрива станции!");
        stationService.delete(id);
        return "redirect:/stations";
    }

    private boolean isAdmin(HttpSession session) {
        return "ADMIN".equals(session.getAttribute("role"));
    }
}