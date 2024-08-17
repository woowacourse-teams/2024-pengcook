package net.pengcook.android.data.datasource.makingrecipe

import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipe
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipeDescription
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity

interface MakingRecipeLocalDataSource {
    suspend fun saveRecipeDescription(
        recipeDescription: RecipeDescriptionEntity,
        ingredients: List<IngredientEntity>,
        categories: List<CategoryEntity>,
    ): Long

    suspend fun fetchTotalRecipeData(): CreatedRecipe?

    suspend fun fetchRecipeDescription(): CreatedRecipeDescription?

    suspend fun deleteCreatedRecipeById(recipeId: Long)

    suspend fun deleteAllCreatedRecipes()
}
