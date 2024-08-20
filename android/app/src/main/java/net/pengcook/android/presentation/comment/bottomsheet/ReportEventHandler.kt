package net.pengcook.android.presentation.comment.bottomsheet

import net.pengcook.android.presentation.core.model.ReportReason

interface ReportEventHandler {
    fun onCompleteReport(reportReason: ReportReason)
}
