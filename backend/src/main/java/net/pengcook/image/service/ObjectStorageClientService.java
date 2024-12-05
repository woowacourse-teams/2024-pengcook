package net.pengcook.image.service;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails.AccessType;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import java.time.Instant;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pengcook.image.dto.ImageUrlResponse;
import net.pengcook.image.dto.UploadUrlResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ObjectStorageClientService implements ImageClientService {

    private final ObjectStorage client;

    @Value("${oracle.cloud.bucket-name}")
    private String bucketName;

    @Value("${oracle.cloud.namespace}")
    private String namespace;

    @Value("${oracle.cloud.url-prefix}")
    private String urlPrefix;

    @Value("${oracle.cloud.duration-seconds}")
    private int durationSeconds;

    public UploadUrlResponse generateUploadUrl(String fileName) {
        CreatePreauthenticatedRequestDetails details = CreatePreauthenticatedRequestDetails.builder()
                .accessType(AccessType.ObjectWrite)
                .name("upload-" + fileName)
                .timeExpires(Date.from(Instant.now().plusSeconds(durationSeconds)))
                .objectName(fileName)
                .build();

        CreatePreauthenticatedRequestRequest request = CreatePreauthenticatedRequestRequest.builder()
                .namespaceName(namespace)
                .bucketName(bucketName)
                .createPreauthenticatedRequestDetails(details)
                .build();

        String uploadUri = client.createPreauthenticatedRequest(request)
                .getPreauthenticatedRequest()
                .getAccessUri();

        return new UploadUrlResponse(urlPrefix + uploadUri);
    }

    public ImageUrlResponse getImageUrl(String fileName) {
        return new ImageUrlResponse(String.format("%s/n/%s/b/%s/o/%s",
                client.getEndpoint(),
                namespace,
                bucketName,
                fileName));
    }
}
