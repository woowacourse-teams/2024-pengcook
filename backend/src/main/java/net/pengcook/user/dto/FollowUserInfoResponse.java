package net.pengcook.user.dto;

import net.pengcook.user.domain.User;

public record FollowUserInfoResponse(String username, String image) {

    public FollowUserInfoResponse(User user) {
        this(user.getUsername(), user.getImage());
    }
}
