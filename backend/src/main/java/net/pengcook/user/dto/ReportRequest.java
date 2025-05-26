package net.pengcook.user.dto;

import jakarta.validation.constraints.NotNull;
import net.pengcook.user.domain.Reason;
import net.pengcook.user.domain.Type;

public record ReportRequest(
        long reporteeId,
        @NotNull Reason reason,
        @NotNull Type type,
        @NotNull long targetId,
        String details
) {
}
