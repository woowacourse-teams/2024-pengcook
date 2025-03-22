package net.pengcook.android.presentation.profile

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemProfileDescriptionBinding
import net.pengcook.android.domain.model.profile.UserProfile

class ProfileDescriptionViewHolder(
    private val binding: ItemProfileDescriptionBinding,
    private val profileButtonClickListener: ProfileButtonClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(profile: UserProfile) {
        binding.profile = profile
        binding.profileButtonClickListener = profileButtonClickListener
    }
}
