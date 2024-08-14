package net.pengcook.android.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import net.pengcook.android.data.dao.RecipeDao
import net.pengcook.android.data.model.making.Recipe
import net.pengcook.android.data.model.making.RecipeStep

@Database(entities = [Recipe::class, RecipeStep::class], version = 1)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        private var instance: RecipeDatabase? = null

        fun getInstance(context: Context): RecipeDatabase =
            instance ?: synchronized(this) {
                Room
                    .databaseBuilder(
                        context.applicationContext,
                        RecipeDatabase::class.java,
                        "recipe.db",
                    ).build()
                    .also { instance = it }
            }
    }
}
