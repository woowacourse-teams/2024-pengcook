package net.pengcook.ingredient.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import net.pengcook.ingredient.domain.IngredientSubstitution;

@Repository
public interface IngredientSubstitutionRepository extends JpaRepository<IngredientSubstitution, Long> {
}
