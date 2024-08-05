package net.pengcook.user.dto;

public record UserReportRequest(
        long reporteeId,
        String reason,
        String details
) {
}
