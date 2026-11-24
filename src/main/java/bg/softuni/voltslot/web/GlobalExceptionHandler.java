package bg.softuni.voltslot.web;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainRuleViolationException.class)
    public String handleDomainException(DomainRuleViolationException ex, Model model) {
        model.addAttribute("errorTitle", "Нарушено бизнес правило");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    @ExceptionHandler(Exception.class)
    public String handleGeneralException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "Възникна системна грешка");
        model.addAttribute("errorMessage", "Моля, проверете въведените данни и опитайте отново.");
        return "error";
    }
}