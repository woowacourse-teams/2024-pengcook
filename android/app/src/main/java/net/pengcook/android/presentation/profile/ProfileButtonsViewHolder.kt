package net.pengcook.android.presentation.profile

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.R
import net.pengcook.android.databinding.ItemProfileButtonBinding

class ProfileButtonsViewHolder(
    binding: ItemProfileButtonBinding,
    profileButtonClickListener: ProfileButtonClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        val context = binding.root.context
        binding.profileButtonClickListener = profileButtonClickListener
        binding.editProfile = context.getString(R.string.profile_edit_button)
        binding.settings = context.getString(R.string.profile_setting_button)
    }
}
