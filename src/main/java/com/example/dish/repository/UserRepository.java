package com.example.dish.repository;

import com.example.dish.model.User_BIT240024;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User_BIT240024, Long> {
    Optional<User_BIT240024> findByUsername(String username);
}
