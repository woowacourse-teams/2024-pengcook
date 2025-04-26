package net.pengcook.block.domain;

import java.util.Set;
import lombok.AllArgsConstructor;
import net.pengcook.user.domain.User;

@AllArgsConstructor
public class BlockerGroup {

    private final Set<User> users;

    public boolean contains(long userId) {
        return users.stream()
                .anyMatch(user -> user.isSameUser(userId));
    }
}
