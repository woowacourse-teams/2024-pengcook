package net.pengcook.android.data.model.makingrecipe.entity

import androidx.room.Embedded
import androidx.room.Relation
import net.pengcook.android.data.local.database.contract.CategoryContract
import net.pengcook.android.data.local.database.contract.IngredientContract
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract
import net.pengcook.android.data.local.database.contract.RecipeStepContract
import net.pengcook.android.data.model.step.RecipeStepEntity

data class CreatedRecipe(
    @Embedded val recipeDescription: RecipeDescriptionEntity,
    @Relation(
        parentColumn = RecipeDescriptionContract.COLUMN_ID,
        entityColumn = RecipeStepContract.COLUMN_RECIPE_DESCRIPTION_ID,
    )
    val steps: List<RecipeStepEntity>,
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
