package net.pengcook.user.dto;

import java.time.LocalDateTime;
import net.pengcook.user.domain.UserReport;

public record UserReportResponse(
        long reportId,
        long reporterId,
        long reporteeId,
        String reason,
        String details,
        LocalDateTime createdAt
) {
    public UserReportResponse(UserReport userReport) {
        this(
                userReport.getId(),
                userReport.getReporter().getId(),
                userReport.getReportee().getId(),
                userReport.getReason(),
                userReport.getDetails(),
                userReport.getCreatedAt()
        );
    }
}



