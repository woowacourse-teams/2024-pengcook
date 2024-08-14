package net.pengcook.android.data.model.making

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val requirement: String = "REQUIRED",
    val substitutions: List<String>? = null,
)
