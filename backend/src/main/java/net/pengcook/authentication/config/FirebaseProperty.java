package net.pengcook.authentication.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("firebase")
@RequiredArgsConstructor
@Getter
public class FirebaseProperty {

    private final String serviceAccount;
    private final String projectName;
}
