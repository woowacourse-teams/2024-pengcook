package net.pengcook.user.dto;

import net.pengcook.user.domain.User;

public record FollowUserInfoResponse(long userId, String username, String image) {

    public FollowUserInfoResponse(User user) {
        this(user.getId(), user.getUsername(), user.getImage());
    }
}
