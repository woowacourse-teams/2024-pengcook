package net.pengcook.android.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.pengcook.android.data.local.database.RecipeDatabase
import net.pengcook.android.data.local.database.dao.CategoryDao
import net.pengcook.android.data.local.database.dao.IngredientDao
import net.pengcook.android.data.local.database.dao.RecipeDescriptionDao
import net.pengcook.android.data.local.database.dao.RecipeStepDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRecipeDatabase(
        @ApplicationContext context: Context,
    ): RecipeDatabase = RecipeDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideRecipeStepDao(recipeDatabase: RecipeDatabase): RecipeStepDao = recipeDatabase.recipeStepDao()

    @Provides
    @Singleton
    fun provideRecipeDescriptionDao(recipeDatabase: RecipeDatabase): RecipeDescriptionDao = recipeDatabase.recipeDescriptionDao()

    @Provides
    @Singleton
    fun provideCategoryDao(recipeDatabase: RecipeDatabase): CategoryDao = recipeDatabase.categoryDao()

    @Provides
    @Singleton
    fun provideIngredientDao(recipeDatabase: RecipeDatabase): IngredientDao = recipeDatabase.ingredientDao()
}
