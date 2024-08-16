package net.pengcook.authentication.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserInfo {

    private final long id;
    private final String email;

    public boolean isSameUser(long userId) {
        return this.id == userId;
    }
}
