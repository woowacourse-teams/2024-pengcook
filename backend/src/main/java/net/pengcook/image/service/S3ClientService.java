package net.pengcook.image.service;

import java.net.URL;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
public class S3ClientService {

    private static final int DURATION_MINUTES = 10;

    private final S3Presigner s3Presigner;

    @Value("${cloud.aws.S3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.S3.path}")
    private String imagePath;

    public URL generatePresignedPutUrl(String keyName) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imagePath + keyName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(DURATION_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        return s3Presigner.presignPutObject(presignRequest).url();
    }
}
