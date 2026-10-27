package com.example.dish.controller;

import com.example.dish.model.User_BIT240024;
import com.example.dish.repository.UserRepository;
import com.example.dish.security.JwtAuthFilter;
import com.example.dish.security.JwtService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

/** API dang nhap: server xac thuc username/password va tra JWT ve cho client. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/auth/login  {"username":"...","password":"..."}
     *  -> 200 {token, type:"Bearer", username, role, expiresInMs}  (+ cookie "jwt" cho cac trang HTML)
     *  -> 401 neu sai thong tin (thong bao chung, khong tiet lo username co ton tai hay khong)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        User_BIT240024 user = userRepository.findByUsername(req.username().trim()).orElse(null);
        if (user == null || !passwordEncoder.matches(req.password(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", 401, "result", "unauthorized", "error", "invalid_credentials"));
        }

        String token = jwtService.generateToken(user.getUsername(), user.getRole());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, buildCookie(token, Duration.ofMillis(jwtService.getExpirationMs())).toString())
                .body(Map.of(
                        "token", token,
                        "type", "Bearer",
                        "username", user.getUsername(),
                        "role", user.getRole(),
                        "expiresInMs", jwtService.getExpirationMs()));
    }

    /** POST /api/auth/logout -> xoa cookie jwt (client tu xoa token trong localStorage). */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, buildCookie("", Duration.ZERO).toString())
                .build();
    }

    /** GET /api/auth/me -> thong tin nguoi dung hien tai (can JWT hop le). */
    @GetMapping("/me")
    public Map<String, Object> me(Authentication auth) {
        return Map.of("username", auth.getName(),
                "roles", auth.getAuthorities().stream().map(a -> a.getAuthority()).toList());
    }

    private ResponseCookie buildCookie(String value, Duration maxAge) {
        return ResponseCookie.from(JwtAuthFilter.COOKIE_NAME, value)
                .httpOnly(true)          // JS khong doc duoc cookie -> chong danh cap qua XSS
                .sameSite("Lax")         // khong gui kem request POST tu site khac
                .path("/")
                .maxAge(maxAge)
                // .secure(true)         // bat khi trien khai HTTPS
                .build();
    }
}
