package net.pengcook.user.dto;

import java.util.Arrays;
import java.util.List;
import net.pengcook.user.domain.Reason;

public record ReportReasonResponse(Reason reason, String message) {

    public static final List<ReportReasonResponse> REASONS = Arrays.stream(Reason.values())
            .map(reason -> new ReportReasonResponse(reason, reason.getMessage()))
            .toList();
}
