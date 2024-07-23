package net.pengcook.category.repository;

import net.pengcook.category.domain.CategoryRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRecipeRepository extends JpaRepository<CategoryRecipe, Long> {
}
