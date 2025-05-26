package net.pengcook.android.presentation.detail.dialog

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemReportReasonDetailBinding
import net.pengcook.android.presentation.core.model.ReportReason

class ReportReasonViewHolder(
    private val binding: ItemReportReasonDetailBinding,
    private val eventHandler: ReportEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(reportReason: ReportReason) {
        binding.reason = reportReason
        binding.eventHandler = eventHandler
    }
}
