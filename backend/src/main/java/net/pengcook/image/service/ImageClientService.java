package net.pengcook.image.service;

import net.pengcook.image.dto.ImageUrlResponse;
import net.pengcook.image.dto.UploadUrlResponse;

public interface ImageClientService {

    UploadUrlResponse generateUploadUrl(String fileName);

    ImageUrlResponse getImageUrl(String fileName);
}
