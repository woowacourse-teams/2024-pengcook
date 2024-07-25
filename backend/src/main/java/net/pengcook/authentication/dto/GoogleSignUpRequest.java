package net.pengcook.authentication.dto;

import com.google.firebase.database.annotations.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

public record GoogleSignUpRequest(
        @NotBlank String idToken,
        @NotBlank String username,
        @NotBlank String nickname,
        @NotNull LocalDate birthday,
        @NotBlank String country
) {
}
