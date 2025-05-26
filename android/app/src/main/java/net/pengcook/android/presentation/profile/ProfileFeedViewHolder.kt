package net.pengcook.android.presentation.profile

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemProfileFeedBinding
import net.pengcook.android.presentation.core.model.RecipeForList

class ProfileFeedViewHolder(
    private val binding: ItemProfileFeedBinding,
    profileFeedClickListener: ProfileFeedClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.profileFeedClickListener = profileFeedClickListener
    }

    fun bind(recipe: RecipeForList) {
        binding.recipe = recipe
    }
}
