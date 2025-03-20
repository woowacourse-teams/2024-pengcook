package net.pengcook.user.dto;

import net.pengcook.user.domain.UserBlock;

public record UserBlockResponse(UserResponse blocker, UserResponse blockee) {

    public UserBlockResponse(UserBlock userBlock) {
        this(new UserResponse(userBlock.getBlocker()), new UserResponse(userBlock.getBlockee()));
    }
}
