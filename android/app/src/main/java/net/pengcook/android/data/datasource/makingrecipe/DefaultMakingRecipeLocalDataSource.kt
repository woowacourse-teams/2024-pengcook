package net.pengcook.android.data.datasource.makingrecipe

import androidx.room.withTransaction
import net.pengcook.android.data.local.database.RecipeDatabase
import net.pengcook.android.data.local.database.dao.CategoryDao
import net.pengcook.android.data.local.database.dao.IngredientDao
import net.pengcook.android.data.local.database.dao.RecipeDescriptionDao
import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipe
import net.pengcook.android.data.model.makingrecipe.entity.CreatedRecipeDescription
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultMakingRecipeLocalDataSource @Inject constructor(
    private val database: RecipeDatabase,
) : MakingRecipeLocalDataSource {
    private val recipeDescriptionDao: RecipeDescriptionDao = database.recipeDescriptionDao()
    private val ingredientDao: IngredientDao = database.ingredientDao()
    private val categoryDao: CategoryDao = database.categoryDao()

    override suspend fun saveRecipeDescription(
        recipeDescription: RecipeDescriptionEntity,
        ingredients: List<IngredientEntity>,
        categories: List<CategoryEntity>,
    ): Long {
        return database.withTransaction {
            recipeDescriptionDao.insertCreatedRecipeDescription(recipeDescription)
            ingredientDao.saveIngredients(ingredients)
            categoryDao.saveCategories(categories)
            recipeDescription.id
        }
    }

    override suspend fun fetchTotalRecipeData(): CreatedRecipe? {
        return recipeDescriptionDao.fetchRecentlyCreatedRecipe()
    }

    override suspend fun fetchRecipeDescription(): CreatedRecipeDescription? {
        return recipeDescriptionDao.fetchLatestRecipeDescription()
    }

    override suspend fun deleteCreatedRecipeById(recipeId: Long) {
        recipeDescriptionDao.deleteCreatedRecipeById(recipeId)
    }

    override suspend fun deleteAllCreatedRecipes() {
        recipeDescriptionDao.deleteAllCreatedRecipes()
    }
}
