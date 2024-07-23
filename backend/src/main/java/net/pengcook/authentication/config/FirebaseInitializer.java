package net.pengcook.authentication.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseInitializer {
    private final String firebaseServiceAccount;
    private final String projectName;

    public FirebaseInitializer(
            @Value("${firebase.config.service-account}") String firebaseServiceAccount,
            @Value("${firebase.project.name}") String projectName
    ) {
        this.firebaseServiceAccount = firebaseServiceAccount;
        this.projectName = projectName;
    }

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        try (InputStream serviceAccount = new ByteArrayInputStream(firebaseServiceAccount.getBytes())) {

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setProjectId(projectName)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                return FirebaseApp.initializeApp(options);
            }
            return FirebaseApp.getInstance();
        }
    }

    @Bean
    public FirebaseAuth getFirebaseAuth() throws IOException {
        return FirebaseAuth.getInstance(firebaseApp());
    }
}
