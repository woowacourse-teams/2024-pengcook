package net.pengcook.image.service;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pengcook.image.dto.ImageUrlResponse;
import net.pengcook.image.dto.PresignedUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3ClientService {

    private static final int DURATION_MINUTES = 10;

    private final S3Presigner s3Presigner;

    private final S3Client s3Client;

    @Value("${cloud.aws.S3.bucket}")
    private String bucketName;

    @Value("${cloud.aws.S3.path}")
    private String imagePath;

    @Value("${cloud.aws.S3.s3-url}")
    private String s3Url;

    @Value("${cloud.aws.S3.cloudfront-cname}")
    private String cloudFrontCname;

    public PresignedUrlResponse generatePresignedPutUrl(String fileName) {

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(imagePath + fileName)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(DURATION_MINUTES))
                .putObjectRequest(putObjectRequest)
                .build();

        String url = s3Presigner.presignPutObject(presignRequest).url().toString();

        return new PresignedUrlResponse(url);
    }

    public ImageUrlResponse getImageUrl(String fileName) {
        GetUrlRequest request = GetUrlRequest.builder()
                .bucket(bucketName)
                .key(imagePath + fileName)
                .build();

        String savedUrl = s3Client.utilities()
                .getUrl(request)
                .toString()
                .replace(s3Url, cloudFrontCname);

        return new ImageUrlResponse(savedUrl);
    }
}
