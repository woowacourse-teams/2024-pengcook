package net.pengcook.android.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.pengcook.android.data.local.database.dao.CategoryDao
import net.pengcook.android.data.local.database.dao.IngredientDao
import net.pengcook.android.data.local.database.dao.RecipeDescriptionDao
import net.pengcook.android.data.local.database.dao.RecipeStepDao
import net.pengcook.android.data.model.makingrecipe.entity.CategoryEntity
import net.pengcook.android.data.model.makingrecipe.entity.IngredientEntity
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity
import net.pengcook.android.data.model.step.RecipeStepEntity

@Database(
    entities = [RecipeDescriptionEntity::class, RecipeStepEntity::class, CategoryEntity::class, IngredientEntity::class],
    version = 6,
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDescriptionDao(): RecipeDescriptionDao

    abstract fun recipeStepDao(): RecipeStepDao

    abstract fun categoryDao(): CategoryDao

    abstract fun ingredientDao(): IngredientDao

    companion object {
        private var instance: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe.db",
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { instance = it }
            }
    }
}
