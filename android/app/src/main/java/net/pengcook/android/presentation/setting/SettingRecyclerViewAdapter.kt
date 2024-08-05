package net.pengcook.android.presentation.setting

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemSettingContainerBinding

class SettingRecyclerViewAdapter(
    private val settings: List<Setting>,
    private val settingMenuItemClickListener: SettingMenuItemClickListener,
) : RecyclerView.Adapter<SettingViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): SettingViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSettingContainerBinding.inflate(layoutInflater, parent, false)
        return SettingViewHolder(binding, settingMenuItemClickListener)
    }

    override fun getItemCount(): Int {
        return settings.size
    }

    override fun onBindViewHolder(
        holder: SettingViewHolder,
        position: Int,
    ) {
        holder.bind(settings[position])
    }
}
