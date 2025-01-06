package net.pengcook.user.domain;

import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BlockeeGroup {

    private final Set<User> users;

    public boolean contains(long userId) {
        return users.stream()
                .anyMatch(user -> user.isSameUser(userId));
    }
}
