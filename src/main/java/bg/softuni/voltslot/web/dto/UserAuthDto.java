package bg.softuni.voltslot.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserAuthDto {

    @NotBlank(message = "Потребителското име е задължително!")
    @Size(min = 3, max = 20, message = "Трябва да е между 3 и 20 символа!")
    private String username;

    @NotBlank(message = "Паролата е задължителна!")
    @Size(min = 8, max = 30, message = "Паролата трябва да е между 8 и 30 символа!")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-]).+$",
            message = "Паролата трябва да съдържа поне 1 малка буква, 1 главна буква, 1 цифра и 1 специален символ (@$!%*?&._-)!"
    )
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}