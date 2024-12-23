package net.pengcook.user.dto;

import jakarta.validation.constraints.NotNull;

public record UserFollowRequest(@NotNull long targetId) {
}
