package net.pengcook.user.dto;

import java.util.List;

public record FollowInfoResponse(
        List<FollowUserInfoResponse> follows,
        long followCount
) {
    public FollowInfoResponse(List<FollowUserInfoResponse> follows) {
        this(follows, follows.size());
    }
}
