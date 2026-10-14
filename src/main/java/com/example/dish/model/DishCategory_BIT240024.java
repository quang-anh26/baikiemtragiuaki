package com.example.dish.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BIT240024_DishCategories")
@Getter
@Setter
public class DishCategory_BIT240024 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIT240024_Id")
    private Long id;

    @NotBlank(message = "{category.name.required}")
    @Size(max = 100, message = "{validation.size.max}")
    @Column(name = "BIT240024_Name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "{validation.size.max}")
    @Column(name = "BIT240024_Description", length = 500)
    private String description;

    /** Mot DishCategory co nhieu Dish */
    @OneToMany(mappedBy = "dishCategory")
    private List<Dish_BIT240024> dishes = new ArrayList<>();
}
