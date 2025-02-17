package net.pengcook.authentication.extension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import net.pengcook.authentication.annotation.WithLoginUser;
import net.pengcook.authentication.domain.JwtTokenManager;
import net.pengcook.authentication.domain.TokenPayload;
import net.pengcook.authentication.domain.TokenType;
import net.pengcook.user.domain.User;
import net.pengcook.user.repository.UserRepository;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

public class LoginExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {

    @Override
    public void beforeTestExecution(ExtensionContext context) {

        WithLoginUser annotation = context.getRequiredTestMethod().getAnnotation(WithLoginUser.class);
        if (annotation != null) {
            ApplicationContext applicationContext = SpringExtension.getApplicationContext(context);
            JwtTokenManager jwtTokenManager = applicationContext.getBean(JwtTokenManager.class);
            UserRepository userRepository = applicationContext.getBean(UserRepository.class);

            User user = findOrSaveUser(annotation, userRepository);
            String accessToken = jwtTokenManager.createToken(
                    new TokenPayload(user.getId(), user.getEmail(), TokenType.ACCESS));

            RestAssured.requestSpecification = new RequestSpecBuilder().build()
                    .header("Authorization", "Bearer " + accessToken);
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
                annotation.region(),
                null
        );
        return userRepository.save(user);
    }
}
