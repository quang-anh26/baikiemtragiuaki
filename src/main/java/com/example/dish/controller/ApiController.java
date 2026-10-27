package com.example.dish.controller;

import com.example.dish.repository.DishCategoryRepository;
import com.example.dish.repository.DishRepository;
import com.example.dish.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * API mau de kiem tra phan quyen bang JWT:
 *  - GET /api/dishes       : USER + ADMIN
 *  - GET /api/admin/stats  : chi ADMIN  (USER se nhan 403)
 *  - khong co / sai JWT    : 401
 * (quy tac nam trong SecurityConfig)
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    public record DishDto(Long id, String name, BigDecimal price, Integer preparationTime,
                          Boolean isAvailable, Long dishCategoryId) {}

    private final DishRepository dishRepository;
    private final DishCategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public ApiController(DishRepository dishRepository, DishCategoryRepository categoryRepository,
                         UserRepository userRepository) {
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/dishes")
    public List<DishDto> dishes() {
        return dishRepository.findAll().stream()
                .map(d -> new DishDto(d.getId(), d.getName(), d.getPrice(), d.getPreparationTime(),
                        d.getIsAvailable(), d.getDishCategoryId()))
                .toList();
    }

    @GetMapping("/admin/stats")
    public Map<String, Long> stats() {
        return Map.of("dishes", dishRepository.count(),
                "categories", categoryRepository.count(),
                "users", userRepository.count());
    }
}
