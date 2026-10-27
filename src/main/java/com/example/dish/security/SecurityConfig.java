package com.example.dish.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            // Stateless: khong dung HttpSession, moi request tu mang JWT
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // /api/** dung Bearer token (khong bi CSRF) -> bo qua; form HTML van duoc bao ve boi CSRF token
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/api/**"))
            .formLogin(f -> f.disable())
            .httpBasic(b -> b.disable())
            .logout(l -> l.disable())
            .authorizeHttpRequests(auth -> auth
                    // ---- cong khai ----
                    .requestMatchers("/login", "/access-denied", "/error", "/images/**",
                            "/api/auth/login", "/api/auth/logout").permitAll()
                    // ---- chi ADMIN: tao / sua / xoa ----
                    .requestMatchers(HttpMethod.GET, "/dishes/create", "/dishes/*/edit",
                            "/categories/create", "/categories/*/edit").hasRole("ADMIN")
                    // ---- USER va ADMIN: xem ----
                    .requestMatchers(HttpMethod.GET, "/", "/dishes", "/dishes/*", "/categories",
                            "/api-demo").hasAnyRole("USER", "ADMIN")
                    // moi POST con lai tren /dishes/** va /categories/** (create/edit/delete/images) -> ADMIN
                    .requestMatchers("/dishes/**", "/categories/**").hasRole("ADMIN")
                    // ---- REST API ----
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                    .anyRequest().authenticated())
            .exceptionHandling(e -> e
                    .authenticationEntryPoint(unauthorizedHandler())
                    .accessDeniedHandler(forbiddenHandler()))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /** Chua dang nhap / token sai / het han: API -> 401 JSON, trang web -> chuyen ve /login. */
    private AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, ex) -> {
            if (isApi(request)) {
                writeJson(response, HttpServletResponse.SC_UNAUTHORIZED, "unauthorized",
                        "Missing, invalid or expired JWT.");
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }
        };
    }

    /** Da dang nhap nhung sai role: API -> 403 JSON, trang web -> trang access-denied. */
    private AccessDeniedHandler forbiddenHandler() {
        return (request, response, ex) -> {
            if (isApi(request)) {
                writeJson(response, HttpServletResponse.SC_FORBIDDEN, "forbidden",
                        "Your role is not allowed to access this resource.");
            } else {
                response.sendRedirect(request.getContextPath() + "/access-denied");
            }
        };
    }

    private boolean isApi(HttpServletRequest request) {
        return request.getRequestURI().startsWith(request.getContextPath() + "/api/");
    }

    private void writeJson(HttpServletResponse response, int status, String error, String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"result\":\""
                + (status == 401 ? "unauthorized" : "forbidden") + "\",\"error\":\"" + error
                + "\",\"message\":\"" + message + "\"}");
    }
}
