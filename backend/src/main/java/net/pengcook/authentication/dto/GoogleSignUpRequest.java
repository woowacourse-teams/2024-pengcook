package net.pengcook.authentication.dto;

import java.time.LocalDate;

public record GoogleSignUpRequest(
        String idToken,
        String username,
        String nickname,
        LocalDate birthday,
        String country
) {
}
