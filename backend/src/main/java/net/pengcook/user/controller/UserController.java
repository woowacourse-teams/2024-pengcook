package net.pengcook.user.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import net.pengcook.authentication.domain.UserInfo;
import net.pengcook.authentication.resolver.LoginUser;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/me")
    public UserResponse getUserProfile(@LoginUser UserInfo userInfo) {
        return userService.getUserById(userInfo.getId());
    }

    @GetMapping("/api/user/username/check")
    public UsernameCheckResponse checkUsername(@RequestParam @NotBlank String username) {
        return userService.checkUsername(username);
    }
}
