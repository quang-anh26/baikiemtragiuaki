package com.example.dish.controller;

import com.example.dish.exception.ResourceNotFoundException;
import com.example.dish.model.DishCategory_BIT240024;
import com.example.dish.repository.DishCategoryRepository;
import com.example.dish.repository.DishRepository;
import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/categories")
public class CategoryController {

    private final DishCategoryRepository categoryRepository;
    private final DishRepository dishRepository;

    public CategoryController(DishCategoryRepository categoryRepository, DishRepository dishRepository) {
        this.categoryRepository = categoryRepository;
        this.dishRepository = dishRepository;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.setDisallowedFields("id", "dishes");
    }

    private DishCategory_BIT240024 getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("error.category.notfound", id));
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", categoryRepository.findAll(Sort.by("id")));
        return "category/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("category", new DishCategory_BIT240024());
        return "category/form";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("category") DishCategory_BIT240024 category,
                         BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) return "category/form";
        category.setId(null);
        categoryRepository.save(category);
        ra.addFlashAttribute("successKey", "category.created");
        return "redirect:/categories";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", getCategory(id));
        return "category/form";
    }

    @PostMapping("/{id}/edit")
    public String edit(@PathVariable Long id, @Valid @ModelAttribute("category") DishCategory_BIT240024 form,
                       BindingResult br, RedirectAttributes ra) {
        DishCategory_BIT240024 existing = getCategory(id);
        form.setId(id);
        if (br.hasErrors()) return "category/form";
        existing.setName(form.getName());
        existing.setDescription(form.getDescription());
        categoryRepository.save(existing);
        ra.addFlashAttribute("successKey", "category.updated");
        return "redirect:/categories";
    }

    /** Khong cho xoa loai mon dang co mon su dung. */
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        DishCategory_BIT240024 category = getCategory(id);
        if (dishRepository.existsByDishCategoryId(id)) {
            ra.addFlashAttribute("errorKey", "category.delete.inuse");
            return "redirect:/categories";
        }
        categoryRepository.delete(category);
        ra.addFlashAttribute("successKey", "category.deleted");
        return "redirect:/categories";
    }
}
