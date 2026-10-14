package com.example.dish.controller;

import com.example.dish.exception.ResourceNotFoundException;
import com.example.dish.model.Dish_BIT240024;
import com.example.dish.repository.DishCategoryRepository;
import com.example.dish.repository.DishRepository;
import com.example.dish.service.DishService;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Controller
public class DishController {

    private final DishService dishService;
    private final DishRepository dishRepository;
    private final DishCategoryRepository categoryRepository;

    public DishController(DishService dishService, DishRepository dishRepository,
                          DishCategoryRepository categoryRepository) {
        this.dishService = dishService;
        this.dishRepository = dishRepository;
        this.categoryRepository = categoryRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true)); // "" -> null, trim
        binder.setDisallowedFields("id", "images", "dishCategory");               // chong mass-assignment
    }

    // ================= LIST =================
    @GetMapping({"/", "/dishes"})
    public String list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) Boolean available,
                       @RequestParam(required = false) String minPrice,
                       @RequestParam(required = false) String maxPrice,
                       @RequestParam(required = false) String sort,
                       Model model) {

        BigDecimal min = null, max = null;
        boolean priceError = false;
        try {
            if (minPrice != null && !minPrice.isBlank()) min = new BigDecimal(minPrice.trim());
            if (maxPrice != null && !maxPrice.isBlank()) max = new BigDecimal(maxPrice.trim());
            if ((min != null && min.signum() < 0) || (max != null && max.signum() < 0)
                    || (min != null && max != null && min.compareTo(max) > 0)) {
                priceError = true;
            }
        } catch (NumberFormatException e) {
            priceError = true;
        }

        List<Dish_BIT240024> dishes = priceError
                ? Collections.emptyList()
                : dishService.search(keyword, categoryId, available, min, max, sort);

        model.addAttribute("dishes", dishes);
        model.addAttribute("priceError", priceError);
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
        // Giu nguyen dieu kien loc de hien lai tren form
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("available", available);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("sort", sort);
        return "dish/list";
    }

    // ================= DETAIL =================
    @GetMapping("/dishes/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("dish", dishService.getDish(id));
        return "dish/detail";
    }

    // ================= CREATE =================
    @GetMapping("/dishes/create")
    public String createForm(Model model) {
        model.addAttribute("dish", new Dish_BIT240024());
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
        return "dish/form";
    }

    @PostMapping("/dishes/create")
    public String create(@Valid @ModelAttribute("dish") Dish_BIT240024 dish, BindingResult br,
                         Model model, RedirectAttributes ra) {
        validateBusiness(dish, null, br);
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
            return "dish/form";
        }
        dish.setId(null);
        dishRepository.save(dish);
        ra.addFlashAttribute("successKey", "dish.created");
        return "redirect:/dishes";
    }

    // ================= EDIT =================
    @GetMapping("/dishes/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("dish", dishService.getDish(id));
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
        return "dish/form";
    }

    @PostMapping("/dishes/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("dish") Dish_BIT240024 form,
                       BindingResult br, Model model, RedirectAttributes ra) {
        Dish_BIT240024 existing = dishService.getDish(id);
        form.setId(id);
        validateBusiness(form, id, br);
        if (br.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findAll(Sort.by("name")));
            return "dish/form";
        }
        existing.setName(form.getName());
        existing.setPrice(form.getPrice());
        existing.setPreparationTime(form.getPreparationTime());
        existing.setIsAvailable(form.getIsAvailable());
        existing.setDescription(form.getDescription());
        existing.setDishCategoryId(form.getDishCategoryId());
        dishRepository.save(existing);
        ra.addFlashAttribute("successKey", "dish.updated");
        return "redirect:/dishes";
    }

    // ================= DELETE =================
    @PostMapping("/dishes/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        dishRepository.delete(dishService.getDish(id));
        ra.addFlashAttribute("successKey", "dish.deleted");
        return "redirect:/dishes";
    }

    // ================= IMAGES =================
    @PostMapping("/dishes/{id}/images")
    public String addImage(@PathVariable Long id,
                           @RequestParam(required = false) String imageUrl,
                           @RequestParam(defaultValue = "false") boolean thumbnail,
                           RedirectAttributes ra) {
        dishService.getDish(id);
        String url = imageUrl == null ? "" : imageUrl.trim();
        if (!url.matches("^(https?://|/)\\S+$") || url.length() > 500) {
            ra.addFlashAttribute("errorKey", "image.url.invalid");
            return "redirect:/dishes/" + id;
        }
        dishService.addImage(id, url, thumbnail);
        ra.addFlashAttribute("successKey", "image.added");
        return "redirect:/dishes/" + id;
    }

    @PostMapping("/dishes/{id}/images/{imageId}/thumbnail")
    public String setThumbnail(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes ra) {
        dishService.setThumbnail(id, imageId);
        ra.addFlashAttribute("successKey", "image.thumbnail.set");
        return "redirect:/dishes/" + id;
    }

    @PostMapping("/dishes/{id}/images/{imageId}/delete")
    public String deleteImage(@PathVariable Long id, @PathVariable Long imageId, RedirectAttributes ra) {
        dishService.deleteImage(id, imageId);
        ra.addFlashAttribute("successKey", "image.deleted");
        return "redirect:/dishes/" + id;
    }

    // ================= helper =================
    /** DishCategoryId phai ton tai; ten mon khong trung trong cung mot loai. */
    private void validateBusiness(Dish_BIT240024 dish, Long excludeId, BindingResult br) {
        if (dish.getDishCategoryId() != null && !br.hasFieldErrors("dishCategoryId")) {
            if (!categoryRepository.existsById(dish.getDishCategoryId())) {
                br.rejectValue("dishCategoryId", "dish.category.invalid");
                return;
            }
            if (dish.getName() != null && !br.hasFieldErrors("name")) {
                boolean dup = excludeId == null
                        ? dishRepository.existsByNameIgnoreCaseAndDishCategoryId(dish.getName(), dish.getDishCategoryId())
                        : dishRepository.existsByNameIgnoreCaseAndDishCategoryIdAndIdNot(dish.getName(), dish.getDishCategoryId(), excludeId);
                if (dup) br.rejectValue("name", "dish.name.duplicate");
            }
        }
    }
}
