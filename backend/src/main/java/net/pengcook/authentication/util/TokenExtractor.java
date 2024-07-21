package net.pengcook.authentication.util;

public class TokenExtractor {

    private static final String BEARER = "Bearer ";

    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null) {
            throw new IllegalArgumentException("Authorization header is required.");
        }
        if (!authorizationHeader.startsWith(BEARER)) {
            throw new IllegalArgumentException("Invalid Authorization header.");
        }
        return authorizationHeader.substring(BEARER.length());
    }
}
