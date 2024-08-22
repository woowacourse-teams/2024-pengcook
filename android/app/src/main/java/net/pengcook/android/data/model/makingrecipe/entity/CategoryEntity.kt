package net.pengcook.android.data.model.makingrecipe.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import net.pengcook.android.data.local.database.contract.CategoryContract
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract

@Entity(
    tableName = CategoryContract.TABLE_NAME,
    foreignKeys = [
        ForeignKey(
            entity = RecipeDescriptionEntity::class,
            parentColumns = [RecipeDescriptionContract.COLUMN_ID],
            childColumns = [CategoryContract.COLUMN_RECIPE_DESCRIPTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(CategoryContract.COLUMN_ID) val id: Long = 0,
    @ColumnInfo(CategoryContract.COLUMN_RECIPE_DESCRIPTION_ID) val recipeId: Long,
    @ColumnInfo(CategoryContract.COLUMN_CATEGORY_NAME) val categoryName: String,
)
