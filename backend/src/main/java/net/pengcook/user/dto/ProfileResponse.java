package net.pengcook.user.dto;

import net.pengcook.user.domain.User;

public record ProfileResponse(
        long id,
        String email,
        String username,
        String nickname,
        String image,
        String region,
        String introduction,
        long follower,
        long following,
        long recipeCount
        long followerCount,
        long followingCount,
        long recipeCount,
) {

    public ProfileResponse(User user, long recipeCount) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getRegion(),
                "hello world",
                user.getUserFollowerCount(),
                user.getUserFolloweeCount(),
                recipeCount
                user.getFollowerCount(),
                user.getFolloweeCount(),
                recipeCount,
        );
    }
}
