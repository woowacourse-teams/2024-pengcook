package net.pengcook.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import net.pengcook.user.dto.UserResponse;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@Import(UserService.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/data/users.sql")
class UserServiceTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    @Test
    @DisplayName("id를 통해 사용자의 정보를 불러올 수 있다.")
    void getUserById() {
        long id = 1L;
        UserResponse expected = new UserResponse(
                id,
                "loki@pengcook.net",
                "loki",
                "로키",
                "loki.jpg",
                LocalDate.of(1999, 8, 8),
                "KOREA"
        );

        UserResponse actual = userService.getUserById(id);

        assertThat(actual).usingRecursiveAssertion().isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 id의 사용자를 불러오려고 하면 예외가 발생한다.")
    void getUserByIdWhenNotExistId() {
        long id = 2000L;

        assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(NoSuchElementException.class);
    }
}
