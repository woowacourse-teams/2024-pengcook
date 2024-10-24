package net.pengcook.user.domain;

import java.util.Set;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BlockedUserGroup {

    private final Set<User> users;

    public boolean isBlocked(long userId) {
        return users.stream()
                .anyMatch(user -> user.isSameUser(userId));
    }
}
