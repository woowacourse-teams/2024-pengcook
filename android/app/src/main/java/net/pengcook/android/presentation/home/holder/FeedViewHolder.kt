package net.pengcook.android.presentation.home.holder

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    private val eventListener: FeedItemEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeForList) {
        binding.recipe = item
        binding.eventListener = eventListener
        binding.executePendingBindings()
    }
}
