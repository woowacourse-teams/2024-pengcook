package net.pengcook.user.dto;

import net.pengcook.user.domain.User;

public record UpdateProfileResponse(
        long userId,
        String email,
        String username,
        String nickname,
        String image,
        String region,
        String introduction
) {
    public UpdateProfileResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getRegion(),
                "hello world"
        );
    }
}
