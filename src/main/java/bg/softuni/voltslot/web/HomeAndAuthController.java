package bg.softuni.voltslot.web;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.User;
import bg.softuni.voltslot.service.ChargingStationService;
import bg.softuni.voltslot.service.UserService;
import bg.softuni.voltslot.web.dto.UserAuthDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class HomeAndAuthController {

    private final UserService userService;
    private final ChargingStationService stationService;

    public HomeAndAuthController(UserService userService, ChargingStationService stationService) {
        this.userService = userService;
        this.stationService = stationService;
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("stations", stationService.findAll());
        model.addAttribute("loggedIn", session.getAttribute("user_id") != null);
        return "index";
    }

    @GetMapping("/rules")
    public String rules() {
        return "rules";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("userDto", new UserAuthDto());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserAuthDto userDto,
                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            userService.register(userDto.getUsername(), userDto.getPassword());
            return "redirect:/login";
        } catch (DomainRuleViolationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("userDto", new UserAuthDto());
        return "login";
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("userDto") UserAuthDto userDto,
                        BindingResult bindingResult, HttpSession session, Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        try {
            User user = userService.login(userDto.getUsername(), userDto.getPassword());
            session.setAttribute("user_id", user.getId());
            session.setAttribute("username", user.getUsername());
            session.setAttribute("role", user.getRole().name());
            return "redirect:/sessions";
        } catch (DomainRuleViolationException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}