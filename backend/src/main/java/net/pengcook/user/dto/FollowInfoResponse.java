package net.pengcook.user.dto;

import java.util.List;

public record FollowInfoResponse(
        List<FollowUserInfoResponse> followers,
        long followerCount,
        List<FollowUserInfoResponse> followings,
        long followeeCount
) {
    public FollowInfoResponse(List<FollowUserInfoResponse> followers, List<FollowUserInfoResponse> followings) {
        this(followers, followers.size(), followings, followings.size());
    }
}
