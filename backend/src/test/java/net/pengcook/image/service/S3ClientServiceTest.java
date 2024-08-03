package net.pengcook.image.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;
import net.pengcook.image.dto.ImageUrlResponse;
import net.pengcook.image.dto.PresignedUrlResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class S3ClientServiceTest {

    private static final String FILE_NAME = "testImage.jpg";

    @Autowired
    private S3ClientService s3ClientService;

    @MockBean
    private S3Presigner s3Presigner;

    @Value("${cloud.aws.S3.cloudfront-cname}")
    private String cloudfrontCname;

    @Test
    @DisplayName("presigned url을 반환한다.")
    void generatePresignedUrl() throws MalformedURLException {
        String expectedPresignedUrl =
                "https://bucketName.s3.region.amazonaws.com/serviceName/image/fileName?X-Amz-Security-Token=REDACTED&X-Amz-Algorithm=REDACTED&X-Amz-Date=REDACTED&X-Amz-SignedHeaders=REDACTED&X-Amz-Expires=REDACTED&X-Amz-Credential=REDACTED&X-Amz-Signature=REDACTED";
        URL presignedUrl = new URL(expectedPresignedUrl);
        PresignedPutObjectRequest presignedPutObjectRequest = mock(PresignedPutObjectRequest.class);

        when(presignedPutObjectRequest.url())
                .thenReturn(presignedUrl);
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class)))
                .thenReturn(presignedPutObjectRequest);
        PresignedUrlResponse presignedUrlResponse = s3ClientService.generatePresignedPutUrl(FILE_NAME);

        assertThat(presignedUrlResponse.url())
                .isEqualTo(expectedPresignedUrl);
    }

    @Test
    @DisplayName("이미지 조회 url을 반환한다.")
    void getImageUrl() {
        String expectedImageUrl = cloudfrontCname + "/pengcook/image/" + FILE_NAME;
        ImageUrlResponse imageUrlResponse = new ImageUrlResponse(expectedImageUrl);

        assertThat(s3ClientService.getImageUrl(FILE_NAME))
                .isEqualTo(imageUrlResponse);
    }
}
