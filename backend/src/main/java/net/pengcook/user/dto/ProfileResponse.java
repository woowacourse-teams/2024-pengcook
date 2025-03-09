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
        long followerCount,
        long followingCount,
        long recipeCount,
        boolean isFollow
) {
    public ProfileResponse(User user, long recipeCount, boolean isFollow) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getRegion(),
                user.getIntroduction(),
                user.getFollowerCount(),
                user.getFolloweeCount(),
                recipeCount,
                isFollow
        );
    }
}
