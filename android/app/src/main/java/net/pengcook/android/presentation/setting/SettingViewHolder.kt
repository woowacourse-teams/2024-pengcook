package net.pengcook.android.presentation.setting

import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import net.pengcook.android.databinding.ItemSettingContainerBinding
import net.pengcook.android.presentation.setting.model.Setting

class SettingViewHolder(
    private val binding: ItemSettingContainerBinding,
    private val settingMenuItemClickListener: SettingMenuItemClickListener,
) : RecyclerView.ViewHolder(binding.root) {
    private val dividerItemDecoration =
        MaterialDividerItemDecoration(binding.root.context, LinearLayout.VERTICAL).apply {
            isLastItemDecorated = false
        }

    init {
        binding.rvSettingContainer.addItemDecoration(dividerItemDecoration)
    }

    fun bind(item: Setting) {
        val adapter = SettingItemRecyclerViewAdapter(item.menus, settingMenuItemClickListener)
        binding.adapter = adapter
    }
}
