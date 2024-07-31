package net.pengcook.user.dto;

import net.pengcook.user.domain.User;

public record UserResponse(
        long id,
        String email,
        String username,
        String nickname,
        String image,
        String region
) {

    public UserResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getRegion()
        );
    }
}
