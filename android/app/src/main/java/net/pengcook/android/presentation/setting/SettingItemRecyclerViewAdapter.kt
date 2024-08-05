package net.pengcook.android.presentation.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemSettingBinding

class SettingItemRecyclerViewAdapter(
    private val menus: List<MenuItem>,
    private val settingMenuItemClickListener: SettingMenuItemClickListener,
) : RecyclerView.Adapter<SettingItemViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSettingBinding.inflate(layoutInflater, parent, false)
        return SettingItemViewHolder(binding, settingMenuItemClickListener)
    }

    override fun getItemCount(): Int {
        return menus.size
    }

    override fun onBindViewHolder(
        holder: SettingItemViewHolder,
        position: Int,
    ) {
        holder.bind(menus[position])
    }
}
