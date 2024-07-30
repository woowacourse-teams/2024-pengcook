package net.pengcook.user.service;

import lombok.AllArgsConstructor;
import net.pengcook.user.domain.User;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.dto.UsernameCheckResponse;
import net.pengcook.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResponse getUserById(long id) {
        User user = userRepository.findById(id).orElseThrow();
        return new UserResponse(user);
    }

    public UsernameCheckResponse checkUsername(String username) {
        boolean userExists = userRepository.existsByUsername(username);
        return new UsernameCheckResponse(!userExists);
    }
}
