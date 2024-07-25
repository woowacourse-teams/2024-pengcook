package net.pengcook.recipe.repository;

import java.util.List;
import net.pengcook.recipe.domain.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    List<RecipeStep> findAllByRecipeIdOrderBySequence(long id);
}
