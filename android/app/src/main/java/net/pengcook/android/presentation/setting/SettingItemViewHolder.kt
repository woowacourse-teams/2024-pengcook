package net.pengcook.android.presentation.setting

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemSettingBinding

class SettingItemViewHolder(
    private val binding: ItemSettingBinding,
    settingMenuItemClickListener: SettingMenuItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    init {
        binding.settingMenuItemClickListener = settingMenuItemClickListener
    }

    fun bind(menuItem: MenuItem) {
        binding.menuItem = menuItem
    }
}
