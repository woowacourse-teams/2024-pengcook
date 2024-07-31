package net.pengcook.user.dto;

public record UserBlockResponse(
        UserResponse blocker,
        UserResponse blockee
) {
}
