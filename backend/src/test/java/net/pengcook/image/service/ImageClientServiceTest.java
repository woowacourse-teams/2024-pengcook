package net.pengcook.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import net.pengcook.image.dto.ImageUrlResponse;
import net.pengcook.image.dto.UploadUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ImageClientServiceTest {

    private static final String OBJECT_NAME = "testImage.jpg";

    @Value("${oracle.cloud.region}")
    private String region;

    @Value("${oracle.cloud.bucket-name}")
    private String bucketName;

    @Value("${oracle.cloud.namespace}")
    private String namespace;

    @MockBean
    @Qualifier("imageClientService")
    private ImageClientService imageClientService;

    @Test
    @DisplayName("upload url을 반환한다.")
    void generateUploadUrl() {
        String uploadUrl = String.format("https://objectstorage.%s.oraclecloud.com/p/REDACTED_TOKEN/n/%s/b/%s/o/%s",
                region, namespace, bucketName, OBJECT_NAME);

        when(imageClientService.generateUploadUrl(OBJECT_NAME))
                .thenReturn(new UploadUrlResponse(uploadUrl));

        UploadUrlResponse uploadUrlResponse = imageClientService.generateUploadUrl(OBJECT_NAME);

        assertThat(uploadUrlResponse.url())
                .isEqualTo(uploadUrl);
    }

    @Test
    @DisplayName("이미지 조회 url을 반환한다.")
    void getImageUrl() {
        String publicUrl = String.format("https://objectstorage.%s.oraclecloud.com/n/%s/b/%s/o/%s",
                region, namespace, bucketName, OBJECT_NAME);

        when(imageClientService.getImageUrl(OBJECT_NAME))
                .thenReturn(new ImageUrlResponse(publicUrl));

        ImageUrlResponse imageUrlResponse = imageClientService.getImageUrl(OBJECT_NAME);

        assertThat(imageUrlResponse.url())
                .isEqualTo(publicUrl);
    }
}
