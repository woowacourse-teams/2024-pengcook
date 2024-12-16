package net.pengcook.user.dto;

import net.pengcook.user.domain.UserFollow;

public record UserFollowResponse(
        long followerId,
        long followeeId
) {
    public UserFollowResponse(UserFollow userFollow) {
        this(
                userFollow.getFollower().getId(),
                userFollow.getFollowee().getId()
        );
    }
}
