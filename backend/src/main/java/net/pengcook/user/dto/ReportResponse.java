package net.pengcook.user.dto;

import java.time.LocalDateTime;
import net.pengcook.user.domain.Reason;
import net.pengcook.user.domain.Type;
import net.pengcook.user.domain.UserReport;

public record ReportResponse(
        long reportId,
        long reporterId,
        long reporteeId,
        Reason reason,
        Type type,
        long targetId,
        String details,
        LocalDateTime createdAt
) {
    public ReportResponse(UserReport userReport) {
        this(
                userReport.getId(),
                userReport.getReporter().getId(),
                userReport.getReportee().getId(),
                userReport.getReason(),
                userReport.getType(),
                userReport.getTargetId(),
                userReport.getDetails(),
                userReport.getCreatedAt()
        );
    }
}



