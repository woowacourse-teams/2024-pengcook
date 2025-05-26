package net.pengcook.authentication.domain;

import net.pengcook.authentication.exception.JwtTokenException;

public record TokenPayload(
        long userId,
        String email,
        TokenType tokenType
) {

    public void validateAccessToken(String message) {
        if (tokenType != TokenType.ACCESS) {
            throw new JwtTokenException(message);
        }
    }

    public void validateRefreshToken(String message) {
        if (tokenType != TokenType.REFRESH) {
            throw new JwtTokenException(message);
        }
    }
}
