package com.example.dish.repository;

import com.example.dish.model.DishImage_BIT240024;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DishImageRepository extends JpaRepository<DishImage_BIT240024, Long> {

    List<DishImage_BIT240024> findByDishIdOrderByIdAsc(Long dishId);
}
