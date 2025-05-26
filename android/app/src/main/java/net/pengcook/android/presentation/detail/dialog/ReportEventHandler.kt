package net.pengcook.android.presentation.detail.dialog

import net.pengcook.android.presentation.core.model.ReportReason

interface ReportEventHandler {
    fun onCompleteReport(reportReason: ReportReason)
}
