package net.pengcook.android.presentation.making

interface StepItemEventListener {
    fun onImageChange(id: Int)

    fun onDelete(id: Int)

    fun onOrderChange(items: List<RecipeStepImage>)
}
