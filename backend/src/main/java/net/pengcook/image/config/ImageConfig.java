package net.pengcook.image.config;

import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import net.pengcook.image.service.ImageClientService;
import net.pengcook.image.service.ObjectStorageClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImageConfig {

    @Value("${oracle.cloud.user-id}")
    private String userId;

    @Value("${oracle.cloud.tenancy-id}")
    private String tenancyId;

    @Value("${oracle.cloud.fingerprint}")
    private String fingerprint;

    @Value("${oracle.cloud.private-key}")
    private String privateKey;

    @Value("${oracle.cloud.region}")
    private String region;

    @Bean
    public ObjectStorage objectStorage() {
        AuthenticationDetailsProvider provider = SimpleAuthenticationDetailsProvider.builder()
                .userId(userId)
                .tenantId(tenancyId)
                .fingerprint(fingerprint)
                .privateKeySupplier(() -> {
                    try {
                        return new ByteArrayInputStream(privateKey.getBytes(StandardCharsets.UTF_8));
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to load private key", e);
                    }
                })
                .region(Region.fromRegionId(region))
                .build();

        return ObjectStorageClient.builder()
                .build(provider);
    }

    @Bean
    public ImageClientService imageClientService() {
        return new ObjectStorageClientService(objectStorage());
    }
}
