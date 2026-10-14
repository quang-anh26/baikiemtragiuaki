package com.example.dish.service;

import com.example.dish.exception.ResourceNotFoundException;
import com.example.dish.model.DishImage_BIT240024;
import com.example.dish.model.Dish_BIT240024;
import com.example.dish.repository.DishImageRepository;
import com.example.dish.repository.DishRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DishService {

    private final DishRepository dishRepository;
    private final DishImageRepository imageRepository;

    public DishService(DishRepository dishRepository, DishImageRepository imageRepository) {
        this.dishRepository = dishRepository;
        this.imageRepository = imageRepository;
    }

    public Dish_BIT240024 getDish(Long id) {
        return dishRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.dish.notfound", id));
    }

    /** Loc / tim kiem / sap xep TREN DATABASE bang Specification (khong loc bang vong lap). */
    @Transactional(readOnly = true)
    public List<Dish_BIT240024> search(String keyword, Long categoryId, Boolean available,
                                       BigDecimal minPrice, BigDecimal maxPrice, String sort) {
        Specification<Dish_BIT240024> spec = (root, query, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase()
                        .replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
                ps.add(cb.like(cb.lower(root.<String>get("name")), like, '\\'));
            }
            if (categoryId != null) {
                ps.add(cb.equal(root.get("dishCategoryId"), categoryId));
            }
            if (available != null) {
                ps.add(cb.equal(root.get("isAvailable"), available));
            }
            if (minPrice != null) {
                ps.add(cb.greaterThanOrEqualTo(root.<BigDecimal>get("price"), minPrice));
            }
            if (maxPrice != null) {
                ps.add(cb.lessThanOrEqualTo(root.<BigDecimal>get("price"), maxPrice));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return dishRepository.findAll(spec, toSort(sort));
    }

    private Sort toSort(String sort) {
        if (sort == null) return Sort.by(Sort.Direction.ASC, "id");
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price").and(Sort.by("id"));
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price").and(Sort.by("id"));
            case "time_asc" -> Sort.by(Sort.Direction.ASC, "preparationTime").and(Sort.by("id"));
            default -> Sort.by(Sort.Direction.ASC, "id");
        };
    }

    // ================= IMAGES =================

    /** Them anh. Moi mon toi da 1 anh dai dien. Anh dau tien tu dong la anh dai dien. */
    @Transactional
    public void addImage(Long dishId, String url, boolean thumbnail) {
        getDish(dishId); // 404 neu khong ton tai
        List<DishImage_BIT240024> existing = imageRepository.findByDishIdOrderByIdAsc(dishId);
        boolean makeThumb = thumbnail || existing.stream().noneMatch(i -> Boolean.TRUE.equals(i.getIsThumbnail()));
        if (makeThumb) {
            existing.forEach(i -> i.setIsThumbnail(false));
        }
        DishImage_BIT240024 img = new DishImage_BIT240024();
        img.setDishId(dishId);
        img.setImageUrl(url);
        img.setIsThumbnail(makeThumb);
        imageRepository.save(img);
    }

    @Transactional
    public void setThumbnail(Long dishId, Long imageId) {
        getDish(dishId);
        DishImage_BIT240024 target = findImageOfDish(dishId, imageId);
        for (DishImage_BIT240024 i : imageRepository.findByDishIdOrderByIdAsc(dishId)) {
            i.setIsThumbnail(i.getId().equals(target.getId()));
        }
    }

    @Transactional
    public void deleteImage(Long dishId, Long imageId) {
        getDish(dishId);
        DishImage_BIT240024 target = findImageOfDish(dishId, imageId);
        boolean wasThumb = Boolean.TRUE.equals(target.getIsThumbnail());
        imageRepository.delete(target);
        imageRepository.flush();
        if (wasThumb) {
            imageRepository.findByDishIdOrderByIdAsc(dishId).stream().findFirst()
                    .ifPresent(i -> i.setIsThumbnail(true));
        }
    }

    private DishImage_BIT240024 findImageOfDish(Long dishId, Long imageId) {
        return imageRepository.findById(imageId)
                .filter(i -> i.getDishId().equals(dishId))
                .orElseThrow(() -> new ResourceNotFoundException("error.image.notfound", imageId));
    }
}
