package net.pengcook.authentication.dto;

import net.pengcook.user.domain.User;

public record GoogleSignUpResponse(
        long id,
        String email,
        String username,
        String nickname,
        String image,
        String birthday,
        String country,
        String accessToken
) {
    public GoogleSignUpResponse(User user, String accessToken) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getBirth().toString(),
                user.getRegion(),
                accessToken
        );
    }
}
