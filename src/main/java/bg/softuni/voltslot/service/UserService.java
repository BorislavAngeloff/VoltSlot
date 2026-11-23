package bg.softuni.voltslot.service;

import bg.softuni.voltslot.exception.DomainRuleViolationException;
import bg.softuni.voltslot.model.User;
import bg.softuni.voltslot.model.enums.UserRole;
import bg.softuni.voltslot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String username, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DomainRuleViolationException("Потребителското име вече съществува!");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashPassword(rawPassword));
        user.setRole(userRepository.count() == 0 ? UserRole.ADMIN : UserRole.USER);
        userRepository.save(user);
    }

    public User login(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new DomainRuleViolationException("Невалидно потребителско име или парола!"));
        if (!user.getPassword().equals(hashPassword(rawPassword))) {
            throw new DomainRuleViolationException("Невалидно потребителско име или парола!");
        }
        return user;
    }

    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DomainRuleViolationException("Потребителят не е намерен!"));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}