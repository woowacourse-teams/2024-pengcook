package net.pengcook.ingredient.repository;

import java.util.Optional;
import net.pengcook.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByName(String name);
}
