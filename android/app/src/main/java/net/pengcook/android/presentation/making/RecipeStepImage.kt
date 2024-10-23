package net.pengcook.android.presentation.making

import android.net.Uri
import java.io.File

data class RecipeStepImage(
    val itemId: Int,
    val isLoading: Boolean = true,
    val file: File?,
    val uri: Uri,
    val uploaded: Boolean = false,
    val description: String = "",
    val cookingTime: String = "00:00:00",
    val sequence: Int,
    val imageTitle: String,
) {
    companion object {
        private var id: Int = 0

        fun of(
            uri: Uri,
            file: File? = null,
            sequence: Int,
            title: String,
        ): RecipeStepImage {
            return RecipeStepImage(
                itemId = id++,
                file = file,
                uri = uri,
                sequence = sequence,
                imageTitle = title,
            )
        }
    }
}
