package com.example.dish.config;

import com.example.dish.model.User_BIT240024;
import com.example.dish.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Tai khoan mau: admin / admin123 (ADMIN) va user / user123 (USER). Chi tao neu chua ton tai. */
@Component
public class UserDataInitializer implements CommandLineRunner {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    public UserDataInitializer(UserRepository userRepo, PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.encoder = encoder;
    }

    @Override
    public void run(String... args) {
        create("admin", "admin123", "ADMIN");
        create("user", "user123", "USER");
    }

    private void create(String username, String rawPassword, String role) {
        if (userRepo.findByUsername(username).isPresent()) return;
        User_BIT240024 u = new User_BIT240024();
        u.setUsername(username);
        u.setPassword(encoder.encode(rawPassword));
        u.setRole(role);
        userRepo.save(u);
    }
}
