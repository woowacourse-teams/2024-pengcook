package net.pengcook.android.data.model.makingrecipe.entity

import androidx.room.Embedded
import androidx.room.Relation
import net.pengcook.android.data.local.database.contract.CategoryContract
import net.pengcook.android.data.local.database.contract.IngredientContract
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract

data class CreatedRecipeDescription(
    @Embedded val recipeDescription: RecipeDescriptionEntity,
    @Relation(
        parentColumn = RecipeDescriptionContract.COLUMN_ID,
        entityColumn = CategoryContract.COLUMN_RECIPE_DESCRIPTION_ID,
    )
    val categories: List<CategoryEntity>,
    @Relation(
        parentColumn = RecipeDescriptionContract.COLUMN_ID,
        entityColumn = IngredientContract.COLUMN_RECIPE_DESCRIPTION_ID,
    )
    val ingredients: List<IngredientEntity>,
)
