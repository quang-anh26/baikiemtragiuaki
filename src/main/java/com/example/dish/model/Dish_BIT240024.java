package com.example.dish.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "BIT240024_Dishes")
@Getter
@Setter
public class Dish_BIT240024 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BIT240024_Id")
    private Long id;

    @NotBlank(message = "{dish.name.required}")
    @Size(max = 150, message = "{validation.size.max}")
    @Column(name = "BIT240024_Name", nullable = false, length = 150)
    private String name;

    @NotNull(message = "{dish.price.required}")
    @DecimalMin(value = "0", inclusive = false, message = "{dish.price.positive}")
    @Column(name = "BIT240024_Price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @NotNull(message = "{dish.time.required}")
    @Min(value = 1, message = "{dish.time.positive}")
    @Column(name = "BIT240024_PreparationTime", nullable = false)
    private Integer preparationTime;

    @Column(name = "BIT240024_IsAvailable", nullable = false)
    private Boolean isAvailable = true;

    @Size(max = 1000, message = "{validation.size.max}")
    @Column(name = "BIT240024_Description", length = 1000)
    private String description;

    /** Cot khoa ngoai (ghi/doc truc tiep bang Id) */
    @NotNull(message = "{dish.category.required}")
    @Column(name = "BIT240024_DishCategoryId", nullable = false)
    private Long dishCategoryId;

    /** Quan he chi doc de hien thi ten loai mon */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BIT240024_DishCategoryId", insertable = false, updatable = false)
    private DishCategory_BIT240024 dishCategory;

    /** Mot Dish co nhieu DishImage */
    @OneToMany(mappedBy = "dish", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DishImage_BIT240024> images = new ArrayList<>();

    /** Phan loai thoi gian che bien - TINH KHI HIEN THI, khong luu DB. */
    @Transient
    public boolean isQuick() {
        return preparationTime != null && preparationTime <= 15;
    }

    /** Anh dai dien (neu khong co thi lay anh dau tien, khong co anh -> null). */
    @Transient
    public DishImage_BIT240024 getThumbnail() {
        return images.stream()
                .filter(i -> Boolean.TRUE.equals(i.getIsThumbnail()))
                .findFirst()
                .orElse(images.isEmpty() ? null : images.get(0));
    }
}
