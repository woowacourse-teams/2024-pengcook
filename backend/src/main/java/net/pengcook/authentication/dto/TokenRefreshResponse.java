package net.pengcook.authentication.dto;

public record TokenRefreshResponse(
        String accessToken,
        String refreshToken
) {
}
