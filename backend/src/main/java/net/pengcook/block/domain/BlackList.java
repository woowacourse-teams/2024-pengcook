package net.pengcook.block.domain;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import net.pengcook.user.domain.User;

@AllArgsConstructor
public class BlackList {

    private final Set<User> users;

    public BlackList(List<User> blockees, List<User> blockers) {
        this.users = Stream.of(blockees, blockers)
                .flatMap(List::stream)
                .collect(Collectors.toSet());
    }

    public boolean contains(long userId) {
        return users.stream()
                .anyMatch(user -> user.isSameUser(userId));
    }
}
