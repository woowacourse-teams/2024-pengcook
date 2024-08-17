package net.pengcook.android.data.model.step

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import net.pengcook.android.data.local.database.contract.RecipeDescriptionContract
import net.pengcook.android.data.local.database.contract.RecipeStepContract
import net.pengcook.android.data.model.makingrecipe.entity.RecipeDescriptionEntity

@Entity(
    tableName = RecipeStepContract.TABLE_NAME,
    indices = [
        Index(
            value = [RecipeStepContract.COLUMN_RECIPE_DESCRIPTION_ID, RecipeStepContract.COLUMN_STEP_NUMBER],
            unique = true,
        ),
    ],
    primaryKeys = [
        RecipeStepContract.COLUMN_ID,
        RecipeStepContract.COLUMN_RECIPE_DESCRIPTION_ID,
    ],
    foreignKeys = [
        ForeignKey(
            entity = RecipeDescriptionEntity::class,
            parentColumns = [RecipeDescriptionContract.COLUMN_ID],
            childColumns = [RecipeStepContract.COLUMN_RECIPE_DESCRIPTION_ID],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
)
data class RecipeStepEntity(
    @ColumnInfo(RecipeStepContract.COLUMN_ID) val id: Long = System.currentTimeMillis(),
    @ColumnInfo(RecipeStepContract.COLUMN_RECIPE_DESCRIPTION_ID) val recipeDescriptionId: Long,
    @ColumnInfo(RecipeStepContract.COLUMN_IMAGE_URI) val imageUri: String,
    @ColumnInfo(RecipeStepContract.COLUMN_IMAGE_TITLE) val imageTitle: String,
    @ColumnInfo(RecipeStepContract.COLUMN_COOKING_TIME) val cookingTime: String,
    @ColumnInfo(RecipeStepContract.COLUMN_STEP_NUMBER) val stepNumber: Int,
    @ColumnInfo(RecipeStepContract.COLUMN_DESCRIPTION) val description: String,
)
