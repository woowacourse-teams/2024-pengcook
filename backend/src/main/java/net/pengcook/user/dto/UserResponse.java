package net.pengcook.user.dto;

import java.time.LocalDate;
import net.pengcook.user.domain.User;

public record UserResponse(
        long id,
        String email,
        String username,
        String nickname,
        String image,
        LocalDate birth,
        String region
) {

    public UserResponse(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getImage(),
                user.getBirth(),
                user.getRegion()
        );
    }
}
