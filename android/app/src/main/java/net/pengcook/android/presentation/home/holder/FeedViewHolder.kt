package net.pengcook.android.presentation.home.holder

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemFeedBinding
import net.pengcook.android.presentation.core.model.RecipeForList
import net.pengcook.android.presentation.home.HomeEventListener
import net.pengcook.android.presentation.home.listener.FeedItemEventListener

class FeedViewHolder(
    private val binding: ItemFeedBinding,
    private val homeEventListener: HomeEventListener,
    private val eventListener: FeedItemEventListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: RecipeForList) {
        binding.recipe = item
        binding.homeEventListener = homeEventListener
        binding.eventListener = eventListener
        binding.executePendingBindings()
    }
}
