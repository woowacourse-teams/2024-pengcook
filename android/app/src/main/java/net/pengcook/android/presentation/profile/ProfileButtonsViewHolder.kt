package net.pengcook.android.presentation.profile

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.R
import net.pengcook.android.databinding.ItemDoubleButtonBinding

class ProfileButtonsViewHolder(
    binding: ItemDoubleButtonBinding,
    doubleButtonClickListener: DoubleButtonClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        val context = binding.root.context
        binding.doubleButtonClickListener = doubleButtonClickListener
        binding.leftButtonText = context.getString(R.string.profile_edit_button)
        binding.rightButtonText = context.getString(R.string.profile_setting_button)
    }
}
