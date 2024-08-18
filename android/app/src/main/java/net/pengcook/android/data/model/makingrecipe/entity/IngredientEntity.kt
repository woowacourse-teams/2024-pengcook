package net.pengcook.android.data.model.makingrecipe.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.pengcook.android.data.local.database.contract.IngredientContract
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract
import net.pengcook.android.domain.model.recipemaking.IngredientRequirement

@Entity(
    tableName = IngredientContract.TABLE_NAME,
    indices = [Index(value = [IngredientContract.COLUMN_NAME], unique = true)],
    foreignKeys = [
        ForeignKey(
            entity = RecipeDescriptionEntity::class,
            parentColumns = [RecipeDescriptionContract.COLUMN_ID],
            childColumns = [IngredientContract.COLUMN_RECIPE_DESCRIPTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class IngredientEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(IngredientContract.COLUMN_ID) val id: Long = 0,
    @ColumnInfo(IngredientContract.COLUMN_RECIPE_DESCRIPTION_ID) val recipeId: Long,
    @ColumnInfo(IngredientContract.COLUMN_NAME) val name: String,
    @ColumnInfo(IngredientContract.COLUMN_REQUIREMENT) val requirement: String = IngredientRequirement.REQUIRED.value,
    @ColumnInfo(IngredientContract.COLUMN_SUBSTITUTIONS) val substitutions: String? = null,
)
