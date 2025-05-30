package net.pengcook.category.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import net.pengcook.category.domain.Category;
import net.pengcook.category.domain.CategoryRecipe;
import net.pengcook.category.repository.CategoryRecipeRepository;
import net.pengcook.category.repository.CategoryRepository;
import net.pengcook.recipe.domain.Recipe;
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

    private void saveCategoryRecipe(Recipe recipe, String name) {
        Category category = categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));

        categoryRecipeRepository.save(new CategoryRecipe(category, recipe));
    }
}
