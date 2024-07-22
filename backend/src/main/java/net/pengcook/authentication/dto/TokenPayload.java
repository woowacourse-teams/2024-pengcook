package net.pengcook.authentication.dto;

public record TokenPayload(
        long userId,
        String email
) {
}
