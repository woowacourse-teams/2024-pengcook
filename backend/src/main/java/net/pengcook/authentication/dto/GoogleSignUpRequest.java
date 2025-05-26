package net.pengcook.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleSignUpRequest(
        @NotBlank String idToken,
        @NotBlank String username,
        @NotBlank String nickname,
        @NotBlank String country,
        String image
) {
}
