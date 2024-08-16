package net.pengcook.user.dto;

import net.pengcook.user.domain.Reason;

public record ReportReasonResponse(Reason reason, String message) {
}
