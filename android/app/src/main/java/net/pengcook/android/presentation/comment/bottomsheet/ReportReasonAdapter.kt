package net.pengcook.android.presentation.comment.bottomsheet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import net.pengcook.android.databinding.ItemReportReasonBinding
import net.pengcook.android.presentation.core.model.ReportReason

class ReportReasonAdapter(
    private val eventHandler: ReportEventHandler,
) : ListAdapter<ReportReason, ReportReasonViewHolder>(diffUtil) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ReportReasonViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemReportReasonBinding.inflate(layoutInflater, parent, false)
        return ReportReasonViewHolder(
            binding = binding,
            eventHandler = eventHandler,
        )
    }

    override fun onBindViewHolder(
        holder: ReportReasonViewHolder,
        position: Int,
    ) {
        holder.bind(getItem(position))
    }

    companion object {
        private val diffUtil =
            object : DiffUtil.ItemCallback<ReportReason>() {
                override fun areItemsTheSame(
                    oldItem: ReportReason,
                    newItem: ReportReason,
                ): Boolean = oldItem.reason == newItem.reason

                override fun areContentsTheSame(
                    oldItem: ReportReason,
                    newItem: ReportReason,
                ): Boolean = oldItem == newItem
            }
    }
}
