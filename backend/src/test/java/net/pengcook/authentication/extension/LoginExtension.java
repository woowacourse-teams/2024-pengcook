package net.pengcook.authentication.extension;

import io.restassured.RestAssured;
import java.time.LocalDate;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.dto.TokenPayload;
import net.pengcook.authentication.util.JwtTokenManager;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class LoginExtension implements BeforeEachCallback, AfterTestExecutionCallback {

    @Override
    public void beforeEach(ExtensionContext context) {

        WithLoginUser annotation = context.getRequiredTestMethod().getAnnotation(WithLoginUser.class);
        if (annotation != null) {
            ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
            JwtTokenManager jwtTokenManager = applicationContext.getBean(JwtTokenManager.class);
            UserRepository userRepository = applicationContext.getBean(UserRepository.class);

            User user = findOrSaveUser(annotation, userRepository);
            String accessToken = jwtTokenManager.createToken(new TokenPayload(user.getId(), user.getEmail()));

            RestAssured.port = ((ServletWebServerApplicationContext) applicationContext).getWebServer().getPort();
            RestAssured.requestSpecification = RestAssured.given().header("Authorization", "Bearer " + accessToken);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext extensionContext) {
        RestAssured.reset();
    }

    private User findOrSaveUser(WithLoginUser annotation, UserRepository userRepository) {
        return userRepository.findByEmail(annotation.email())
                .orElseGet(() -> saveUser(annotation, userRepository));
    }

    private User saveUser(WithLoginUser annotation, UserRepository userRepository) {
        User user = new User(
                annotation.email(),
                annotation.username(),
                annotation.nickname(),
                annotation.image(),
                LocalDate.parse(annotation.birth()),
                annotation.region()
        );
        return userRepository.save(user);
    }
}
