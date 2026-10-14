package com.example.dish.repository;

import com.example.dish.model.Dish_BIT240024;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DishRepository extends JpaRepository<Dish_BIT240024, Long>, JpaSpecificationExecutor<Dish_BIT240024> {

    boolean existsByDishCategoryId(Long dishCategoryId);

    boolean existsByNameIgnoreCaseAndDishCategoryId(String name, Long dishCategoryId);

    boolean existsByNameIgnoreCaseAndDishCategoryIdAndIdNot(String name, Long dishCategoryId, Long id);
}
