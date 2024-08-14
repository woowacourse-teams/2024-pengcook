package net.pengcook.android.data.model.making

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipe_descriptions")
data class RecipeDescription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val cookingTime: String,
    val difficulty: Int,
    val thumbnail: String,
    val categories: List<String>,
    val ingredients: List<Ingredient>,
)
