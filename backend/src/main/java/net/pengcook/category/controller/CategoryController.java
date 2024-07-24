package net.pengcook.category.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.category.dto.RecipeOfCategoryRequest;
import net.pengcook.category.service.CategoryService;
import net.pengcook.recipe.dto.MainRecipeResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<MainRecipeResponse> readRecipesOfCategory(@ModelAttribute RecipeOfCategoryRequest request) {
        return categoryService.readRecipesOfCategory(request);
    }
}
