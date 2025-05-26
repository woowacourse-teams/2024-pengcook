package net.pengcook.authentication.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithLoginUser {

    String email() default "tester@pengcook.net";

    String username() default "tester";

    String nickname() default "테스트 유저";

    String image() default "tester.jpg";

    String region() default "KOREA";
}
