package net.pengcook.authentication.dto;

import net.pengcook.user.domain.User;

public record GoogleSignUpResponse(
        long id,
        String email,
        String username,
        String nickname,
        String image,
        String country,
        String accessToken,
        String refreshToken
) {
    public GoogleSignUpResponse(User user, String accessToken, String refreshToken) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getRegion(),
                accessToken,
                refreshToken
        );
    }
}
