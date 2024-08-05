package net.pengcook.android.presentation.profile

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemProfileDescriptionBinding

class ProfileDescriptionViewHolder(
    private val binding: ItemProfileDescriptionBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(profile: Profile) {
        binding.profile = profile
    }
}
