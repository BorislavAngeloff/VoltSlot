package bg.softuni.voltslot.web;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.User;
import bg.softuni.voltslot.service.ChargingSessionService;
import bg.softuni.voltslot.service.ChargingStationService;
import bg.softuni.voltslot.service.UserService;
import bg.softuni.voltslot.web.dto.BookingDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/sessions")
public class SessionController {

    private final ChargingSessionService sessionService;
    private final ChargingStationService stationService;
    private final UserService userService;

    public SessionController(ChargingSessionService sessionService,
                             ChargingStationService stationService,
                             UserService userService) {
        this.sessionService = sessionService;
        this.stationService = stationService;
        this.userService = userService;
    }

    @GetMapping
    public String mySessions(@RequestParam(required = false) UUID stationId, HttpSession session, Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        model.addAttribute("sessions", sessionService.getUserSessions(userId));
        model.addAttribute("stations", stationService.findAll());
        if (!model.containsAttribute("bookingDto")) {
            BookingDto dto = new BookingDto();
            if (stationId != null) {
                dto.setStationId(stationId);
            }
            model.addAttribute("bookingDto", dto);
        }
        return "sessions";
    }

    @PostMapping("/book")
    public String bookSlot(@Valid @ModelAttribute("bookingDto") BookingDto bookingDto,
                           BindingResult bindingResult, HttpSession session, Model model) {
        UUID userId = (UUID) session.getAttribute("user_id");
        if (userId == null) return "redirect:/login";

        if (bindingResult.hasErrors()) {
            model.addAttribute("sessions", sessionService.getUserSessions(userId));
            model.addAttribute("stations", stationService.findAll());
            return "sessions";
        }

        User user = userService.findById(userId);
        sessionService.bookSlot(user, bookingDto.getStationId(), bookingDto.getLicensePlate(),
                bookingDto.getSlotStartTime(), bookingDto.getDurationMinutes());
        return "redirect:/sessions";
    }

    @PostMapping("/{id}/anpr")
    public String verifyAnpr(@PathVariable UUID id, @RequestParam String detectedPlate,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user_id") == null) return "redirect:/login";
        try {
            sessionService.verifyPlateAndStartCharging(id, detectedPlate);
            redirectAttributes.addFlashAttribute("successMessage", "ANPR потвърден! Зареждането стартира.");
        } catch (DomainRuleViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/sessions";
    }

    @PostMapping("/{id}/no-show")
    public String applyNoShow(@PathVariable UUID id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user_id") == null) return "redirect:/login";
        sessionService.applyNoShowPenalty(id);
        redirectAttributes.addFlashAttribute("errorMessage", "Начислена неустойка от 5.00 EUR за незает слот до 15 мин!");
        return "redirect:/sessions";
    }

    @PostMapping("/{id}/finish")
    public String finishCharging(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean overstayed,
                                 HttpSession session, RedirectAttributes redirectAttributes) {
        if (session.getAttribute("user_id") == null) return "redirect:/login";
        sessionService.finishCharging(id, overstayed);
        if (overstayed) {
            redirectAttributes.addFlashAttribute("errorMessage", "Батерията е на 100%, но е начислена неустойка 5.00 EUR за просрочване над 15 мин!");
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Зареждането приключи успешно на 100% без неустойки.");
        }
        return "redirect:/sessions";
    }
}