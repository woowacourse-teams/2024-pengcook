package net.pengcook.user.controller;

import lombok.RequiredArgsConstructor;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/api/user/{id}")
    public UserResponse getUserById(@PathVariable long id) {
        return userService.getUserById(id);
    }
}
