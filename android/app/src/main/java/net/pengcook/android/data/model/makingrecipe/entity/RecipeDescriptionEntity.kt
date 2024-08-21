package net.pengcook.android.data.model.makingrecipe.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract

@Entity(RecipeDescriptionContract.TABLE_NAME)
data class RecipeDescriptionEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(RecipeDescriptionContract.COLUMN_ID) val id: Long,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_TITLE) val title: String,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_IMAGE_URI) val imageUri: String,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_INTRODUCTION) val description: String,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_COOKING_TIME) val cookingTime: String,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_DIFFICULTY) val difficulty: Int,
    @ColumnInfo(RecipeDescriptionContract.COLUMN_THUMBNAIL) val thumbnail: String,
)
