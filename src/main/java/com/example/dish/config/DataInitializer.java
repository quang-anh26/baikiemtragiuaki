package com.example.dish.config;

import com.example.dish.model.DishCategory_BIT240024;
import com.example.dish.model.DishImage_BIT240024;
import com.example.dish.model.Dish_BIT240024;
import com.example.dish.repository.DishCategoryRepository;
import com.example.dish.repository.DishImageRepository;
import com.example.dish.repository.DishRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/** Du lieu mau: 3 loai mon, 6 mon (moi mon dung 1 loai). Chi chay khi DB trong. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final DishCategoryRepository categoryRepo;
    private final DishRepository dishRepo;
    private final DishImageRepository imageRepo;

    public DataInitializer(DishCategoryRepository categoryRepo, DishRepository dishRepo, DishImageRepository imageRepo) {
        this.categoryRepo = categoryRepo;
        this.dishRepo = dishRepo;
        this.imageRepo = imageRepo;
    }

    @Override
    public void run(String... args) {
        if (categoryRepo.count() > 0) return;

        DishCategory_BIT240024 main = category("Main course", "Rice, noodles and main dishes");
        DishCategory_BIT240024 appetizer = category("Appetizer", "Starters and snacks");
        DishCategory_BIT240024 drink = category("Drink", "Beverages");
        category("Dessert", "Sweet dishes (unused - can be deleted)");

        Dish_BIT240024 pho = dish("Pho Bo", "55000", 20, true, "Hanoi beef noodle soup", main);
        dish("Com Tam", "45000", 15, true, "Broken rice with grilled pork", main);
        Dish_BIT240024 rolls = dish("Spring Rolls", "30000", 10, true, "Crispy fried rolls", appetizer);
        dish("Goi Cuon", "28000", 8, false, "Fresh rolls (currently unavailable)", appetizer);
        Dish_BIT240024 coffee = dish("Ca Phe Sua Da", "25000", 5, true, "Vietnamese iced milk coffee", drink);
        dish("Tra Dao Cam Sa", "35000", 12, true, "Peach tea with lemongrass", drink);

        image(pho, "/images/pho-bo.png", true);
        image(pho, "https://picsum.photos/seed/pho2/400/300", false);
        image(rolls, "https://picsum.photos/seed/rolls/400/300", true);
        image(coffee, "https://picsum.photos/seed/coffee/400/300", true);
    }

    private DishCategory_BIT240024 category(String name, String desc) {
        DishCategory_BIT240024 c = new DishCategory_BIT240024();
        c.setName(name);
        c.setDescription(desc);
        return categoryRepo.save(c);
    }

    private Dish_BIT240024 dish(String name, String price, int time, boolean available, String desc, DishCategory_BIT240024 c) {
        Dish_BIT240024 d = new Dish_BIT240024();
        d.setName(name);
        d.setPrice(new BigDecimal(price));
        d.setPreparationTime(time);
        d.setIsAvailable(available);
        d.setDescription(desc);
        d.setDishCategoryId(c.getId());
        return dishRepo.save(d);
    }

    private void image(Dish_BIT240024 d, String url, boolean thumb) {
        DishImage_BIT240024 i = new DishImage_BIT240024();
        i.setDishId(d.getId());
        i.setImageUrl(url);
        i.setIsThumbnail(thumb);
        imageRepo.save(i);
    }
}
