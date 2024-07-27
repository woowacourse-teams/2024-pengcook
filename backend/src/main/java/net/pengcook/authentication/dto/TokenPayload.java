package net.pengcook.authentication.dto;

import net.pengcook.authentication.util.TokenType;

public record TokenPayload(
        long userId,
        String email,
        TokenType tokenType
) {
}
