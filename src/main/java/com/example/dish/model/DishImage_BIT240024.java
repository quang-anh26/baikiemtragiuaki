package com.example.dish.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "BIT240024_DishImages")
@Getter
@Setter
public class DishImage_BIT240024 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIT240024_Id")
    private Long id;

    @NotBlank(message = "{image.url.invalid}")
    @Size(max = 500, message = "{validation.size.max}")
    @Column(name = "BIT240024_ImageUrl", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "BIT240024_IsThumbnail", nullable = false)
    private Boolean isThumbnail = false;

    @Column(name = "BIT240024_DishId", nullable = false)
    private Long dishId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIT240024_DishId", insertable = false, updatable = false)
    private Dish_BIT240024 dish;
}
