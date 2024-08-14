package net.pengcook.android.di

import android.content.Context
import androidx.room.Room
import net.pengcook.android.data.database.RecipeDatabase

object DatabaseModule {
    fun provideDatabase(context: Context): RecipeDatabase =
        Room
            .databaseBuilder(
                context.applicationContext,
                RecipeDatabase::class.java,
                "recipe_database",
            ).build()

    fun provideRecipeDao(database: RecipeDatabase) = database.recipeDao()
}
