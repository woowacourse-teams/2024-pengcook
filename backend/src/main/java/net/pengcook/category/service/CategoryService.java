package net.pengcook.category.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.category.domain.Category;
import net.pengcook.category.domain.CategoryRecipe;
import net.pengcook.category.repository.CategoryRecipeRepository;
import net.pengcook.category.repository.CategoryRepository;
import net.pengcook.recipe.domain.Recipe;
import net.pengcook.category.dto.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryRecipeRepository categoryRecipeRepository;

    @Transactional
    public void saveCategories(Recipe recipe, List<String> categories) {
        categories.forEach(category -> saveCategoryRecipe(recipe, category));
    }

    @Transactional
    public void deleteCategoryRecipe(Recipe recipe) {
        categoryRecipeRepository.deleteByRecipe(recipe);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> findCategoryByRecipe(Recipe recipe) {
        List<CategoryRecipe> categoryRecipes = categoryRecipeRepository.findAllByRecipe(recipe);
        return categoryRecipes.stream()
                .map(CategoryRecipe::getCategory)
                .map(CategoryResponse::new)
                .toList();
    }

    private void saveCategoryRecipe(Recipe recipe, String name) {
        Category category = categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));

        categoryRecipeRepository.save(new CategoryRecipe(category, recipe));
    }
}
