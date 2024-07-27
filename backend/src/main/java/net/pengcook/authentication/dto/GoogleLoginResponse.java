package net.pengcook.authentication.dto;

public record GoogleLoginResponse(
        boolean registered,
        String accessToken,
        String refreshToken
) {
}
