package net.pengcook.recipe.repository;

import java.util.List;
import java.util.Optional;
import net.pengcook.recipe.domain.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    List<RecipeStep> findAllByRecipeIdOrderBySequence(long id);

    Optional<RecipeStep> findByRecipeIdAndSequence(long recipeId, long sequence);
}
