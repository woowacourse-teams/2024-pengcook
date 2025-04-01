package net.pengcook.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateProfileRequest(
        @NotBlank String username,
        @NotBlank String nickname,
        @NotBlank String image,
        @NotBlank String region,
        String introduction
) {
    public UpdateProfileRequest {
        if (introduction == null) {
            introduction = "";
        }
    }
}
