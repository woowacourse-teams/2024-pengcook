package net.pengcook.user.dto;

import com.google.firebase.database.annotations.NotNull;

public record UserBlockRequest(@NotNull long blockeeId) {
}
