package net.pengcook.android.presentation.comment.bottomsheet

import androidx.recyclerview.widget.RecyclerView
import net.pengcook.android.databinding.ItemReportReasonBinding
import net.pengcook.android.presentation.core.model.ReportReason

class ReportReasonViewHolder(
    private val binding: ItemReportReasonBinding,
    private val eventHandler: ReportEventHandler,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(reportReason: ReportReason) {
        binding.reason = reportReason
        binding.eventHandler = eventHandler
    }
}
