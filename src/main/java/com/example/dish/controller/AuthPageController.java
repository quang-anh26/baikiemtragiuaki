package com.example.dish.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/** Cac trang HTML lien quan den dang nhap. */
@Controller
public class AuthPageController {

    @GetMapping("/login")
    public String login(Authentication auth) {
        boolean loggedIn = auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
        return loggedIn ? "redirect:/dishes" : "auth/login";
    }

    /** Trang bao 403 cho request HTML (API thi tra JSON). */
    @GetMapping("/access-denied")
    public String accessDenied(Model model, jakarta.servlet.http.HttpServletResponse response) {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        model.addAttribute("messageKey", "error.forbidden");
        model.addAttribute("messageArg", null);
        return "error/error";
    }

    /** Trang thu goi API bang JWT (hien ket qua authorized / unauthorized). */
    @GetMapping("/api-demo")
    public String apiDemo() {
        return "auth/api-demo";
    }
}
